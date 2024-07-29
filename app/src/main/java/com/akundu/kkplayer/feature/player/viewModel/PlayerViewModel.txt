package com.akundu.kkplayer.feature.player.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class PlayerViewModel : ViewModel() {

    val isPlaying = MutableLiveData(true)
    fun playPauseToggle() {
        isPlaying.value = (isPlaying.value)?.not()
    }
}