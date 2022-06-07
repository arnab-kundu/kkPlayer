package com.akundu.kkplayer

import android.content.Context
import android.os.Build
import android.os.Environment
import android.util.Log
import okhttp3.ResponseBody
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream


/**
 * @author Arnab Kundu
 * @param context Context required for API level R
 */

@Suppress("unused")
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
     * Generic method to Create File
     * @param context Context required for API level R
     * @param folderName
     * @param fileName
     * @param fileExtension
     */
    fun createFile(context: Context? = null, folderName: String, fileName: String, fileExtension: String? = null): File {
        Log.d(TAG, "Android Device Build Version: ${Build.VERSION.SDK_INT}")
        val file: File = if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R)
            File(Environment.getExternalStorageDirectory(), "$PARENT_DIRECTORY_NAME/$folderName/$fileName.$fileExtension")
        else
            if (fileExtension != null)
                File(context?.getExternalFilesDir(null), "$folderName/$fileName.$fileExtension")
            else
                File(context?.getExternalFilesDir(null), "$folderName/$fileName")
        //File(context?.externalCacheDir?.path, "$folderName/$fileName")
        //File(context?.obbDir?.path, "$folderName/$fileName")
        if (!file.exists()) {
            try {
                file.createNewFile()
            } catch (e: IOException) {
                e.printStackTrace()
            }
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
}