package com.akundu.kkplayer

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.akundu.kkplayer.R.drawable
import com.akundu.kkplayer.data.Song
import com.akundu.kkplayer.data.SongDataProvider
import com.akundu.kkplayer.ui.theme.KkPlayerTheme

private val TAG = "PlayerActivity"

class PlayerActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            KkPlayerTheme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {
                    PlayerPreview()
                }
            }
        }
    }

}

@Preview
@Composable
fun PlayerPreview() {

    Column() {
        Row(modifier = Modifier.weight(1F)) {
            AlbumArt()
        }
        Row(modifier = Modifier.weight(1F)) {

        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(drawable.background),
            contentDescription = "Background",
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(),
            contentScale = ContentScale.FillBounds,
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {
        PlayerController(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .background(Color(0x00F3D3C8))
        )
    }
}

@Composable
fun AlbumArt() {
    val context = LocalContext.current

    Image(
        painterResource(id = drawable.bajrangi_bhaijaan),
        contentDescription = null,
        contentScale = ContentScale.FillBounds,
        modifier = Modifier
            .fillMaxHeight()
            .size(400.dp, 300.dp)
    )

}


@Preview
@Composable
fun PlayerButtons(
    modifier: Modifier = Modifier
        .size(width = 144.dp, height = 48.dp)
        .fillMaxWidth(1F)
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {

        IconButton(
            onClick = { Log.d(TAG, "playerController: Previous") }
        ) {
            Icon(
                painterResource(id = android.R.drawable.ic_media_previous),
                contentDescription = "Download",
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .padding(8.dp),
                tint = Color.White
            )
        }

        IconButton(
            onClick = { Log.d(TAG, "playerController: Play") }
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

        IconButton(
            onClick = { Log.d(TAG, "playerController: Next") }
        ) {
            Icon(
                painterResource(id = android.R.drawable.ic_media_next),
                contentDescription = "Download",
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .padding(8.dp),
                tint = Color.White
            )
        }
    }
}


@Preview
@Composable
fun PlayerController(
    modifier: Modifier = Modifier.background(Color(0xFFF3D3C8)),
    song: Song = SongDataProvider.kkSongList[0]
) {

    var sliderPosition = 0F

    Column(
        modifier = modifier
            .padding(24.dp),
        verticalArrangement = Arrangement.Bottom,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {

        Text(
            text = song.title,
            modifier = Modifier
                .padding(horizontal = 16.dp)
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
        PlayerButtons(
            modifier = Modifier
                .size(width = 144.dp, height = 80.dp)
                .fillMaxWidth(1F)
                .padding(0.dp, 0.dp, 0.dp, 32.dp),
        )

    }
}