package com.akundu.kkplayer.feature.player.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.akundu.kkplayer.service.BackgroundSoundService
import com.akundu.kkplayer.service.playback.PlaybackController
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class PlayerViewModel(
    private val playbackController: () -> PlaybackController? = { BackgroundSoundService.getServiceObject() },
    private val pollIntervalMs: Long = POLL_INTERVAL_MS,
) : ViewModel() {
    val isPlaying = MutableLiveData(true)
    val currentSongId = MutableLiveData(0)
    val currentPositionMs = MutableLiveData(0)
    val durationMs = MutableLiveData(0)

    init {
        pollPlaybackState()
    }

    fun playPauseToggle() {
        playbackController()?.playPausePlayer()
    }

    fun nextSong() {
        playbackController()?.nextSong()
    }

    fun previousSong() {
        playbackController()?.previousSong()
    }

    fun seekTo(positionMs: Int) {
        playbackController()?.seekTo(positionMs)
        currentPositionMs.value = positionMs
    }

    private fun pollPlaybackState() {
        viewModelScope.launch {
            while (isActive) {
                playbackController()?.let { controller ->
                    isPlaying.value = controller.isCurrentlyPlaying()
                    currentSongId.value = controller.getCurrentSongId()
                    currentPositionMs.value = controller.getCurrentPositionMs()
                    durationMs.value = controller.getDurationMs()
                }
                delay(pollIntervalMs)
            }
        }
    }

    companion object {
        private const val POLL_INTERVAL_MS = 500L
    }
}
