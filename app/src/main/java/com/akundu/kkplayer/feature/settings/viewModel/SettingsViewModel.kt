package com.akundu.kkplayer.feature.settings.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.akundu.kkplayer.feature.settings.datastore.AppTheme
import com.akundu.kkplayer.feature.settings.datastore.DataStoreManager
import com.akundu.kkplayer.feature.settings.datastore.DisplayOptions
import com.akundu.kkplayer.feature.settings.datastore.RepeatMode
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SettingsViewModel(
    application: Application,
) : AndroidViewModel(application) {
    private val dataStoreManager = DataStoreManager(application)

    private val _repeatMode = MutableStateFlow(RepeatMode.NONE)
    val repeatMode: StateFlow<String> = _repeatMode.asStateFlow()

    private val _theme = MutableStateFlow(AppTheme.DEFAULT)
    val theme: StateFlow<String> = _theme.asStateFlow()

    private val _displayOption = MutableStateFlow(DisplayOptions.ALL_SONGS)
    val displayOption: StateFlow<String> = _displayOption.asStateFlow()

    init {
        viewModelScope.launch {
            dataStoreManager.repeatModeFlow.collect {
                _repeatMode.value = it
            }
        }
        viewModelScope.launch {
            dataStoreManager.themeFlow.collect {
                _theme.value = it
            }
        }
        viewModelScope.launch {
            dataStoreManager.displayOptionFlow.collect {
                _displayOption.value = it
            }
        }
    }

    fun onRepeatModeSelected(newMode: String) {
        viewModelScope.launch {
            dataStoreManager.saveRepeatMode(newMode)
        }
    }

    fun onThemeSelected(newTheme: String) {
        viewModelScope.launch {
            dataStoreManager.saveTheme(newTheme)
        }
    }

    fun onDisplayOptionSelected(option: String) {
        viewModelScope.launch {
            dataStoreManager.saveDisplayOption(option)
        }
    }
}
