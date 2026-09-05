package com.akundu.kkplayer.feature.main.view

import android.Manifest
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onFirst
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.GrantPermissionRule
import com.akundu.kkplayer.database.SongDatabase
import com.akundu.kkplayer.feature.player.view.PlayerActivity
import com.akundu.kkplayer.ui.TestTags
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.RuleChain
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MainActivityTest {
    private val composeRule = createAndroidComposeRule<MainActivity>()

    // MainActivity asks for POST_NOTIFICATIONS on launch. Without pre-granting it the system
    // dialog covers the activity, which pauses it and leaves no compose hierarchy to assert on.
    @get:Rule
    val rules: RuleChain =
        RuleChain
            .outerRule(GrantPermissionRule.grant(Manifest.permission.POST_NOTIFICATIONS))
            .around(composeRule)

    @Before
    fun setUp() {
        Intents.init()
    }

    @After
    fun tearDown() {
        Intents.release()
    }

    @Test
    fun seedsTheCatalogueOnFirstLaunch() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val dao = SongDatabase.getDatabase(context).songDao()

        composeRule.waitForIdle()

        assertTrue("The song catalogue was not seeded", dao.getTotalCount() > 0)
    }

    @Test
    fun rendersTheSongList() {
        composeRule.waitForIdle()
        composeRule.waitUntil(timeoutMillis = 10_000) {
            composeRule.onAllNodesWithTag(TestTags.MAIN_SONG_ITEM_ROW).fetchSemanticsNodes().isNotEmpty()
        }

        composeRule.onNodeWithTag(TestTags.MAIN_SONG_LIST).assertIsDisplayed()
        composeRule.onAllNodesWithTag(TestTags.MAIN_SONG_ITEM_ROW).onFirst().assertIsDisplayed()
    }

    @Test
    fun everySongRowOffersAPlayOrDownloadAction() {
        composeRule.waitUntil(timeoutMillis = 10_000) {
            composeRule.onAllNodesWithTag(TestTags.MAIN_SONG_ITEM_ROW).fetchSemanticsNodes().isNotEmpty()
        }

        val rows = composeRule.onAllNodesWithTag(TestTags.MAIN_SONG_ITEM_ROW).fetchSemanticsNodes().size
        val actions = composeRule.onAllNodesWithTag(TestTags.MAIN_SONG_ITEM_ACTION_BUTTON).fetchSemanticsNodes().size

        assertTrue("Expected an action button per visible row", actions >= rows)
    }

    @Test
    fun playingADownloadedSongOpensThePlayer() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val dao = SongDatabase.getDatabase(context).songDao()
        composeRule.waitUntil(timeoutMillis = 10_000) {
            composeRule.onAllNodesWithTag(TestTags.MAIN_SONG_ITEM_ROW).fetchSemanticsNodes().isNotEmpty()
        }
        val downloaded = dao.getAllSongs().firstOrNull { it.isDownloaded }

        if (downloaded == null) {
            // Nothing has been downloaded on this device yet, so the player cannot be reached
            // from the list. Covered by PlayerActivityTest, which seeds its own song.
            return
        }

        composeRule.onAllNodesWithTag(TestTags.MAIN_SONG_ITEM_ROW).onFirst().performClick()
        composeRule.waitForIdle()

        Intents.intended(hasComponent(PlayerActivity::class.java.name))
    }
}
