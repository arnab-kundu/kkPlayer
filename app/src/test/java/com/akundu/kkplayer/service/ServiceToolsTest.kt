package com.akundu.kkplayer.service

import android.app.ActivityManager
import android.content.ComponentName
import android.content.Context
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33])
class ServiceToolsTest {
    private val activityManager: ActivityManager = mock()
    private val context: Context = mock()

    @Before
    fun setUp() {
        whenever(context.getSystemService(Context.ACTIVITY_SERVICE)).thenReturn(activityManager)
    }

    @Suppress("DEPRECATION")
    private fun runningService(className: String): ActivityManager.RunningServiceInfo =
        ActivityManager.RunningServiceInfo().apply {
            service = ComponentName("com.akundu.kkplayer", className)
        }

    @Suppress("DEPRECATION")
    @Test
    fun `reports a running service`() {
        whenever(activityManager.getRunningServices(any())).thenReturn(
            listOf(runningService("com.akundu.kkplayer.service.BackgroundSoundService")),
        )

        assertTrue(ServiceTools.isServiceRunning(context, "com.akundu.kkplayer.service.BackgroundSoundService"))
    }

    @Suppress("DEPRECATION")
    @Test
    fun `reports a service that is not running`() {
        whenever(activityManager.getRunningServices(any())).thenReturn(
            listOf(runningService("com.example.OtherService")),
        )

        assertFalse(ServiceTools.isServiceRunning(context, "com.akundu.kkplayer.service.BackgroundSoundService"))
    }

    @Suppress("DEPRECATION")
    @Test
    fun `reports nothing running when the list is empty`() {
        whenever(activityManager.getRunningServices(any())).thenReturn(emptyList())

        assertFalse(ServiceTools.isServiceRunning(context, "com.akundu.kkplayer.service.BackgroundSoundService"))
    }
}
