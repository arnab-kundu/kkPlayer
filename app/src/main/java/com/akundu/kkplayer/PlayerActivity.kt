package com.akundu.kkplayer

import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Slider
import androidx.compose.material.SliderDefaults
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.akundu.kkplayer.data.Song
import com.akundu.kkplayer.data.SongDataProvider
import com.akundu.kkplayer.storage.Constants.MUSIC_PATH
import com.akundu.kkplayer.ui.theme.KkPlayerTheme
import java.io.File

private val TAG = "PlayerActivity"

class PlayerActivity : ComponentActivity() {

    var songIndex = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        val bundle = intent.extras
        songIndex = bundle?.getInt("index") ?: 0

        setContent {
            KkPlayerTheme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {
                    Player(SongDataProvider.kkSongList[songIndex])
                }
            }
        }
    }

}

@Preview
@Composable
fun PlayerPreview(song: Song = SongDataProvider.kkSongList[1]) {
    Player(song = song)
}

@Composable
fun Player(song: Song) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.4f)
    ) {
        AlbumArt(
            drawable = getDrawable(song.movie),
            songTitle = song.title
        )
    }


    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color.Transparent,
                        Color.Black,
                        Color(0xFF101010),
                        Color.DarkGray
                    ),
                    startY = 300f
                )
            )
    )

    Box(modifier = Modifier.fillMaxSize()) {
        PlayerController(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .background(Color(0x00F3D3C8)),
            song = song
        )
    }
}

@Composable
fun AlbumArt(
    drawable: Int = R.drawable.bajrangi_bhaijaan,
    songTitle: String? = null
) {

    Image(
        painterResource(id = drawable),
        contentDescription = songTitle,
        contentScale = ContentScale.Crop,
        modifier = Modifier
            .fillMaxSize()
    )
}


@Composable
fun PlayerButtons(song: Song) {

    val context = LocalContext.current
    val index = remember { mutableStateOf(0) }
    val isPlaying = remember { mutableStateOf(false) }

    val uriString: String = File("$MUSIC_PATH${song.fileName}").toString()
    val mediaPlayer = remember {
        MediaPlayer.create(context, Uri.parse(uriString))
    }

    val length = remember { mutableStateOf(0) }

    Row(
        modifier = Modifier
            .size(width = 144.dp, height = 48.dp)
            .fillMaxWidth(1F),
        verticalAlignment = Alignment.CenterVertically
    ) {

        IconButton(
            onClick = {
                Log.d(TAG, "playerController: Previous")

                if (index.value > 0) index.value--
                else index.value = SongDataProvider.kkSongList.size - 1

                playSong(context = context, SongDataProvider.kkSongList[index.value])
            }
        ) {
            Icon(
                painterResource(id = android.R.drawable.ic_media_previous),
                contentDescription = "Previous",
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .padding(8.dp),
                tint = Color.White
            )
        }


        if (isPlaying.value) {
            IconButton(
                onClick = {
                    Log.d(TAG, "playerController: Pause")
                    isPlaying.value = false
                    length.value = mediaPlayer.currentPosition
                    mediaPlayer.pause()
                }
            ) {
                Icon(
                    painterResource(id = android.R.drawable.ic_media_pause),
                    contentDescription = "Pause",
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .padding(8.dp),
                    tint = Color.White
                )
            }
        } else {

            IconButton(
                onClick = {
                    Log.d(TAG, "playerController: Play")
                    isPlaying.value = true
                    mediaPlayer.start()
                    mediaPlayer.seekTo(length.value)
                }
            ) {
                Icon(
                    painterResource(id = android.R.drawable.ic_media_play),
                    contentDescription = "Play",
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .padding(8.dp),
                    tint = Color.White
                )
            }
        }


        IconButton(
            onClick = {
                Log.d(TAG, "playerController: Next")

                if (index.value < SongDataProvider.kkSongList.size - 1) index.value++
                else index.value = 0
                mediaPlayer.release()
                playSong(context = context, SongDataProvider.kkSongList[index.value])
            }
        ) {
            Icon(
                painterResource(id = android.R.drawable.ic_media_next),
                contentDescription = "Next",
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .padding(8.dp),
                tint = Color.White
            )
        }
    }
}

private fun playSong(context: Context, song: Song) {
    val uriString: String = File("$MUSIC_PATH${song.fileName}").toString()

    val mediaPlayer = MediaPlayer.create(context, Uri.parse(uriString))
    mediaPlayer.start()
}


@Composable
fun PlayerController(
    modifier: Modifier = Modifier,
    song: Song = SongDataProvider.kkSongList[0]
) {

    var sliderPosition = 0F

    Column(
        modifier = modifier.padding(24.dp),
        verticalArrangement = Arrangement.Bottom,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {

        Text(
            text = song.title,
            modifier = Modifier.padding(horizontal = 16.dp),
            style = TextStyle(color = Color.White, fontSize = 16.sp)
        )

        Slider(
            value = sliderPosition,
            onValueChange = { sliderPosition = it },
            valueRange = 0f..100f,
            onValueChangeFinished = {
                // launch some business logic update with the state you hold
                // viewModel.updateSelectedSliderValue(sliderPosition)
            },
            steps = 5,
            colors = SliderDefaults.colors(
                thumbColor = MaterialTheme.colors.secondary,
                activeTrackColor = MaterialTheme.colors.secondary
            ),
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        PlayerButtons(song = song)

    }
}

private fun getDrawable(movie: String): Int {
    return when (movie) {
        "Bajrangi Bhaijaan" -> R.drawable.bajrangi_bhaijaan
        "EK THA TIGER"      -> R.drawable.ek_tha_tiger
        "Gangster"          -> R.drawable.gangster
        "Jannat"            -> R.drawable.jannat
        "Jism"              -> R.drawable.jism
        "Kabir Singh (2019)" -> R.drawable.kabir_singh
        "Kites"             -> R.drawable.kites
        "Laali Ki Shaadi"   -> R.drawable.laali_ki_shaadi
        "Musafir"           -> R.drawable.musafir
        "New York"          -> R.drawable.new_york
        "Om Shanti Om"      -> R.drawable.om_shanti_om
        "Raaz Reboot"       -> R.drawable.raaz_reboot
        "Race"              -> R.drawable.race
        "Raees"             -> R.drawable.raees
        "Saathiya"          -> R.drawable.saathiya
        "The Killer"        -> R.drawable.the_killer
        "Live-The Train"    -> R.drawable.the_train
        "Woh Lamhe"         -> R.drawable.woh_lamhe
        "Zeher"             -> R.drawable.zeher
        else                -> R.drawable.ic_music
    }
}