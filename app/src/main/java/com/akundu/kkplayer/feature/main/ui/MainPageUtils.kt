@file:Suppress("RedundantExplicitType", "UNUSED_PARAMETER", "ImplicitSamInstance")

package com.akundu.kkplayer.feature.main.ui

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.widget.Toast
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.core.net.toUri
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequest.Builder
import androidx.work.WorkManager
import com.akundu.kkplayer.AppsNotificationManager
import com.akundu.kkplayer.Logg
import com.akundu.kkplayer.R
import com.akundu.kkplayer.database.SongDatabase
import com.akundu.kkplayer.download.AndroidDownloader
import com.akundu.kkplayer.feature.main.view.MainActivity
import com.akundu.kkplayer.feature.player.view.PlayerActivity
import com.akundu.kkplayer.getRawFileResourceID
import com.akundu.kkplayer.service.BackgroundSoundService
import com.akundu.kkplayer.service.ServiceTools
import com.akundu.kkplayer.storage.AppFileManager
import com.akundu.kkplayer.storage.Constants.INTERNAL_MEDIA_PATH
import com.akundu.kkplayer.work.DownloadWork
import wseemann.media.FFmpegMediaMetadataRetriever
import java.io.File
import java.io.FileNotFoundException
import java.io.InputStream

/**
 * **PlaySong**
 *
 * @param context Context
 * @param title String
 * @param fileName String
 * @param index Int
 */
internal fun playSong(context: Context, title: String, fileName: String, index: Int) {

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

/**
 * **IsFileExists**
 *
 * @param fileName String
 * @return Boolean
 */
internal fun isFileExists(fileName: String): Boolean {
    val uriString: String = File("$INTERNAL_MEDIA_PATH$fileName").toString()
    val songFile = File(uriString)
    val isFileExist = songFile.exists()
    Logg.i("Is file exists: $isFileExist. Filename: $fileName")
    return isFileExist
}

/**
 * **Download function: Initiate a WorkManager task to download the provided song**
 *
 * @param context Context
 * @param id Long
 * @param fileName
 * @param url String
 * @param movie String
 */
internal fun download(context: Context, id: Long, fileName: String, url: String, movie: String) {
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
 * @param context Context
 * @param fileName String
 */
internal fun copyFileFromRaw(context: Context, fileName: String) {
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
 *  - Downloaded file will be saved in **Public Downloads Folder**
 *  - This API have build-in feature for showing notifications of in-progress-downloads and download-completion
 *
 *  @param context Context
 *  @param fileName String
 *  @param url String
 *  @param movie String
 */
internal fun downloadUsingAndroidDownloadManagerAPI(context: Context, fileName: String, url: String, movie: String) {
    val downloader = AndroidDownloader(context = context)
    downloader.downloadFile(
        url = url,
        fileName = fileName
    )
}

/**
 * MediaMetaDataRetriever function: Extract the media file metadata using FFmpeg library.
 * Metadata can have many information, but here only fetching the song album image.
 *
 * @param context Context
 * @param fileName String
 * @param movie String
 * @return albumArt: ImageBitmap
 */
internal fun mediaMetaDataRetriever(context: Context, fileName: String, movie: String): ImageBitmap {
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
        val icon: Bitmap = ContextCompat.getDrawable(context, getDrawable(movie))!!.toBitmap()
        return icon.asImageBitmap()
    }
}

/**
 * GetDrawable provides the movie avatar.
 * @param movie String
 */
internal fun getDrawable(movie: String): Int {
    return when (movie) {
        "Bajrangi Bhaijaan"  -> R.drawable.bajrangi_bhaijaan
        "Bhool Bhulaiyaa"    -> R.drawable.bhool_bhulaiyaa
        "Crook"              -> R.drawable.crook
        "EK THA TIGER"       -> R.drawable.ek_tha_tiger
        "Force"              -> R.drawable.force
        "G"                  -> R.drawable.g
        "Gangster"           -> R.drawable.gangster
        "Golmaal Returns"    -> R.drawable.golmaal_returns
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
        "raqeeb"             -> R.drawable.raqeeb
        "Raaz - TMC"         -> R.drawable.razz
        "Razz"               -> R.drawable.razz
        "Saathiya"           -> R.drawable.saathiya
        "The Killer"         -> R.drawable.the_killer
        "Life...Metro"       -> R.drawable.life_metro
        "Live-The Train"     -> R.drawable.the_train
        "Woh Lamhe"          -> R.drawable.woh_lamhe
        "Zeher"              -> R.drawable.zeher
        else                 -> R.drawable.ic_music
    }
}
