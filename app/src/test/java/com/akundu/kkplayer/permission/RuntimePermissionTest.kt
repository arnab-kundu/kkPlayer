package com.akundu.kkplayer.permission

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import androidx.activity.result.ActivityResultLauncher
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33])
class RuntimePermissionTest {
    private lateinit var activity: Activity
    private val launcher: ActivityResultLauncher<String> = mock()

    @Before
    fun setUp() {
        activity = Robolectric.buildActivity(Activity::class.java).create().get()
    }

    private fun grantNotificationPermission() {
        shadowOf(RuntimeEnvironment.getApplication()).grantPermissions(Manifest.permission.POST_NOTIFICATIONS)
    }

    private fun denyNotificationPermission() {
        shadowOf(RuntimeEnvironment.getApplication()).denyPermissions(Manifest.permission.POST_NOTIFICATIONS)
    }

    @Test
    fun `does not ask again once the permission is granted`() {
        grantNotificationPermission()

        RuntimePermission.askNotificationPermission(activity, launcher)

        verify(launcher, never()).launch(Manifest.permission.POST_NOTIFICATIONS)
    }

    @Test
    fun `requests the permission when it has not been granted`() {
        denyNotificationPermission()

        RuntimePermission.askNotificationPermission(activity, launcher)

        verify(launcher).launch(Manifest.permission.POST_NOTIFICATIONS)
    }

    @Test
    fun `the permission state drives the decision`() {
        denyNotificationPermission()
        RuntimePermission.askNotificationPermission(activity, launcher)

        grantNotificationPermission()
        RuntimePermission.askNotificationPermission(activity, launcher)

        verify(launcher).launch(Manifest.permission.POST_NOTIFICATIONS)
        org.junit.Assert.assertEquals(
            PackageManager.PERMISSION_GRANTED,
            activity.checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS),
        )
    }
}
