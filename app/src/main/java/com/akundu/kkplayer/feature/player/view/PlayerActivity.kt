package com.akundu.kkplayer.feature.player.view

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaPlayer
import android.net.Uri
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
import com.akundu.kkplayer.MetaDataExtractor
import com.akundu.kkplayer.R
import com.akundu.kkplayer.database.SongDatabase
import com.akundu.kkplayer.feature.player.ui.PlayerPage
import com.akundu.kkplayer.feature.player.viewModel.PlayerViewModel
import com.akundu.kkplayer.service.StopServiceReceiver
import com.akundu.kkplayer.ui.theme.KkPlayerTheme
import wseemann.media.FFmpegMediaMetadataRetriever
import java.io.File

private const val TAG = "PlayerActivity"

class PlayerActivity : ComponentActivity() {

    private var songIndex = 0
    private lateinit var viewModel: PlayerViewModel
    // State to control dialog visibility
    private lateinit var showInfoDialogState: MutableState<Boolean>

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
                    val mediaPlayer = MediaPlayer.create(this, Uri.parse(File("/storage/emulated/0/Android/media/com.akundu.kkplayer/${song.fileName}").toString()))
                    Log.d(TAG, "onCreate: duration ${mediaPlayer.duration}")
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
                        },
                        previousClick = {
                            Log.i(TAG, "previousClick: ")
                        },
                        backClick = {
                            val stopServicePendingIntent = PendingIntent.getBroadcast(
                                this,
                                400,
                                Intent(this, StopServiceReceiver::class.java).putExtra("isStopService", true),
                                PendingIntent.FLAG_IMMUTABLE
                            )
                            stopServicePendingIntent.send()
                            finish()
                        },
                        infoClick = {
                            showInfoDialogState.value = true
                            MetaDataExtractor.extractMp3("/storage/emulated/0/Android/media/com.akundu.kkplayer/${song.fileName}") // Does nothing to UI
                        }
                    )
                    InfoAlertDialog(showInfoDialogState, title = song.title, body = MetaDataExtractor.extractMediaInfo("/storage/emulated/0/Android/media/com.akundu.kkplayer/${song.fileName}"))
                }
            }
        }
    }


    @Suppress("RedundantExplicitType")
    private fun mediaMetaDataRetriever(fileName: String, movie: String, context: Context): ImageBitmap {
        val bitmap: Bitmap
        try {
            val uri: String = Uri.parse(File("/storage/emulated/0/Android/media/com.akundu.kkplayer/${fileName}").toString()).toString()
            val retriever: FFmpegMediaMetadataRetriever = FFmpegMediaMetadataRetriever()
            retriever.setDataSource(uri)
            val data: ByteArray = retriever.embeddedPicture

            // convert the byte array to a bitmap
            bitmap = BitmapFactory.decodeByteArray(data, 0, data.size)

            // do something with the image ...
            // mImageView.setImageBitmap(bitmap);
            retriever.release()
            return bitmap.asImageBitmap()
        } catch (e: Exception) {
            val icon: Bitmap = ContextCompat.getDrawable(context, getDrawable(movie))!!.toBitmap()
            return icon.asImageBitmap()
        }
    }

    private fun getDrawable(movie: String): Int {
        return when (movie) {
            "Bajrangi Bhaijaan"  -> R.drawable.bajrangi_bhaijaan
            "EK THA TIGER"       -> R.drawable.ek_tha_tiger
            "Gangster"           -> R.drawable.gangster
            "Jannat"             -> R.drawable.jannat
            "Jism"               -> R.drawable.jism
            "Kabir Singh (2019)" -> R.drawable.kabir_singh
            "Kites"              -> R.drawable.kites
            "Laali Ki Shaadi"    -> R.drawable.laali_ki_shaadi
            "Musafir"            -> R.drawable.musafir
            "New York"           -> R.drawable.new_york
            "Om Shanti Om"       -> R.drawable.om_shanti_om
            "Raaz Reboot"        -> R.drawable.raaz_reboot
            "Race"               -> R.drawable.race
            "Raees"              -> R.drawable.raees
            "Saathiya"           -> R.drawable.saathiya
            "The Killer"         -> R.drawable.the_killer
            "Live-The Train"     -> R.drawable.the_train
            "Woh Lamhe"          -> R.drawable.woh_lamhe
            "Zeher"              -> R.drawable.zeher
            else                 -> R.drawable.ic_music
        }
    }
}