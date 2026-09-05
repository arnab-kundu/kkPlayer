package com.akundu.kkplayer.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SongDataProviderTest {
    private val songs = SongDataProvider.kkSongList

    @Test
    fun `provides a non empty catalogue`() {
        assertTrue(songs.isNotEmpty())
    }

    // SongEntity has a unique index on fileName, so duplicates here would surface on the
    // device as a constraint violation while seeding the database.
    @Test
    fun `file names are unique`() {
        val duplicates = songs.groupBy { it.fileName }.filterValues { it.size > 1 }.keys

        assertEquals(emptySet<String>(), duplicates)
    }

    @Test
    fun `every song has a title and a file name`() {
        val incomplete = songs.filter { it.title.isBlank() || it.fileName.isBlank() }

        assertEquals(emptyList<Song>(), incomplete)
    }

    @Test
    fun `songs without a url are bundled as raw resources`() {
        val bundled = songs.filter { it.url.isEmpty() }

        assertTrue(bundled.all { it.fileName.endsWith(".mp3") })
    }
}
