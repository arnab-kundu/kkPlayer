package com.akundu.kkplayer.feature.settings.view

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.akundu.kkplayer.feature.settings.datastore.AppTheme
import com.akundu.kkplayer.feature.settings.datastore.DataStoreManager
import com.akundu.kkplayer.feature.settings.datastore.DisplayOptions
import com.akundu.kkplayer.feature.settings.datastore.RepeatMode
import com.akundu.kkplayer.ui.TestTags
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SettingsActivityTest {
    @get:Rule
    val composeRule = createAndroidComposeRule<SettingsActivity>()

    private val dataStoreManager = DataStoreManager(InstrumentationRegistry.getInstrumentation().targetContext)

    @After
    fun tearDown() =
        runBlocking {
            dataStoreManager.clearAll()
        }

    private fun storedRepeatMode() = runBlocking { dataStoreManager.repeatModeFlow.first() }

    private fun storedTheme() = runBlocking { dataStoreManager.themeFlow.first() }

    private fun storedDisplayOption() = runBlocking { dataStoreManager.displayOptionFlow.first() }

    @Test
    fun selectingAThemePersistsIt() {
        composeRule.onNodeWithTag(TestTags.settingsTheme(AppTheme.DARK)).performScrollTo().performClick()
        composeRule.waitForIdle()

        composeRule.waitUntil(timeoutMillis = 5_000) { storedTheme() == AppTheme.DARK }
        assertEquals(AppTheme.DARK, storedTheme())
    }

    @Test
    fun selectingARepeatModePersistsIt() {
        composeRule.onNodeWithTag(TestTags.settingsRepeatMode(RepeatMode.ALL)).performScrollTo().performClick()
        composeRule.waitForIdle()

        composeRule.waitUntil(timeoutMillis = 5_000) { storedRepeatMode() == RepeatMode.ALL }
        assertEquals(RepeatMode.ALL, storedRepeatMode())
    }

    @Test
    fun selectingADisplayOptionPersistsIt() {
        composeRule
            .onNodeWithTag(TestTags.settingsDisplayOption(DisplayOptions.DOWNLOADED_SONGS_ONLY))
            .performScrollTo()
            .performClick()
        composeRule.waitForIdle()

        composeRule.waitUntil(timeoutMillis = 5_000) { storedDisplayOption() == DisplayOptions.DOWNLOADED_SONGS_ONLY }
        assertEquals(DisplayOptions.DOWNLOADED_SONGS_ONLY, storedDisplayOption())
    }

    @Test
    fun theSelectionSurvivesRecreation() {
        composeRule.onNodeWithTag(TestTags.settingsTheme(AppTheme.LIGHT)).performScrollTo().performClick()
        composeRule.waitUntil(timeoutMillis = 5_000) { storedTheme() == AppTheme.LIGHT }

        composeRule.activityRule.scenario.recreate()
        composeRule.waitForIdle()

        composeRule.onNodeWithTag(TestTags.settingsTheme(AppTheme.LIGHT)).performScrollTo().assertExists()
        assertEquals(AppTheme.LIGHT, storedTheme())
    }

    @Test
    fun clearingTheCacheEmptiesTheCacheDirectory() {
        val cacheDir = InstrumentationRegistry.getInstrumentation().targetContext.cacheDir
        val cached = java.io.File(cacheDir, "settings_activity_cached.tmp").apply { writeText("cached") }

        composeRule.onNodeWithTag(TestTags.SETTINGS_CLEAR_CACHE_BUTTON).performScrollTo().performClick()

        composeRule.waitUntil(timeoutMillis = 5_000) { !cached.exists() }
    }

    @Test
    fun backNavigationFinishesTheScreen() {
        composeRule.onNodeWithTag(TestTags.SETTINGS_BACK_BUTTON).performClick()

        composeRule.waitUntil(timeoutMillis = 5_000) { composeRule.activity.isFinishing }
    }
}
