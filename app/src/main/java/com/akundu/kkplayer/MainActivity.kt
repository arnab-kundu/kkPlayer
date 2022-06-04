package com.akundu.kkplayer

import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.akundu.kkplayer.data.Song
import com.akundu.kkplayer.data.SongDataProvider
import com.akundu.kkplayer.network.ApiRequest
import com.akundu.kkplayer.network.RetrofitRequest
import com.akundu.kkplayer.ui.theme.KkPlayerTheme
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream


class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            KkPlayerTheme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {
                    SongListPreview()
                }
            }
        }
    }
}

@Composable
fun SongItem(
    song: Song,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    Row(
        modifier = Modifier
            .background(Color(0xFFF3D3C8))
            .clickable {
                val mPlayer: MediaPlayer = MediaPlayer.create(
                    context,
                    //R.raw.tu_hi_meri_shab_hai,
                    Uri.parse(File("/storage/emulated/0/Android/data/com.akundu.kkplayer/files/${song.fileName}").toString())
                )
                mPlayer.start()
            },
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Image(
            painterResource(id = getDrawable(song.movie)),
            contentDescription = null,
            contentScale = ContentScale.FillBounds,
            modifier = Modifier
                .size(88.dp)
                //.clip(CircleShape)
                .padding(12.dp)
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(text = song.title, style = MaterialTheme.typography.h6)
            Row() {
                Text(
                    stringResource(id = R.string.artist),
                    style = MaterialTheme.typography.body2,
                    color = Color.DarkGray
                )
                Text(
                    text = song.artist, style = MaterialTheme.typography.body2,
                    color = Color.DarkGray
                )
            }
        }
        IconButton(
            onClick = { download(song.fileName, context) }
        ) {
            Icon(
                painterResource(id = R.drawable.ic_download),
                contentDescription = "Download",
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .padding(8.dp),
                tint = Color.Blue
            )
        }
    }
}


fun download(fileName: String, context: Context) {
    Toast.makeText(context, "Downloading: $fileName", Toast.LENGTH_LONG).show()

    val apiRequest = RetrofitRequest.getRetrofitInstance().create(ApiRequest::class.java)
    apiRequest.downloadSong(fileName = fileName).enqueue(object : Callback<ResponseBody> {
        override fun onResponse(call: Call<ResponseBody>, response: retrofit2.Response<ResponseBody>) {
            if (response.isSuccessful) {
                val inputStream: InputStream? = response.body()?.byteStream()
                val file = FolderFiles.createFile(context = context, folderName = "", fileName = fileName)
                if (inputStream != null)
                    copyInputStreamToFile(inputStream = inputStream, file = file)
            } else {
                Log.e("TAG", "StatusCode: ${response.code()}")
            }
        }

        override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
            Log.e("TAG", "onFailure: ${t.localizedMessage}")
        }
    })
}


@Throws(IOException::class)
private fun copyInputStreamToFile(inputStream: InputStream, file: File) {

    FileOutputStream(file, false).use { outputStream ->
        var read: Int
        val bytes = ByteArray(DEFAULT_BUFFER_SIZE)
        while (inputStream.read(bytes).also { read = it } != -1) {
            outputStream.write(bytes, 0, read)
        }
    }
}

@Preview
@Composable
fun SongItemPreview() {
    SongItem(SongDataProvider.kkSongList[0])
}


@Composable
fun SongListCompose(
    songList: List<Song>,
    modifier: Modifier = Modifier
) {
    // Use LazyRow when making horizontal lists
    LazyColumn(modifier = modifier) {
        items(songList) { song ->
            SongItem(song = song)
        }
    }
}


@Preview
@Composable
fun SongListPreview() {
    SongListCompose(SongDataProvider.kkSongList)
}


private fun getDrawable(movie: String): Int {
    return when (movie) {
        "Gangster" -> R.drawable.gangster
        "Jannat" -> R.drawable.jannat
        "Woh Lamhe" -> R.drawable.woh_lamhe
        "Bajrangi Bhaijaan" -> R.drawable.bajrangi_bhaijaan
        "Kites" -> R.drawable.kites
        "Live-The Train" -> R.drawable.the_train
        else -> R.drawable.ic_music
    }
}

