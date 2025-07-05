package com.akundu.kkplayer.domain

import androidx.lifecycle.LiveData
import com.akundu.kkplayer.database.SongDatabase
import com.akundu.kkplayer.database.entity.SongEntity
import com.akundu.kkplayer.network.ApiRequest

class RepositoryImpl(
    private val api: ApiRequest,
    private val database: SongDatabase,
) : Repository {

    override fun updateSongDownloadStatus(id: Long) {
        database.songDao().updateSongDownloadInfo(id, true)
    }

    override fun getAllSongsLiveData(): LiveData<List<SongEntity>> {
        return database.songDao().getAllSongsLiveData()
    }
}
