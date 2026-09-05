package com.akundu.kkplayer.database.entity

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SongEntityTest {
    private fun songEntity(
        id: Long = 1L,
        title: String = "Bhole Shankar",
        artist: String = "Hansraj Raghuwanshi",
        fileName: String = "bhole_shankar.mp3",
        url: String = "",
        movie: String = "OMG 2",
        isDownloaded: Boolean = false,
    ) = SongEntity(
        id = id,
        title = title,
        artist = artist,
        fileName = fileName,
        url = url,
        movie = movie,
        isDownloaded = isDownloaded,
    )

    @Test
    fun `a new song leaves the id to Room and starts undownloaded`() {
        val song = SongEntity(title = "Bhole Shankar", artist = "", fileName = "bhole_shankar.mp3", url = "", movie = "OMG 2")

        assertEquals(0L, song.id)
        assertFalse(song.isDownloaded)
    }

    @Test
    fun `positional arguments are read in column order`() {
        // The Compose previews build entities positionally, so the column order is part of the contract.
        val song = SongEntity(7L, "Bhole Shankar", "Hansraj Raghuwanshi", "bhole_shankar.mp3", "https://example.com/b.mp3", "OMG 2")

        assertEquals(7L, song.id)
        assertEquals("Bhole Shankar", song.title)
        assertEquals("Hansraj Raghuwanshi", song.artist)
        assertEquals("bhole_shankar.mp3", song.fileName)
        assertEquals("https://example.com/b.mp3", song.url)
        assertEquals("OMG 2", song.movie)
        assertFalse(song.isDownloaded)
    }

    @Test
    fun `the download flag can be flipped in place`() {
        val song = songEntity()

        song.isDownloaded = true

        assertTrue(song.isDownloaded)
    }

    @Test
    fun `entities with the same columns are equal`() {
        assertEquals(songEntity(), songEntity())
        assertEquals(songEntity().hashCode(), songEntity().hashCode())
    }

    @Test
    fun `the same file downloaded and not downloaded are different rows`() {
        assertNotEquals(songEntity(isDownloaded = false), songEntity(isDownloaded = true))
    }

    @Test
    fun `two rows for the same file name differ by id`() {
        // fileName carries a unique index, so this pairing only shows up as a mistake -
        // equality has to notice it rather than treat the rows as one.
        assertNotEquals(songEntity(id = 1L), songEntity(id = 2L))
    }

    @Test
    fun `copy carries the id across unless it is replaced`() {
        val downloaded = songEntity(id = 9L).copy(isDownloaded = true)

        assertEquals(9L, downloaded.id)
        assertTrue(downloaded.isDownloaded)
        assertEquals(11L, downloaded.copy(id = 11L).id)
    }
}
