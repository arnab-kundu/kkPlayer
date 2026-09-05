package com.akundu.kkplayer.database

import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.akundu.kkplayer.data.SongDataProvider
import com.akundu.kkplayer.database.dao.SongDao
import com.akundu.kkplayer.database.entity.SongEntity
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SongDatabaseTest {
    private lateinit var database: SongDatabase
    private lateinit var dao: SongDao

    @Before
    fun setUp() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        database =
            Room
                .inMemoryDatabaseBuilder(context, SongDatabase::class.java)
                .allowMainThreadQueries()
                .build()
        dao = database.songDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    private fun seedCatalogue(): List<SongEntity> {
        SongDataProvider.kkSongList.forEach { song ->
            dao.addSong(
                SongEntity(
                    title = song.title,
                    artist = song.artist,
                    fileName = song.fileName,
                    url = song.url,
                    movie = song.movie,
                ),
            )
        }
        return dao.getAllSongs()
    }

    @Test
    fun storesTheWholeCatalogueOnRealSqlite() {
        val stored = seedCatalogue()

        assertEquals(SongDataProvider.kkSongList.size, stored.size)
        assertEquals(SongDataProvider.kkSongList.size, dao.getTotalCount())
    }

    @Test
    fun enforcesTheUniqueFileNameIndex() {
        val stored = seedCatalogue()
        val first = stored.first()

        dao.addSong(SongEntity(title = "Replacement", artist = "", fileName = first.fileName, url = "", movie = ""))

        assertEquals(stored.size, dao.getTotalCount())
        assertEquals("Replacement", dao.searchSongByName("Replacement").single().title)
    }

    @Test
    fun findsASeededSongById() {
        val stored = seedCatalogue()

        assertEquals(stored.first().title, dao.findSongById(stored.first().id).title)
    }

    @Test
    fun findsASongIdByFileName() {
        val stored = seedCatalogue()

        assertEquals(stored.first().id, dao.findSongIdByFilename(stored.first().fileName))
    }

    @Test
    fun walksThroughDownloadedSongsOnly() {
        val stored = seedCatalogue()
        dao.updateSongDownloadInfo(stored[0].id, true)
        dao.updateSongDownloadInfo(stored[2].id, true)

        assertEquals(stored[2].id, dao.getNextDownloadedSong(stored[0].id)?.id)
        assertEquals(stored[0].id, dao.getPreviousDownloadedSong(stored[2].id)?.id)
        assertNull(dao.getNextDownloadedSong(stored[2].id))
        assertNull(dao.getPreviousDownloadedSong(stored[0].id))
    }

    @Test
    fun marksASongAsDownloaded() {
        val stored = seedCatalogue()

        assertEquals(1, dao.updateSongDownloadInfo(stored.first().id, true))
        assertTrue(dao.findSongById(stored.first().id).isDownloaded)
        assertEquals(stored.first().id, dao.getFirstDownloadedSong().id)
    }

    @Test
    fun searchesSongsByPartialTitle() {
        seedCatalogue()
        val expected = SongDataProvider.kkSongList.first().title

        assertNotNull(dao.searchSongByTitle(expected.take(4)).firstOrNull())
    }

    @Test
    fun deletesASingleSong() {
        val stored = seedCatalogue()

        dao.deleteSong(stored.first().id)

        assertEquals(stored.size - 1, dao.getTotalCount())
    }

    @Test
    fun truncatingEmptiesTheTable() {
        val stored = seedCatalogue()

        assertEquals(stored.size, dao.truncateTable())
        assertEquals(0, dao.getTotalCount())
    }
}
