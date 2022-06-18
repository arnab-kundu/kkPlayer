package com.akundu.kkplayer.work

import android.app.PendingIntent
import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.akundu.kkplayer.AppsNotificationManager
import com.akundu.kkplayer.FolderFiles
import com.akundu.kkplayer.Logg
import com.akundu.kkplayer.MainActivity
import com.akundu.kkplayer.R
import com.akundu.kkplayer.copyInputStreamToFile
import com.akundu.kkplayer.network.ApiRequest
import com.akundu.kkplayer.network.RetrofitRequest
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import java.io.InputStream

class DownloadWork(val context: Context, workerParameters: WorkerParameters) :
    CoroutineWorker(context, workerParameters) {

    override suspend fun doWork(): Result {

        val fileName = inputData.getString("fileName") ?: ""
        val movie = inputData.getString("movie") ?: ""
        val notificationID = inputData.getInt("notificationID", 0)

        val apiRequest = RetrofitRequest.getRetrofitInstance().create(ApiRequest::class.java)
        apiRequest.downloadSong(fileName = fileName).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: retrofit2.Response<ResponseBody>) {
                Logg.i("StatusCode: ${response.code()}")

                if (response.isSuccessful) {
                    val inputStream: InputStream? = response.body()?.byteStream()
                    val file =
                        FolderFiles.createFile(context = context, folderName = "", fileName = fileName)
                    if (inputStream != null)
                        copyInputStreamToFile(inputStream = inputStream, file = file)
                } else {
                    Logg.e("StatusCode: ${response.code()}")
                }
                AppsNotificationManager.getInstance(context)?.cancelNotification(notificationID)
                AppsNotificationManager.getInstance(context)?.updateWithPicture(
                    targetNotificationActivity = MainActivity::class.java,
                    channelId = "CHANNEL_ID",
                    title = fileName,
                    text = "Download Completed",
                    notificationId = 0,
                    pendingIntentflag = PendingIntent.FLAG_IMMUTABLE,
                    drawableId = getDrawable(movie)
                )
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Logg.e("onFailure: ${t.localizedMessage}")
            }
        })
        return Result.success()
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
}