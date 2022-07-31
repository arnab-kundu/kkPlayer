package com.akundu.kkplayer.media

import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import com.akundu.kkplayer.BuildConfig
import com.akundu.kkplayer.Logg
import okhttp3.ResponseBody
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.util.*


/**
 * @author Arnab Kundu
 * @param context Context required for API level R
 */

//@Suppress("unused")
object FolderFiles {

    private const val TAG = "FolderFiles"
    const val PARENT_DIRECTORY_NAME = "kkPlayer"

    /**
     * Generic method to Create folder
     * @param context Context required for API level R
     * @param folderName
     */
    fun createFolder(context: Context? = null, folderName: String): String {

        val logFolder: File = if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R)
            File(Environment.getExternalStorageDirectory(), "$PARENT_DIRECTORY_NAME/$folderName")
        else
            File(context?.getExternalFilesDir(PARENT_DIRECTORY_NAME), folderName)

        if (!logFolder.exists()) {
            try {
                logFolder.mkdirs()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return logFolder.absolutePath;
    }


    /**
     * Creates Media folder in app package folder
     *
     * @param path Optional
     * @return Path of created media folder
     */
    fun createInternalMediaDirectory(path: String = "media/${BuildConfig.APPLICATION_ID}"): String? {

        try {
            val rootFolderPath = "/storage/emulated/0/Android/"
            var folder: File = File(rootFolderPath)

            val pathFoldersList: List<String> = path.split("/")
            pathFoldersList.forEach { childFolder ->
                folder = File(folder, childFolder)

                if (!folder.exists()) {
                    Logg.d("Is Folder Created at: ${folder.absolutePath}: ${folder.mkdirs()}")
                }
            }

            return folder.path

        } catch (e: IOException) {
            e.printStackTrace()
            return null
        }
    }
    fun createFileAtPath(context: Context, path: String, fileName: String = "", fileExtension: String? = null): File {

        val file: File =
            if (fileExtension == null)
                File(path, "$fileName")
            else
                File(path, "$fileName.$fileExtension")

        if (!file.exists()) {
            try {
                file.createNewFile()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        // Without MediaScan file not visible to in PC after connecting via USB
        MediaScannerConnection.scanFile(context, arrayOf(file.toString()), null) { path, uri ->

        }
        Log.d(TAG, "File path: ${file.absolutePath}")
        return file
    }

    /**
     * Generic method to Create File
     * @param context Context required for API level R
     * @param folderName
     * @param fileName
     * @param fileExtension
     */
    fun createFile(context: Context, folderName: String, fileName: String, fileExtension: String? = null): File {
        val manufacturer = Build.MANUFACTURER
        val model = Build.MODEL
        val version = Build.VERSION.SDK_INT
        val versionRelease = Build.VERSION.RELEASE

        Log.e(TAG, "Android Device details: \nAPI Level: $version \nOS version: Android $versionRelease \nManufacturer: $manufacturer \nModel: $model")

        val file: File =
            if (fileExtension == null)
                File(context.getExternalFilesDir(null), "$folderName/$fileName")
            else
                File(context.getExternalFilesDir(null), "$folderName/$fileName.$fileExtension")
        /**
         * Different options to save files in Scoped storage Android
         *
         * File(context?.filesDir, "$folderName/$fileName")
         * File(context?.obbDir, "$folderName/$fileName")
         * File(context?.cacheDir, "$folderName/$fileName")
         * File(context?.externalCacheDir, "$folderName/$fileName")
         */


        if (!file.exists()) {
            try {
                file.createNewFile()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }

        // Without MediaScan file not visible to in PC after connecting via USB
        MediaScannerConnection.scanFile(context, arrayOf(file.toString()), null) { path, uri ->

        }
        Log.d(TAG, "File path: ${file.absolutePath}")
        return file
    }

    /**
     * Generic method to Create Text File
     * @param context Context required for API level R
     * @param folderName
     * @param fileName
     */
    fun createTextFile(context: Context? = null, folderName: String, fileName: String): File {

        val textFile: File = if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R)
            File(Environment.getExternalStorageDirectory(), "$PARENT_DIRECTORY_NAME/$folderName/$fileName.txt")
        else
            File(context?.getExternalFilesDir(PARENT_DIRECTORY_NAME), "$folderName/$fileName.txt")

        if (!textFile.exists()) {
            try {
                textFile.createNewFile()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return textFile
    }


    /**
     * createLogFile() this method specfic to this project to save logs
     * @param context Context required for API level R
     * @param folderName
     * @param fileName
     */
    fun createLogFile(context: Context? = null, folderName: String, fileName: String): File {

        val logFile: File = if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R)
            File(Environment.getExternalStorageDirectory(), "$PARENT_DIRECTORY_NAME/$folderName/log-$fileName.txt")
        else
            File(context?.getExternalFilesDir(PARENT_DIRECTORY_NAME), "$folderName/log-$fileName.txt")

        if (!logFile.exists()) {
            try {
                logFile.createNewFile()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return logFile
    }


    //private const val FILE_PATH_PREFIX = "/storage/emulated/0/ZebraApp/"

    /**
     * Generic function to delete a file
     * @param context
     * @param folderName
     * @param fileName
     * @param fileExtension
     * @return Boolean
     */
    fun deleteFile(context: Context?, folderName: String, fileName: String, fileExtension: String): Boolean {

        val file: File = if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R)
            File(Environment.getExternalStorageDirectory(), "$PARENT_DIRECTORY_NAME/$folderName/$fileName$fileExtension")
        else
            File(context?.getExternalFilesDir(PARENT_DIRECTORY_NAME), "$folderName/$fileName$fileExtension")

        if (file.exists()) {
            return file.delete()
        }
        return false
    }

    /**
     * Writes inputStream to file
     * Writes data to file
     *
     * @param inputStream
     * @param file
     */
    @Throws(IOException::class)
    fun copyInputStreamToFile(inputStream: InputStream, file: File) {

        // append = false
        FileOutputStream(file, false).use { outputStream ->
            var read: Int
            val bytes = ByteArray(DEFAULT_BUFFER_SIZE)
            while (inputStream.read(bytes).also { read = it } != -1) {
                outputStream.write(bytes, 0, read)
            }
        }
    }

    /**
     * Custom function downloaded from internet
     * Tested working
     */
    fun writeResponseBodyToDisk(body: ResponseBody, context: Context): Boolean {
        return try {
            // todo change the file location/name according to your needs
            val futureStudioIconFile: File = File(context.getExternalFilesDir(null).toString() + File.separator + "Future Studio Icon.png")

            Log.d(TAG, "writeResponseBodyToDisk: ${futureStudioIconFile.absolutePath}")
            var inputStream: InputStream? = null
            var outputStream: OutputStream? = null
            try {
                val fileReader = ByteArray(4096)
                val fileSize = body.contentLength()
                var fileSizeDownloaded: Long = 0
                inputStream = body.byteStream()
                outputStream = FileOutputStream(futureStudioIconFile)
                while (true) {
                    val read: Int = inputStream.read(fileReader)
                    if (read == -1) {
                        break
                    }
                    outputStream.write(fileReader, 0, read)
                    fileSizeDownloaded += read.toLong()
                    Log.d(TAG, "file download: $fileSizeDownloaded of $fileSize")
                }
                outputStream.flush()
                true
            } catch (e: IOException) {
                false
            } finally {
                if (inputStream != null) {
                    inputStream.close()
                }
                if (outputStream != null) {
                    outputStream.close()
                }
            }
        } catch (e: IOException) {
            false
        }
    }


    /**
     * Save file in External Download Folder using content resolver
     * Issue: Empty File saved but no data
     */
    fun saveFileToPhone(context: Context, inputStream: InputStream, filename: String): File? {

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val contentResolver: ContentResolver = context.contentResolver
                val contentValues: ContentValues = ContentValues()
                contentValues.put(MediaStore.Downloads.DISPLAY_NAME, filename)
                contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
                val collection: Uri = MediaStore.Downloads.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
                val fileUri: Uri? = contentResolver.insert(collection, contentValues)
                if (fileUri != null) {
                    val outputStream: OutputStream? = contentResolver.openOutputStream(fileUri)
                    Objects.requireNonNull(outputStream)
                }
                return File(fileUri.toString())
            }
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }
        return null
    }

    /**
     * Save file in Scoped Storage. Music Folder
     */
    suspend fun addMusic(context: Context, inputStream: InputStream, filename: String) {

        val uri = MediaStoreUtils.createAudioUri(context, filename) ?: return

        try {
            context.contentResolver.openOutputStream(uri, "w")?.use { outputStream ->
                inputStream.use { inputStream ->
                    inputStream.copyTo(outputStream)
                    MediaStoreUtils.scanUri(context, uri, "music/mp3")
                    MediaStoreUtils.getResourceByUri(context, uri)
                }
            }

        } catch (e: IOException) {
            Log.e(TAG, e.printStackTrace().toString())
        }
    }
}