@file:Suppress("RedundantExplicitType", "UNUSED_PARAMETER", "ImplicitSamInstance")

package com.akundu.kkplayer.feature.main.ui

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequest.Builder
import androidx.work.WorkManager
import com.akundu.kkplayer.AppsNotificationManager
import com.akundu.kkplayer.Logg
import com.akundu.kkplayer.database.SongDatabase
import com.akundu.kkplayer.download.AndroidDownloader
import com.akundu.kkplayer.feature.main.view.MainActivity
import com.akundu.kkplayer.feature.player.view.PlayerActivity
import com.akundu.kkplayer.getDrawable
import com.akundu.kkplayer.getRawFileResourceID
import com.akundu.kkplayer.service.BackgroundSoundService
import com.akundu.kkplayer.service.ServiceTools
import com.akundu.kkplayer.storage.AppFileManager
import com.akundu.kkplayer.storage.Constants.MEDIA_PATH
import com.akundu.kkplayer.work.DownloadWork
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
internal fun playSong(
    context: Context,
    title: String,
    fileName: String,
    index: Int,
) {
    Logg.i("FileName: $fileName")

    try {
        val uriString: String = File("$MEDIA_PATH/$fileName").toString()

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
    val uriString: String = File("$MEDIA_PATH/$fileName").toString()
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
internal fun download(
    context: Context,
    id: Long,
    fileName: String,
    url: String,
    movie: String,
) {
    Logg.i("Downloading: $fileName")

    if (url.isEmpty()) {
        copyFileFromRaw(context, fileName)
        return
    }

    val notificationID = System.currentTimeMillis().toInt()

    val data: Data =
        Data
            .Builder()
            .putLong("id", id)
            .putString("fileName", fileName)
            .putString("url", url)
            .putString("movie", movie)
            .putInt("notificationID", notificationID)
            .build()

    val constraints: Constraints =
        Constraints
            .Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            // .setRequiresBatteryNotLow(true)
            // .setRequiresStorageNotLow(true)
            .build()

    val request =
        Builder(DownloadWork::class.java)
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
        drawableId = getDrawable(movie),
    )
}

/**
 * Makes a copy of the file(provided fileName) from **RAW** folder into app's **MEDIA DIRECTORY**
 * @param context Context
 * @param fileName String
 */
internal fun copyFileFromRaw(
    context: Context,
    fileName: String,
) {
    Logg.d("DownloadFromRaw: $fileName")

    val resourceID: Int = getRawFileResourceID(fileName) // R.raw.i_dont_wanna_live_forever_fifty_shades_darker
    val fileInputStream: InputStream = context.resources.openRawResource(resourceID) // Getting InputStream of raw file
    AppFileManager().saveFile(fileInputStream = fileInputStream, destinationPath = "$MEDIA_PATH/$fileName")

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
internal fun downloadUsingAndroidDownloadManagerAPI(
    context: Context,
    fileName: String,
    url: String,
    movie: String,
) {
    val downloader = AndroidDownloader(context = context)
    downloader.downloadFile(
        url = url,
        fileName = fileName,
    )
}
