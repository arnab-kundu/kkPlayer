package com.akundu.kkplayer.storage

import android.content.Context
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.mock
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33])
class FileLocationCategoryTest {
    private lateinit var context: Context
    private lateinit var fileManager: AppFileManager

    @Before
    fun setUp() {
        context = mock()
        fileManager = AppFileManager()
    }

    @Test
    fun `declares every storage location the app writes to`() {
        assertEquals(
            listOf(
                "CACHE_DIRECTORY",
                "DATA_DIRECTORY",
                "FILES_DIRECTORY",
                "EXTERNAL_CACHE_DIRECTORY",
                "EXTERNAL_FILES_DIRECTORY",
                "MEDIA_DIRECTORY",
                "OBB_DIRECTORY",
                "DOWNLOADS_DIRECTORY",
                "DOCUMENT_DIRECTORY",
                "MUSIC_DIRECTORY",
                "PICTURES_DIRECTORY",
                "VIDEOS_DIRECTORY",
            ),
            FileLocationCategory.entries.map { it.name },
        )
    }

    @Test
    fun `every category can be looked up by name`() {
        FileLocationCategory.entries.forEach { category ->
            assertEquals(category, FileLocationCategory.valueOf(category.name))
        }
    }

    // The four categories below are declared but left as TODO() in AppFileManager.createFile.
    // The tests pin that down so an unimplemented location fails loudly rather than silently
    // writing somewhere unexpected once one of them is wired up.

    @Test(expected = NotImplementedError::class)
    fun `the document directory is not implemented yet`() {
        fileManager.createFile(context, FileLocationCategory.DOCUMENT_DIRECTORY, "song", "mp3")
    }

    @Test(expected = NotImplementedError::class)
    fun `the music directory is not implemented yet`() {
        fileManager.createFile(context, FileLocationCategory.MUSIC_DIRECTORY, "song", "mp3")
    }

    @Test(expected = NotImplementedError::class)
    fun `the pictures directory is not implemented yet`() {
        fileManager.createFile(context, FileLocationCategory.PICTURES_DIRECTORY, "art", "png")
    }

    @Test(expected = NotImplementedError::class)
    fun `the videos directory is not implemented yet`() {
        fileManager.createFile(context, FileLocationCategory.VIDEOS_DIRECTORY, "clip", "mp4")
    }
}
