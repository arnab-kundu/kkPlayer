package com.akundu.kkplayer.storage

import android.content.Context
import android.media.MediaScannerConnection
import android.os.Build.VERSION_CODES
import android.os.Environment
import androidx.annotation.RequiresApi
import com.akundu.kkplayer.BuildConfig
import com.akundu.kkplayer.Logg
import com.akundu.kkplayer.storage.FileLocationCategory.CACHE_DIRECTORY
import com.akundu.kkplayer.storage.FileLocationCategory.DATA_DIRECTORY
import com.akundu.kkplayer.storage.FileLocationCategory.DOCUMENT_DIRECTORY
import com.akundu.kkplayer.storage.FileLocationCategory.DOWNLOADS_DIRECTORY
import com.akundu.kkplayer.storage.FileLocationCategory.EXTERNAL_CACHE_DIRECTORY
import com.akundu.kkplayer.storage.FileLocationCategory.EXTERNAL_FILES_DIRECTORY
import com.akundu.kkplayer.storage.FileLocationCategory.FILES_DIRECTORY
import com.akundu.kkplayer.storage.FileLocationCategory.MEDIA_DIRECTORY
import com.akundu.kkplayer.storage.FileLocationCategory.MUSIC_DIRECTORY
import com.akundu.kkplayer.storage.FileLocationCategory.OBB_DIRECTORY
import com.akundu.kkplayer.storage.FileLocationCategory.PICTURES_DIRECTORY
import com.akundu.kkplayer.storage.FileLocationCategory.VIDEOS_DIRECTORY
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream


@Suppress("RedundantExplicitType")
class AppFileManager : FileManager {

    override fun createFolder(folderName: String, path: String): Boolean {
        val rootFolder: File = File(path, folderName)
        return rootFolder.mkdirs()
    }

    override fun createAppsInternalPrivateStoragePath(path: String): File? {

        try {
            val rootFolderPath = "/storage/emulated/0/Android"
            var folder: File = File(rootFolderPath)

            val pathFoldersList: List<String> = path.split("/")
            pathFoldersList.forEach { childFolder ->
                folder = File(folder, childFolder)

                if (!folder.exists()) {
                    Logg.d("Is Folder Created at: ${folder.absolutePath}: ${folder.mkdirs()}")
                }
            }

            return folder

        } catch (e: IOException) {
            e.printStackTrace()
            return null
        }
    }

    override fun createSharedStoragePath(path: String): File? {
        TODO("Not yet implemented")
    }

    override fun renameFolder(folderPath: String, newFolderName: String) {
        TODO("Not yet implemented")
    }

    override fun deleteFolder(directory: File) {
        for (file in directory.listFiles()) {
            if (!file.isDirectory) {
                file.delete()
            }
        }
    }

    private fun createMediaFile(context: Context, path: String, fileName: String, fileExtension: String?): File {

        /** Create path */
        val folder = createAppsInternalPrivateStoragePath("media/${BuildConfig.APPLICATION_ID}")

        val file: File = if (fileExtension == null) File(folder!!.path, "$fileName")
        else File(folder!!.path, "$fileName.$fileExtension")

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
        return file
    }

    @RequiresApi(VERSION_CODES.N)
    override fun createFile(context: Context, fileLocationCategory: FileLocationCategory, fileName: String, fileExtension: String?): File {

        /** Create path */
        val folder: File? = when(fileLocationCategory) {
            CACHE_DIRECTORY          -> context.cacheDir
            DATA_DIRECTORY           -> context.dataDir
            FILES_DIRECTORY          -> context.filesDir
            EXTERNAL_CACHE_DIRECTORY -> context.externalCacheDir.let { if (it == null) Logg.w("externalCacheDir returns null"); it }

            EXTERNAL_FILES_DIRECTORY -> context.getExternalFilesDir(null).let { if (it == null) Logg.w("getExternalFilesDir returns null"); it }
            MEDIA_DIRECTORY          -> createAppsInternalPrivateStoragePath("media/${BuildConfig.APPLICATION_ID}").let { if (it == null) Logg.w("createMediaDir returns null"); it }
            OBB_DIRECTORY            -> context.obbDir

            DOWNLOADS_DIRECTORY      -> File("/storage/emulated/0/Download/")
            DOCUMENT_DIRECTORY       -> TODO()
            MUSIC_DIRECTORY          -> TODO()
            PICTURES_DIRECTORY       -> TODO()
            VIDEOS_DIRECTORY         -> TODO()
        }

        val file: File = if (fileExtension == null) File(folder!!.path, "$fileName")
        else File(folder!!.path, "$fileName.$fileExtension")

        if (!file.exists()) {
            try {
                file.createNewFile()
                Logg.v("Created file successfully")
            } catch (e: IOException) {
                e.printStackTrace()
                Logg.e("Failed to create file: $e")
            }
        }
        // Without MediaScan file not visible to in PC after connecting via USB
        MediaScannerConnection.scanFile(context, arrayOf(file.toString()), null) { path, uri ->
            Logg.v("Created file path: $path")
            Logg.v("Created file uri: $uri")
        }
        return file
    }

    @Throws(IOException::class)
    override fun copyFile(sourcePath: String, destinationPath: String): Boolean {
        val inputStream: InputStream = FileInputStream(sourcePath)
        try {
            val outputStream: OutputStream = FileOutputStream(destinationPath)
            try {
                // Transfer bytes from in to out
                val buffer = ByteArray(1024)
                var len: Int
                while (inputStream.read(buffer).also { len = it } > 0) {
                    outputStream.write(buffer, 0, len)
                }
            } catch (e: Exception) {
                Logg.e(e.toString())
                return false
            } finally {
                outputStream.close()
            }
            return true
        } catch (e: FileNotFoundException) {
            Logg.e(e.toString())
            return false
        } finally {
            inputStream.close()
        }
    }

    override fun moveFile(sourcePath: String, destinationPath: String) {
        TODO("Not yet implemented")
    }

    override fun renameFile(existingFilePath: String, newFileName: String) {
        TODO("Not yet implemented")
    }

    /**
     * Writes inputStream to file
     * Writes data to file
     *
     * @param inputStream
     * @param file
     */
    @Throws(IOException::class)
    override fun copyInputStreamToFile(inputStream: InputStream, file: File) {

        FileOutputStream(file, false).use { outputStream ->
            var read: Int
            val bytes = ByteArray(DEFAULT_BUFFER_SIZE)
            while (inputStream.read(bytes).also { read = it } != -1) {
                outputStream.write(bytes, 0, read)
            }
        }
    }

    override fun zipFiles(filesPath: String, zipFilePath: String) {
        TODO("Not yet implemented")
    }

    override fun unZipFile(zipFilePath: String, extractLocationPath: String) {
        TODO("Not yet implemented")
    }
}