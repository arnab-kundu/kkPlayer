package com.akundu.kkplayer.feature.player.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.akundu.kkplayer.service.BackgroundSoundService
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class PlayerViewModel : ViewModel() {
    val isPlaying = MutableLiveData(true)
    val currentSongId = MutableLiveData(0)
    val currentPositionMs = MutableLiveData(0)
    val durationMs = MutableLiveData(0)

    init {
        pollPlaybackState()
    }

    fun playPauseToggle() {
        BackgroundSoundService.getServiceObject()?.playPausePlayer()
    }

    fun nextSong() {
        BackgroundSoundService.getServiceObject()?.nextSong()
    }

    fun previousSong() {
        BackgroundSoundService.getServiceObject()?.previousSong()
    }

    fun seekTo(positionMs: Int) {
        BackgroundSoundService.getServiceObject()?.seekTo(positionMs)
        currentPositionMs.value = positionMs
    }

    private fun pollPlaybackState() {
        viewModelScope.launch {
            while (true) {
                BackgroundSoundService.getServiceObject()?.let { service ->
                    isPlaying.value = service.isCurrentlyPlaying()
                    currentSongId.value = service.getCurrentSongId()
                    currentPositionMs.value = service.getCurrentPositionMs()
                    durationMs.value = service.getDurationMs()
                }
                delay(500)
            }
        }
    }
}
