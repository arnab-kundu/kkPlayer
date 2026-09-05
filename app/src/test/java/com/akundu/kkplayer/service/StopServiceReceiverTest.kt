package com.akundu.kkplayer.service

import android.content.Context
import android.content.Intent
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33])
class StopServiceReceiverTest {
    private val receiver = StopServiceReceiver()
    private val context: Context = mock()

    private fun intent(extra: String? = null): Intent =
        Intent(RuntimeEnvironment.getApplication(), StopServiceReceiver::class.java).apply {
            extra?.let { putExtra(it, true) }
        }

    @Test
    fun `stops the playback service when asked to stop`() {
        receiver.onReceive(context, intent("isStopService"))

        val captor = argumentCaptor<Intent>()
        verify(context).stopService(captor.capture())
        assertEquals(BackgroundSoundService::class.java.name, captor.firstValue.component?.className)
    }

    @Test
    fun `does not stop the service for other actions`() {
        receiver.onReceive(context, intent("isPauseService"))

        verify(context, never()).stopService(org.mockito.kotlin.any())
    }

    @Test
    fun `an intent without extras is ignored`() {
        receiver.onReceive(context, intent())

        verify(context, never()).stopService(org.mockito.kotlin.any())
    }

    @Test
    fun `transport actions are no-ops when the service is not running`() {
        receiver.onReceive(context, intent("isNextSong"))
        receiver.onReceive(context, intent("isPreviousSong"))
        receiver.onReceive(context, intent("isPauseService"))

        verify(context, never()).stopService(org.mockito.kotlin.any())
    }
}
