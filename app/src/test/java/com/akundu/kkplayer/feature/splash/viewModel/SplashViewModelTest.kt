package com.akundu.kkplayer.feature.splash.viewModel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.viewModelScope
import com.akundu.kkplayer.testing.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33])
class SplashViewModelTest {
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `starts in the loading state`() {
        assertTrue(SplashViewModel().isLoading.value)
    }

    @Test
    fun `stops loading after the splash delay`() =
        runTest(mainDispatcherRule.testDispatcher) {
            val viewModel = SplashViewModel()

            advanceTimeBy(1_500)
            runCurrent()

            assertFalse(viewModel.isLoading.value)
        }

    @Test
    fun `starts with the login layout visible and dots hidden`() {
        val state = SplashViewModel().uiState.value

        assertTrue(state.isLoginLayoutVisible)
        assertFalse(state.isLoadingDotsVisible)
        assertEquals("", state.email)
        assertEquals("", state.password)
    }

    @Test
    fun `loading swaps the login layout for the loading dots`() {
        val viewModel = SplashViewModel()

        viewModel.loading()

        assertTrue(viewModel.uiState.value.isLoadingDotsVisible)
        assertFalse(viewModel.uiState.value.isLoginLayoutVisible)
    }

    @Test
    fun `typing an email stores it and clears the error flags`() {
        val viewModel = SplashViewModel()
        viewModel.loginButtonClickStateChangeEvent()

        viewModel.typingEmail("user@example.com")

        assertEquals("user@example.com", viewModel.uiState.value.email)
        assertFalse(viewModel.uiState.value.emailError)
        assertFalse(viewModel.uiState.value.passwordError)
    }

    @Test
    fun `typing a password stores it and clears the error flags`() {
        val viewModel = SplashViewModel()
        viewModel.loginButtonClickStateChangeEvent()

        viewModel.typingPassword("longenoughpassword")

        assertEquals("longenoughpassword", viewModel.uiState.value.password)
        assertFalse(viewModel.uiState.value.emailError)
        assertFalse(viewModel.uiState.value.passwordError)
    }

    @Test
    fun `valid credentials switch the screen to the loading state`() {
        val viewModel = SplashViewModel()
        viewModel.typingEmail("user@example.com")
        viewModel.typingPassword("12345678")

        viewModel.loginButtonClickStateChangeEvent()

        assertTrue(viewModel.uiState.value.isLoadingDotsVisible)
        assertFalse(viewModel.uiState.value.isLoginLayoutVisible)
        assertFalse(viewModel.uiState.value.emailError)
        assertFalse(viewModel.uiState.value.passwordError)
    }

    @Test
    fun `empty credentials flag both fields`() {
        val viewModel = SplashViewModel()

        viewModel.loginButtonClickStateChangeEvent()

        assertTrue(viewModel.uiState.value.emailError)
        assertTrue(viewModel.uiState.value.passwordError)
    }

    @Test
    fun `an empty email flags only the email field`() {
        val viewModel = SplashViewModel()
        viewModel.typingPassword("12345678")

        viewModel.loginButtonClickStateChangeEvent()

        assertTrue(viewModel.uiState.value.emailError)
        assertFalse(viewModel.uiState.value.passwordError)
    }

    @Test
    fun `an empty password flags only the password field`() {
        val viewModel = SplashViewModel()
        viewModel.typingEmail("user@example.com")

        viewModel.loginButtonClickStateChangeEvent()

        assertTrue(viewModel.uiState.value.passwordError)
        assertFalse(viewModel.uiState.value.emailError)
    }

    @Test
    fun `a malformed email flags the email field`() {
        val viewModel = SplashViewModel()
        viewModel.typingEmail("not-an-email")
        viewModel.typingPassword("12345678")

        viewModel.loginButtonClickStateChangeEvent()

        assertTrue(viewModel.uiState.value.emailError)
    }

    @Test
    fun `a password of exactly seven characters is rejected`() {
        val viewModel = SplashViewModel()
        viewModel.typingEmail("user@example.com")
        viewModel.typingPassword("1234567")

        viewModel.loginButtonClickStateChangeEvent()

        assertTrue(viewModel.uiState.value.passwordError)
        assertFalse(viewModel.uiState.value.isLoadingDotsVisible)
    }

    // The method returns true even when validation fails; the source carries a `// TODO false`
    // for this. Locked in here so the behaviour cannot change silently.
    @Test
    fun `login always reports success even for invalid input`() {
        val viewModel = SplashViewModel()

        assertTrue(viewModel.loginButtonClickStateChangeEvent())

        viewModel.typingEmail("not-an-email")
        assertTrue(viewModel.loginButtonClickStateChangeEvent())
    }

    // reverseAnimation re-schedules itself forever, so the scope has to be cancelled before
    // runTest drains the remaining virtual time.
    @Test
    fun `reverseAnimation flips the animation flag`() =
        runTest(mainDispatcherRule.testDispatcher) {
            val viewModel = SplashViewModel()
            val initial = viewModel.isAnimationEndLiveData.value

            viewModel.reverseAnimation()
            runCurrent()

            assertEquals(initial?.not(), viewModel.isAnimationEndLiveData.value)
            viewModel.viewModelScope.cancel()
        }

    @Test
    fun `the animation flow alternates for six emissions`() =
        runTest(mainDispatcherRule.testDispatcher) {
            val emissions = SplashViewModel().isAnimationEndFlow.toList()

            assertEquals(6, emissions.size)
            assertEquals(listOf(false, true, false, true, false, true), emissions)
        }
}
