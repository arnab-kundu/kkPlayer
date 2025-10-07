package com.akundu.kkplayer.feature.settings.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.IOException
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStoreFile
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

class DataStoreManager(
    context: Context,
) {
    // ❌ This causes multiple instances
    private val dataStoreError =
        PreferenceDataStoreFactory.create(
            produceFile = { context.preferencesDataStoreFile("settings") },
        )

    private val dataStore = DataStoreProvider.getInstance(context)

    companion object {
        private val REPEAT_MODE_KEY = stringPreferencesKey("repeat_mode")
        private val THEME_KEY = stringPreferencesKey("theme")
    }

    val repeatModeFlow: Flow<String> =
        dataStore.data
            .catch { exception ->
                if (exception is IOException) emit(emptyPreferences()) else throw exception
            }.map { preferences ->
                preferences[REPEAT_MODE_KEY] ?: RepeatMode.NONE
            }

    suspend fun saveRepeatMode(mode: String) {
        dataStore.edit { prefs ->
            prefs[REPEAT_MODE_KEY] = mode
        }
    }

    val themeFlow: Flow<String> =
        dataStore.data
            .catch { if (it is IOException) emit(emptyPreferences()) else throw it }
            .map { it[THEME_KEY] ?: AppTheme.DEFAULT }

    suspend fun saveTheme(theme: String) {
        dataStore.edit { prefs -> prefs[THEME_KEY] = theme }
    }
}

object RepeatMode {
    const val ONE = "Repeat One"
    const val ALL = "Repeat All"
    const val NONE = "Repeat None"
}

object AppTheme {
    const val DEFAULT = "Default"
    const val LIGHT = "Light"
    const val DARK = "Dark"
}

object DisplayOptions {
    const val ALL_SONGS = "All Songs"
    const val DOWNLOADED_SONGS_ONLY = "Downloaded Songs Only"
}

object DataStoreProvider {
    @Volatile
    private var INSTANCE: DataStore<Preferences>? = null

    fun getInstance(context: Context): DataStore<Preferences> =
        INSTANCE ?: synchronized(this) {
            INSTANCE ?: PreferenceDataStoreFactory
                .create(
                    produceFile = { context.preferencesDataStoreFile("settings") },
                ).also { INSTANCE = it }
        }
}
