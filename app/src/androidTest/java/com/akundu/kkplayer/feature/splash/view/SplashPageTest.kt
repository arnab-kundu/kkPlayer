package com.akundu.kkplayer.feature.splash.view

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsOff
import androidx.compose.ui.test.assertIsOn
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.akundu.kkplayer.feature.splash.model.SplashUiState
import com.akundu.kkplayer.feature.splash.viewModel.SplashViewModel
import com.akundu.kkplayer.ui.TestTags
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SplashPageTest {
    @get:Rule
    val composeRule = createComposeRule()

    private var loginClicks = 0

    private fun setContent(
        uiState: SplashUiState = SplashUiState(),
        viewModel: SplashViewModel = SplashViewModel(),
    ) {
        composeRule.setContent {
            SplashPage(
                viewModel = viewModel,
                version = "v9.9.9",
                uiState = uiState,
                loginButtonClick = { loginClicks++ },
            )
        }
    }

    @Test
    fun rendersTheBrandingAndVersion() {
        setContent()

        composeRule.onNodeWithTag(TestTags.SPLASH_LOGO).assertIsDisplayed()
        composeRule.onNodeWithTag(TestTags.SPLASH_VERSION_TEXT).assertIsDisplayed()
        composeRule.onNodeWithText("v9.9.9").assertIsDisplayed()
    }

    @Test
    fun showsTheLoginFormByDefault() {
        setContent()

        composeRule.onNodeWithTag(TestTags.SPLASH_EMAIL_FIELD).assertIsDisplayed()
        composeRule.onNodeWithTag(TestTags.SPLASH_PASSWORD_FIELD).assertIsDisplayed()
        composeRule.onNodeWithTag(TestTags.SPLASH_LOGIN_BUTTON).assertIsDisplayed()
    }

    @Test
    fun hidesTheLoginFormWhileLoading() {
        setContent(uiState = SplashUiState(isLoginLayoutVisible = false, isLoadingDotsVisible = true))

        composeRule.onNodeWithTag(TestTags.SPLASH_EMAIL_FIELD).assertDoesNotExist()
        composeRule.onNodeWithTag(TestTags.SPLASH_LOADING_DOTS).assertIsDisplayed()
    }

    @Test
    fun hidesTheLoadingDotsWhileTheFormIsShown() {
        setContent()

        composeRule.onNodeWithTag(TestTags.SPLASH_LOADING_DOTS).assertDoesNotExist()
    }

    @Test
    fun typingAnEmailReachesTheViewModel() {
        val viewModel = SplashViewModel()
        setContent(viewModel = viewModel)

        composeRule.onNodeWithTag(TestTags.SPLASH_EMAIL_FIELD).performTextInput("user@example.com")

        assertEquals("user@example.com", viewModel.uiState.value.email)
    }

    @Test
    fun typingAPasswordReachesTheViewModel() {
        val viewModel = SplashViewModel()
        setContent(viewModel = viewModel)

        composeRule.onNodeWithTag(TestTags.SPLASH_PASSWORD_FIELD).performTextInput("supersecret")

        assertEquals("supersecret", viewModel.uiState.value.password)
    }

    @Test
    fun rendersStoredCredentials() {
        setContent(uiState = SplashUiState(email = "stored@example.com"))

        composeRule.onNodeWithText("stored@example.com").assertIsDisplayed()
    }

    @Test
    fun tappingLoginReportsTheClick() {
        setContent()

        composeRule.onNodeWithTag(TestTags.SPLASH_LOGIN_BUTTON).performClick()

        assertEquals(1, loginClicks)
    }

    @Test
    fun theBiometricSwitchToggles() {
        setContent()

        composeRule.onNodeWithTag(TestTags.SPLASH_BIOMETRIC_SWITCH).assertIsOff()
        composeRule.onNodeWithTag(TestTags.SPLASH_BIOMETRIC_SWITCH).performClick()
        composeRule.onNodeWithTag(TestTags.SPLASH_BIOMETRIC_SWITCH).assertIsOn()
    }

    @Test
    fun showsTheForgotPasswordAffordance() {
        setContent()

        composeRule.onNodeWithTag(TestTags.SPLASH_FORGOT_PASSWORD_TEXT).assertIsDisplayed()
    }
}
