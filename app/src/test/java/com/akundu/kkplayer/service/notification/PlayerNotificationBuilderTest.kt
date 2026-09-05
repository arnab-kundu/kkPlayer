package com.akundu.kkplayer.service.notification

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.support.v4.media.session.MediaSessionCompat
import androidx.core.app.NotificationCompat
import androidx.core.graphics.createBitmap
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33])
class PlayerNotificationBuilderTest {
    private lateinit var context: Context
    private lateinit var actions: PlayerNotificationBuilder.Actions

    @Before
    fun setUp() {
        context = RuntimeEnvironment.getApplication()
        actions =
            PlayerNotificationBuilder.Actions(
                previous = broadcast(500),
                playPause = broadcast(200),
                next = broadcast(600),
                stop = broadcast(400),
            )
    }

    private fun broadcast(requestCode: Int): PendingIntent =
        PendingIntent.getBroadcast(context, requestCode, Intent("action_$requestCode"), PendingIntent.FLAG_IMMUTABLE)

    private fun build(
        songTitle: String = "Tu hi meri sab hay",
        isPlaying: Boolean = true,
        albumArt: Bitmap? = null,
        sessionToken: MediaSessionCompat.Token? = null,
    ) = PlayerNotificationBuilder.build(
        context = context,
        songTitle = songTitle,
        isPlaying = isPlaying,
        albumArt = albumArt,
        actions = actions,
        contentIntent = broadcast(0),
        sessionToken = sessionToken,
    )

    @Test
    fun `registers the player notification channel`() {
        PlayerNotificationBuilder.registerChannel(context)

        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channel = manager.getNotificationChannel(PlayerNotificationBuilder.CHANNEL_ID)
        assertNotNull(channel)
        assertEquals("Player channel", channel.name)
        assertEquals(NotificationManager.IMPORTANCE_HIGH, channel.importance)
    }

    @Test
    fun `builds a notification carrying the song title`() {
        val notification = build(songTitle = "Alvida")

        assertEquals("Alvida", notification.extras.getString(NotificationCompat.EXTRA_TITLE))
    }

    @Test
    fun `exposes previous play next and stop actions`() {
        val notification = build()

        assertEquals(4, notification.actions.size)
        assertEquals("Previous", notification.actions[0].title)
        assertEquals("Next", notification.actions[2].title)
        assertEquals("Stop", notification.actions[3].title)
    }

    @Test
    fun `shows a pause action while playing`() {
        val notification = build(isPlaying = true)

        assertEquals("Pause", notification.actions[1].title)
        assertEquals(android.R.drawable.ic_media_pause, notification.actions[1].icon)
    }

    @Test
    fun `shows a play action while paused`() {
        val notification = build(isPlaying = false)

        assertEquals("Play", notification.actions[1].title)
        assertEquals(android.R.drawable.ic_media_play, notification.actions[1].icon)
    }

    @Test
    fun `the notification is ongoing and silent`() {
        val notification = build()

        assertTrue(notification.flags and android.app.Notification.FLAG_ONGOING_EVENT != 0)
        assertEquals(PlayerNotificationBuilder.CHANNEL_ID, notification.channelId)
    }

    @Test
    fun `accepts album art when one is available`() {
        val notification = build(albumArt = createBitmap(8, 8))

        assertNotNull(notification)
        assertEquals(4, notification.actions.size)
    }
}
