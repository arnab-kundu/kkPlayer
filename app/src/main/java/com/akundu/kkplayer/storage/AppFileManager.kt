package com.akundu.kkplayer.storage

import android.content.Context
import android.media.MediaScannerConnection
import android.os.Build.VERSION_CODES
import android.util.Log
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
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream


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

    override fun deleteFile(sourceFilePath: String): Boolean {
        return File(sourceFilePath).delete()
    }

    override fun moveFile(sourcePath: String, destinationPath: String) {
        copyFile(sourcePath, destinationPath)
        deleteFile(sourcePath)
    }

    @RequiresApi(VERSION_CODES.N)
    override fun renameFile(context: Context, existingFilePath: String, newFileName: String): File {
        val newFile = createFile(context = context, EXTERNAL_FILES_DIRECTORY, fileName = newFileName, fileExtension = null)
        val existingFile = File(existingFilePath)
        val existingFileInputStream: InputStream = FileInputStream(existingFile)
        copyInputStreamToFile(inputStream = existingFileInputStream, file = newFile)
        deleteFile(existingFilePath)
        return newFile
    }

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

    override fun zipFiles(srcFolderPath: String, destZipFilePath: String) {
        var zip: ZipOutputStream? = null
        var fileWriter: FileOutputStream? = null
        fileWriter = FileOutputStream(destZipFilePath)
        zip = ZipOutputStream(fileWriter)
        addFolderToZip("", srcFolderPath, zip)
        zip.flush()
        zip.close()
    }

    /**
     * This unzip process is comparatively slower. For faster unzipping process use **unZipFile()**
     * The reason of preforming slow is its using **FileOutputStream** instead of **BufferedOutputStream**
     * @see     com.akundu.kkplayer.storage.AppFileManager.unZipFile
     */
    @Deprecated(
        message = "Use unZipFile() for fast unzipping process",
        replaceWith = ReplaceWith("unZipFile(zipFilePath = , extractLocationPath = )"),
        level = DeprecationLevel.WARNING
    )
    override fun unZip(zipFilePath: String, extractLocationPath: String) {
        try {
            val fin = FileInputStream(zipFilePath)
            val zin = ZipInputStream(fin)
            var ze: ZipEntry? = null
            while (zin.nextEntry.also { ze = it } != null) {

                //create dir if required while unzipping
                if (ze!!.isDirectory) {
                    //TODO dirChecker(ze.getName());
                } else {
                    val fout = FileOutputStream(extractLocationPath + ze!!.name)
                    var c = zin.read()
                    while (c != -1) {
                        fout.write(c)
                        c = zin.read()
                    }
                    zin.closeEntry()
                    fout.close()
                }
            }
            zin.close()
        } catch (e: Exception) {
            println(e)
        }
    }

    /**
     * Improved in terms of performance. Unzipping time is very less compare to unZip()
     * The reason of preforming fast is its using **BufferedOutputStream** instead of **FileOutputStream**
     * @see     com.akundu.kkplayer.storage.AppFileManager.unZip
     * @see     java.io.BufferedOutputStream
     */
    override fun unZipFile(zipFilePath: String, extractLocationPath: String) {
        try {
            val inputStream: FileInputStream = FileInputStream(zipFilePath)
            val zipStream = ZipInputStream(inputStream)
            var zEntry: ZipEntry? = null
            while (zipStream.nextEntry.also { zEntry = it } != null) {
                Log.d("Unzip", "Unzipping " + zEntry!!.name + " at " + extractLocationPath)
                if (zEntry!!.isDirectory) {
                    handleDirectory(extractLocationPath, zEntry!!.name)
                } else {
                    val fout: FileOutputStream = FileOutputStream(extractLocationPath + "/" + zEntry!!.name)
                    val bufout = BufferedOutputStream(fout)
                    val buffer = ByteArray(1024)
                    var read = 0
                    while (zipStream.read(buffer).also { read = it } != -1) {
                        bufout.write(buffer, 0, read)
                    }
                    zipStream.closeEntry()
                    bufout.close()
                    fout.close()
                }
            }
            zipStream.close()
            Log.d("Unzip", "Unzipping complete. path :  $extractLocationPath")
        } catch (e: java.lang.Exception) {
            Log.d("Unzip", "Unzipping failed")
            e.printStackTrace()
        }

    }

    private fun handleDirectory(extractLocationPath: String, dir: String) {
        val f: File = File(extractLocationPath + dir)
        if (!f.isDirectory) {
            f.mkdirs()
        }
    }

    override fun encryptFile(filePath: String, encryptionRule: String): File {
        TODO("Not yet implemented")
    }

    override fun decryptFile(filePath: String, rule: String): File {
        TODO("Not yet implemented")
    }

    private fun addFolderToZip(path: String, srcFolder: String, zip: ZipOutputStream) {
        val folder = File(srcFolder)
        for (fileName in folder.list()) {
            if (path == "") {
                addFileToZip(folder.name, "$srcFolder/$fileName", zip)
            } else {
                addFileToZip(path + "/" + folder.name, srcFolder + "/" + fileName, zip)
            }
        }
    }

    private fun addFileToZip(path: String, srcFile: String, zip: ZipOutputStream) {
        val folder = File(srcFile)
        if (folder.isDirectory) {
            addFolderToZip(path, srcFile, zip)
        } else {
            val buf = ByteArray(1024)
            var len: Int
            val `in` = FileInputStream(srcFile)
            zip.putNextEntry(ZipEntry(path + "/" + folder.name))
            while (`in`.read(buf).also { len = it } > 0) {
                zip.write(buf, 0, len)
            }
        }
    }
}