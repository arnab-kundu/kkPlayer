package com.akundu.kkplayer

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationCompat.BigTextStyle
import androidx.core.app.NotificationCompat.Builder
import androidx.core.app.NotificationManagerCompat

class AppsNotificationManager private constructor(private val context: Context) {
    private val notificationManagerCompat: NotificationManagerCompat = NotificationManagerCompat.from(context)
    private val notificationManager: NotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    fun registerNotificationChannel(channelId: String?, channelName: String?, channelDescription: String?) {
        if (VERSION.SDK_INT >= VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH)
            notificationChannel.description = channelDescription
            val notificationManager = context.getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }

    fun downloadingNotification(
        targetNotificationActivity: Class<*>?,
        channelId: String,
        title: String?,
        text: String?,
        bigText: String?,
        notificationId: Int,
        drawableId: Int,
    ) {
        val intent = Intent(context, targetNotificationActivity)
        intent.putExtra("count", title)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        val pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)
        val builder: Builder = Builder(context, channelId)
            .setSmallIcon(R.mipmap.ic_launcher) // .setSmallIcon(R.drawable.ic_notification)
            .setLargeIcon(BitmapFactory.decodeResource(context.resources, drawableId))
            .setContentTitle(title)
            .setDefaults(Notification.VISIBILITY_PUBLIC)
            .setContentText(text)
            .setStyle(BigTextStyle().bigText(bigText))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setChannelId("1")
            .setAutoCancel(true)
            .setOngoing(true)
            .setProgress(100, 0, true)
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (VERSION.SDK_INT >= VERSION_CODES.O) {
            val downloadingNotificationChannel = NotificationChannel("1", "Downloading song", NotificationManager.IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(downloadingNotificationChannel)
        }
        notificationManager.notify(notificationId, builder.build())
    }

    fun downloadCompletedNotification(
        targetNotificationActivity: Class<*>?,
        title: String?,
        text: String?,
        channelId: String,
        notificationId: Int,
        pendingIntentFlag: Int,
        drawableId: Int,
    ) {
        val intent = Intent(context, targetNotificationActivity)
        intent.putExtra("count", title)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        val pendingIntent = PendingIntent.getActivity(context, 0, intent, pendingIntentFlag)
        val builder: Builder = Builder(context, channelId)
            .setSmallIcon(R.mipmap.ic_launcher) // .setSmallIcon(R.drawable.ic_notification)
            .setLargeIcon(BitmapFactory.decodeResource(context.resources, drawableId))
            .setContentTitle(title)
            .setContentText(text)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setChannelId("1")
            .setAutoCancel(true)
        notificationManager.notify(notificationId, builder.build())
    }

    fun cancelNotification(notificationId: Int) {
        notificationManager.cancel(notificationId)
    }

    companion object {
        private var instance: AppsNotificationManager? = null
        fun getInstance(context: Context): AppsNotificationManager? {
            if (instance == null) {
                instance = AppsNotificationManager(context)
            }
            return instance
        }
    }
}
