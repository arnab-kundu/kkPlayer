package com.akundu.kkplayer.storage

import android.content.Context
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File

/**
 * Exercises the parts of [AppFileManager] that depend on a real device: scoped storage
 * directories handed out by the framework. The behavioural coverage lives in the JVM test of
 * the same name, which runs against a temporary directory instead.
 */
@RunWith(AndroidJUnit4::class)
class AppFileManagerTest {
    private lateinit var context: Context
    private lateinit var fileManager: AppFileManager
    private val created = mutableListOf<File>()

    @Before
    fun setUp() {
        context = InstrumentationRegistry.getInstrumentation().targetContext
        fileManager = AppFileManager()
    }

    private fun createAndTrack(
        category: FileLocationCategory,
        name: String,
    ): File =
        fileManager.createFile(context, category, name, "txt").also {
            created += it
            it.deleteOnExit()
        }

    @Test
    fun createsFilesInTheAppsPrivateDirectories() {
        listOf(
            FileLocationCategory.CACHE_DIRECTORY,
            FileLocationCategory.DATA_DIRECTORY,
            FileLocationCategory.FILES_DIRECTORY,
            FileLocationCategory.OBB_DIRECTORY,
        ).forEach { category ->
            val file = createAndTrack(category, "instrumented_${category.name}")

            assertTrue("Failed to create a file in $category", file.exists())
        }
    }

    @Test
    fun createsFilesInTheAppsExternalDirectories() {
        listOf(
            FileLocationCategory.EXTERNAL_CACHE_DIRECTORY,
            FileLocationCategory.EXTERNAL_FILES_DIRECTORY,
            FileLocationCategory.MEDIA_DIRECTORY,
        ).forEach { category ->
            val file = createAndTrack(category, "instrumented_${category.name}")

            assertTrue("Failed to create a file in $category", file.exists())
        }
    }

    @Test
    fun copiesAFileBetweenScopedDirectories() {
        val source = createAndTrack(FileLocationCategory.CACHE_DIRECTORY, "instrumented_copy_source")
        source.writeText("kkPlayer")
        val destination = File(context.filesDir, "instrumented_copy_target.txt").also { created += it }

        val copied = fileManager.copyFile(source.path, destination.path)

        assertTrue(copied)
        assertEquals("kkPlayer", destination.readText())
    }

    @Test
    fun encryptsAndDecryptsThroughScopedStorage() {
        val source = createAndTrack(FileLocationCategory.CACHE_DIRECTORY, "instrumented_secret")
        source.writeText("top secret payload")

        val encrypted = fileManager.encryptFile(context, source.path, "instrumented_encrypted")
        val decrypted = fileManager.decryptFile(context, encrypted!!.path, "instrumented_decrypted.txt")

        assertEquals("top secret payload", decrypted!!.readText())
        encrypted.delete()
        decrypted.delete()
    }

    @Test
    fun zipsAndUnzipsThroughScopedStorage() {
        val source = createAndTrack(FileLocationCategory.CACHE_DIRECTORY, "instrumented_zip_source")
        source.writeText("zip me")
        val zip = File(context.cacheDir, "instrumented.zip").also { created += it }

        fileManager.zipListOfFiles(arrayListOf(source.path), zip.path)
        val extractTo = File(context.cacheDir, "instrumented_extracted").apply { mkdirs() }
        fileManager.unZipFile(zip.path, extractTo.path)

        assertEquals("zip me", File(extractTo, source.name).readText())
        extractTo.deleteRecursively()
    }
}
