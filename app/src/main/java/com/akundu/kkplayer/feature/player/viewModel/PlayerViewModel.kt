package com.akundu.kkplayer.feature.player.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class PlayerViewModel : ViewModel() {

    val isPlaying = MutableLiveData(true)
    val progressPercent = MutableLiveData<Float>(0.0F)

    init {
        progressTracker()
    }

    fun playPauseToggle() {
        isPlaying.value = (isPlaying.value)?.not()
    }

    fun progressTracker() {
        viewModelScope.launch {
            progressPercent.value = ((progressPercent.value) ?: (0F)) + 1F
            delay(1000)
            progressTracker()
        }
    }
}