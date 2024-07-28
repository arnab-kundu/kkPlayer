package com.akundu.kkplayer.feature.player.ui

import android.content.Context
import android.graphics.BitmapFactory
import android.media.MediaPlayer
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.akundu.kkplayer.R
import com.akundu.kkplayer.data.Song
import com.akundu.kkplayer.data.SongDataProvider
import com.akundu.kkplayer.storage.Constants
import java.io.File

var playProgress: Float = 50.0F

@Preview
@Composable
fun PlayerPagePreview(song: Song = SongDataProvider.kkSongList[8]) {
    PlayerPage(song = song)
}

@Composable
fun PlayerPage(song: Song, bitmap: ImageBitmap = BitmapFactory.decodeResource(LocalContext.current.resources, R.drawable.gangster).asImageBitmap()) {
    Image(painter = painterResource(id = R.drawable.background), contentDescription = null, contentScale = ContentScale.FillBounds)
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        AlbumArt(
            bitmap = bitmap,
            songTitle = song.title,
        )
        Spacer(modifier = Modifier.weight(1F))
        Slider(
            value = playProgress,
            onValueChange = { playProgress = it },
            valueRange = 0f..100f,
            onValueChangeFinished = {
                // launch some business logic update with the state you hold
                // viewModel.updateSelectedSliderValue(sliderPosition)
            },
            steps = 5,
            colors = SliderDefaults.colors(
                thumbColor = Color.DarkGray,
                activeTrackColor = Color.Gray
            ),
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        PlayerButtons()
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Preview
@Composable
fun AlbumArt(bitmap: ImageBitmap = BitmapFactory.decodeResource(LocalContext.current.resources, R.drawable.gangster).asImageBitmap(), songTitle: String = "Tu hi meri sab hay") {
    Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Image(
            bitmap = bitmap,
            contentDescription = songTitle,
            contentScale = ContentScale.FillWidth,
            modifier = Modifier
                .fillMaxWidth()
                .clip(shape = RoundedCornerShape(16.dp, 16.dp, 16.dp, 16.dp))
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = songTitle, color = Color.White, fontSize = 26.sp)
        Spacer(modifier = Modifier.height(16.dp))
    }
}


@Preview
@Composable
fun PlayerButtons(song: Song = SongDataProvider.kkSongList[1]) {

    val context = LocalContext.current
    val index = remember { mutableStateOf(0) }
    val isPlaying = remember { mutableStateOf(false) }

    val length = remember { mutableStateOf(0) }

    Row(
        modifier = Modifier
            .size(width = 200.dp, height = 96.dp)
            .fillMaxWidth(1F),
        verticalAlignment = Alignment.CenterVertically
    ) {

        IconButton(
            onClick = {
                Log.d("TAG", "playerController: Previous")

                if (index.value > 0) index.value--
                else index.value = SongDataProvider.kkSongList.size - 1

                playSong(context = context, SongDataProvider.kkSongList[index.value])
            }
        ) {
            Icon(
                painterResource(id = android.R.drawable.ic_media_previous),
                contentDescription = "Previous",
                modifier = Modifier
                    .clip(CircleShape)
                ,
                tint = Color.White,
            )
        }
        Spacer(modifier = Modifier.weight(1F))


        if (isPlaying.value) {
            IconButton(
                onClick = {
                    Log.d("TAG", "playerController: Pause")
                    isPlaying.value = false
                }
            ) {
                Icon(
                    painterResource(id = android.R.drawable.ic_media_pause),
                    contentDescription = "Pause",
                    modifier = Modifier
                        .clip(CircleShape)
                    ,
                    tint = Color.White
                )
            }
        } else {

            IconButton(
                onClick = {
                    Log.d("TAG", "playerController: Play")
                    isPlaying.value = true
                }
            ) {
                Icon(
                    painterResource(id = R.drawable.ic_play_circle),
                    contentDescription = "Play",
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .shadow(elevation = 5.dp, shape = CircleShape, ambientColor = Color.White, spotColor = Color.White),
                    tint = Color.White
                )
            }
        }

Spacer(modifier = Modifier.weight(1F))
        IconButton(
            onClick = {
                Log.d("TAG", "playerController: Next")

                if (index.value < SongDataProvider.kkSongList.size - 1) index.value++
                else index.value = 0
                playSong(context = context, SongDataProvider.kkSongList[index.value])
            }
        ) {
            Icon(
                painterResource(id = android.R.drawable.ic_media_next),
                contentDescription = "Next",
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape),
                tint = Color.White
            )
        }
    }
}

private fun playSong(context: Context, song: Song) {
    val uriString: String = File("${Constants.MUSIC_PATH}${song.fileName}").toString()

    val mediaPlayer = MediaPlayer.create(context, Uri.parse(uriString))
    mediaPlayer.start()
}

private fun getDrawable(movie: String): Int {
    return when (movie) {
        "Bajrangi Bhaijaan" -> R.drawable.bajrangi_bhaijaan
        "EK THA TIGER" -> R.drawable.ek_tha_tiger
        "Gangster" -> R.drawable.gangster
        "Jannat" -> R.drawable.jannat
        "Jism" -> R.drawable.jism
        "Kabir Singh (2019)" -> R.drawable.kabir_singh
        "Kites" -> R.drawable.kites
        "Laali Ki Shaadi" -> R.drawable.laali_ki_shaadi
        "Musafir" -> R.drawable.musafir
        "New York" -> R.drawable.new_york
        "Om Shanti Om" -> R.drawable.om_shanti_om
        "Raaz Reboot" -> R.drawable.raaz_reboot
        "Race" -> R.drawable.race
        "Raees" -> R.drawable.raees
        "Saathiya" -> R.drawable.saathiya
        "The Killer" -> R.drawable.the_killer
        "Live-The Train" -> R.drawable.the_train
        "Woh Lamhe" -> R.drawable.woh_lamhe
        "Zeher" -> R.drawable.zeher
        else -> R.drawable.ic_music
    }
}