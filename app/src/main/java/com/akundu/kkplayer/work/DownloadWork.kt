package com.akundu.kkplayer.work

import android.app.PendingIntent
import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.akundu.kkplayer.AppsNotificationManager
import com.akundu.kkplayer.Logg
import com.akundu.kkplayer.MainActivity
import com.akundu.kkplayer.R
import com.akundu.kkplayer.media.FolderFiles
import com.akundu.kkplayer.network.ApiRequest
import com.akundu.kkplayer.network.RetrofitRequest
import com.akundu.kkplayer.storage.AppFileManager
import com.akundu.kkplayer.storage.FileManager
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.InputStream


class DownloadWork(val context: Context, workerParameters: WorkerParameters) :
    CoroutineWorker(context, workerParameters) {

    override suspend fun doWork(): Result {

        val fileName = inputData.getString("fileName") ?: ""
        val movie = inputData.getString("movie") ?: ""
        val notificationID = inputData.getInt("notificationID", 0)

        //downloadFileAndSaveInAppDirectory(fileName, movie, notificationID)
        downloadFileAndSaveInScopedStorage(fileName, movie, notificationID)

        return Result.success()
    }


    /**
     * Download and Save file in Scoped Storage,
     * Use Coroutines
     *
     * @param fileName (required)
     * @param movie (required) for displaying in notification
     * @param notificationID (required) for canceling on going downloading notification
     */
    private suspend fun downloadFileAndSaveInScopedStorage(fileName: String, movie: String, notificationID: Int) {

        val apiRequest = RetrofitRequest.getRetrofitInstance().create(ApiRequest::class.java)

        withContext(Dispatchers.IO) {
            val deferred: Deferred<Response<ResponseBody>> = async {
                apiRequest.downloadSong(fileName = fileName).execute()
            }
            val response = deferred.await()
            Logg.i("StatusCode: ${response.code()}")

            if (response.isSuccessful) {
                val inputStream: InputStream? = response.body()?.byteStream()

                /** Save file in scoped storage in Music folder */
                /*if (inputStream != null) {
                    FolderFiles.addMusic(context = context, inputStream = inputStream, filename = fileName)
                }*/

                /** Save file in app own storage media directory */
                /*val path = FolderFiles.createInternalMediaDirectory()
                if (path != null) {
                    val file = FolderFiles.createFileAtPath(context = context, fileName = fileName, path = path)
                    if (inputStream != null)
                        FolderFiles.copyInputStreamToFile(inputStream = inputStream, file = file)
                }*/

                /** Save file in app own storage media directory */
                val fileManager: FileManager = AppFileManager()
                val file: File = fileManager.createFile(context = context, path = "", fileName = fileName, fileExtension = null)
                if (inputStream != null) {
                    fileManager.copyInputStreamToFile(inputStream = inputStream, file = file)
                }

            } else {
                Logg.e("StatusCode: ${response.code()}")
            }
            AppsNotificationManager.getInstance(context)?.cancelNotification(notificationID)
            AppsNotificationManager.getInstance(context)?.downloadCompletedNotification(
                targetNotificationActivity = MainActivity::class.java,
                channelId = "CHANNEL_ID",
                title = fileName,
                text = "Download Completed",
                notificationId = System.currentTimeMillis().toInt(),
                pendingIntentFlag = PendingIntent.FLAG_IMMUTABLE,
                drawableId = getDrawable(movie)
            )
        }
    }


    /**
     * Download and Save file in app directory
     *
     * @param fileName (required)
     * @param movie (required) for displaying in notification
     * @param notificationID (required) for canceling on going downloading notification
     */
    @Deprecated(message = "Download & save file in app directory", replaceWith = ReplaceWith("downloadFileAndSaveInScopedStorage"))
    private fun downloadFileAndSaveInAppDirectory(fileName: String, movie: String, notificationID: Int) {
        val apiRequest = RetrofitRequest.getRetrofitInstance().create(ApiRequest::class.java)
        apiRequest.downloadSong(fileName = fileName).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: retrofit2.Response<ResponseBody>) {
                Logg.i("StatusCode: ${response.code()}")

                if (response.isSuccessful) {
                    val inputStream: InputStream? = response.body()?.byteStream()

                    val file = FolderFiles.createFile(context = context, folderName = "", fileName = fileName)
                    if (inputStream != null)
                        FolderFiles.copyInputStreamToFile(inputStream = inputStream, file = file)

                } else {
                    Logg.e("StatusCode: ${response.code()}")
                }
                AppsNotificationManager.getInstance(context)?.cancelNotification(notificationID)
                AppsNotificationManager.getInstance(context)?.downloadCompletedNotification(
                    targetNotificationActivity = MainActivity::class.java,
                    channelId = "CHANNEL_ID",
                    title = fileName,
                    text = "Download Completed",
                    notificationId = System.currentTimeMillis().toInt(),
                    pendingIntentFlag = PendingIntent.FLAG_IMMUTABLE,
                    drawableId = getDrawable(movie)
                )
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                AppsNotificationManager.getInstance(context)?.cancelNotification(notificationID)
                Logg.e("Song download failed: ${t.message}")
            }
        })
    }

    private fun getDrawable(movie: String): Int {
        return when(movie) {
            "Bajrangi Bhaijaan" -> R.drawable.bajrangi_bhaijaan
            "EK THA TIGER"      -> R.drawable.ek_tha_tiger
            "Gangster"          -> R.drawable.gangster
            "Jannat"            -> R.drawable.jannat
            "Jism"              -> R.drawable.jism
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
}