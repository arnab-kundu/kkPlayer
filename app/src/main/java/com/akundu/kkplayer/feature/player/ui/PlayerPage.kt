package com.akundu.kkplayer.feature.player.ui

import android.content.Context
import android.graphics.BitmapFactory
import android.media.MediaPlayer
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import com.akundu.kkplayer.R
import com.akundu.kkplayer.data.Song
import com.akundu.kkplayer.database.entity.SongEntity
import com.akundu.kkplayer.feature.player.viewModel.PlayerViewModel
import com.akundu.kkplayer.storage.Constants
import com.akundu.kkplayer.ui.TestTags
import java.io.File

@Preview
@Composable
fun PlayerPagePreview() {
    PlayerPage(song = SongEntity(0, "Tu hi meri sab hay", "KK", "", "", ""), playClick = {
    }, pauseClick = {}, nextClick = {}, previousClick = {}, backClick = {}, settingsClick = {})
}

@Composable
fun PlayerPage(
    verticalPadding: Dp = 10.dp,
    viewModel: PlayerViewModel = PlayerViewModel(),
    song: SongEntity,
    duration: Int = 0,
    currentPosition: Int = 0,
    bitmap: ImageBitmap = BitmapFactory.decodeResource(LocalResources.current, R.drawable.ic_music_album_avatar1).asImageBitmap(),
    playClick: () -> Unit,
    pauseClick: () -> Unit,
    nextClick: () -> Unit,
    previousClick: () -> Unit,
    onSeek: (Int) -> Unit = {},
    onVolumeChange: (Float) -> Unit = {},
    backClick: () -> Unit,
    settingsClick: () -> Unit,
) {
    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            modifier = Modifier.fillMaxSize().blur(16.dp),
            painter = painterResource(id = R.drawable.background),
            contentDescription = null,
            contentScale = ContentScale.FillBounds,
        )
        Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
            Spacer(modifier = Modifier.height(48.dp + verticalPadding))
            AlbumArt(
                bitmap = bitmap,
                songTitle = song.title,
                artist = song.artist,
            )
            Spacer(modifier = Modifier.weight(1F))
            PlaybackSeekBar(
                duration = duration,
                currentPosition = currentPosition,
                onSeek = onSeek,
            )
            Spacer(modifier = Modifier.height(16.dp))
            MediaControllerButtons(
                viewModel = viewModel,
                playClick = playClick,
                pauseClick = pauseClick,
                nextClick = nextClick,
                previousClick = previousClick,
            )
            Spacer(modifier = Modifier.height(16.dp + verticalPadding))
        }
        Row(
            modifier =
                Modifier
                    .padding(horizontal = 24.dp, vertical = 32.dp + verticalPadding),
        ) {
            Icon(
                painter = painterResource(id = R.drawable.baseline_west_24),
                contentDescription = null,
                modifier =
                    Modifier
                        .size(24.dp)
                        .testTag(TestTags.PLAYER_BACK_BUTTON)
                        .clickable { backClick.invoke() },
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(modifier = Modifier.height(48.dp).weight(1.0F))
            Icon(
                imageVector = Icons.Outlined.Settings,
                contentDescription = null,
                modifier =
                    Modifier
                        .size(24.dp)
                        .testTag(TestTags.PLAYER_SETTINGS_BUTTON)
                        .clickable { settingsClick.invoke() },
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        VolumeSwipeZone(
            onVolumeChange = onVolumeChange,
            modifier =
                Modifier
                    .align(Alignment.CenterEnd)
                    .height(350.dp)
                    .width(72.dp)
                    .testTag(TestTags.PLAYER_VOLUME_SWIPE_ZONE),
        )
    }
}

@Composable
private fun PlaybackSeekBar(
    duration: Int,
    currentPosition: Int,
    onSeek: (Int) -> Unit,
) {
    var draggingPosition by remember { mutableStateOf<Float?>(null) }
    val sliderMax = duration.toFloat().coerceAtLeast(1f)
    val sliderValue = (draggingPosition ?: currentPosition.toFloat()).coerceIn(0f, sliderMax)

    Slider(
        value = sliderValue,
        onValueChange = { draggingPosition = it },
        valueRange = 0f..sliderMax,
        onValueChangeFinished = {
            draggingPosition?.let { onSeek(it.toInt()) }
            draggingPosition = null
        },
        colors =
            SliderDefaults.colors(
                activeTrackColor = Color.Gray,
            ),
        modifier =
            Modifier
                .padding(horizontal = 16.dp)
                .testTag(TestTags.PLAYER_SEEK_BAR),
    )
}

@Composable
private fun VolumeSwipeZone(
    onVolumeChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
) {
    var dragAccumulatorPx by remember { mutableFloatStateOf(0f) }
    Box(
        modifier =
            modifier.pointerInput(Unit) {
                val stepPx = 24.dp.toPx()
                detectVerticalDragGestures(
                    onDragEnd = { dragAccumulatorPx = 0f },
                    onDragCancel = { dragAccumulatorPx = 0f },
                    onVerticalDrag = { change, dragAmount ->
                        change.consume()
                        dragAccumulatorPx += dragAmount
                        while (dragAccumulatorPx <= -stepPx) {
                            onVolumeChange(1f)
                            dragAccumulatorPx += stepPx
                        }
                        while (dragAccumulatorPx >= stepPx) {
                            onVolumeChange(-1f)
                            dragAccumulatorPx -= stepPx
                        }
                    },
                )
            },
    )
}

@Composable
fun AlbumArt(
    bitmap: ImageBitmap = BitmapFactory.decodeResource(LocalResources.current, R.drawable.ic_music_album_avatar1).asImageBitmap(),
    songTitle: String = "Tu hi meri sab hay",
    artist: String = "Arijit Singh",
) {
    Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Image(
            bitmap = bitmap,
            contentDescription = songTitle,
            contentScale = ContentScale.FillWidth,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .testTag(TestTags.PLAYER_ALBUM_ART)
                    .clip(shape = RoundedCornerShape(16.dp, 16.dp, 16.dp, 16.dp)),
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = songTitle,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 26.sp,
            fontFamily = FontFamily.Cursive,
            fontWeight = FontWeight.Bold,
        )
        Spacer(modifier = Modifier.height(64.dp))
        Text(text = artist, color = Color.DarkGray, fontSize = 26.sp, fontFamily = FontFamily.Cursive, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(0.dp))
    }
}

@Composable
fun MediaControllerButtons(
    viewModel: PlayerViewModel,
    playClick: () -> Unit,
    pauseClick: () -> Unit,
    nextClick: () -> Unit,
    previousClick: () -> Unit,
) {
    Row(modifier = Modifier.height(64.dp), verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = Icons.Filled.SkipPrevious,
            contentDescription = "Previous",
            modifier =
                Modifier
                    .size(40.dp)
                    .testTag(TestTags.PLAYER_PREVIOUS_BUTTON)
                    .clickable { previousClick.invoke() },
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(modifier = Modifier.width(24.dp))
        if (viewModel.isPlaying.observeAsState(true).value) {
            MediaButton(drawableResId = R.drawable.ic_pause_circle, buttonClick = pauseClick)
        } else {
            MediaButton(drawableResId = R.drawable.ic_play_circle, buttonClick = playClick)
        }
        Spacer(modifier = Modifier.width(24.dp))
        Icon(
            imageVector = Icons.Filled.SkipNext,
            contentDescription = "Next",
            modifier =
                Modifier
                    .size(40.dp)
                    .testTag(TestTags.PLAYER_NEXT_BUTTON)
                    .clickable { nextClick.invoke() },
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
fun MediaButton(
    drawableResId: Int = R.drawable.ic_play_circle,
    size: Dp = 64.dp,
    buttonClick: () -> Unit,
) {
    Image(
        painter = painterResource(id = drawableResId),
        contentDescription = null,
        modifier =
            Modifier
                .size(size)
                .testTag(TestTags.PLAYER_PLAY_PAUSE_BUTTON)
                .clickable { buttonClick.invoke() },
    )
}

private fun playSong(
    context: Context,
    song: Song,
) {
    val uriString: String = File("${Constants.MUSIC_PATH}${song.fileName}").toString()

    val mediaPlayer = MediaPlayer.create(context, uriString.toUri())
    mediaPlayer.start()
}
