package com.akundu.kkplayer.feature.main.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.akundu.kkplayer.database.entity.SongEntity
import com.akundu.kkplayer.domain.Repository
import kotlinx.coroutines.launch

class MainViewModel(private val repository: Repository) : ViewModel() {
    lateinit var songList: LiveData<List<SongEntity>>

    fun updateSongDownloadStatus(id: Long) {
        viewModelScope.launch {
            repository.updateSongDownloadStatus(id)
        }
    }

    fun allSongsLiveData() {
        viewModelScope.launch {
           songList = repository.getAllSongsLiveData()
        }
    }
}