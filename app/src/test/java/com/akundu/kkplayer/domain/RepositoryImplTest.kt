package com.akundu.kkplayer.domain

import androidx.lifecycle.MutableLiveData
import com.akundu.kkplayer.database.SongDatabase
import com.akundu.kkplayer.database.dao.SongDao
import com.akundu.kkplayer.database.entity.SongEntity
import com.akundu.kkplayer.network.ApiRequest
import org.junit.Assert.assertSame
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class RepositoryImplTest {
    private val api: ApiRequest = mock()
    private val songDao: SongDao = mock()
    private val database: SongDatabase = mock()
    private lateinit var repository: RepositoryImpl

    @Before
    fun setUp() {
        whenever(database.songDao()).thenReturn(songDao)
        repository = RepositoryImpl(api, database)
    }

    @Test
    fun `updateSongDownloadStatus marks the song as downloaded`() {
        repository.updateSongDownloadStatus(12L)

        verify(songDao).updateSongDownloadInfo(12L, true)
    }

    @Test
    fun `getAllSongsLiveData returns the dao live data`() {
        val songs = MutableLiveData<List<SongEntity>>(emptyList())
        whenever(songDao.getAllSongsLiveData()).thenReturn(songs)

        assertSame(songs, repository.getAllSongsLiveData())
    }
}
