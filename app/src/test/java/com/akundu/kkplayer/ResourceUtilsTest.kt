package com.akundu.kkplayer

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33])
class ResourceUtilsTest {
    private val avatars = setOf(R.drawable.ic_music_album_avatar, R.drawable.ic_music_album_avatar1)

    // Every per-movie branch in getDrawable is commented out, so the movie argument is ignored
    // and one of the two fallback avatars is always returned.
    @Test
    fun `getDrawable returns a fallback avatar for a known movie`() {
        assertTrue(getDrawable("Jannat") in avatars)
    }

    @Test
    fun `getDrawable returns a fallback avatar for an unknown movie`() {
        assertTrue(getDrawable("Not A Real Movie") in avatars)
    }

    @Test
    fun `getDrawable returns a fallback avatar for an empty movie`() {
        assertTrue(getDrawable("") in avatars)
    }

    @Test
    fun `getRawFileResourceID finds a bundled raw file`() {
        assertEquals(R.raw.test_mp3_file_10_sec, getRawFileResourceID("test_mp3_file_10_sec.mp3"))
    }

    @Test
    fun `getRawFileResourceID ignores the file extension`() {
        assertEquals(R.raw.test_mp3_file_10_sec, getRawFileResourceID("test_mp3_file_10_sec"))
    }

    @Test(expected = RuntimeException::class)
    fun `getRawFileResourceID throws for a missing raw file`() {
        getRawFileResourceID("not_bundled.mp3")
    }

    @Test
    fun `listRaw enumerates the raw resources without failing`() {
        listRaw()
    }
}
