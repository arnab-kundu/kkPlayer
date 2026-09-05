package com.akundu.kkplayer.feature.settings.view.ui

import androidx.compose.ui.Modifier
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotSelected
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.akundu.kkplayer.feature.settings.datastore.AppTheme
import com.akundu.kkplayer.feature.settings.datastore.DisplayOptions
import com.akundu.kkplayer.feature.settings.datastore.RepeatMode
import com.akundu.kkplayer.ui.TestTags
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SettingsScreenTest {
    @get:Rule
    val composeRule = createComposeRule()

    private val selectedThemes = mutableListOf<String>()
    private val selectedRepeatModes = mutableListOf<String>()
    private val selectedDisplayModes = mutableListOf<String>()
    private var clearCacheCount = 0
    private var clearDatabaseCount = 0
    private var clearDataCount = 0
    private var backCount = 0

    private fun setContent(
        theme: String = AppTheme.DEFAULT,
        repeatMode: String = RepeatMode.NONE,
        displayMode: String = DisplayOptions.ALL_SONGS,
    ) {
        composeRule.setContent {
            SettingsScreen(
                modifier = Modifier,
                backClick = { backCount++ },
                selectedTheme = theme,
                onThemeSelected = { selectedThemes += it },
                selectedRepeatMode = repeatMode,
                onRepeatModeSelected = { selectedRepeatModes += it },
                selectedDisplayMode = displayMode,
                onDisplayModeSelected = { selectedDisplayModes += it },
                onClearCache = { clearCacheCount++ },
                onClearDatabase = { clearDatabaseCount++ },
                onClearData = { clearDataCount++ },
            )
        }
    }

    @Test
    fun rendersEverySettingsSection() {
        setContent()

        composeRule.onNodeWithText("Settings").assertIsDisplayed()
        composeRule.onNodeWithText("Theme").assertIsDisplayed()
        composeRule.onNodeWithText("Repeat Mode").assertIsDisplayed()
        composeRule.onNodeWithText("Display Options").assertIsDisplayed()
    }

    @Test
    fun marksTheStoredThemeAsSelected() {
        setContent(theme = AppTheme.DARK)

        composeRule.onNodeWithTag(TestTags.settingsTheme(AppTheme.DARK)).assertIsSelected()
        composeRule.onNodeWithTag(TestTags.settingsTheme(AppTheme.LIGHT)).assertIsNotSelected()
    }

    @Test
    fun selectingAThemeReportsTheChoice() {
        setContent()

        composeRule.onNodeWithTag(TestTags.settingsTheme(AppTheme.DARK)).performClick()

        assertEquals(listOf(AppTheme.DARK), selectedThemes)
    }

    @Test
    fun marksTheStoredRepeatModeAsSelected() {
        setContent(repeatMode = RepeatMode.ALL)

        composeRule.onNodeWithTag(TestTags.settingsRepeatMode(RepeatMode.ALL)).assertIsSelected()
        composeRule.onNodeWithTag(TestTags.settingsRepeatMode(RepeatMode.ONE)).assertIsNotSelected()
    }

    @Test
    fun selectingARepeatModeReportsTheChoice() {
        setContent()

        composeRule.onNodeWithTag(TestTags.settingsRepeatMode(RepeatMode.ONE)).performClick()

        assertEquals(listOf(RepeatMode.ONE), selectedRepeatModes)
    }

    @Test
    fun selectingADisplayOptionReportsTheChoice() {
        setContent()

        composeRule.onNodeWithTag(TestTags.settingsDisplayOption(DisplayOptions.DOWNLOADED_SONGS_ONLY)).performClick()

        assertEquals(listOf(DisplayOptions.DOWNLOADED_SONGS_ONLY), selectedDisplayModes)
    }

    @Test
    fun marksTheStoredDisplayOptionAsSelected() {
        setContent(displayMode = DisplayOptions.DOWNLOADED_SONGS_ONLY)

        composeRule.onNodeWithTag(TestTags.settingsDisplayOption(DisplayOptions.DOWNLOADED_SONGS_ONLY)).assertIsSelected()
    }

    @Test
    fun clearCacheButtonReportsTheAction() {
        setContent()

        composeRule.onNodeWithTag(TestTags.SETTINGS_CLEAR_CACHE_BUTTON).performScrollTo().performClick()

        assertEquals(1, clearCacheCount)
        assertEquals(0, clearDatabaseCount)
        assertEquals(0, clearDataCount)
    }

    @Test
    fun clearDatabaseButtonReportsTheAction() {
        setContent()

        composeRule.onNodeWithTag(TestTags.SETTINGS_CLEAR_DATABASE_BUTTON).performScrollTo().performClick()

        assertEquals(1, clearDatabaseCount)
        assertEquals(0, clearCacheCount)
    }

    @Test
    fun clearAllDataButtonReportsTheAction() {
        setContent()

        composeRule.onNodeWithTag(TestTags.SETTINGS_CLEAR_ALL_DATA_BUTTON).performScrollTo().performClick()

        assertEquals(1, clearDataCount)
    }

    @Test
    fun backButtonReportsTheAction() {
        setContent()

        composeRule.onNodeWithTag(TestTags.SETTINGS_BACK_BUTTON).performClick()

        assertEquals(1, backCount)
    }
}
