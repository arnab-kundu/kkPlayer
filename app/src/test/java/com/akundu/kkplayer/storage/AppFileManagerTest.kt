package com.akundu.kkplayer.storage

import android.content.Context
import com.akundu.kkplayer.BuildConfig
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import org.junit.runner.RunWith
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.io.ByteArrayInputStream
import java.io.File

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33])
class AppFileManagerTest {
    @get:Rule
    val temporaryFolder = TemporaryFolder()

    private lateinit var context: Context
    private lateinit var fileManager: AppFileManager

    private lateinit var cacheDir: File
    private lateinit var dataDir: File
    private lateinit var filesDir: File
    private lateinit var externalCacheDir: File
    private lateinit var externalFilesDir: File
    private lateinit var obbDir: File
    private lateinit var externalStorageRoot: File

    @Before
    fun setUp() {
        cacheDir = temporaryFolder.newFolder("cache")
        dataDir = temporaryFolder.newFolder("data")
        filesDir = temporaryFolder.newFolder("files")
        externalCacheDir = temporaryFolder.newFolder("externalCache")
        externalFilesDir = temporaryFolder.newFolder("externalFiles")
        obbDir = temporaryFolder.newFolder("obb")
        externalStorageRoot = temporaryFolder.newFolder("emulated")
        File(externalStorageRoot, "Download").mkdirs()

        context = mock()
        whenever(context.cacheDir).thenReturn(cacheDir)
        whenever(context.dataDir).thenReturn(dataDir)
        whenever(context.filesDir).thenReturn(filesDir)
        whenever(context.externalCacheDir).thenReturn(externalCacheDir)
        whenever(context.getExternalFilesDir(null)).thenReturn(externalFilesDir)
        whenever(context.obbDir).thenReturn(obbDir)

        fileManager = AppFileManager(externalStorageRoot = externalStorageRoot)
    }

    private fun sourceFile(
        name: String = "source.txt",
        content: String = "Arnab",
    ): File = temporaryFolder.newFile(name).apply { writeText(content) }

    // AppFileManager splits paths on '/' literals, so tests must hand it Android-style
    // paths rather than the host's native separator.
    private val File.androidPath: String
        get() = path.replace(File.separatorChar, '/')

    @Test
    fun `creates a file in the cache directory`() {
        val file = fileManager.createFile(context, FileLocationCategory.CACHE_DIRECTORY, "created", "txt")

        assertTrue(file.exists())
        assertEquals(File(cacheDir, "created.txt"), file)
    }

    @Test
    fun `creates a file in the data directory`() {
        val file = fileManager.createFile(context, FileLocationCategory.DATA_DIRECTORY, "created", "txt")

        assertTrue(file.exists())
        assertEquals(dataDir, file.parentFile)
    }

    @Test
    fun `creates a file in the files directory`() {
        val file = fileManager.createFile(context, FileLocationCategory.FILES_DIRECTORY, "created", "txt")

        assertTrue(file.exists())
        assertEquals(filesDir, file.parentFile)
    }

    @Test
    fun `creates a file in the external cache directory`() {
        val file = fileManager.createFile(context, FileLocationCategory.EXTERNAL_CACHE_DIRECTORY, "created", "txt")

        assertTrue(file.exists())
        assertEquals(externalCacheDir, file.parentFile)
    }

    @Test
    fun `creates a file in the external files directory`() {
        val file = fileManager.createFile(context, FileLocationCategory.EXTERNAL_FILES_DIRECTORY, "created", "txt")

        assertTrue(file.exists())
        assertEquals(externalFilesDir, file.parentFile)
    }

    @Test
    fun `creates a file in the obb directory`() {
        val file = fileManager.createFile(context, FileLocationCategory.OBB_DIRECTORY, "created", "txt")

        assertTrue(file.exists())
        assertEquals(obbDir, file.parentFile)
    }

    @Test
    fun `creates a file in the scoped media directory`() {
        val file = fileManager.createFile(context, FileLocationCategory.MEDIA_DIRECTORY, "created", "txt")

        assertTrue(file.exists())
        assertEquals(File(externalStorageRoot, "Android/media/${BuildConfig.APPLICATION_ID}"), file.parentFile)
    }

    @Test
    fun `creates a file in the public downloads directory`() {
        val file = fileManager.createFile(context, FileLocationCategory.DOWNLOADS_DIRECTORY, "created", "txt")

        assertTrue(file.exists())
        assertEquals(File(externalStorageRoot, "Download"), file.parentFile)
    }

    @Test
    fun `creates a file without an extension when none is given`() {
        val file = fileManager.createFile(context, FileLocationCategory.CACHE_DIRECTORY, "no_extension", null)

        assertTrue(file.exists())
        assertEquals("no_extension", file.name)
    }

    @Test
    fun `creating an existing file keeps its contents`() {
        val existing = File(cacheDir, "existing.txt").apply { writeText("keep me") }

        val file = fileManager.createFile(context, FileLocationCategory.CACHE_DIRECTORY, "existing", "txt")

        assertEquals("keep me", file.readText())
        assertEquals(existing, file)
    }

    @Test
    fun `createFolder creates a nested directory`() {
        val created = fileManager.createFolder("nested", cacheDir.path)

        assertTrue(created)
        assertTrue(File(cacheDir, "nested").isDirectory)
    }

    @Test
    fun `createFolder returns false when the directory already exists`() {
        fileManager.createFolder("nested", cacheDir.path)

        assertFalse(fileManager.createFolder("nested", cacheDir.path))
    }

    @Test
    fun `createAppsInternalPrivateStoragePath builds the whole path`() {
        val folder = fileManager.createAppsInternalPrivateStoragePath("media/com.example.app")

        assertTrue(folder!!.isDirectory)
        assertEquals(File(externalStorageRoot, "Android/media/com.example.app"), folder)
    }

    @Test
    fun `copyFile duplicates content to the destination`() {
        val source = sourceFile(content = "kkPlayer")
        val destination = File(cacheDir, "copied.txt")

        val copied = fileManager.copyFile(source.androidPath, destination.androidPath)

        assertTrue(copied)
        assertEquals("kkPlayer", destination.readText())
    }

    // The source stream is opened outside copyFile's try block, so a missing source escapes
    // as an exception instead of the documented false return value.
    @Test(expected = java.io.FileNotFoundException::class)
    fun `copyFile throws when the source is missing`() {
        fileManager.copyFile(
            sourcePath = File(cacheDir, "missing.txt").androidPath,
            destinationPath = File(cacheDir, "target.txt").androidPath,
        )
    }

    @Test
    fun `copyFile returns false when the destination directory does not exist`() {
        val source = sourceFile()

        val copied = fileManager.copyFile(source.androidPath, File(cacheDir, "missing_dir/target.txt").androidPath)

        assertFalse(copied)
    }

    @Test
    fun `saveFile writes an input stream to disk`() {
        val destination = File(cacheDir, "saved.txt")

        val saved = fileManager.saveFile(ByteArrayInputStream("stream".toByteArray()), destination.androidPath)

        assertTrue(saved)
        assertEquals("stream", destination.readText())
    }

    @Test
    fun `saveFile creates the destination directory when needed`() {
        val destination = File(cacheDir, "created_dir/saved.txt")

        val saved = fileManager.saveFile(ByteArrayInputStream("stream".toByteArray()), destination.androidPath)

        assertTrue(saved)
        assertTrue(destination.exists())
    }

    @Test
    fun `deleteFile removes an existing file`() {
        val file = sourceFile("deletable.txt")

        assertTrue(fileManager.deleteFile(file.androidPath))
        assertFalse(file.exists())
    }

    @Test
    fun `deleteFile returns false for a missing file`() {
        assertFalse(fileManager.deleteFile(File(cacheDir, "ghost.txt").androidPath))
    }

    @Test
    fun `moveFile copies to the destination and removes the source`() {
        val source = sourceFile(content = "moving")
        val destination = File(cacheDir, "moved.txt")

        fileManager.moveFile(source.androidPath, destination.androidPath)

        assertFalse(source.exists())
        assertEquals("moving", destination.readText())
    }

    @Test
    fun `copyInputStreamToFile overwrites the target file`() {
        val target = File(cacheDir, "target.txt").apply { writeText("old content that is long") }

        fileManager.copyInputStreamToFile(ByteArrayInputStream("new".toByteArray()), target)

        assertEquals("new", target.readText())
    }

    // The original file is only removed on platforms that allow deleting an open file:
    // copyInputStreamToFile never closes the source stream, so the handle is still held here.
    @Test
    fun `renameFile writes the content into the external files directory`() {
        val existing = sourceFile("before.txt", "renamed content")

        val renamed = fileManager.renameFile(context, existing.androidPath, "after.txt")

        assertTrue(renamed.exists())
        assertEquals("renamed content", renamed.readText())
        assertEquals(externalFilesDir, renamed.parentFile)
        assertEquals("after.txt", renamed.name)
    }

    @Test
    fun `deleteFolder removes files directly inside the directory`() {
        val folder = temporaryFolder.newFolder("deletable")
        val file = File(folder, "child.txt").apply { writeText("child") }

        fileManager.deleteFolder(folder)

        assertFalse(file.exists())
    }

    @Test
    fun `deleteFolder leaves subdirectories untouched`() {
        val folder = temporaryFolder.newFolder("partly_deletable")
        val subFolder = File(folder, "sub").apply { mkdirs() }
        val nested = File(subFolder, "nested.txt").apply { writeText("nested") }

        fileManager.deleteFolder(folder)

        assertTrue(nested.exists())
    }

    @Test
    fun `zipListOfFiles then unZipFile round trips the content`() {
        val first = sourceFile("first.txt", "first content")
        val second = sourceFile("second.txt", "second content")
        val zip = File(cacheDir, "bundle.zip")

        fileManager.zipListOfFiles(arrayListOf(first.androidPath, second.androidPath), zip.androidPath)
        assertTrue(zip.exists())
        assertTrue(zip.length() > 0)

        val extractTo = temporaryFolder.newFolder("extracted")
        fileManager.unZipFile(zip.androidPath, extractTo.androidPath)

        assertEquals("first content", File(extractTo, "first.txt").readText())
        assertEquals("second content", File(extractTo, "second.txt").readText())
    }

    @Test
    fun `zipFiles archives an entire folder`() {
        val folder = temporaryFolder.newFolder("to_zip")
        File(folder, "inside.txt").writeText("inside content")
        val zip = File(cacheDir, "folder.zip")

        val result = fileManager.zipFiles(folder.androidPath, zip.androidPath)

        assertTrue(result.exists())
        assertTrue(result.length() > 0)
    }

    @Test
    fun `unZipFileSlowly extracts the archive content`() {
        val source = sourceFile("slow.txt", "slow content")
        val zip = File(cacheDir, "slow.zip")
        fileManager.zipListOfFiles(arrayListOf(source.androidPath), zip.androidPath)
        val extractTo = temporaryFolder.newFolder("slow_extracted")

        @Suppress("DEPRECATION")
        fileManager.unZipFileSlowly(zip.androidPath, extractTo.androidPath + "/")

        assertEquals("slow content", File(extractTo, "slow.txt").readText())
    }

    @Test
    fun `unZipFile swallows errors for a missing archive`() {
        fileManager.unZipFile(File(cacheDir, "missing.zip").androidPath, cacheDir.androidPath)
    }

    @Test
    fun `zipListOfFiles swallows errors for missing sources`() {
        fileManager.zipListOfFiles(
            arrayListOf(File(cacheDir, "missing.txt").androidPath),
            File(cacheDir, "broken.zip").androidPath,
        )
    }

    @Test
    fun `encryptFile then decryptFile round trips the content`() {
        val plain = sourceFile("plain.txt", "top secret payload")

        val encrypted = fileManager.encryptFile(context, plain.androidPath, "secret")

        assertTrue(encrypted!!.exists())
        assertTrue(encrypted.length() > 0)
        assertFalse(encrypted.readText() == "top secret payload")

        val decrypted = fileManager.decryptFile(context, encrypted.androidPath, "decrypted.txt")

        assertEquals("top secret payload", decrypted!!.readText())
    }

    @Test
    fun `encryptFile writes into the scoped media directory`() {
        val plain = sourceFile("scoped.txt", "payload")

        val encrypted = fileManager.encryptFile(context, plain.androidPath, "scoped-secret")

        assertEquals(File(externalStorageRoot, "Android/media/${BuildConfig.APPLICATION_ID}"), encrypted!!.parentFile)
        assertEquals("scoped-secret.enc", encrypted.name)
    }

    // Decrypting non-encrypted input produces a garbage file rather than failing:
    // the cipher stream reports no error for input it cannot meaningfully decrypt.
    @Test
    fun `decryptFile produces a file that does not match the original for invalid input`() {
        val notEncrypted = sourceFile("garbage.enc", "this is not encrypted at all")

        val decrypted = fileManager.decryptFile(context, notEncrypted.androidPath, "output.txt")

        assertTrue(decrypted!!.exists())
        assertFalse("this is not encrypted at all" == decrypted.readText())
    }

    @Test
    fun `encryptFile returns null when the source is missing`() {
        val encrypted = fileManager.encryptFile(context, File(cacheDir, "missing.txt").androidPath, "secret")

        assertEquals(null, encrypted)
    }
}
