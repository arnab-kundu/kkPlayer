package com.akundu.kkplayer.work

import android.app.PendingIntent
import android.content.Context
import android.graphics.Bitmap
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.akundu.kkplayer.AppsNotificationManager
import com.akundu.kkplayer.Logg
import com.akundu.kkplayer.database.SongDatabase
import com.akundu.kkplayer.feature.main.view.MainActivity
import com.akundu.kkplayer.getDrawable
import com.akundu.kkplayer.media.FolderFiles
import com.akundu.kkplayer.media.MediaMetaDataRetriever
import com.akundu.kkplayer.network.ApiRequest
import com.akundu.kkplayer.network.RetrofitRequest
import com.akundu.kkplayer.provider.FileAccessPermissionProvider
import com.akundu.kkplayer.storage.AppFileManager
import com.akundu.kkplayer.storage.FileLocationCategory.MEDIA_DIRECTORY
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

class DownloadWork
    @JvmOverloads
    constructor(
        val context: Context,
        workerParameters: WorkerParameters,
        private val apiRequest: ApiRequest = RetrofitRequest.getRetrofitInstance().create(ApiRequest::class.java),
        private val fileManager: FileManager = AppFileManager(),
    ) : CoroutineWorker(context, workerParameters) {
        override suspend fun doWork(): Result {
            val id = inputData.getLong("id", 0L)
            val fileName = inputData.getString("fileName") ?: ""
            val url = inputData.getString("url") ?: ""
            val movie = inputData.getString("movie") ?: ""
            val notificationID = inputData.getInt("notificationID", 0)

            // downloadFileAndSaveInAppDirectory(fileName, movie, notificationID)
            // The download is awaited rather than detached, so WorkManager is told whether the
            // song actually arrived and can retry when it did not.
            return runCatching {
                val isDownloaded = downloadFileAndSaveInScopedStorage(id, fileName, url, movie, notificationID)
                if (isDownloaded) {
                    val bitmap: Bitmap? = MediaMetaDataRetriever.getMediaImage(fileName)
                    val isSaved: Boolean = MediaMetaDataRetriever.hasSavedMediaThumbnailsInCache(context, fileName, bitmap)
                    Logg.d("Is $fileName thumbnail saved: $isSaved")
                }
                isDownloaded
            }.fold(
                onSuccess = { isDownloaded -> if (isDownloaded) Result.success() else Result.retry() },
                onFailure = { error ->
                    Logg.e("Error: ${error.message}, FileName: $fileName")

                    val database: SongDatabase = SongDatabase.getDatabase(context)
                    database.songDao().deleteSong(id)

                    AppsNotificationManager.getInstance(context)?.cancelNotification(notificationID)
                    AppsNotificationManager.getInstance(context)?.downloadCompletedNotification(
                        targetNotificationActivity = MainActivity::class.java,
                        channelId = "CHANNEL_ID",
                        title = fileName,
                        text = "Download Failed",
                        notificationId = System.currentTimeMillis().toInt(),
                        pendingIntentFlag = PendingIntent.FLAG_IMMUTABLE,
                        drawableId = getDrawable(movie),
                    )
                    Result.failure()
                },
            )
        }

        /**
         * Download and Save file in Scoped Storage,
         * Use Coroutines
         *
         * @param fileName (required)
         * @param url (required)
         * @param movie (required) for displaying in notification
         * @param notificationID (required) for canceling on going downloading notification
         * @return true when the song was saved to storage
         */
        internal suspend fun downloadFileAndSaveInScopedStorage(
            id: Long,
            fileName: String,
            url: String,
            movie: String,
            notificationID: Int,
        ): Boolean =
            withContext(Dispatchers.IO) {
                val deferred: Deferred<Response<ResponseBody>> =
                    async {
                        apiRequest.downloadSongByUrl(url = url).execute()
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
                    val file: File =
                        fileManager.createFile(
                            context = context,
                            fileLocationCategory = MEDIA_DIRECTORY,
                            fileName = fileName,
                            fileExtension = null,
                        )
                    if (inputStream != null) {
                        fileManager.copyInputStreamToFile(inputStream = inputStream, file = file)
                    }

                    /** Share file between third party apps using content provider */
                    try {
                        val fileProvider = FileAccessPermissionProvider(context, file)
                        fileProvider.grandUriPermissionsToThirdPartyApp(FileAccessPermissionProvider.PACKAGE_NAME_OF_YOUTUBE_MUSIC)
                        fileProvider.grandUriPermissionsToThirdPartyApp(FileAccessPermissionProvider.PACKAGE_NAME_OF_WYNK_MUSIC)
                    } catch (e: Exception) {
                        Logg.e("createFile: $e")
                    }

                    val database: SongDatabase = SongDatabase.getDatabase(context)
                    database.songDao().updateSongDownloadInfo(id, true)

                    AppsNotificationManager.getInstance(context)?.downloadCompletedNotification(
                        targetNotificationActivity = MainActivity::class.java,
                        channelId = "CHANNEL_ID",
                        title = fileName,
                        text = "Download Completed",
                        notificationId = System.currentTimeMillis().toInt(),
                        pendingIntentFlag = PendingIntent.FLAG_IMMUTABLE,
                        drawableId = getDrawable(movie),
                    )
                    AppsNotificationManager.getInstance(context)?.cancelNotification(notificationID)
                    true
                } else {
                    Logg.e("StatusCode: ${response.code()}")

                    AppsNotificationManager.getInstance(context)?.downloadCompletedNotification(
                        targetNotificationActivity = MainActivity::class.java,
                        channelId = "CHANNEL_ID",
                        title = fileName,
                        text = "Download failed. Error: ${response.code()}",
                        notificationId = System.currentTimeMillis().toInt(),
                        pendingIntentFlag = PendingIntent.FLAG_IMMUTABLE,
                        drawableId = getDrawable(movie),
                    )
                    AppsNotificationManager.getInstance(context)?.cancelNotification(notificationID)
                    false
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
        private fun downloadFileAndSaveInAppDirectory(
            fileName: String,
            movie: String,
            notificationID: Int,
        ) {
            val apiRequest = RetrofitRequest.getRetrofitInstance().create(ApiRequest::class.java)
            apiRequest.downloadSong(fileName = fileName).enqueue(
                object : Callback<ResponseBody> {
                    override fun onResponse(
                        call: Call<ResponseBody>,
                        response: Response<ResponseBody>,
                    ) {
                        Logg.i("StatusCode: ${response.code()}")

                        if (response.isSuccessful) {
                            val inputStream: InputStream? = response.body()?.byteStream()

                            val file = FolderFiles.createFile(context = context, folderName = "", fileName = fileName)
                            if (inputStream != null) {
                                FolderFiles.copyInputStreamToFile(inputStream = inputStream, file = file)
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
                            drawableId = getDrawable(movie),
                        )
                    }

                    override fun onFailure(
                        call: Call<ResponseBody>,
                        t: Throwable,
                    ) {
                        AppsNotificationManager.getInstance(context)?.cancelNotification(notificationID)
                        Logg.e("Song download failed: ${t.message}")
                    }
                },
            )
        }
    }
