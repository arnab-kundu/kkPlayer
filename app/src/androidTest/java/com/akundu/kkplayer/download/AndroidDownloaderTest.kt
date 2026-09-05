package com.akundu.kkplayer.download

import android.app.DownloadManager
import android.content.Context
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Verifies the enqueue contract against the real DownloadManager. The URL is deliberately
 * unresolvable: the download does not need to complete for the queueing behaviour to be
 * observable, and depending on a real transfer would make this test flaky.
 */
@RunWith(AndroidJUnit4::class)
class AndroidDownloaderTest {
    private lateinit var context: Context
    private lateinit var downloadManager: DownloadManager
    private val enqueuedIds = mutableListOf<Long>()

    @Before
    fun setUp() {
        context = InstrumentationRegistry.getInstrumentation().targetContext
        downloadManager = context.getSystemService(DownloadManager::class.java)
    }

    @After
    fun tearDown() {
        enqueuedIds.forEach { downloadManager.remove(it) }
    }

    @Test
    fun enqueuesADownloadAndReturnsItsId() {
        val downloader = AndroidDownloader(context)

        val id = downloader.downloadFile("https://invalid.invalid/song.mp3", "instrumented_song_${System.nanoTime()}.mp3")
        enqueuedIds += id

        assertTrue("DownloadManager returned a non-positive id", id > 0)
    }

    @Test
    fun theEnqueuedDownloadIsVisibleToTheDownloadManager() {
        val downloader = AndroidDownloader(context)
        val id = downloader.downloadFile("https://invalid.invalid/song.mp3", "instrumented_song_${System.nanoTime()}.mp3")
        enqueuedIds += id

        downloadManager.query(DownloadManager.Query().setFilterById(id)).use { cursor ->
            assertTrue("The enqueued download was not found", cursor.moveToFirst())
        }
    }

    @Test
    fun eachDownloadGetsItsOwnId() {
        val downloader = AndroidDownloader(context)

        val first = downloader.downloadFile("https://invalid.invalid/a.mp3", "instrumented_a_${System.nanoTime()}.mp3")
        val second = downloader.downloadFile("https://invalid.invalid/b.mp3", "instrumented_b_${System.nanoTime()}.mp3")
        enqueuedIds += first
        enqueuedIds += second

        assertTrue(first != second)
    }
}
