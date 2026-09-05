package com.akundu.kkplayer.download

import android.app.DownloadManager
import android.content.Context
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33])
class AndroidDownloaderTest {
    private val downloadManager: DownloadManager = mock()
    private val context: Context = mock()
    private lateinit var downloader: AndroidDownloader

    @Before
    fun setUp() {
        whenever(context.getSystemService(DownloadManager::class.java)).thenReturn(downloadManager)
        downloader = AndroidDownloader(context)
    }

    @Test
    fun `enqueues the request and returns the download id`() {
        whenever(downloadManager.enqueue(any())).thenReturn(99L)

        val id = downloader.downloadFile("https://example.com/song.mp3", "song.mp3")

        assertEquals(99L, id)
        verify(downloadManager).enqueue(any())
    }

    @Test
    fun `each download is enqueued separately`() {
        whenever(downloadManager.enqueue(any())).thenReturn(1L, 2L)

        assertEquals(1L, downloader.downloadFile("https://example.com/a.mp3", "a.mp3"))
        assertEquals(2L, downloader.downloadFile("https://example.com/b.mp3", "b.mp3"))
    }
}
