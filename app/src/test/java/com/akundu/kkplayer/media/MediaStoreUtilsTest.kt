package com.akundu.kkplayer.media

import android.Manifest
import android.content.Context
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33])
class MediaStoreUtilsTest {
    private lateinit var context: Context

    @Before
    fun setUp() {
        context = RuntimeEnvironment.getApplication()
    }

    @Test
    fun `reports read access when the storage permission is granted`() {
        shadowOf(RuntimeEnvironment.getApplication()).grantPermissions(Manifest.permission.READ_EXTERNAL_STORAGE)

        assertTrue(MediaStoreUtils.canReadInMediaStore(context))
    }

    @Test
    fun `reports no read access when the storage permission is denied`() {
        shadowOf(RuntimeEnvironment.getApplication()).denyPermissions(Manifest.permission.READ_EXTERNAL_STORAGE)

        assertFalse(MediaStoreUtils.canReadInMediaStore(context))
    }

    // Writing to MediaStore has not needed a permission since API 29, so this is always allowed.
    @Test
    fun `always reports write access`() {
        shadowOf(RuntimeEnvironment.getApplication()).denyPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE)

        assertTrue(MediaStoreUtils.canWriteInMediaStore(context))
    }

    @Test
    fun `creates an image entry in the media store`() =
        runTest {
            assertNotNull(MediaStoreUtils.createImageUri(context, "picture.jpg"))
        }

    @Test
    fun `creates a video entry in the media store`() =
        runTest {
            assertNotNull(MediaStoreUtils.createVideoUri(context, "clip.mp4"))
        }
}
