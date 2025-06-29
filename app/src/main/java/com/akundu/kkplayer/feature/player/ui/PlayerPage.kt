package com.akundu.kkplayer.feature.player.ui

import android.content.Context
import android.graphics.BitmapFactory
import android.media.MediaPlayer
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.akundu.kkplayer.R
import com.akundu.kkplayer.data.Song
import com.akundu.kkplayer.data.SongDataProvider
import com.akundu.kkplayer.feature.player.viewModel.PlayerViewModel
import com.akundu.kkplayer.storage.Constants
import java.io.File

var playProgress: Float = 50.0F

@Preview
@Composable
fun PlayerPagePreview(song: Song = SongDataProvider.kkSongList[8]) {
    PlayerPage(song = song, playClick = {}, pauseClick = {}, nextClick = {}, previousClick = {})
}

@Composable
fun PlayerPage(
    viewModel: PlayerViewModel = PlayerViewModel(),
    song: Song,
    duration: Int = 0,
    bitmap: ImageBitmap = BitmapFactory.decodeResource(LocalContext.current.resources, R.drawable.gangster).asImageBitmap(),
    playClick: () -> Unit, pauseClick: () -> Unit, nextClick: () -> Unit, previousClick: () -> Unit
) {
    Image(modifier = Modifier.blur(16.dp), painter = painterResource(id = R.drawable.background), contentDescription = null, contentScale = ContentScale.FillBounds)
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        AlbumArt(
            bitmap = bitmap,
            songTitle = song.title,
        )
        Spacer(modifier = Modifier.weight(1F))
        Slider(
            value = viewModel.progressPercent.observeAsState(initial = 0F).value,
            onValueChange = { playProgress = it },
            valueRange = 0f..duration.toFloat(),
            onValueChangeFinished = {
                // launch some business logic update with the state you hold
                // viewModel.updateSelectedSliderValue(sliderPosition)
            },
            steps = duration.toInt(),
            colors = SliderDefaults.colors(
                // thumbColor = Color.DarkGray,
                activeTrackColor = Color.Gray
            ),
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        MediaControllerButtons(
            viewModel = viewModel,
            playClick = playClick,
            pauseClick = pauseClick,
            nextClick = nextClick,
            previousClick = previousClick
        )
        Spacer(modifier = Modifier.height(16.dp))
    }
}

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

@Composable
fun MediaControllerButtons(
    viewModel: PlayerViewModel,
    playClick: () -> Unit,
    pauseClick: () -> Unit,
    nextClick: () -> Unit,
    previousClick: () -> Unit
) {
    Row(modifier = Modifier.height(64.dp), verticalAlignment = Alignment.CenterVertically) {
        MediaButton(drawableResId = android.R.drawable.ic_media_previous, size = 32.dp, buttonClick = previousClick)
        Spacer(modifier = Modifier.width(24.dp))
        if (viewModel.isPlaying.observeAsState(true).value) {
            MediaButton(drawableResId = R.drawable.ic_play_circle, buttonClick = playClick)
        } else {
            MediaButton(drawableResId = R.drawable.ic_pause_circle, buttonClick = pauseClick)
        }
        Spacer(modifier = Modifier.width(24.dp))
        MediaButton(drawableResId = android.R.drawable.ic_media_next, size = 32.dp, buttonClick = nextClick)
    }
}

@Composable
fun MediaButton(drawableResId: Int = R.drawable.ic_play_circle, size: Dp = 64.dp, buttonClick: () -> Unit) {
    Image(
        painter = painterResource(id = drawableResId),
        contentDescription = null,
        modifier = Modifier
            .size(size)
            .clickable { buttonClick.invoke() }
    )
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