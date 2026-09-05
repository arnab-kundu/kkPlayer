package com.akundu.kkplayer.feature.player.view

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.AudioManager
import android.media.MediaMetadataRetriever
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
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
        viewModel.currentSongId.value = songIndex

        setContent {
            // Initialize the state within a Composable context
            showInfoDialogState = remember { mutableStateOf(false) }
            KkPlayerTheme {
                // A surface container using the 'background' color from the theme
                Scaffold { innerPadding ->
                    val dataBase = SongDatabase.getDatabase(this)
                    val currentSongId by viewModel.currentSongId.observeAsState(initial = songIndex)
                    val song = remember(currentSongId) { dataBase.songDao().findSongById(id = currentSongId.toLong()) }
                    val bitmap =
                        remember(currentSongId) {
                            mediaMetaDataRetriever(fileName = song.fileName, movie = song.movie, context = this)
                        }
                    val durationMs by viewModel.durationMs.observeAsState(initial = 0)
                    val positionMs by viewModel.currentPositionMs.observeAsState(initial = 0)
                    val audioManager = getSystemService(AUDIO_SERVICE) as AudioManager

                    PlayerPage(
                        verticalPadding = innerPadding.calculateTopPadding(),
                        viewModel = viewModel,
                        song = song,
                        duration = durationMs / 1000,
                        currentPosition = positionMs / 1000,
                        bitmap = bitmap,
                        playClick = {
                            viewModel.playPauseToggle()
                        },
                        pauseClick = {
                            viewModel.playPauseToggle()
                        },
                        nextClick = {
                            viewModel.nextSong()
                        },
                        previousClick = {
                            viewModel.previousSong()
                        },
                        onSeek = { seekSeconds ->
                            viewModel.seekTo(seekSeconds * 1000)
                        },
                        onVolumeChange = { delta ->
                            audioManager.adjustStreamVolume(
                                AudioManager.STREAM_MUSIC,
                                if (delta > 0) AudioManager.ADJUST_RAISE else AudioManager.ADJUST_LOWER,
                                AudioManager.FLAG_SHOW_UI,
                            )
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
