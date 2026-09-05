package com.akundu.kkplayer.service.playback

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import androidx.core.graphics.createBitmap
import com.akundu.kkplayer.database.entity.SongEntity
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config
import java.io.ByteArrayOutputStream

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33])
class AlbumArtExtractorTest {
    private lateinit var context: Context

    @Before
    fun setUp() {
        context = RuntimeEnvironment.getApplication()
    }

    private fun pngBytes(
        width: Int,
        height: Int,
    ): ByteArray =
        ByteArrayOutputStream()
            .apply { createBitmap(width, height).compress(Bitmap.CompressFormat.PNG, 100, this) }
            .toByteArray()

    private fun song(
        isDownloaded: Boolean = true,
        movie: String = "Jannat",
    ) = SongEntity(
        id = 1L,
        title = "Tu hi meri sab hay",
        artist = "KK",
        fileName = "song.mp3",
        url = "https://example.com/song.mp3",
        movie = movie,
        isDownloaded = isDownloaded,
    )

    @Test
    fun `decodes a small image without downsampling`() {
        val decoded = AlbumArtExtractor.decodeAndScale(pngBytes(64, 64))

        assertNotNull(decoded)
        assertEquals(64, decoded.width)
    }

    @Test
    fun `downsamples an image larger than the maximum size`() {
        val decoded = AlbumArtExtractor.decodeAndScale(pngBytes(128, 128), maxSize = 32)

        assertTrue(decoded.width <= 32)
    }

    @Test
    fun `keeps an image that exactly matches the maximum size`() {
        val decoded = AlbumArtExtractor.decodeAndScale(pngBytes(32, 32), maxSize = 32)

        assertEquals(32, decoded.width)
    }

    @Test
    fun `uses the embedded album art when the retriever provides one`() {
        val artwork = pngBytes(64, 64)
        val retriever: MediaMetadataRetriever = mock()
        whenever(retriever.embeddedPicture).thenReturn(artwork)

        val bitmap = AlbumArtExtractor.extractAlbumArt(context, song(), retriever)

        assertEquals(64, bitmap.width)
        verify(retriever).release()
    }

    @Test
    fun `reads a downloaded song from local storage`() {
        val retriever: MediaMetadataRetriever = mock()
        whenever(retriever.embeddedPicture).thenReturn(pngBytes(16, 16))

        AlbumArtExtractor.extractAlbumArt(context, song(isDownloaded = true), retriever)

        verify(retriever).setDataSource(org.mockito.kotlin.any<String>())
    }

    @Test
    fun `streams a song that has not been downloaded`() {
        val retriever: MediaMetadataRetriever = mock()
        whenever(retriever.embeddedPicture).thenReturn(pngBytes(16, 16))

        AlbumArtExtractor.extractAlbumArt(context, song(isDownloaded = false), retriever)

        verify(retriever).setDataSource(org.mockito.kotlin.eq("https://example.com/song.mp3"), org.mockito.kotlin.any())
    }

    @Test
    fun `falls back to the movie drawable when there is no embedded art`() {
        val retriever: MediaMetadataRetriever = mock()
        whenever(retriever.embeddedPicture).thenReturn(null)

        val bitmap = AlbumArtExtractor.extractAlbumArt(context, song(), retriever)

        assertNotNull(bitmap)
        verify(retriever).release()
    }

    @Test
    fun `falls back to the movie drawable when the retriever fails`() {
        val retriever: MediaMetadataRetriever = mock()
        whenever(retriever.setDataSource(org.mockito.kotlin.any<String>())).thenThrow(RuntimeException("unreadable"))

        val bitmap = AlbumArtExtractor.extractAlbumArt(context, song(), retriever)

        assertNotNull(bitmap)
        verify(retriever).release()
    }

    @Test
    fun `decodeAndScale is used through the default max size`() {
        val decoded = AlbumArtExtractor.decodeAndScale(pngBytes(1024, 1024))

        assertTrue(decoded.width <= AlbumArtExtractor.DEFAULT_MAX_SIZE)
    }

    @Test
    fun `bitmap factory decodes bounds without allocating the image`() {
        val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        val bytes = pngBytes(48, 24)

        BitmapFactory.decodeByteArray(bytes, 0, bytes.size, bounds)

        assertEquals(48, bounds.outWidth)
        assertEquals(24, bounds.outHeight)
    }
}
