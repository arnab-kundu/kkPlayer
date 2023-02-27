package com.akundu.kkplayer.download

import android.content.Context
import androidx.test.platform.app.InstrumentationRegistry
import com.akundu.kkplayer.data.SongDataProvider
import org.junit.After
import org.junit.Before
import org.junit.Test

class AndroidDownloaderTest {

    lateinit var instrumentationContext: Context

    @Before
    fun setup() {
        instrumentationContext = InstrumentationRegistry.getInstrumentation().context
    }

    @After
    fun tearDown() {
    }

    @Test
    fun downloadFile() {

        // How to use AndroidDownloader
        val song = SongDataProvider.kkSongList[0]
        val downloader = AndroidDownloader(context = instrumentationContext)
        downloader.downloadFile(song.url, song.fileName)
    }
}