package com.akundu.kkplayer.service

import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.graphics.Bitmap
import android.media.MediaPlayer
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import androidx.core.net.toUri
import com.akundu.kkplayer.database.SongDatabase
import com.akundu.kkplayer.database.entity.SongEntity
import com.akundu.kkplayer.feature.settings.datastore.DataStoreManager
import com.akundu.kkplayer.feature.settings.datastore.RepeatMode
import com.akundu.kkplayer.service.notification.PlayerNotificationBuilder
import com.akundu.kkplayer.service.playback.AlbumArtExtractor
import com.akundu.kkplayer.service.playback.PlaybackController
import com.akundu.kkplayer.service.playback.PlaybackQueueController
import com.akundu.kkplayer.storage.Constants.MEDIA_PATH
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class BackgroundSoundService :
    Service(),
    PlaybackController {
    private var player: MediaPlayer? = null
    private var mediaSession: MediaSessionCompat? = null
    private var uriString: String = ""
    private var songTitle: String = ""
    private var currentSongId: Int = 0
    private var albumArt: Bitmap? = null
    private val _repeatMode = MutableStateFlow(RepeatMode.NONE)

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private var repeatModeJob: Job? = null

    private val queueController: PlaybackQueueController by lazy {
        PlaybackQueueController(SongDatabase.getDatabase(this).songDao())
    }

    private val positionUpdateHandler = Handler(Looper.getMainLooper())
    private val positionUpdateRunnable: Runnable =
        object : Runnable {
            override fun run() {
                if (player?.isPlaying == true) {
                    // Re-posting the notification (not just updating the session) is what actually
                    // refreshes the visible progress/animation on most devices.
                    runAsForeground()
                }
                positionUpdateHandler.postDelayed(this, 1000)
            }
        }

    companion object {
        private var self: BackgroundSoundService? = null

        fun getServiceObject(): BackgroundSoundService? = self
    }

    override fun onBind(intent: Intent): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        mediaSession =
            MediaSessionCompat(this, "BackgroundSoundService").apply {
                setCallback(
                    object : MediaSessionCompat.Callback() {
                        override fun onPlay() {
                            if (player?.isPlaying != true) playPausePlayer()
                        }

                        override fun onPause() {
                            if (player?.isPlaying == true) playPausePlayer()
                        }

                        override fun onSkipToNext() = nextSong()

                        override fun onSkipToPrevious() = previousSong()

                        override fun onSeekTo(pos: Long) = seekTo(pos.toInt())

                        override fun onStop() = stopSelf()
                    },
                )
                setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS or MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS)
                isActive = true
            }
        positionUpdateHandler.post(positionUpdateRunnable)
    }

    override fun onStartCommand(
        intent: Intent?,
        flags: Int,
        startId: Int,
    ): Int {
        fetchRepeatMode()
        uriString = intent?.extras?.getString("uri") ?: ""
        songTitle = intent?.extras?.getString("songTitle") ?: ""
        currentSongId = intent?.extras?.getInt("id", 0) ?: 0
        Log.d("BackgroundSoundService", "onStartCommand: $uriString, $currentSongId")
        self = this
        Handler(Looper.getMainLooper()).postDelayed({
            player = MediaPlayer.create(this, uriString.toUri())
            if (player != null) {
                player?.isLooping = false
                player?.setVolume(100f, 100f)
                player?.start()
                player?.setOnCompletionListener { handleCompletion() }
                loadAlbumArt(queueController.current(currentSongId))
                updateMetadata()
                runAsForeground()
            }
        }, 50)
        return START_STICKY
    }

    private fun handleCompletion() {
        when (val action = queueController.onCompletion(_repeatMode.value, currentSongId)) {
            is PlaybackQueueController.CompletionAction.LoopCurrent -> {
                player?.release()
                player = MediaPlayer.create(this, File("$MEDIA_PATH/${action.song.fileName}").toString().toUri())
                if (player != null) {
                    player?.isLooping = true
                    player?.setVolume(100f, 100f)
                    player?.start()
                    updateMetadata()
                    runAsForeground()
                }
            }

            is PlaybackQueueController.CompletionAction.Advance -> playSongEntity(action.song)

            PlaybackQueueController.CompletionAction.EndOfPlaylist -> stopSelf()
        }
    }

    override fun nextSong() {
        val songEntity: SongEntity? = queueController.next(currentSongId)

        if (songEntity == null) {
            // End of playlist
            this.stopSelf()
            return
        }

        playSongEntity(songEntity)
    }

    override fun previousSong() {
        val songEntity: SongEntity? = queueController.previous(currentSongId)

        if (songEntity == null) {
            // Already at the first song, just restart it
            player?.seekTo(0)
            if (player?.isPlaying != true) {
                player?.start()
            }
            runAsForeground()
            return
        }

        playSongEntity(songEntity)
    }

    private fun playSongEntity(songEntity: SongEntity) {
        currentSongId = songEntity.id.toInt()
        songTitle = songEntity.title
        loadAlbumArt(songEntity)

        if (player != null) {
            player?.stop()
            player?.release()
        }
        player =
            if (songEntity.isDownloaded) {
                // Retrieve song/media file from storage
                MediaPlayer.create(this, File("$MEDIA_PATH/${songEntity.fileName}").toString().toUri())
            } else {
                // Retrieve song/media from cloud / network
                MediaPlayer.create(this, songEntity.url.toUri())
            }
        if (player != null) {
            player?.isLooping = false
            player?.setVolume(100f, 100f)
            player?.start()
            player?.setOnCompletionListener {
                nextSong()
            }
            updateMetadata()
            runAsForeground()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
        positionUpdateHandler.removeCallbacks(positionUpdateRunnable)
        mediaSession?.release()
        if (player != null) {
            player?.stop()
            player?.release()
        }
        self = null
    }

    private fun runAsForeground() {
        val notificationIntent = Intent(this, this.javaClass)
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
        val pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE)
        PlayerNotificationBuilder.registerChannel(this)

        val isPlaying = player?.isPlaying == true
        updatePlaybackState(if (isPlaying) PlaybackStateCompat.STATE_PLAYING else PlaybackStateCompat.STATE_PAUSED)

        val notification =
            PlayerNotificationBuilder.build(
                context = this,
                songTitle = songTitle,
                isPlaying = isPlaying,
                albumArt = albumArt,
                actions =
                    PlayerNotificationBuilder.Actions(
                        previous = stopServiceBroadcast(requestCode = 500, extra = "isPreviousSong"),
                        playPause = stopServiceBroadcast(requestCode = 200, extra = "isPauseService"),
                        next = stopServiceBroadcast(requestCode = 600, extra = "isNextSong"),
                        stop = stopServiceBroadcast(requestCode = 400, extra = "isStopService"),
                    ),
                contentIntent = pendingIntent,
                sessionToken = mediaSession?.sessionToken,
            )
        startForeground(PlayerNotificationBuilder.NOTIFICATION_ID, notification)
    }

    private fun stopServiceBroadcast(
        requestCode: Int,
        extra: String,
    ): PendingIntent =
        PendingIntent.getBroadcast(
            this,
            requestCode,
            Intent(this, StopServiceReceiver::class.java).putExtra(extra, true),
            PendingIntent.FLAG_IMMUTABLE,
        )

    override fun playPausePlayer() {
        if (player != null) {
            if (player?.isPlaying == true) {
                player?.pause()
            } else {
                player?.start()
            }
            runAsForeground()
        }
    }

    override fun seekTo(positionMs: Int) {
        player?.seekTo(positionMs)
        runAsForeground()
    }

    override fun getCurrentSongId(): Int = currentSongId

    override fun getCurrentPositionMs(): Int = player?.currentPosition ?: 0

    override fun getDurationMs(): Int = player?.duration ?: 0

    override fun isCurrentlyPlaying(): Boolean = player?.isPlaying == true

    private fun updateMetadata() {
        val metadataBuilder =
            MediaMetadataCompat
                .Builder()
                .putString(MediaMetadataCompat.METADATA_KEY_TITLE, songTitle)
                .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, player?.duration?.toLong() ?: 0L)
        albumArt?.let { metadataBuilder.putBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART, it) }
        mediaSession?.setMetadata(metadataBuilder.build())
    }

    private fun loadAlbumArt(songEntity: SongEntity) {
        albumArt = null
        serviceScope.launch {
            val bitmap = AlbumArtExtractor.extractAlbumArt(this@BackgroundSoundService, songEntity)
            withContext(Dispatchers.Main) {
                albumArt = bitmap
                updateMetadata()
                runAsForeground()
            }
        }
    }

    private fun updatePlaybackState(state: Int) {
        val position = player?.currentPosition?.toLong() ?: 0L
        mediaSession?.setPlaybackState(
            PlaybackStateCompat
                .Builder()
                .setActions(
                    PlaybackStateCompat.ACTION_PLAY or
                        PlaybackStateCompat.ACTION_PAUSE or
                        PlaybackStateCompat.ACTION_PLAY_PAUSE or
                        PlaybackStateCompat.ACTION_SKIP_TO_NEXT or
                        PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS or
                        PlaybackStateCompat.ACTION_SEEK_TO or
                        PlaybackStateCompat.ACTION_STOP,
                ).setState(state, position, 1.0f)
                .build(),
        )
    }

    private fun fetchRepeatMode() {
        val dataStoreManager = DataStoreManager(application)

        repeatModeJob?.cancel()
        repeatModeJob =
            serviceScope.launch {
                dataStoreManager.repeatModeFlow.collect {
                    _repeatMode.value = it
                }
            }
    }
}
