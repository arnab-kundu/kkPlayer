package com.akundu.kkplayer.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.akundu.kkplayer.FolderFiles
import com.akundu.kkplayer.Logg
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
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Logg.e("onFailure: ${t.localizedMessage}")
            }
        })
        return Result.success()
    }
}