package com.akundu.kkplayer.domain

import androidx.lifecycle.LiveData
import com.akundu.kkplayer.database.entity.SongEntity

interface Repository {
    fun updateSongDownloadStatus(id: Long)

    fun getAllSongsLiveData(): LiveData<List<SongEntity>>
}
