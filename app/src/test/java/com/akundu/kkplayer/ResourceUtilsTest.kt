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

    // Per-movie artwork is not bundled, so every movie resolves to one of the two avatars.
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

    // A song's artwork must not change between recompositions.
    @Test
    fun `getDrawable is stable for the same movie`() {
        val first = getDrawable("Jannat")

        repeat(50) { assertEquals(first, getDrawable("Jannat")) }
    }

    @Test
    fun `getDrawable spreads movies across the available avatars`() {
        val movies = listOf("Jannat", "Gangster", "Race", "Raees", "Kites", "Musafir", "Zeher", "Saathiya")

        assertEquals(avatars, movies.map { getDrawable(it) }.toSet())
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
