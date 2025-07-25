package com.akundu.kkplayer.feature.main.ui

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.ImageBitmapConfig
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.akundu.kkplayer.R
import com.akundu.kkplayer.database.entity.SongEntity
import com.akundu.kkplayer.ui.theme.AlleanaFontFamily
import com.akundu.kkplayer.ui.theme.Blue

@Preview
@Composable
fun SongItemPreview() {
    SongItem(SongEntity(0, "Tu hi meri sab hay", "KK", "", "", "Black"))
}

@Composable
fun SongItem(song: SongEntity = SongEntity(id = 1L, title = "", artist = "", fileName = "", url = "", isDownloaded = true, movie = "")) {
    val context = LocalContext.current
    Box(
        modifier = Modifier.padding(2.dp, 2.dp, 2.dp, 0.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(start = 8.dp, top = 8.dp, end = 8.dp, bottom = 0.dp)
                .clip(RoundedCornerShape(16.dp, 16.dp, 16.dp, 16.dp))
                .background(Color(0x85FFFFFF))
                .semantics { contentDescription = "songItem" }
                .clickable { playSong(context, song.title, song.fileName, song.id.toInt()) },
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Image(
                bitmap = if (song.movie == "Black") ImageBitmap(88, 88, ImageBitmapConfig.Rgb565) else mediaMetaDataRetriever(context, song.fileName, song.movie),
                contentDescription = null,
                contentScale = ContentScale.FillBounds,
                modifier = Modifier
                    .size(88.dp)
                    .clip(CircleShape)
                    .padding(12.dp)
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = song.title,
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.DarkGray,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Row {
                    Text(
                        stringResource(id = R.string.artist),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onBackground,
                        fontFamily = AlleanaFontFamily,
                        fontSize = 24.sp,
                    )
                    Text(
                        text = song.artist, style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onBackground,
                    )
                }
            }
            IconButton(
                onClick = {
                    val downloadUsingAndroidDownloaderAPI = false
                    if (!downloadUsingAndroidDownloaderAPI) {
                        if (isFileExists(fileName = song.fileName)) playSong(context, song.title, song.fileName, song.id.toInt()) else download(
                            context,
                            song.id,
                            song.fileName,
                            song.url,
                            song.movie
                        )
                    } else {
                        if (isFileExists(fileName = song.fileName)) playSong(context, song.title, song.fileName, song.id.toInt()) else downloadUsingAndroidDownloadManagerAPI(
                            context,
                            song.fileName,
                            song.url,
                            song.movie
                        )
                    }
                }
            ) {
                Icon(
                    painter = painterResource(id = if (song.isDownloaded) android.R.drawable.ic_media_play else R.drawable.ic_download),
                    contentDescription = if (song.isDownloaded) "Play" else "Download",
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .padding(8.dp),
                    tint = if (song.isDownloaded) Color(0xFF0C610C) else Blue
                )
            }
        }
    }
}

@Composable
fun SongListComposeWithStaticData(
    songList: List<SongEntity>,
    modifier: Modifier = Modifier
) {
    // Use LazyRow when making horizontal lists
    LazyColumn(modifier = modifier) {
        itemsIndexed(songList) { index, song ->
            SongItem(song = song)
        }
    }
}

@Composable
fun SongListCompose(
    songList: List<SongEntity>,
    @SuppressLint("ModifierParameter")
    modifier: Modifier = Modifier.semantics { contentDescription = "songsList" }
) {
    Image(modifier = Modifier.blur(16.dp), painter = painterResource(id = R.drawable.background), contentDescription = null, contentScale = ContentScale.FillBounds)
    LazyColumn(modifier = modifier) {
        itemsIndexed(songList) { _, song ->
            SongItem(song = song)
        }
    }
}

@Composable
fun SongListPreview(songList: State<List<SongEntity>?>) {
    // SongListComposeWithStaticData(kkSongList)
    songList.value?.let { SongListCompose(songList = it) }
}
