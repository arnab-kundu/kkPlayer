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

@RunWith(AndroidJUnit4::class)
class AppFileManagerTest {

    private val appContext: Context = InstrumentationRegistry.getInstrumentation().targetContext
    private lateinit var fileManager: AppFileManager

    @Before
    fun setUp() {
        fileManager = AppFileManager()
        fileManager.createFile(appContext, "/storage/emulated/0/Android/media/${BuildConfig.APPLICATION_ID}/", "testFile", "txt")
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
}