package com.akundu.kkplayer.domain

import androidx.lifecycle.LiveData
import com.akundu.kkplayer.database.entity.SongEntity

interface Repository {
    suspend fun login(email: String, password: String)

    fun updateSongDownloadStatus(id: Long)

    fun getAllSongsLiveData(): LiveData<List<SongEntity>>
}