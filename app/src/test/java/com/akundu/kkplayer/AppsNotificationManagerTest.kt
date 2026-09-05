package com.akundu.kkplayer

import android.app.NotificationManager
import android.content.Context
import com.akundu.kkplayer.feature.main.view.MainActivity
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertSame
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
class AppsNotificationManagerTest {
    private lateinit var context: Context
    private lateinit var manager: AppsNotificationManager
    private lateinit var notificationManager: NotificationManager

    @Before
    fun setUp() {
        context = RuntimeEnvironment.getApplication()
        manager = AppsNotificationManager.getInstance(context)!!
        notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancelAll()
    }

    @Test
    fun `returns the same instance for repeated calls`() {
        assertSame(AppsNotificationManager.getInstance(context), AppsNotificationManager.getInstance(context))
    }

    @Test
    fun `registers a notification channel`() {
        manager.registerNotificationChannel("test_channel", "Test channel", "Used by tests")

        val channel = notificationManager.getNotificationChannel("test_channel")
        assertNotNull(channel)
        assertEquals("Test channel", channel.name)
        assertEquals("Used by tests", channel.description)
    }

    @Test
    fun `posts a downloading notification`() {
        manager.downloadingNotification(
            targetNotificationActivity = MainActivity::class.java,
            channelId = "1",
            title = "song.mp3",
            text = "Downloading",
            bigText = "",
            notificationId = 101,
            drawableId = R.drawable.ic_music_album_avatar,
        )

        val posted = shadowOf(notificationManager).getNotification(101)
        assertNotNull(posted)
        assertEquals("song.mp3", posted.extras.getString(android.app.Notification.EXTRA_TITLE))
    }

    @Test
    fun `a downloading notification is ongoing`() {
        manager.downloadingNotification(
            targetNotificationActivity = MainActivity::class.java,
            channelId = "1",
            title = "song.mp3",
            text = "Downloading",
            bigText = "big text",
            notificationId = 102,
            drawableId = R.drawable.ic_music_album_avatar,
        )

        val posted = shadowOf(notificationManager).getNotification(102)
        assertTrue(posted.flags and android.app.Notification.FLAG_ONGOING_EVENT != 0)
    }

    @Test
    fun `posts a download completed notification`() {
        manager.downloadCompletedNotification(
            targetNotificationActivity = MainActivity::class.java,
            channelId = "1",
            title = "song.mp3",
            text = "Download Completed",
            notificationId = 103,
            pendingIntentFlag = android.app.PendingIntent.FLAG_IMMUTABLE,
            drawableId = R.drawable.ic_music_album_avatar,
        )

        val posted = shadowOf(notificationManager).getNotification(103)
        assertNotNull(posted)
        assertEquals("Download Completed", posted.extras.getString(android.app.Notification.EXTRA_TEXT))
    }

    @Test
    fun `cancels a posted notification`() {
        manager.downloadCompletedNotification(
            targetNotificationActivity = MainActivity::class.java,
            channelId = "1",
            title = "song.mp3",
            text = "Download Completed",
            notificationId = 104,
            pendingIntentFlag = android.app.PendingIntent.FLAG_IMMUTABLE,
            drawableId = R.drawable.ic_music_album_avatar,
        )

        manager.cancelNotification(104)

        assertEquals(null, shadowOf(notificationManager).getNotification(104))
    }
}
