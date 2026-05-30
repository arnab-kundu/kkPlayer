package com.akundu.kkplayer.feature.player.view

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.AudioManager
import android.media.MediaMetadataRetriever
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.core.net.toUri
import com.akundu.kkplayer.Logg
import com.akundu.kkplayer.database.SongDatabase
import com.akundu.kkplayer.feature.player.ui.PlayerPage
import com.akundu.kkplayer.feature.player.viewModel.PlayerViewModel
import com.akundu.kkplayer.feature.settings.view.SettingsActivity
import com.akundu.kkplayer.getDrawable
import com.akundu.kkplayer.media.MetaDataExtractor
import com.akundu.kkplayer.service.StopServiceReceiver
import com.akundu.kkplayer.storage.Constants.MEDIA_PATH
import com.akundu.kkplayer.ui.theme.KkPlayerTheme
import java.io.File

private const val TAG = "PlayerActivity"

class PlayerActivity : ComponentActivity() {
    private var songIndex = 0
    private lateinit var viewModel: PlayerViewModel
    private lateinit var showInfoDialogState: MutableState<Boolean> // State to control dialog visibility

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        viewModel = PlayerViewModel()
        val bundle = intent.extras
        songIndex = bundle?.getInt("index") ?: 0

        setContent {
            // Initialize the state within a Composable context
            showInfoDialogState = remember { mutableStateOf(false) }
            KkPlayerTheme {
                // A surface container using the 'background' color from the theme
                Scaffold { innerPadding ->
                    val dataBase = SongDatabase.getDatabase(this)
                    val song = dataBase.songDao().findSongById(id = songIndex.toLong())
                    val mediaPlayer = MediaPlayer.create(this, File("$MEDIA_PATH/${song.fileName}").toString().toUri())
                    Log.d(TAG, "onCreate: duration ${mediaPlayer.duration}")
                    val audioManager = getSystemService(AUDIO_SERVICE) as AudioManager
                    audioManager.adjustStreamVolume(
                        AudioManager.STREAM_MUSIC,
                        AudioManager.ADJUST_RAISE,
                        0, // Displays the system volume slider
                    )
                    // Get maximum index allowed for media stream
                    val maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
                    var currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)

                    PlayerPage(
                        verticalPadding = innerPadding.calculateTopPadding(),
                        viewModel = viewModel,
                        song = song,
                        duration = mediaPlayer.duration / 1000,
                        bitmap = mediaMetaDataRetriever(fileName = song.fileName, movie = song.movie, context = this),
                        playClick = {
                            viewModel.playPauseToggle()
                        },
                        pauseClick = {
                            viewModel.playPauseToggle()
                        },
                        nextClick = {
                            Log.i(TAG, "nextClick: ")

                            if (currentVolume < maxVolume) {
                                // Increase the device volume
                                audioManager.setStreamVolume(
                                    AudioManager.STREAM_MUSIC,
                                    ++currentVolume,
                                    AudioManager.FLAG_SHOW_UI,
                                )
                            } else {
                                // Increase the device volume
                                audioManager.setStreamVolume(
                                    AudioManager.STREAM_MUSIC,
                                    maxVolume,
                                    AudioManager.FLAG_SHOW_UI,
                                )
                            }
                        },
                        previousClick = {
                            Log.i(TAG, "previousClick: ")

                            if (currentVolume > 0) {
                                // Decrease the device volume
                                audioManager.setStreamVolume(
                                    AudioManager.STREAM_MUSIC,
                                    --currentVolume,
                                    AudioManager.FLAG_SHOW_UI,
                                )
                            } else {
                                audioManager.setStreamVolume(
                                    AudioManager.STREAM_MUSIC,
                                    0,
                                    AudioManager.FLAG_SHOW_UI,
                                )
                            }
                        },
                        backClick = {
                            val stopServicePendingIntent =
                                PendingIntent.getBroadcast(
                                    this,
                                    400,
                                    Intent(this, StopServiceReceiver::class.java).putExtra("isStopService", true),
                                    PendingIntent.FLAG_IMMUTABLE,
                                )
                            stopServicePendingIntent.send()
                            finish()
                        },
                        settingsClick = {
                            val intent = Intent(this, SettingsActivity::class.java)
                            startActivity(intent)
                            // showInfoDialogState.value = true
                            // MetaDataExtractor.extractMp3("$MEDIA_PATH/${song.fileName}") // Does nothing to UI
                        },
                    )
                    InfoAlertDialog(
                        showInfoDialogState,
                        title = song.title,
                        body = MetaDataExtractor.extractMediaInfo("$MEDIA_PATH/${song.fileName}"),
                    ) // Show metadata to UI
                }
            }
        }
    }

    @Suppress("RedundantExplicitType")
    private fun mediaMetaDataRetriever(
        context: Context,
        fileName: String,
        movie: String,
    ): ImageBitmap {
        val bitmap: Bitmap
        try {
            val uri: String = File("/storage/emulated/0/Android/media/com.akundu.kkplayer/$fileName").toString().toUri().toString()
            val retriever: MediaMetadataRetriever = MediaMetadataRetriever()
            retriever.setDataSource(uri)
            val data: ByteArray = retriever.embeddedPicture ?: throw RuntimeException("Data is null in MediaMetadataRetriever")

            // convert the byte array to a bitmap
            bitmap = BitmapFactory.decodeByteArray(data, 0, data.size)

            // do something with the image ...
            // mImageView.setImageBitmap(bitmap);
            retriever.release()
            return bitmap.asImageBitmap()
        } catch (e: Exception) {
            Logg.d("Image not found in metadata: ${e.message}")
            val icon: Bitmap = ContextCompat.getDrawable(context, getDrawable(movie))!!.toBitmap()
            return icon.asImageBitmap()
        }
    }
}
