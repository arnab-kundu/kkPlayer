package com.akundu.kkplayer.provider

import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Granting a content URI needs FileProvider to resolve a real external-storage path, which
 * Robolectric cannot reproduce faithfully on every host platform. The sharing behaviour is
 * therefore left to on-device testing; only the sharing targets are pinned here.
 */
class FileAccessPermissionProviderTest {
    @Test
    fun `names the music apps it can share with`() {
        assertEquals("com.google.android.apps.youtube.music", FileAccessPermissionProvider.PACKAGE_NAME_OF_YOUTUBE_MUSIC)
        assertEquals("com.bsbportal.music", FileAccessPermissionProvider.PACKAGE_NAME_OF_WYNK_MUSIC)
    }
}
