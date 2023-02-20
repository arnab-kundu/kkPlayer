package com.akundu.kkplayer.feature.splash.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


class SplashViewModel : ViewModel() {

    // region How to create delay in splashScreen viewModel using StateFlow
    private val _isLoading = MutableStateFlow(true)
    val isLoading = _isLoading.asStateFlow()

    init {
        viewModelScope.launch {
            delay(2500)
            _isLoading.value = false
        }
    }
    // endregion

}