package com.akundu.kkplayer.ui

import com.akundu.kkplayer.feature.settings.datastore.AppTheme
import com.akundu.kkplayer.feature.settings.datastore.DisplayOptions
import com.akundu.kkplayer.feature.settings.datastore.RepeatMode
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.lang.reflect.Modifier

class TestTagsTest {
    private val declaredTags: List<Pair<String, String>> =
        TestTags::class.java.declaredFields
            .filter { Modifier.isStatic(it.modifiers) && it.type == String::class.java }
            .map { field ->
                field.isAccessible = true
                field.name to field.get(null) as String
            }

    @Test
    fun `declares a tag for every screen`() {
        assertTrue(declaredTags.isNotEmpty())
        listOf("main_", "player_", "playerInfoDialog_", "settings_", "splash_").forEach { prefix ->
            assertTrue("no tag starts with $prefix", declaredTags.any { (_, tag) -> tag.startsWith(prefix) })
        }
    }

    @Test
    fun `no two nodes share a tag`() {
        // Compose finders match on the tag alone, so a duplicate would make one of the two
        // nodes unreachable from the instrumentation tests.
        val duplicates = declaredTags.groupBy { (_, tag) -> tag }.filterValues { it.size > 1 }

        assertEquals(emptyMap<String, List<Pair<String, String>>>(), duplicates)
    }

    @Test
    fun `no tag is blank or padded with whitespace`() {
        declaredTags.forEach { (name, tag) ->
            assertTrue("$name is blank", tag.isNotBlank())
            assertEquals("$name is padded with whitespace", tag.trim(), tag)
        }
    }

    @Test
    fun `settingsTheme namespaces the theme name`() {
        assertEquals("settings_theme_Dark", TestTags.settingsTheme(AppTheme.DARK))
        assertEquals("settings_theme_Light", TestTags.settingsTheme(AppTheme.LIGHT))
        assertEquals("settings_theme_Default", TestTags.settingsTheme(AppTheme.DEFAULT))
    }

    @Test
    fun `settingsRepeatMode namespaces the repeat mode`() {
        assertEquals("settings_repeat_Repeat One", TestTags.settingsRepeatMode(RepeatMode.ONE))
        assertEquals("settings_repeat_Repeat All", TestTags.settingsRepeatMode(RepeatMode.ALL))
        assertEquals("settings_repeat_Repeat None", TestTags.settingsRepeatMode(RepeatMode.NONE))
    }

    @Test
    fun `settingsDisplayOption namespaces the display option`() {
        assertEquals("settings_display_All Songs", TestTags.settingsDisplayOption(DisplayOptions.ALL_SONGS))
        assertEquals("settings_display_Downloaded Songs Only", TestTags.settingsDisplayOption(DisplayOptions.DOWNLOADED_SONGS_ONLY))
    }

    @Test
    fun `the generated tags never collide with each other`() {
        val label = "Default"

        val generated =
            setOf(
                TestTags.settingsTheme(label),
                TestTags.settingsRepeatMode(label),
                TestTags.settingsDisplayOption(label),
            )

        assertEquals(3, generated.size)
    }

    @Test
    fun `the generated tags never collide with a declared one`() {
        val declared = declaredTags.map { (_, tag) -> tag }.toSet()

        listOf(AppTheme.DEFAULT, RepeatMode.NONE, DisplayOptions.ALL_SONGS).forEach { label ->
            assertFalse(TestTags.settingsTheme(label) in declared)
            assertFalse(TestTags.settingsRepeatMode(label) in declared)
            assertFalse(TestTags.settingsDisplayOption(label) in declared)
        }
    }
}
