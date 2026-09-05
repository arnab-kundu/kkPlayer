package com.akundu.kkplayer.media

import android.content.Context
import android.content.ContextWrapper
import androidx.core.graphics.createBitmap
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config
import java.io.File

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33])
class MediaMetaDataRetrieverTest {
    @get:Rule
    val temporaryFolder = TemporaryFolder()

    private lateinit var cacheRoot: File
    private lateinit var context: Context

    // The fallback path resolves a drawable, so this needs a real resource-backed context
    // rather than a mock; only the cache location is redirected into the temporary folder.
    @Before
    fun setUp() {
        cacheRoot = temporaryFolder.newFolder("externalCache")
        context =
            object : ContextWrapper(RuntimeEnvironment.getApplication()) {
                override fun getExternalCacheDir(): File = cacheRoot
            }
    }

    private fun thumbnailFile(fileName: String) = File(cacheRoot, "thumbnails/${fileName.replace(".mp3", ".jpg")}")

    @Test
    fun `saves a thumbnail into the cache`() {
        val saved = MediaMetaDataRetriever.hasSavedMediaThumbnailsInCache(context, "song.mp3", createBitmap(32, 32))

        assertTrue(saved)
        assertTrue(thumbnailFile("song.mp3").exists())
        assertTrue(thumbnailFile("song.mp3").length() > 0)
    }

    @Test
    fun `refuses to save a thumbnail without a file name`() {
        assertFalse(MediaMetaDataRetriever.hasSavedMediaThumbnailsInCache(context, "", createBitmap(32, 32)))
    }

    @Test
    fun `refuses to save a missing bitmap`() {
        assertFalse(MediaMetaDataRetriever.hasSavedMediaThumbnailsInCache(context, "song.mp3", null))
    }

    @Test
    fun `stores the thumbnail as a jpg next to the song name`() {
        MediaMetaDataRetriever.hasSavedMediaThumbnailsInCache(context, "Om Deva deva.mp3", createBitmap(16, 16))

        assertTrue(File(cacheRoot, "thumbnails/Om Deva deva.jpg").exists())
    }

    @Test
    fun `reads a cached thumbnail back`() {
        MediaMetaDataRetriever.hasSavedMediaThumbnailsInCache(context, "song.mp3", createBitmap(64, 64))

        val image = MediaMetaDataRetriever.fetchMetadataFromCache(context, "song.mp3", "Jannat")

        assertNotNull(image)
        assertTrue(image.width > 0)
    }

    @Test
    fun `falls back to the movie avatar when nothing is cached`() {
        val image = MediaMetaDataRetriever.fetchMetadataFromCache(context, "never_cached.mp3", "Jannat")

        assertNotNull(image)
        assertTrue(image.width > 0)
    }

    @Test
    fun `falls back to the movie avatar for an empty cached file`() {
        thumbnailFile("empty.mp3").apply {
            parentFile?.mkdirs()
            createNewFile()
        }

        val image = MediaMetaDataRetriever.fetchMetadataFromCache(context, "empty.mp3", "Jannat")

        assertNotNull(image)
    }

    @Test
    fun `returns no album art for a song that is not on disk`() {
        assertNull(MediaMetaDataRetriever.getMediaImage("not_downloaded_${System.nanoTime()}.mp3"))
    }

    @Test
    fun `mediaMetaDataRetriever falls back to the movie avatar`() {
        val image = MediaMetaDataRetriever.mediaMetaDataRetriever(context, "not_downloaded.mp3", "Jannat")

        assertNotNull(image)
        assertTrue(image.height > 0)
    }

    @Test
    fun `thumbnails share one cache folder`() {
        MediaMetaDataRetriever.hasSavedMediaThumbnailsInCache(context, "first.mp3", createBitmap(8, 8))
        MediaMetaDataRetriever.hasSavedMediaThumbnailsInCache(context, "second.mp3", createBitmap(8, 8))

        assertEquals(2, File(cacheRoot, "thumbnails").listFiles()?.size)
    }
}
