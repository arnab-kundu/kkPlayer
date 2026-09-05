package com.akundu.kkplayer.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.app.NotificationCompat
import androidx.core.net.toUri
import com.akundu.kkplayer.R
import com.akundu.kkplayer.database.SongDatabase
import com.akundu.kkplayer.database.entity.SongEntity
import com.akundu.kkplayer.feature.settings.datastore.DataStoreManager
import com.akundu.kkplayer.feature.settings.datastore.RepeatMode
import com.akundu.kkplayer.storage.Constants.MEDIA_PATH
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.io.File
import androidx.media.app.NotificationCompat as MediaNotificationCompat

class BackgroundSoundService : Service() {
    private var player: MediaPlayer? = null
    private var mediaSession: MediaSessionCompat? = null
    private var uriString: String = ""
    private var songTitle: String = ""
    private var currentSongId: Int = 0
    private val _repeatMode = MutableStateFlow(RepeatMode.NONE)

    private val positionUpdateHandler = Handler(Looper.getMainLooper())
    private val positionUpdateRunnable: Runnable =
        object : Runnable {
            override fun run() {
                if (player?.isPlaying == true) {
                    updatePlaybackState(PlaybackStateCompat.STATE_PLAYING)
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
                player?.isLooping = false // Set looping
                player?.setVolume(100f, 100f)
                player?.start()
                player?.setOnCompletionListener {
                    when (_repeatMode.value) {
                        RepeatMode.NONE -> nextSong()
                        RepeatMode.ONE -> {
                            val database = SongDatabase.getDatabase(this)
                            val songEntity: SongEntity = database.songDao().findSongById(currentSongId.toLong())
                            player?.release()
                            player = MediaPlayer.create(this, File("$MEDIA_PATH/${songEntity.fileName}").toString().toUri())
                            if (player != null) {
                                player?.isLooping = true // Set looping
                                player?.setVolume(100f, 100f)
                                player?.start()
                                updateMetadata()
                                runAsForeground()
                            }
                        }

                        RepeatMode.ALL -> {
                            val database = SongDatabase.getDatabase(this)
                            val nextSongId = currentSongId + 1
                            val songEntity: SongEntity? = database.songDao().getNextDownloadedSong(nextSongId.toLong())
                            if (songEntity == null) {
                                currentSongId = 0
                            }
                            nextSong()
                        }
                    }
                }
                updateMetadata()
                runAsForeground()
            }
        }, 50)
        return START_STICKY
    }

    fun nextSong() {
        val database = SongDatabase.getDatabase(this)
        val nextSongId = currentSongId + 1
        val songEntity: SongEntity? = database.songDao().getNextDownloadedSong(nextSongId.toLong())

        if (songEntity == null) {
            // End of playlist
            this.stopSelf()
            return
        }

        currentSongId = songEntity.id.toInt()
        songTitle = songEntity.title

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
            player?.isLooping = false // Set looping
            player?.setVolume(100f, 100f)
            player?.start()
            player?.setOnCompletionListener {
                nextSong()
            }
            updateMetadata()
            runAsForeground()
        }
    }

    fun previousSong() {
        val database = SongDatabase.getDatabase(this)
        val songEntity: SongEntity? = database.songDao().getPreviousDownloadedSong(currentSongId.toLong())

        if (songEntity == null) {
            // Already at the first song, just restart it
            player?.seekTo(0)
            if (player?.isPlaying != true) {
                player?.start()
            }
            runAsForeground()
            return
        }

        currentSongId = songEntity.id.toInt()
        songTitle = songEntity.title

        if (player != null) {
            player?.stop()
            player?.release()
        }
        player =
            if (songEntity.isDownloaded) {
                MediaPlayer.create(this, File("$MEDIA_PATH/${songEntity.fileName}").toString().toUri())
            } else {
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
        val mNotificationManager = applicationContext.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= 26) {
            val channel = NotificationChannel("2", "Player channel", NotificationManager.IMPORTANCE_HIGH)
            channel.description = "Playing song notification"
            channel.setShowBadge(true)
            mNotificationManager.createNotificationChannel(channel)
        }

        val stopServicePendingIntent =
            PendingIntent.getBroadcast(
                this,
                400,
                Intent(this, StopServiceReceiver::class.java).putExtra("isStopService", true),
                PendingIntent.FLAG_IMMUTABLE,
            )

        val pauseServicePendingIntent =
            PendingIntent.getBroadcast(
                this,
                200,
                Intent(this, StopServiceReceiver::class.java).putExtra("isPauseService", true),
                PendingIntent.FLAG_IMMUTABLE,
            )

        val previousSongPendingIntent =
            PendingIntent.getBroadcast(
                this,
                500,
                Intent(this, StopServiceReceiver::class.java).putExtra("isPreviousSong", true),
                PendingIntent.FLAG_IMMUTABLE,
            )

        val nextSongPendingIntent =
            PendingIntent.getBroadcast(
                this,
                600,
                Intent(this, StopServiceReceiver::class.java).putExtra("isNextSong", true),
                PendingIntent.FLAG_IMMUTABLE,
            )

        val isPlaying = player?.isPlaying == true
        updatePlaybackState(if (isPlaying) PlaybackStateCompat.STATE_PLAYING else PlaybackStateCompat.STATE_PAUSED)

        val playPauseIcon = if (isPlaying) android.R.drawable.ic_media_pause else android.R.drawable.ic_media_play
        val playPauseLabel = if (isPlaying) "Pause" else "Play"

        val notification =
            NotificationCompat
                .Builder(this, "2")
                .setNumber(0)
                .setOngoing(true)
                .setColorized(true)
                .setColor(Color(0xFF606060).toArgb())
                .setSmallIcon(R.mipmap.ic_launcher) // .setSmallIcon(R.drawable.ic_notification)
                .setSubText("is playing...")
                .setContentTitle(songTitle)
                .addAction(android.R.drawable.ic_media_previous, "Previous", previousSongPendingIntent)
                .addAction(playPauseIcon, playPauseLabel, pauseServicePendingIntent)
                .addAction(android.R.drawable.ic_media_next, "Next", nextSongPendingIntent)
                .addAction(android.R.drawable.ic_menu_close_clear_cancel, "Stop", stopServicePendingIntent)
                .setStyle(
                    MediaNotificationCompat
                        .MediaStyle()
                        .setMediaSession(mediaSession?.sessionToken)
                        .setShowActionsInCompactView(0, 1, 2),
                ).setSilent(true)
                .setContentIntent(pendingIntent)
        startForeground(12345, notification.build())
    }

    fun playPausePlayer() {
        if (player != null) {
            if (player?.isPlaying == true) {
                player?.pause()
            } else {
                player?.start()
            }
            runAsForeground()
        }
    }

    fun seekTo(positionMs: Int) {
        player?.seekTo(positionMs)
        runAsForeground()
    }

    private fun updateMetadata() {
        mediaSession?.setMetadata(
            MediaMetadataCompat
                .Builder()
                .putString(MediaMetadataCompat.METADATA_KEY_TITLE, songTitle)
                .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, player?.duration?.toLong() ?: 0L)
                .build(),
        )
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

        CoroutineScope(Dispatchers.IO).launch {
            dataStoreManager.repeatModeFlow.collect {
                _repeatMode.value = it
            }
        }
    }
}
