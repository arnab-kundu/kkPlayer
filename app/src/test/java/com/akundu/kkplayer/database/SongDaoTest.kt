package com.akundu.kkplayer.database

import androidx.room.Room
import com.akundu.kkplayer.database.dao.SongDao
import com.akundu.kkplayer.database.entity.SongEntity
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33])
class SongDaoTest {
    private lateinit var database: SongDatabase
    private lateinit var dao: SongDao

    @Before
    fun setUp() {
        database =
            Room
                .inMemoryDatabaseBuilder(RuntimeEnvironment.getApplication(), SongDatabase::class.java)
                .allowMainThreadQueries()
                .build()
        dao = database.songDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    private fun song(
        title: String,
        fileName: String,
        isDownloaded: Boolean = false,
    ) = SongEntity(
        title = title,
        artist = "KK",
        fileName = fileName,
        url = "https://example.com/$fileName",
        movie = "Movie",
        isDownloaded = isDownloaded,
    )

    private fun seed(vararg songs: SongEntity): List<SongEntity> {
        songs.forEach { dao.addSong(it) }
        return dao.getAllSongs()
    }

    @Test
    fun `adds a song and reads it back`() {
        dao.addSong(song("Tu hi meri sab hay", "tu_hi.mp3"))

        val stored = dao.getAllSongs()
        assertEquals(1, stored.size)
        assertEquals("Tu hi meri sab hay", stored.first().title)
    }

    @Test
    fun `an empty table reports a zero count`() {
        assertEquals(0, dao.getTotalCount())
    }

    @Test
    fun `counts every stored song`() {
        seed(song("A", "a.mp3"), song("B", "b.mp3"), song("C", "c.mp3"))

        assertEquals(3, dao.getTotalCount())
    }

    @Test
    fun `re-adding the same file name replaces the existing row`() {
        dao.addSong(song("Original", "same.mp3"))
        dao.addSong(song("Replacement", "same.mp3"))

        assertEquals(1, dao.getTotalCount())
        assertEquals("Replacement", dao.getAllSongs().single().title)
    }

    @Test
    fun `finds a song by id`() {
        val stored = seed(song("Alvida", "alvida.mp3"))

        assertEquals("Alvida", dao.findSongById(stored.first().id).title)
    }

    @Test
    fun `finds a song id by file name`() {
        val stored = seed(song("Alvida", "alvida.mp3"))

        assertEquals(stored.first().id, dao.findSongIdByFilename("alvida.mp3"))
    }

    @Test
    fun `searches a song by exact name`() {
        seed(song("Alvida", "alvida.mp3"), song("Zara Si", "zara.mp3"))

        val found = dao.searchSongByName("Alvida")

        assertEquals(1, found.size)
        assertEquals("alvida.mp3", found.first().fileName)
    }

    @Test
    fun `searches songs by partial title`() {
        seed(song("Tu hi meri sab hay", "tu_hi.mp3"), song("Tu Jaane Na", "tu_jaane.mp3"), song("Alvida", "alvida.mp3"))

        assertEquals(2, dao.searchSongByTitle("Tu").size)
    }

    @Test
    fun `partial search returns nothing for an unknown title`() {
        seed(song("Alvida", "alvida.mp3"))

        assertTrue(dao.searchSongByTitle("Nonexistent").isEmpty())
    }

    @Test
    fun `marks a song as downloaded`() {
        val stored = seed(song("Alvida", "alvida.mp3"))
        assertFalse(dao.findSongById(stored.first().id).isDownloaded)

        val updatedRows = dao.updateSongDownloadInfo(stored.first().id, true)

        assertEquals(1, updatedRows)
        assertTrue(dao.findSongById(stored.first().id).isDownloaded)
    }

    @Test
    fun `updating a missing song changes no rows`() {
        assertEquals(0, dao.updateSongDownloadInfo(999L, true))
    }

    @Test
    fun `returns the first downloaded song`() {
        val stored = seed(song("A", "a.mp3"), song("B", "b.mp3", isDownloaded = true))

        assertEquals("B", dao.getFirstDownloadedSong().title)
        assertEquals(stored[1].id, dao.getFirstDownloadedSong().id)
    }

    @Test
    fun `walks forward through downloaded songs only`() {
        val stored =
            seed(
                song("A", "a.mp3", isDownloaded = true),
                song("B", "b.mp3", isDownloaded = false),
                song("C", "c.mp3", isDownloaded = true),
            )

        val next = dao.getNextDownloadedSong(stored[0].id)

        assertEquals("C", next?.title)
    }

    @Test
    fun `returns null past the last downloaded song`() {
        val stored = seed(song("A", "a.mp3", isDownloaded = true))

        assertNull(dao.getNextDownloadedSong(stored[0].id))
    }

    @Test
    fun `walks backward through downloaded songs only`() {
        val stored =
            seed(
                song("A", "a.mp3", isDownloaded = true),
                song("B", "b.mp3", isDownloaded = false),
                song("C", "c.mp3", isDownloaded = true),
            )

        val previous = dao.getPreviousDownloadedSong(stored[2].id)

        assertEquals("A", previous?.title)
    }

    @Test
    fun `returns null before the first downloaded song`() {
        val stored = seed(song("A", "a.mp3", isDownloaded = true))

        assertNull(dao.getPreviousDownloadedSong(stored[0].id))
    }

    @Test
    fun `skips songs that were never downloaded`() {
        val stored = seed(song("A", "a.mp3"), song("B", "b.mp3"))

        assertNull(dao.getNextDownloadedSong(stored[0].id))
        assertNull(dao.getPreviousDownloadedSong(stored[1].id))
    }

    @Test
    fun `deletes a single song`() {
        val stored = seed(song("A", "a.mp3"), song("B", "b.mp3"))

        dao.deleteSong(stored[0].id)

        assertEquals(1, dao.getTotalCount())
        assertEquals("B", dao.getAllSongs().single().title)
    }

    @Test
    fun `truncating reports the number of removed rows`() {
        seed(song("A", "a.mp3"), song("B", "b.mp3"))

        assertEquals(2, dao.truncateTable())
        assertEquals(0, dao.getTotalCount())
    }

    @Test
    fun `exposes all songs as live data`() {
        seed(song("A", "a.mp3"))

        assertNotNull(dao.getAllSongsLiveData())
    }
}
