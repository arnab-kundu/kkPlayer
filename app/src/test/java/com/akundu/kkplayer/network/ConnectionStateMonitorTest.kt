package com.akundu.kkplayer.network

import android.content.Context
import android.net.ConnectivityManager
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
class ConnectionStateMonitorTest {
    private lateinit var context: Context
    private lateinit var connectivityManager: ConnectivityManager

    @Before
    fun setUp() {
        context = RuntimeEnvironment.getApplication()
        connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    }

    @Test
    fun `registers itself with the connectivity manager`() {
        val monitor = ConnectionStateMonitor()

        monitor.enable(context)

        assertTrue(shadowOf(connectivityManager).networkCallbacks.contains(monitor))
    }

    @Test
    fun `each monitor registers separately`() {
        val first = ConnectionStateMonitor()
        val second = ConnectionStateMonitor()

        first.enable(context)
        second.enable(context)

        assertTrue(shadowOf(connectivityManager).networkCallbacks.containsAll(listOf(first, second)))
    }
}
