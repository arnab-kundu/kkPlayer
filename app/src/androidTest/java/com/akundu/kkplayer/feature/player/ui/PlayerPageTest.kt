package com.akundu.kkplayer.feature.player.ui

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.ImageBitmapConfig
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performSemanticsAction
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeUp
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.akundu.kkplayer.database.entity.SongEntity
import com.akundu.kkplayer.feature.player.viewModel.PlayerViewModel
import com.akundu.kkplayer.service.playback.PlaybackController
import com.akundu.kkplayer.ui.TestTags
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PlayerPageTest {
    @get:Rule
    val composeRule = createComposeRule()

    private var playCount = 0
    private var pauseCount = 0
    private var nextCount = 0
    private var previousCount = 0
    private var backCount = 0
    private var settingsCount = 0
    private val seekPositions = mutableListOf<Int>()
    private val volumeDeltas = mutableListOf<Float>()

    private val song =
        SongEntity(
            id = 1L,
            title = "Tu hi meri sab hay",
            artist = "KK",
            fileName = "tu_hi.mp3",
            url = "",
            movie = "Black",
            isDownloaded = true,
        )

    private class FakeController(
        private val playing: Boolean,
    ) : PlaybackController {
        override fun playPausePlayer() = Unit

        override fun nextSong() = Unit

        override fun previousSong() = Unit

        override fun seekTo(positionMs: Int) = Unit

        override fun isCurrentlyPlaying() = playing

        override fun getCurrentSongId() = 1

        override fun getCurrentPositionMs() = 0

        override fun getDurationMs() = 0
    }

    private fun setContent(
        isPlaying: Boolean = true,
        duration: Int = 240,
        currentPosition: Int = 30,
    ) {
        composeRule.setContent {
            PlayerPage(
                viewModel = PlayerViewModel(playbackController = { FakeController(isPlaying) }, pollIntervalMs = 60_000),
                song = song,
                duration = duration,
                currentPosition = currentPosition,
                bitmap = ImageBitmap(120, 120, ImageBitmapConfig.Rgb565),
                playClick = { playCount++ },
                pauseClick = { pauseCount++ },
                nextClick = { nextCount++ },
                previousClick = { previousCount++ },
                onSeek = { seekPositions += it },
                onVolumeChange = { volumeDeltas += it },
                backClick = { backCount++ },
                settingsClick = { settingsCount++ },
            )
        }
    }

    @Test
    fun rendersTheSongDetails() {
        setContent()

        composeRule.onNodeWithText("Tu hi meri sab hay").assertIsDisplayed()
        composeRule.onNodeWithText("KK").assertIsDisplayed()
        composeRule.onNodeWithTag(TestTags.PLAYER_ALBUM_ART).assertIsDisplayed()
    }

    @Test
    fun rendersTheTransportControls() {
        setContent()

        composeRule.onNodeWithTag(TestTags.PLAYER_PREVIOUS_BUTTON).assertIsDisplayed()
        composeRule.onNodeWithTag(TestTags.PLAYER_PLAY_PAUSE_BUTTON).assertIsDisplayed()
        composeRule.onNodeWithTag(TestTags.PLAYER_NEXT_BUTTON).assertIsDisplayed()
        composeRule.onNodeWithTag(TestTags.PLAYER_SEEK_BAR).assertIsDisplayed()
    }

    @Test
    fun tappingPauseWhilePlayingReportsPause() {
        setContent(isPlaying = true)

        composeRule.onNodeWithTag(TestTags.PLAYER_PLAY_PAUSE_BUTTON).performClick()

        assertEquals(1, pauseCount)
        assertEquals(0, playCount)
    }

    @Test
    fun tappingPlayWhilePausedReportsPlay() {
        setContent(isPlaying = false)

        composeRule.onNodeWithTag(TestTags.PLAYER_PLAY_PAUSE_BUTTON).performClick()

        assertEquals(1, playCount)
        assertEquals(0, pauseCount)
    }

    @Test
    fun tappingNextReportsTheSkip() {
        setContent()

        composeRule.onNodeWithTag(TestTags.PLAYER_NEXT_BUTTON).performClick()

        assertEquals(1, nextCount)
    }

    @Test
    fun tappingPreviousReportsTheSkip() {
        setContent()

        composeRule.onNodeWithTag(TestTags.PLAYER_PREVIOUS_BUTTON).performClick()

        assertEquals(1, previousCount)
    }

    @Test
    fun tappingBackReportsTheNavigation() {
        setContent()

        composeRule.onNodeWithTag(TestTags.PLAYER_BACK_BUTTON).performClick()

        assertEquals(1, backCount)
    }

    @Test
    fun tappingSettingsReportsTheNavigation() {
        setContent()

        composeRule.onNodeWithTag(TestTags.PLAYER_SETTINGS_BUTTON).performClick()

        assertEquals(1, settingsCount)
    }

    @Test
    fun draggingTheSeekBarReportsTheNewPosition() {
        setContent(duration = 240, currentPosition = 30)

        composeRule.onNodeWithTag(TestTags.PLAYER_SEEK_BAR).performSemanticsAction(SemanticsActions.SetProgress) { it(120f) }

        assertEquals(listOf(120), seekPositions)
    }

    @Test
    fun swipingTheVolumeZoneReportsVolumeChanges() {
        setContent()

        composeRule.onNodeWithTag(TestTags.PLAYER_VOLUME_SWIPE_ZONE).performTouchInput { swipeUp() }

        assertTrue(volumeDeltas.isNotEmpty())
        assertTrue(volumeDeltas.all { it == 1f })
    }
}
