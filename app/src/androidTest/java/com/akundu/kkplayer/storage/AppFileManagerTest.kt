package com.akundu.kkplayer.storage

import android.content.Context
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.akundu.kkplayer.BuildConfig
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File
import java.io.FileOutputStream


@RunWith(AndroidJUnit4::class)
class AppFileManagerTest {

    private val appContext: Context = InstrumentationRegistry.getInstrumentation().targetContext
    private lateinit var fileManager: AppFileManager

    /**
     * **Prerequisites for this tests**
     *
     *  - Fine the error logs in case of failing
     */
    @Before
    fun setUp() {
        fileManager = AppFileManager()
        val textFile = fileManager.createFile(appContext, FileLocationCategory.MEDIA_DIRECTORY, "testFile", "txt")
        FileOutputStream(textFile.path).use {
            it.write("Arnab".toByteArray())
        }

        val yourPackage = BuildConfig.APPLICATION_ID
        val sampleImageFile = File("/storage/emulated/0/Android/media/${BuildConfig.APPLICATION_ID}/testImage.jpeg")
        if (!sampleImageFile.exists()) {
            throw java.lang.RuntimeException("Copy `testImage.jpeg` file into device Android/media/$yourPackage/ directory")
        }

        val sampleSongFile = File(
            "/storage/emulated/0/Android/media/${BuildConfig.APPLICATION_ID}/" +
                    "Tu Hi Meri Shab Hai (Gangster) - K.K - 320Kbps.mp3"
        )
        if (!sampleSongFile.exists()) {
            throw java.lang.RuntimeException("Download `Tu Hi Meri Shab Hai (Gangster)` song using kkPlayer before test")
        }

        val zipFile = File("/storage/emulated/0/Android/media/${BuildConfig.APPLICATION_ID}/10mb.zip")
        if (!zipFile.exists()) {
            throw java.lang.RuntimeException("Copy `sampledata/10mb.zip` file into device Android/media/$yourPackage/ directory")
        }
    }


    @Test
    fun testCreateFileInCacheDirectorySuccessful() {
        val file: File = fileManager.createFile(appContext, FileLocationCategory.CACHE_DIRECTORY, "createFileTested", "txt")
        assertTrue(file.exists())
    }

    @Test
    fun testCreateFileInDataDirectorySuccessful() {
        val file: File = fileManager.createFile(appContext, FileLocationCategory.DATA_DIRECTORY, "createFileTested", "txt")
        assertTrue(file.exists())
    }

    @Test
    fun testCreateFileInFilesDirectorySuccessful() {
        val file: File = fileManager.createFile(appContext, FileLocationCategory.FILES_DIRECTORY, "createFileTested", "txt")
        assertTrue(file.exists())
    }

    @Test
    fun testCreateFileInExternalCacheDirectorySuccessful() {
        val file: File = fileManager.createFile(appContext, FileLocationCategory.EXTERNAL_CACHE_DIRECTORY, "createFileTested", "txt")
        assertTrue(file.exists())
    }

    @Test
    fun testCreateFileInExternalFilesDirectorySuccessful() {
        val file: File = fileManager.createFile(appContext, FileLocationCategory.EXTERNAL_FILES_DIRECTORY, "createFileTested", "txt")
        assertTrue(file.exists())
    }

    @Test
    fun testCreateFileInMediaDirectorySuccessful() {
        val file: File = fileManager.createFile(appContext, FileLocationCategory.MEDIA_DIRECTORY, "createFileTested", "txt")
        assertTrue(file.exists())
    }

    @Test
    fun testCreateFileInObbDirectorySuccessful() {
        val file: File = fileManager.createFile(appContext, FileLocationCategory.OBB_DIRECTORY, "createFileTested", "txt")
        assertTrue(file.exists())
    }

    @Test
    fun testCreateFileInDownloadDirectorySuccessful() {
        val file: File = fileManager.createFile(appContext, FileLocationCategory.DOWNLOADS_DIRECTORY, "createFileTested", "txt")
        assertTrue(file.exists())
    }


    @Test
    fun testCopyFileSuccessful() {

        var isCopySuccessful = fileManager.copyFile(
            sourcePath = "/storage/emulated/0/Android/media/${BuildConfig.APPLICATION_ID}/Tu Hi Meri Shab Hai (Gangster) - K.K - 320Kbps.mp3",
            destinationPath = appContext.obbDir.path + "/Tu Hi Meri Shab Hai (Gangster) - K.K - 320Kbps.mp3"
        )
        assertTrue(isCopySuccessful)

        isCopySuccessful = fileManager.copyFile(
            sourcePath = "/storage/emulated/0/Android/media/${BuildConfig.APPLICATION_ID}/Tu Hi Meri Shab Hai (Gangster) - K.K - 320Kbps.mp3",
            destinationPath = appContext.filesDir.path + "/Tu Hi Meri Shab Hai (Gangster) - K.K - 320Kbps.mp3"
        )
        assertTrue(isCopySuccessful)

        isCopySuccessful = fileManager.copyFile(
            sourcePath = "/storage/emulated/0/Android/media/${BuildConfig.APPLICATION_ID}/Tu Hi Meri Shab Hai (Gangster) - K.K - 320Kbps.mp3",
            destinationPath = appContext.dataDir.path + "/Tu Hi Meri Shab Hai (Gangster) - K.K - 320Kbps.mp3"
        )
        assertTrue(isCopySuccessful)
    }

    @Test
    fun testCopyFileToObbDirSuccessful() {

        val isCopySuccessful = fileManager.copyFile(
            sourcePath = "/storage/emulated/0/Android/media/${BuildConfig.APPLICATION_ID}/testFile.txt",
            destinationPath = appContext.obbDir.path + "/testFile.txt"
        )
        assertTrue(isCopySuccessful)
    }

    @Test
    fun testCopyFileToFilesDirSuccessful() {

        val isCopySuccessful = fileManager.copyFile(
            sourcePath = "/storage/emulated/0/Android/media/${BuildConfig.APPLICATION_ID}/testFile.txt",
            destinationPath = appContext.filesDir.path + "/testFile.txt"
        )
        assertTrue(isCopySuccessful)
    }

    @Test
    fun testCopyFileToDataDirSuccessful() {

        val isCopySuccessful = fileManager.copyFile(
            sourcePath = "/storage/emulated/0/Android/media/${BuildConfig.APPLICATION_ID}/testFile.txt",
            destinationPath = appContext.dataDir.path + "/testFile.txt"
        )
        assertTrue(isCopySuccessful)
    }

    @Test
    fun testCopyFileToCacheDirSuccessful() {

        val isCopySuccessful = fileManager.copyFile(
            sourcePath = "/storage/emulated/0/Android/media/${BuildConfig.APPLICATION_ID}/testFile.txt",
            destinationPath = appContext.cacheDir.path + "/testFile.txt"
        )
        assertTrue(isCopySuccessful)
    }

    @Test
    fun testCopyFileToExternalCacheDirSuccessful() {

        val isCopySuccessful = fileManager.copyFile(
            sourcePath = "/storage/emulated/0/Android/media/${BuildConfig.APPLICATION_ID}/testFile.txt",
            destinationPath = appContext.externalCacheDir?.path + "/testFile.txt"
        )
        assertTrue(isCopySuccessful)
    }

    @Test
    fun testCopyFileToExternalFilesDirSuccessful() {

        val isCopySuccessful = fileManager.copyFile(
            sourcePath = "/storage/emulated/0/Android/media/${BuildConfig.APPLICATION_ID}/testFile.txt",
            destinationPath = appContext.getExternalFilesDir(null)?.path + "/testFile.txt"
        )
        assertTrue(isCopySuccessful)
    }

    @Test
    fun testCopyFilePublicMediaDirectorySuccessful() {

        val isCopySuccessful = fileManager.copyFile(
            sourcePath = "/storage/emulated/0/Android/media/${BuildConfig.APPLICATION_ID}/Tu Hi Meri Shab Hai (Gangster) - K.K - 320Kbps.mp3",
            destinationPath = "${Constants.MUSIC_PATH}Tu Hi Meri Shab Hai (Gangster) - K.K - 320Kbps.mp3"
        )
        assertTrue(isCopySuccessful)
    }

    @Test
    fun testCopyFileToAnyRandomDirectoryFailed() {
        val fileManager = AppFileManager()
        val isCopySuccessful = fileManager.copyFile(
            sourcePath = "/storage/emulated/0/Android/media/${BuildConfig.APPLICATION_ID}/Tu Hi Meri Shab Hai (Gangster) - K.K - 320Kbps.mp3",
            destinationPath = "/storage/emulated/0/Android/media/com.example.file/Tu Hi Meri Shab Hai (Gangster) - K.K - 320Kbps.mp3"
        )
        assertFalse(isCopySuccessful)
    }

    @Test
    fun testRenameFileSuccessful() {
        val fileManager = AppFileManager()
        val file = fileManager.createFile(appContext, FileLocationCategory.MEDIA_DIRECTORY, "before_renamed_file", "txt")
        FileOutputStream(file.path).use {
            it.write("Arnab".toByteArray())
        }

        val renamedFile = fileManager.renameFile(
            context = appContext,
            existingFilePath = "/storage/emulated/0/Android/media/${BuildConfig.APPLICATION_ID}/before_renamed_file.txt",
            newFileName = "after_renamed_file.txt"
        )
        val isRenameSuccessful = renamedFile.exists()
        assertTrue(isRenameSuccessful)

        val oldFile = File("/storage/emulated/0/Android/media/${BuildConfig.APPLICATION_ID}/before_renamed_file.txt")
        val isOldFileExist = oldFile.exists()
        assertFalse(isOldFileExist)
    }

    @Test
    fun testZipFileSuccessful() {
        val fileManager = AppFileManager()
        // TODO still have issue in zipFiles() function
        val testOutputFile = fileManager.zipFiles(
            srcFolderPath = "/storage/emulated/0/Android/media/${BuildConfig.APPLICATION_ID}",
            destZipFilePath = "/storage/emulated/0/Android/media/${BuildConfig.APPLICATION_ID}/myTest.zip"
        )
        assertTrue(testOutputFile.exists())
        println("Zip file size: ${testOutputFile.length() / 1024F} kb")
        assertTrue(testOutputFile.length() > 0)
        testOutputFile.delete()                                         // clear generated test file
    }

    @Test
    fun testZipListOfFilesSuccessful() {
        val fileManager = AppFileManager()
        val inputPath = "/storage/emulated/0/Android/media/${BuildConfig.APPLICATION_ID}"

        val listOfFiles = arrayListOf<String>()
        listOfFiles.add("$inputPath/testFile.txt")
        listOfFiles.add("$inputPath/testImage.jpeg")
        listOfFiles.add("$inputPath/big_buck_bunny_240p_10mb.mp4")

        // calling the zip function
        fileManager.zipListOfFiles(listOfFiles, "$inputPath${File.separator}zipFileName.zip")

        val testFile = File("$inputPath${File.separator}zipFileName.zip")
        assertTrue(testFile.exists())
        println("Zip file size: ${testFile.length() / 1024F} kb")
        assertTrue(testFile.length() > 0)
        //testFile.delete()                                               // clear generated test file
    }

    @Test
    fun testUnzipSuccessful() {
        val fileManager = AppFileManager()
        val startingTime = System.currentTimeMillis()
        fileManager.unZipFileSlowly(
            zipFilePath = "/storage/emulated/0/Android/media/${BuildConfig.APPLICATION_ID}/10mb.zip",
            extractLocationPath = "/storage/emulated/0/Android/media/${BuildConfig.APPLICATION_ID}/"
        )
        val endTime = System.currentTimeMillis()
        val unZippingTimeInSeconds = (endTime - startingTime) / 1000
        println("Time taken to unzip: $unZippingTimeInSeconds seconds")
        assertTrue("Failed to complete Unzip in 1 minute", unZippingTimeInSeconds < 60)
        val generatedTestFile = File(
            "/storage/emulated/0/Android/media/${BuildConfig.APPLICATION_ID}/big_buck_bunny_240p_10mb.mp4"
        )
        assertTrue(generatedTestFile.exists())
        assertTrue(generatedTestFile.length() > 0)
        //generatedTestFile.delete()                                      // clear generated test file
    }

    @Test
    fun testUnzipFileSuccessful() {
        val fileManager = AppFileManager()
        val startingTime = System.currentTimeMillis()
        fileManager.unZipFile(
            zipFilePath = "/storage/emulated/0/Android/media/${BuildConfig.APPLICATION_ID}/10mb.zip",
            extractLocationPath = "/storage/emulated/0/Android/media/${BuildConfig.APPLICATION_ID}/"
        )
        val endTime = System.currentTimeMillis()
        val unZippingTimeInMilliSeconds = (endTime - startingTime)
        println("Time taken to unzip: $unZippingTimeInMilliSeconds milli seconds")
        assertTrue("Failed to complete Unzip in 1 second", unZippingTimeInMilliSeconds < 1000)
        val generatedTestFile = File(
            "/storage/emulated/0/Android/media/${BuildConfig.APPLICATION_ID}/big_buck_bunny_240p_10mb.mp4"
        )
        assertTrue(generatedTestFile.exists())
        assertTrue(generatedTestFile.length() > 0)
        //generatedTestFile.delete()                                      // clear generated test file
    }

    @Test
    fun testEncryptFileSuccessful() {
        val fileManager = AppFileManager()
        val startingTime = System.currentTimeMillis()
        val generatedTestFile: File? = fileManager.encryptFile(
            context = appContext,
            srcFilePath = "/storage/emulated/0/Android/media/${BuildConfig.APPLICATION_ID}/big_buck_bunny_240p_10mb.mp4",
            encryptedFileName = "encrypted-TestFile"
        )
        val endTime = System.currentTimeMillis()
        val encryptionTimeInMilliSeconds = (endTime - startingTime)
        println("Time taken to encrypt: $encryptionTimeInMilliSeconds milli seconds")
        assertTrue("Failed to complete encryption in 1 second", encryptionTimeInMilliSeconds < 1000)
        assertTrue("Encrypted file not found", generatedTestFile.let { it?.exists() } ?: false)
        assertTrue("No data available in encrypted file", generatedTestFile?.length()?.let { it > 0 } ?: false)
        //generatedTestFile.delete()                                        // clear generated test file
    }

    @Test
    fun testDecryptFileSuccessful() {
        val fileManager = AppFileManager()
        val startingTime = System.currentTimeMillis()
        val generatedTestFile: File? = fileManager.decryptFile(
            context = appContext,
            encryptedFilePath = "/storage/emulated/0/Android/media/${BuildConfig.APPLICATION_ID}/encrypted-TestFile.enc",
            outputFileName = "decrypted-${System.currentTimeMillis()}.mp4"
        )
        val endTime = System.currentTimeMillis()
        val decryptionTimeInMilliSeconds = (endTime - startingTime)
        println("Time taken to decrypt: $decryptionTimeInMilliSeconds milli seconds")
        assertTrue("Failed to complete decryption in 1 second", decryptionTimeInMilliSeconds < 1000)
        assertTrue("Decrypted file not found", generatedTestFile.let { it?.exists() } ?: false)
        assertTrue("No data available in decrypted file", generatedTestFile?.length()?.let { it > 0 } ?: false)
        generatedTestFile?.delete()                                     // clear generated test file
    }
}