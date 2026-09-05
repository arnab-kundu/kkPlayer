package com.akundu.kkplayer.feature.settings.viewModel

import android.app.Application
import com.akundu.kkplayer.feature.settings.datastore.AppTheme
import com.akundu.kkplayer.feature.settings.datastore.DataStoreManager
import com.akundu.kkplayer.feature.settings.datastore.DisplayOptions
import com.akundu.kkplayer.feature.settings.datastore.RepeatMode
import com.akundu.kkplayer.testing.FakePreferencesDataStore
import com.akundu.kkplayer.testing.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import java.io.File

@OptIn(ExperimentalCoroutinesApi::class)
class SettingsViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @get:Rule
    val temporaryFolder = TemporaryFolder()

    private val dataStore = FakePreferencesDataStore()
    private val dataStoreManager = DataStoreManager(dataStore)
    private var databaseCleared = 0

    private lateinit var cacheDir: File
    private lateinit var externalCacheDir: File
    private lateinit var mediaDir: File

    private fun viewModel(): SettingsViewModel {
        cacheDir = temporaryFolder.newFolder("cache")
        externalCacheDir = temporaryFolder.newFolder("externalCache")
        mediaDir = temporaryFolder.newFolder("media")
        val application: Application = mock()
        whenever(application.cacheDir).thenReturn(cacheDir)
        whenever(application.externalCacheDir).thenReturn(externalCacheDir)
        return SettingsViewModel(
            application = application,
            dataStoreManager = dataStoreManager,
            clearDatabase = { databaseCleared++ },
            mediaDirectory = mediaDir,
            ioDispatcher = mainDispatcherRule.testDispatcher,
        )
    }

    @Test
    fun `exposes stored defaults on creation`() =
        runTest(mainDispatcherRule.testDispatcher) {
            val model = viewModel()
            advanceUntilIdle()

            assertEquals(RepeatMode.NONE, model.repeatMode.value)
            assertEquals(AppTheme.DEFAULT, model.theme.value)
            assertEquals(DisplayOptions.ALL_SONGS, model.displayOption.value)
        }

    @Test
    fun `selecting a repeat mode persists it and updates the exposed state`() =
        runTest(mainDispatcherRule.testDispatcher) {
            val model = viewModel()

            model.onRepeatModeSelected(RepeatMode.ALL)
            advanceUntilIdle()

            assertEquals(RepeatMode.ALL, model.repeatMode.value)
        }

    @Test
    fun `selecting a theme persists it and updates the exposed state`() =
        runTest(mainDispatcherRule.testDispatcher) {
            val model = viewModel()

            model.onThemeSelected(AppTheme.DARK)
            advanceUntilIdle()

            assertEquals(AppTheme.DARK, model.theme.value)
        }

    @Test
    fun `selecting a display option persists it and updates the exposed state`() =
        runTest(mainDispatcherRule.testDispatcher) {
            val model = viewModel()

            model.onDisplayOptionSelected(DisplayOptions.DOWNLOADED_SONGS_ONLY)
            advanceUntilIdle()

            assertEquals(DisplayOptions.DOWNLOADED_SONGS_ONLY, model.displayOption.value)
        }

    @Test
    fun `clearing the cache empties both cache directories`() {
        val model = viewModel()
        val cached = File(cacheDir, "cached.tmp").apply { writeText("cached") }
        val externallyCached = File(externalCacheDir, "external.tmp").apply { writeText("external") }

        model.clearCacheDirs()

        assertFalse(cached.exists())
        assertFalse(externallyCached.exists())
    }

    @Test
    fun `clearing the cache removes nested directories`() {
        val model = viewModel()
        val nested = File(cacheDir, "images").apply { mkdirs() }
        val nestedFile = File(nested, "art.png").apply { writeText("art") }

        model.clearCacheDirs()

        assertFalse(nestedFile.exists())
        assertFalse(nested.exists())
    }

    @Test
    fun `clearing the database delegates to the injected action`() =
        runTest(mainDispatcherRule.testDispatcher) {
            val model = viewModel()

            model.onClearDatabase()
            advanceUntilIdle()

            assertEquals(1, databaseCleared)
        }

    @Test
    fun `clearing all data wipes cache media database and preferences`() =
        runTest(mainDispatcherRule.testDispatcher) {
            val model = viewModel()
            dataStoreManager.saveTheme(AppTheme.DARK)
            val cached = File(cacheDir, "cached.tmp").apply { writeText("cached") }
            val song = File(mediaDir, "song.mp3").apply { writeText("song") }

            model.clearAllData()
            advanceUntilIdle()

            assertFalse(cached.exists())
            assertFalse(song.exists())
            assertEquals(1, databaseCleared)
            assertEquals(AppTheme.DEFAULT, model.theme.value)
        }

    @Test
    fun `clearing the cache tolerates a missing external cache directory`() {
        val application: Application = mock()
        whenever(application.cacheDir).thenReturn(temporaryFolder.newFolder("onlyCache"))
        whenever(application.externalCacheDir).thenReturn(null)
        val model =
            SettingsViewModel(
                application = application,
                dataStoreManager = dataStoreManager,
                clearDatabase = { databaseCleared++ },
                mediaDirectory = temporaryFolder.newFolder("onlyMedia"),
                ioDispatcher = mainDispatcherRule.testDispatcher,
            )

        model.clearCacheDirs()

        assertTrue(databaseCleared == 0)
    }
}
