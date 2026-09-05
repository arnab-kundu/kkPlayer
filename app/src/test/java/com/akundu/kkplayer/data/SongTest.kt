package com.akundu.kkplayer.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test

class SongTest {
    private fun song(
        title: String = "Bhole Shankar",
        artist: String = "Hansraj Raghuwanshi",
        fileName: String = "bhole_shankar.mp3",
        url: String = "",
        movie: String = "OMG 2",
    ) = Song(title = title, artist = artist, fileName = fileName, url = url, movie = movie)

    @Test
    fun `positional arguments are read in catalogue order`() {
        // SongDataProvider and the Room seeding in MainActivity both depend on this order.
        val song = Song("Bhole Shankar", "Hansraj Raghuwanshi", "bhole_shankar.mp3", "https://example.com/b.mp3", "OMG 2")

        assertEquals("Bhole Shankar", song.title)
        assertEquals("Hansraj Raghuwanshi", song.artist)
        assertEquals("bhole_shankar.mp3", song.fileName)
        assertEquals("https://example.com/b.mp3", song.url)
        assertEquals("OMG 2", song.movie)
    }

    @Test
    fun `songs with the same details are equal`() {
        assertEquals(song(), song())
        assertEquals(song().hashCode(), song().hashCode())
    }

    @Test
    fun `a differing field makes two songs unequal`() {
        assertNotEquals(song(), song(title = "Tu Hi Meri Sab Hai"))
        assertNotEquals(song(), song(artist = "KK"))
        assertNotEquals(song(), song(fileName = "other.mp3"))
        assertNotEquals(song(), song(url = "https://example.com/other.mp3"))
        assertNotEquals(song(), song(movie = "Black"))
    }

    @Test
    fun `copy replaces only the named field`() {
        val streamed = song().copy(url = "https://example.com/b.mp3")

        assertEquals("https://example.com/b.mp3", streamed.url)
        assertEquals(song().title, streamed.title)
        assertEquals(song().artist, streamed.artist)
        assertEquals(song().fileName, streamed.fileName)
        assertEquals(song().movie, streamed.movie)
    }

    @Test
    fun `every field is part of the printed form`() {
        val printed = song(url = "https://example.com/b.mp3").toString()

        listOf("Bhole Shankar", "Hansraj Raghuwanshi", "bhole_shankar.mp3", "https://example.com/b.mp3", "OMG 2")
            .forEach { field -> assertEquals("$field missing from $printed", true, printed.contains(field)) }
    }
}
