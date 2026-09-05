package com.akundu.kkplayer.media

import android.content.Context
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
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
class FolderFilesTest {
    @get:Rule
    val temporaryFolder = TemporaryFolder()

    private lateinit var externalFilesDir: File
    private lateinit var context: Context

    @Before
    fun setUp() {
        externalFilesDir = temporaryFolder.newFolder("externalFiles")
        context = mock()
        whenever(context.getExternalFilesDir("kkPlayer")).thenReturn(externalFilesDir)
        whenever(context.getExternalFilesDir(null)).thenReturn(externalFilesDir)
    }

    @Test
    fun `creates a folder under the apps external directory`() {
        val path = FolderFiles.createFolder(context, "logs")

        assertEquals(File(externalFilesDir, "logs").absolutePath, path)
        assertTrue(File(externalFilesDir, "logs").isDirectory)
    }

    @Test
    fun `creating an existing folder is a no-op`() {
        FolderFiles.createFolder(context, "logs")

        val path = FolderFiles.createFolder(context, "logs")

        assertTrue(File(path).isDirectory)
    }

    @Test
    fun `creates a file with an extension at a path`() {
        val file = FolderFiles.createFileAtPath(context, externalFilesDir.path, "song", "mp3")

        assertTrue(file.exists())
        assertEquals("song.mp3", file.name)
    }

    @Test
    fun `creates a file without an extension at a path`() {
        val file = FolderFiles.createFileAtPath(context, externalFilesDir.path, "no_extension")

        assertTrue(file.exists())
        assertEquals("no_extension", file.name)
    }

    @Test
    fun `creates a file inside a named folder`() {
        FolderFiles.createFolder(context, "songs")

        val file = FolderFiles.createFile(context, "songs", "track", "mp3")

        assertTrue(file.exists())
        assertEquals(File(externalFilesDir, "songs").absolutePath, file.parentFile?.absolutePath)
    }

    @Test
    fun `creates a text file`() {
        FolderFiles.createFolder(context, "notes")

        val file = FolderFiles.createTextFile(context, "notes", "readme")

        assertTrue(file.exists())
        assertEquals("readme.txt", file.name)
    }

    @Test
    fun `creates a log file with a log prefix`() {
        FolderFiles.createFolder(context, "logs")

        val file = FolderFiles.createLogFile(context, "logs", "2026-09-05")

        assertTrue(file.exists())
        assertEquals("log-2026-09-05.txt", file.name)
    }

    @Test
    fun `deletes an existing file`() {
        FolderFiles.createFolder(context, "songs")
        FolderFiles.createFile(context, "songs", "removable", "mp3")

        assertTrue(FolderFiles.deleteFile(context, "songs", "removable", ".mp3"))
    }

    @Test
    fun `deleting a missing file reports failure`() {
        assertFalse(FolderFiles.deleteFile(context, "songs", "ghost", ".mp3"))
    }

    @Test
    fun `copies an input stream into a file`() {
        val target = File(externalFilesDir, "copied.txt").apply { createNewFile() }

        FolderFiles.copyInputStreamToFile(ByteArrayInputStream("streamed".toByteArray()), target)

        assertEquals("streamed", target.readText())
    }

    @Test
    fun `writes a response body to disk`() {
        val body = "downloaded-bytes".toResponseBody("image/png".toMediaType())

        val written = FolderFiles.writeResponseBodyToDisk(body, context)

        assertTrue(written)
        assertEquals("downloaded-bytes", File(externalFilesDir, "Future Studio Icon.png").readText())
    }
}
