package com.akundu.kkplayer.storage

import android.R.attr.src
import android.content.Context
import android.media.MediaScannerConnection
import com.akundu.kkplayer.BuildConfig
import com.akundu.kkplayer.Logg
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import kotlin.jvm.Throws


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

    override fun createFile(context: Context, path: String, fileName: String, fileExtension: String?): File {

        /** Create path */
        val folder = createAppsInternalPrivateStoragePath("media/${BuildConfig.APPLICATION_ID}")

        val file: File = if (fileExtension == null)
            File(folder!!.path, "$fileName")
        else
            File(folder!!.path, "$fileName.$fileExtension")

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

    @Throws(IOException::class)
    override fun copyFile(sourcePath: String, destinationPath: String) {
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
            } finally {
                outputStream.close()
            }
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