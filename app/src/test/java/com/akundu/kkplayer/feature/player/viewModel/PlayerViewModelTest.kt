package com.akundu.kkplayer.feature.player.viewModel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.viewModelScope
import com.akundu.kkplayer.service.playback.PlaybackController
import com.akundu.kkplayer.testing.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
class PlayerViewModelTest {
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val controller: PlaybackController = mock()
    private val createdViewModels = mutableListOf<PlayerViewModel>()

    private fun viewModel(
        provider: () -> PlaybackController? = { controller },
        pollIntervalMs: Long = 500,
    ) = PlayerViewModel(playbackController = provider, pollIntervalMs = pollIntervalMs).also { createdViewModels += it }

    // The polling loop never completes on its own, so it has to be cancelled or runTest
    // will keep draining it in virtual time until the heap gives out.
    @After
    fun cancelPolling() {
        createdViewModels.forEach { it.viewModelScope.cancel() }
    }

    @Test
    fun `playPauseToggle delegates to the controller`() {
        viewModel().playPauseToggle()

        verify(controller).playPausePlayer()
    }

    @Test
    fun `nextSong delegates to the controller`() {
        viewModel().nextSong()

        verify(controller).nextSong()
    }

    @Test
    fun `previousSong delegates to the controller`() {
        viewModel().previousSong()

        verify(controller).previousSong()
    }

    @Test
    fun `seekTo forwards the position and updates the observable position`() {
        val model = viewModel()

        model.seekTo(4200)

        verify(controller).seekTo(4200)
        assertEquals(4200, model.currentPositionMs.value)
    }

    @Test
    fun `seekTo still updates the observable position when no service is bound`() {
        val model = viewModel(provider = { null })

        model.seekTo(1500)

        assertEquals(1500, model.currentPositionMs.value)
    }

    @Test
    fun `commands are no-ops when no service is bound`() {
        val model = viewModel(provider = { null })

        model.playPauseToggle()
        model.nextSong()
        model.previousSong()

        verify(controller, never()).playPausePlayer()
        verify(controller, never()).nextSong()
        verify(controller, never()).previousSong()
    }

    @Test
    fun `exposes default playback state before polling runs`() {
        val model = viewModel()

        assertTrue(model.isPlaying.value == true)
        assertEquals(0, model.currentSongId.value)
        assertEquals(0, model.currentPositionMs.value)
        assertEquals(0, model.durationMs.value)
    }

    @Test
    fun `polling copies playback state from the controller`() =
        runTest(mainDispatcherRule.testDispatcher) {
            whenever(controller.isCurrentlyPlaying()).thenReturn(false)
            whenever(controller.getCurrentSongId()).thenReturn(7)
            whenever(controller.getCurrentPositionMs()).thenReturn(30_000)
            whenever(controller.getDurationMs()).thenReturn(210_000)

            val model = viewModel()
            runCurrent()

            assertFalse(model.isPlaying.value == true)
            assertEquals(7, model.currentSongId.value)
            assertEquals(30_000, model.currentPositionMs.value)
            assertEquals(210_000, model.durationMs.value)

            model.viewModelScope.cancel()
        }

    @Test
    fun `polling keeps refreshing on each interval`() =
        runTest(mainDispatcherRule.testDispatcher) {
            whenever(controller.getCurrentPositionMs()).thenReturn(1_000, 2_000, 3_000)

            val model = viewModel(pollIntervalMs = 500)
            runCurrent()
            assertEquals(1_000, model.currentPositionMs.value)

            advanceTimeBy(500)
            runCurrent()
            assertEquals(2_000, model.currentPositionMs.value)

            advanceTimeBy(500)
            runCurrent()
            assertEquals(3_000, model.currentPositionMs.value)

            model.viewModelScope.cancel()
        }
}
