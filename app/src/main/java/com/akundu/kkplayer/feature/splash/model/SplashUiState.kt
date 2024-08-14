package com.akundu.kkplayer.feature.splash.model

data class SplashUiState(
    val isAnimationEndState: Boolean = false,
    val isLoadingDotsVisible: Boolean = false,
    val isLoginLayoutVisible: Boolean = true,
    val isBiometricEnabled: Boolean = false,
    val email: String = "",
    val password: String = "",
    val emailError: Boolean = false,
    val passwordError: Boolean = false,
)
