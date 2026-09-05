package com.akundu.kkplayer.testing

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.mutablePreferencesOf
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class FakePreferencesDataStore(
    initial: Preferences = emptyPreferences(),
) : DataStore<Preferences> {
    private val state = MutableStateFlow(initial)

    override val data: Flow<Preferences> = state.asStateFlow()

    override suspend fun updateData(transform: suspend (t: Preferences) -> Preferences): Preferences {
        val updated = transform(state.value)
        state.value = updated
        return updated
    }

    fun currentPreferences(): Preferences = state.value

    fun failingWith(error: Throwable): DataStore<Preferences> =
        object : DataStore<Preferences> {
            override val data: Flow<Preferences> = kotlinx.coroutines.flow.flow { throw error }

            override suspend fun updateData(transform: suspend (t: Preferences) -> Preferences): Preferences =
                mutablePreferencesOf().toPreferences()
        }
}
