package com.akundu.kkplayer.feature.main.ui

import com.akundu.kkplayer.storage.Constants
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33])
class MainPageUtilsTest {
    @Test
    fun `a song that was never downloaded is not on disk`() {
        assertFalse(isFileExists("definitely_not_downloaded_${System.nanoTime()}.mp3"))
    }

    @Test
    fun `a song name that does not exist on disk is reported missing`() {
        assertFalse(isFileExists("another_missing_${System.nanoTime()}.mp3"))
    }

    @Test
    fun `songs are looked up inside the scoped media directory`() {
        assertTrue(Constants.MEDIA_PATH.endsWith("Android/media/com.akundu.kkplayer"))
    }

    @Test
    fun `download work is tagged so it can be tracked`() {
        assertEquals("song_download", DOWNLOAD_WORK_TAG)
    }
}
