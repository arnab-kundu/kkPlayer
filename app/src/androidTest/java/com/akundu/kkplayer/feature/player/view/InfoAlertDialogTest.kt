package com.akundu.kkplayer.feature.player.view

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.akundu.kkplayer.ui.TestTags
import org.junit.Assert.assertFalse
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class InfoAlertDialogTest {
    @get:Rule
    val composeRule = createComposeRule()

    private val openState = mutableStateOf(true)

    private fun setContent() {
        composeRule.setContent {
            InfoAlertDialog(openDialog = openState, title = "Tu hi meri sab hay", body = "Bitrate: 320kbps")
        }
    }

    @Test
    fun showsTheTitleAndBody() {
        setContent()

        composeRule.onNodeWithText("Tu hi meri sab hay").assertIsDisplayed()
        composeRule.onNodeWithText("Bitrate: 320kbps").assertIsDisplayed()
    }

    @Test
    fun confirmingClosesTheDialog() {
        setContent()

        composeRule.onNodeWithTag(TestTags.PLAYER_INFO_DIALOG_OK_BUTTON).performClick()

        assertFalse(openState.value)
        composeRule.onNodeWithText("Tu hi meri sab hay").assertDoesNotExist()
    }

    @Test
    fun dismissingClosesTheDialog() {
        setContent()

        composeRule.onNodeWithTag(TestTags.PLAYER_INFO_DIALOG_CANCEL_BUTTON).performClick()

        assertFalse(openState.value)
    }

    @Test
    fun rendersNothingWhenClosed() {
        openState.value = false
        setContent()

        composeRule.onNodeWithText("Tu hi meri sab hay").assertDoesNotExist()
    }
}
