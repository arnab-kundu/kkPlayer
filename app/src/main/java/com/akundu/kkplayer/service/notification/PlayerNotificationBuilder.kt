package com.akundu.kkplayer.service.notification

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.graphics.Bitmap
import android.support.v4.media.session.MediaSessionCompat
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.app.NotificationCompat
import com.akundu.kkplayer.R
import androidx.media.app.NotificationCompat as MediaNotificationCompat

object PlayerNotificationBuilder {
    const val CHANNEL_ID = "2"
    const val NOTIFICATION_ID = 12345

    data class Actions(
        val previous: PendingIntent,
        val playPause: PendingIntent,
        val next: PendingIntent,
        val stop: PendingIntent,
    )

    fun registerChannel(context: Context) {
        val notificationManager = context.applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channel = NotificationChannel(CHANNEL_ID, "Player channel", NotificationManager.IMPORTANCE_HIGH)
        channel.description = "Playing song notification"
        channel.setShowBadge(true)
        notificationManager.createNotificationChannel(channel)
    }

    fun build(
        context: Context,
        songTitle: String,
        isPlaying: Boolean,
        albumArt: Bitmap?,
        actions: Actions,
        contentIntent: PendingIntent,
        sessionToken: MediaSessionCompat.Token?,
    ): Notification {
        val playPauseIcon = if (isPlaying) android.R.drawable.ic_media_pause else android.R.drawable.ic_media_play
        val playPauseLabel = if (isPlaying) "Pause" else "Play"

        return NotificationCompat
            .Builder(context, CHANNEL_ID)
            .setNumber(0)
            .setOngoing(true)
            .setColorized(true)
            .setColor(Color(0xFF606060).toArgb())
            .setSmallIcon(R.mipmap.ic_launcher)
            .setSubText("is playing...")
            .setContentTitle(songTitle)
            .setLargeIcon(albumArt)
            .addAction(android.R.drawable.ic_media_previous, "Previous", actions.previous)
            .addAction(playPauseIcon, playPauseLabel, actions.playPause)
            .addAction(android.R.drawable.ic_media_next, "Next", actions.next)
            .addAction(android.R.drawable.ic_menu_close_clear_cancel, "Stop", actions.stop)
            .setStyle(
                MediaNotificationCompat
                    .MediaStyle()
                    .setMediaSession(sessionToken)
                    .setShowActionsInCompactView(0, 1, 2),
            ).setSilent(true)
            .setContentIntent(contentIntent)
            .build()
    }
}
