package com.akundu.kkplayer.feature.settings.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.core.IOException
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.emptyPreferences
import com.akundu.kkplayer.testing.FakePreferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class DataStoreManagerTest {
    private val dataStore = FakePreferencesDataStore()
    private val manager = DataStoreManager(dataStore)

    @Test
    fun `repeat mode defaults to none`() =
        runTest {
            assertEquals(RepeatMode.NONE, manager.repeatModeFlow.first())
        }

    @Test
    fun `theme defaults to default`() =
        runTest {
            assertEquals(AppTheme.DEFAULT, manager.themeFlow.first())
        }

    @Test
    fun `display option defaults to all songs`() =
        runTest {
            assertEquals(DisplayOptions.ALL_SONGS, manager.displayOptionFlow.first())
        }

    @Test
    fun `saved repeat mode is read back`() =
        runTest {
            manager.saveRepeatMode(RepeatMode.ALL)

            assertEquals(RepeatMode.ALL, manager.repeatModeFlow.first())
        }

    @Test
    fun `saved theme is read back`() =
        runTest {
            manager.saveTheme(AppTheme.DARK)

            assertEquals(AppTheme.DARK, manager.themeFlow.first())
        }

    @Test
    fun `saved display option is read back`() =
        runTest {
            manager.saveDisplayOption(DisplayOptions.DOWNLOADED_SONGS_ONLY)

            assertEquals(DisplayOptions.DOWNLOADED_SONGS_ONLY, manager.displayOptionFlow.first())
        }

    @Test
    fun `each preference is stored independently`() =
        runTest {
            manager.saveRepeatMode(RepeatMode.ONE)
            manager.saveTheme(AppTheme.LIGHT)
            manager.saveDisplayOption(DisplayOptions.DOWNLOADED_SONGS_ONLY)

            assertEquals(RepeatMode.ONE, manager.repeatModeFlow.first())
            assertEquals(AppTheme.LIGHT, manager.themeFlow.first())
            assertEquals(DisplayOptions.DOWNLOADED_SONGS_ONLY, manager.displayOptionFlow.first())
        }

    @Test
    fun `clearAll restores every default`() =
        runTest {
            manager.saveRepeatMode(RepeatMode.ONE)
            manager.saveTheme(AppTheme.DARK)
            manager.saveDisplayOption(DisplayOptions.DOWNLOADED_SONGS_ONLY)

            manager.clearAll()

            assertEquals(RepeatMode.NONE, manager.repeatModeFlow.first())
            assertEquals(AppTheme.DEFAULT, manager.themeFlow.first())
            assertEquals(DisplayOptions.ALL_SONGS, manager.displayOptionFlow.first())
        }

    @Test
    fun `io errors fall back to empty preferences`() =
        runTest {
            val failingStore =
                object : DataStore<Preferences> {
                    override val data: Flow<Preferences> = flow { throw IOException("disk gone") }

                    override suspend fun updateData(transform: suspend (t: Preferences) -> Preferences) = emptyPreferences()
                }
            val failingManager = DataStoreManager(failingStore)

            assertEquals(RepeatMode.NONE, failingManager.repeatModeFlow.first())
            assertEquals(AppTheme.DEFAULT, failingManager.themeFlow.first())
            assertEquals(DisplayOptions.ALL_SONGS, failingManager.displayOptionFlow.first())
        }

    @Test(expected = IllegalStateException::class)
    fun `non io errors propagate`() =
        runTest {
            val failingStore =
                object : DataStore<Preferences> {
                    override val data: Flow<Preferences> = flow { throw IllegalStateException("boom") }

                    override suspend fun updateData(transform: suspend (t: Preferences) -> Preferences) = emptyPreferences()
                }

            DataStoreManager(failingStore).repeatModeFlow.first()
        }

    @Test
    fun `repeat mode constants match the settings screen labels`() {
        assertEquals("Repeat One", RepeatMode.ONE)
        assertEquals("Repeat All", RepeatMode.ALL)
        assertEquals("Repeat None", RepeatMode.NONE)
    }

    @Test
    fun `theme and display option constants match the settings screen labels`() {
        assertEquals("Default", AppTheme.DEFAULT)
        assertEquals("Light", AppTheme.LIGHT)
        assertEquals("Dark", AppTheme.DARK)
        assertEquals("All Songs", DisplayOptions.ALL_SONGS)
        assertEquals("Downloaded Songs Only", DisplayOptions.DOWNLOADED_SONGS_ONLY)
    }
}
