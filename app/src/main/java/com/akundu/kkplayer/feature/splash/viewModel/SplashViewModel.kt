package com.akundu.kkplayer.feature.splash.viewModel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.akundu.kkplayer.feature.splash.model.SplashUiState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.update
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

    val isAnimationEndLiveData = MutableLiveData(true)
    fun reverseAnimation() {
        viewModelScope.launch {
            isAnimationEndLiveData.value = (isAnimationEndLiveData.value)?.not()
            delay(2500)
            reverseAnimation()
        }
    }

    val isAnimationEndFlow: Flow<Boolean> = flow<Boolean> {
        var isAnimationEnd = false
        repeat(6) {
            Log.d("TAG", ": $it")
            emit(isAnimationEnd)
            isAnimationEnd = isAnimationEnd.not()
            delay(2500)
        }
    }

    private val _uiState = MutableStateFlow(SplashUiState())
    val uiState: StateFlow<SplashUiState> = _uiState.asStateFlow()

    fun loginButtonClickStateChangeEvent() {
        _uiState.update { currentState ->
            currentState.copy(
                isLoadingDotsVisible = true,
                isLoginLayoutVisible = false
            )
        }
    }
}