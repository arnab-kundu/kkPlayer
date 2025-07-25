package com.akundu.kkplayer.feature.main.ui

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.widget.Toast
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
import androidx.compose.ui.graphics.asImageBitmap
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
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequest.Builder
import androidx.work.WorkManager
import com.akundu.kkplayer.AppsNotificationManager
import com.akundu.kkplayer.Logg
import com.akundu.kkplayer.R
import com.akundu.kkplayer.database.SongDatabase
import com.akundu.kkplayer.database.entity.SongEntity
import com.akundu.kkplayer.download.AndroidDownloader
import com.akundu.kkplayer.feature.main.view.MainActivity
import com.akundu.kkplayer.feature.player.view.PlayerActivity
import com.akundu.kkplayer.getRawFileResourceID
import com.akundu.kkplayer.service.BackgroundSoundService
import com.akundu.kkplayer.service.ServiceTools
import com.akundu.kkplayer.storage.AppFileManager
import com.akundu.kkplayer.storage.Constants.INTERNAL_MEDIA_PATH
import com.akundu.kkplayer.ui.theme.AlleanaFontFamily
import com.akundu.kkplayer.ui.theme.Blue
import com.akundu.kkplayer.work.DownloadWork
import wseemann.media.FFmpegMediaMetadataRetriever
import java.io.File
import java.io.FileNotFoundException
import java.io.InputStream
import androidx.core.net.toUri

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
                bitmap = if (song.movie == "Black") ImageBitmap(88, 88, ImageBitmapConfig.Rgb565) else mediaMetaDataRetriever(song.fileName, song.movie),
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


private fun playSong(context: Context, title: String, fileName: String, index: Int) {

    Logg.i("FileName: $fileName")

    try {

        val uriString: String = File("$INTERNAL_MEDIA_PATH$fileName").toString()

        if (isFileExists(fileName = fileName)) {

            if (ServiceTools.isServiceRunning(context = context, "com.akundu.kkplayer.service.BackgroundSoundService")) {
                context.stopService(Intent(context, BackgroundSoundService::class.java))
            }

            val svc = Intent(context, BackgroundSoundService::class.java)
            svc.putExtra("uri", uriString)
            svc.putExtra("songTitle", title)
            svc.putExtra("id", index)
            context.startService(svc)

            val intent = Intent(context, PlayerActivity::class.java)
            intent.putExtra("index", index)
            context.startActivity(intent)

        } else {
            Logg.e("File exist: false")
            Logg.e("UriString: $uriString")

            Toast.makeText(context, "Please download", Toast.LENGTH_SHORT).show()
        }

    } catch (e: FileNotFoundException) {
        Logg.e("$fileName not found. Cause ${e.localizedMessage}")
        Toast.makeText(context, "Please download", Toast.LENGTH_SHORT).show()

    } catch (e: NullPointerException) {
        Logg.e("Cause ${e.localizedMessage}")
        Toast.makeText(context, "Please download", Toast.LENGTH_SHORT).show()
    }
}

private fun isFileExists(fileName: String): Boolean {
    val uriString: String = File("$INTERNAL_MEDIA_PATH$fileName").toString()
    val songFile = File(uriString)
    val isFileExist = songFile.exists()
    Logg.i("Is file exists: $isFileExist. Filename: $fileName")
    return isFileExist
}


fun download(context: Context, id: Long, fileName: String, url: String, movie: String) {
    Logg.i("Downloading: $fileName")

    if (url.isEmpty()) {
        copyFileFromRaw(context, fileName)
        return
    }

    val notificationID = System.currentTimeMillis().toInt()

    val data: Data = Data.Builder()
        .putLong("id", id)
        .putString("fileName", fileName)
        .putString("url", url)
        .putString("movie", movie)
        .putInt("notificationID", notificationID)
        .build()

    val constraints: Constraints = Constraints.Builder()
        .setRequiredNetworkType(NetworkType.CONNECTED)
        // .setRequiresBatteryNotLow(true)
        // .setRequiresStorageNotLow(true)
        .build()

    val request = Builder(DownloadWork::class.java)
        .setInputData(data)
        .setConstraints(constraints)
        .build()

    WorkManager.getInstance(context).enqueue(request)

    AppsNotificationManager.getInstance(context)?.downloadingNotification(
        targetNotificationActivity = MainActivity::class.java,
        channelId = "CHANNEL_ID",
        title = fileName,
        text = "Downloading",
        bigText = "",
        notificationId = notificationID,
        drawableId = getDrawable(movie)
    )
}

/**
 * Makes a copy of the file(provided fileName) from **RAW** folder into app's **MEDIA DIRECTORY**
 * @param fileName
 */
private fun copyFileFromRaw(context: Context, fileName: String) {
    Logg.d("DownloadFromRaw: $fileName")

    val resourceID: Int = getRawFileResourceID(fileName)                        // R.raw.i_dont_wanna_live_forever_fifty_shades_darker
    val fileInputStream: InputStream = context.resources.openRawResource(resourceID)    // Getting InputStream of raw file
    AppFileManager().saveFile(fileInputStream = fileInputStream, destinationPath = "/storage/emulated/0/Android/media/com.akundu.kkplayer/$fileName")

    val database: SongDatabase = SongDatabase.getDatabase(context)
    val id = database.songDao().findSongIdByFilename(fileName)
    database.songDao().updateSongDownloadInfo(id, true)
}

/**
 * Download file using new - **Android DownloadManager API**.
 *
 *
 *  - Downloaded file will be saved in **Public Downloads Folder**
 *  - This API have build-in feature for showing notifications of in-progress-downloads and download-completion
 */
@Suppress("UNUSED_PARAMETER")
private fun downloadUsingAndroidDownloadManagerAPI(context: Context, fileName: String, url: String, movie: String) {
    val downloader = AndroidDownloader(context = context)
    downloader.downloadFile(
        url = url,
        fileName = fileName
    )
}


private fun getDrawable(movie: String): Int {
    return when (movie) {
        "Bajrangi Bhaijaan" -> R.drawable.bajrangi_bhaijaan
        "Bhool Bhulaiyaa" -> R.drawable.bhool_bhulaiyaa
        "Crook" -> R.drawable.crook
        "EK THA TIGER" -> R.drawable.ek_tha_tiger
        "Force" -> R.drawable.force
        "G" -> R.drawable.g
        "Gangster" -> R.drawable.gangster
        "Golmaal Returns" -> R.drawable.golmaal_returns
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
        "raqeeb" -> R.drawable.raqeeb
        "Raaz - TMC" -> R.drawable.razz
        "Razz" -> R.drawable.razz
        "Saathiya" -> R.drawable.saathiya
        "The Killer" -> R.drawable.the_killer
        "Life...Metro" -> R.drawable.life_metro
        "Live-The Train" -> R.drawable.the_train
        "Woh Lamhe" -> R.drawable.woh_lamhe
        "Zeher" -> R.drawable.zeher
        else -> R.drawable.ic_music
    }
}

@Composable
@Suppress("RedundantExplicitType")
private fun mediaMetaDataRetriever(fileName: String, movie: String): ImageBitmap {
    val bitmap: Bitmap
    try {
        val uri: String = File("/storage/emulated/0/Android/media/com.akundu.kkplayer/${fileName}").toString().toUri().toString()
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
        val icon: Bitmap = ContextCompat.getDrawable(LocalContext.current, getDrawable(movie))!!.toBitmap()
        return icon.asImageBitmap()
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
