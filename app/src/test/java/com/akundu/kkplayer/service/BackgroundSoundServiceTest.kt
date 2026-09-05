package com.akundu.kkplayer.service

import android.content.Intent
import android.os.Looper
import com.akundu.kkplayer.service.playback.PlaybackController
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertSame
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.Shadows.shadowOf
import org.robolectric.android.controller.ServiceController
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33])
class BackgroundSoundServiceTest {
    private lateinit var controller: ServiceController<BackgroundSoundService>

    @Before
    fun setUp() {
        controller = Robolectric.buildService(BackgroundSoundService::class.java)
    }

    @After
    fun tearDown() {
        controller.destroy()
        shadowOf(Looper.getMainLooper()).idle()
    }

    private fun startIntent(
        uri: String = "",
        songTitle: String = "Tu hi meri sab hay",
        id: Int = 1,
    ): Intent =
        Intent(RuntimeEnvironment.getApplication(), BackgroundSoundService::class.java).apply {
            putExtra("uri", uri)
            putExtra("songTitle", songTitle)
            putExtra("id", id)
        }

    @Test
    fun `is not bindable`() {
        val service = controller.create().get()

        assertNull(service.onBind(Intent()))
    }

    @Test
    fun `publishes itself once started`() {
        val service = controller.create().get()

        service.onStartCommand(startIntent(), 0, 1)

        assertSame(service, BackgroundSoundService.getServiceObject())
    }

    @Test
    fun `withdraws itself once destroyed`() {
        val service = controller.create().get()
        service.onStartCommand(startIntent(), 0, 1)

        controller.destroy()

        assertNull(BackgroundSoundService.getServiceObject())
    }

    @Test
    fun `asks to be restarted after being killed`() {
        val service = controller.create().get()

        assertEquals(android.app.Service.START_STICKY, service.onStartCommand(startIntent(), 0, 1))
    }

    @Test
    fun `remembers the song it was started with`() {
        val service = controller.create().get()

        service.onStartCommand(startIntent(id = 7), 0, 1)

        assertEquals(7, service.getCurrentSongId())
    }

    @Test
    fun `defaults to the first song when the intent carries no id`() {
        val service = controller.create().get()

        service.onStartCommand(Intent(RuntimeEnvironment.getApplication(), BackgroundSoundService::class.java), 0, 1)

        assertEquals(0, service.getCurrentSongId())
    }

    @Test
    fun `reports no playback before a track is loaded`() {
        val service = controller.create().get()

        service.onStartCommand(startIntent(), 0, 1)

        assertFalse(service.isCurrentlyPlaying())
        assertEquals(0, service.getCurrentPositionMs())
        assertEquals(0, service.getDurationMs())
    }

    @Test
    fun `implements the playback controller used by the player screen`() {
        val service = controller.create().get()

        assertNotNull(service as PlaybackController)
    }

    @Test
    fun `transport commands are safe with no track loaded`() {
        val service = controller.create().get()
        service.onStartCommand(startIntent(), 0, 1)

        service.playPausePlayer()
        service.seekTo(1_000)

        assertFalse(service.isCurrentlyPlaying())
    }

    @Test
    fun `starting twice keeps the most recent song`() {
        val service = controller.create().get()

        service.onStartCommand(startIntent(id = 1), 0, 1)
        service.onStartCommand(startIntent(id = 4), 0, 2)

        assertEquals(4, service.getCurrentSongId())
    }
}
