package com.akundu.kkplayer.storage

import android.content.Context
import java.io.File
import java.io.InputStream


interface FileManager {

    fun createFolder(folderName: String, path: String): Boolean

    fun createAppsInternalPrivateStoragePath(path: String): File?

    fun createSharedStoragePath(path: String): File?

    fun renameFolder(folderPath: String, newFolderName: String)

    /**
     * Delete folder, subFolders and files
     *
     * @param directory
     */
    fun deleteFolder(directory: File)

    fun createFile(context: Context, path: String, fileName: String, fileExtension: String?): File

    /**
     * Copy a file from source path to destination path
     *
     * @param sourcePath String
     * @param destinationPath String
     * @return isCopySuccessful - Boolean
     */
    fun copyFile(sourcePath: String, destinationPath: String): Boolean

    fun moveFile(sourcePath: String, destinationPath: String)


    /**
     * Writes inputStream to file
     * Writes data to file
     *
     * @param inputStream
     * @param file
     */
    fun copyInputStreamToFile(inputStream: InputStream, file: File)

    fun renameFile(existingFilePath: String, newFileName: String)

    fun zipFiles(filesPath: String, zipFilePath: String)

    fun unZipFile(zipFilePath: String, extractLocationPath: String)
}
