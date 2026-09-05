package com.akundu.kkplayer.storage

import com.akundu.kkplayer.BuildConfig
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ConstantsTest {
    private val allPaths =
        listOf(
            Constants.MEDIA_PATH,
            Constants.MUSIC_PATH,
            Constants.PICTURES_PATH,
            Constants.VIDEOS_PATH,
            Constants.DOWNLOAD_PATH,
        )

    @Test
    fun `the scoped media path is namespaced by the application id`() {
        assertEquals("/storage/emulated/0/Android/media/${BuildConfig.APPLICATION_ID}", Constants.MEDIA_PATH)
    }

    @Test
    fun `the shared public paths point at the standard folders`() {
        assertEquals("/storage/emulated/0/Music/", Constants.MUSIC_PATH)
        assertEquals("/storage/emulated/0/Pictures/", Constants.PICTURES_PATH)
        assertEquals("/storage/emulated/0/Videos/", Constants.VIDEOS_PATH)
        assertEquals("/storage/emulated/0/Download/", Constants.DOWNLOAD_PATH)
    }

    @Test
    fun `every path sits under the external storage root the file manager defaults to`() {
        allPaths.forEach { path ->
            assertTrue("$path is outside the external storage root", path.startsWith(AppFileManager.DEFAULT_EXTERNAL_STORAGE_ROOT))
        }
    }

    @Test
    fun `the paths are distinct`() {
        assertEquals(allPaths.size, allPaths.toSet().size)
    }

    @Test
    fun `only the media path is written without a trailing separator`() {
        // Callers append "/file.mp3" to MEDIA_PATH and "file.mp3" to the shared ones.
        assertFalse(Constants.MEDIA_PATH.endsWith("/"))
        allPaths.minus(Constants.MEDIA_PATH).forEach { path ->
            assertTrue("$path should end with a separator", path.endsWith("/"))
        }
    }
}
