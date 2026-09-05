package com.akundu.kkplayer.feature.settings.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.akundu.kkplayer.KkPlayerApp
import com.akundu.kkplayer.feature.settings.datastore.AppTheme
import com.akundu.kkplayer.feature.settings.datastore.DataStoreManager
import com.akundu.kkplayer.feature.settings.datastore.DisplayOptions
import com.akundu.kkplayer.feature.settings.datastore.RepeatMode
import com.akundu.kkplayer.storage.Constants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File

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

    fun onClearCache() {
        viewModelScope.launch(Dispatchers.IO) {
            clearCacheDirs()
        }
    }

    fun onClearDatabase() {
        viewModelScope.launch(Dispatchers.IO) {
            KkPlayerApp.appModule.database.clearAllTables()
        }
    }

    fun onClearData() {
        viewModelScope.launch(Dispatchers.IO) {
            clearCacheDirs()
            KkPlayerApp.appModule.database.clearAllTables()
            clearDirectoryContents(File(Constants.MEDIA_PATH))
            dataStoreManager.clearAll()
        }
    }

    private fun clearCacheDirs() {
        val application = getApplication<Application>()
        clearDirectoryContents(application.cacheDir)
        application.externalCacheDir?.let { clearDirectoryContents(it) }
    }

    private fun clearDirectoryContents(directory: File) {
        directory.listFiles()?.forEach { it.deleteRecursively() }
    }
}
