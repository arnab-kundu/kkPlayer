package com.akundu.kkplayer.database

import android.content.Context
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.akundu.kkplayer.data.Song
import com.akundu.kkplayer.data.SongDataProvider
import com.akundu.kkplayer.database.dao.SongDao
import com.akundu.kkplayer.database.entity.SongEntity
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SongDatabaseTest {
    private val appContext: Context = InstrumentationRegistry.getInstrumentation().targetContext
    private lateinit var database: SongDatabase
    private lateinit var dao: SongDao
    private val kkSongList: List<Song> = SongDataProvider.kkSongList
    private var mockEntity: SongEntity =
        SongEntity(
            title = kkSongList[0].title,
            artist = kkSongList[0].artist,
            fileName = kkSongList[0].fileName,
            url = kkSongList[0].url,
            movie = kkSongList[0].movie,
            isDownloaded = false,
        )

    @Before
    fun setUp() {
        database = SongDatabase.getDatabase(appContext)
        dao = database.songDao()
        dao.addSong(mockEntity)
    }

    @After
    fun tearDown() {
    }

    @Test
    fun testAddSong() {
        dao.addSong(mockEntity)
        val totalCount = dao.getTotalCount()
        assertEquals("TEST FAILED: testAddSong()", 1, totalCount)
    }

    @Test
    fun testGetAllSongs() {
        var songEntity: SongEntity
        var count: Int = 0
        kkSongList.forEachIndexed { index, song ->
            songEntity =
                SongEntity(
                    title = song.title,
                    artist = song.artist,
                    fileName = song.fileName,
                    url = song.url,
                    movie = song.movie,
                )
            dao.addSong(songEntity)
            count = index + 1
        }
        val databaseSongsList = dao.getAllSongs()
        assertTrue("TEST FAILED: testGetAllSongs()", databaseSongsList.size == count)
    }

    @Test
    fun testFindSongById() {
        val songEntity = dao.findSongById(1)
        assertTrue("TEST FAILED: testFindSongById()", songEntity.title == kkSongList[0].title)
    }

    @Test
    fun testSearchSongByName() {
        val songEntity = dao.searchSongByName(kkSongList[0].title)
        assertTrue("TEST FAILED: testSearchSongByName()", songEntity[0].title == kkSongList[0].title)
    }

    @Test
    fun testSearchSongByTitle() {
        val songEntity = dao.searchSongByTitle(kkSongList[0].title.padEnd(2))
        assertTrue("TEST FAILED: testSearchSongByTitle()", songEntity[0].title == kkSongList[0].title)
    }

    @Test
    fun testUpdateSongDownloadInfo() {
        val allSongsList = dao.getAllSongs()
        if (allSongsList.isNotEmpty()) {
            val songEntity = dao.findSongById(allSongsList[0].id)
            assertFalse(songEntity.isDownloaded)
        } else {
            println("No song found in database")
            return
        }

        val downloadStatus = dao.updateSongDownloadInfo(allSongsList[0].id, true)
        val updatedSongEntity = dao.findSongById(allSongsList[0].id)
        assertTrue("TEST FAILED: testUpdateSongDownloadInfo()", downloadStatus == 1)
        assertTrue("TEST FAILED: testUpdateSongDownloadInfo()", updatedSongEntity.isDownloaded)
    }

    @Test
    fun testGetTotalCount() {
        val totalCount = dao.getTotalCount()
        assertTrue("TEST FAILED: testGetTotalCount()", totalCount == kkSongList.size)
    }

    @Test
    fun testTruncateTable() {
        val numberOfDeletedRecord = dao.truncateTable()
        assertEquals("TEST FAILED: testTruncateTable()", kkSongList.size, numberOfDeletedRecord)
    }
}
