package com.akundu.kkplayer

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TaskStackBuilder
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
    private val notificationManagerCompat: NotificationManagerCompat
    private val notificationManager: NotificationManager
    fun registerNotificationChannelChannel(channelId: String?, channelName: String?, channelDescription: String?) {
        if (VERSION.SDK_INT >= VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT)
            notificationChannel.description = channelDescription
            val notificationManager = context.getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }

    fun triggerNotification(
        targetNotificationActivity: Class<*>?,
        channelId: String?,
        title: String?,
        text: String?,
        bigText: String?,
        priority: Int,
        autoCancel: Boolean,
        notificationId: Int
    ) {
        val intent = Intent(context, targetNotificationActivity)
        intent.putExtra("count", title)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        val pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)
        val builder: Builder = Builder(context, channelId!!)
            .setSmallIcon(R.mipmap.ic_launcher)
            //.setLargeIcon(BitmapFactory.decodeResource(context.resources, R.drawable.avd_delete))
            .setContentTitle(title)
            .setContentText(text)
            .setStyle(BigTextStyle().bigText(bigText))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setChannelId(channelId)
            .setAutoCancel(true)
            .setOngoing(true)
            .setProgress(100, 0, true)
        val notificationManagerCompat = NotificationManagerCompat.from(context)
        notificationManagerCompat.notify(notificationId, builder.build())
    }

    fun downloadingNotification(
        targetNotificationActivity: Class<*>?,
        channelId: String,
        title: String?,
        text: String?,
        bigText: String?,
        notificationId: Int,
        drawableId: Int
    ) {
        val intent = Intent(context, targetNotificationActivity)
        intent.putExtra("count", title)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        val pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)
        val builder: Builder = Builder(context, channelId)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setLargeIcon(BitmapFactory.decodeResource(context.resources, drawableId))
            .setContentTitle(title)
            .setContentText(text)
            .setStyle(BigTextStyle().bigText(bigText))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setChannelId(channelId)
            .setAutoCancel(true)
            .setOngoing(true)
            .setProgress(100, 0, true)
        val notificationManagerCompat = NotificationManagerCompat.from(context)
        notificationManagerCompat.notify(notificationId, builder.build())
    }

    fun triggerNotification(
        targetNotificationActivity: Class<*>?,
        channelId: String?,
        title: String?,
        text: String?,
        bigText: String?,
        priority: Int,
        autoCancel: Boolean,
        notificationId: Int,
        pendingIntentFlag: Int
    ) {
        val intent = Intent(context, targetNotificationActivity)
        intent.putExtra("count", title)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        val pendingIntent = PendingIntent.getActivity(context, 0, intent, pendingIntentFlag)
        val builder: Builder = Builder(context, channelId!!)
            .setSmallIcon(R.mipmap.ic_launcher)
            //.setLargeIcon(BitmapFactory.decodeResource(context.resources, drawable.avd_delete))
            .setContentTitle(title)
            .setContentText(text)
            .setStyle(BigTextStyle().bigText(bigText))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setChannelId(channelId)
            .setAutoCancel(true)
        notificationManagerCompat.notify(notificationId, builder.build())
    }

    fun triggerNotificationWithBackStack(
        targetNotificationActivity: Class<*>?,
        channelId: String?,
        title: String?,
        text: String?,
        bigText: String?,
        priority: Int,
        autoCancel: Boolean,
        notificationId: Int,
        pendingIntentFlag: Int
    ) {
        val intent = Intent(context, targetNotificationActivity)
        val taskStackBuilder = TaskStackBuilder.create(context)
        taskStackBuilder.addNextIntentWithParentStack(intent)
        intent.putExtra("count", title)
        val pendingIntent = taskStackBuilder.getPendingIntent(0, pendingIntentFlag)
        val builder: Builder = Builder(context, channelId!!)
            .setSmallIcon(R.mipmap.ic_launcher)
            //.setLargeIcon(BitmapFactory.decodeResource(context.resources, drawable.avd_delete))
            .setContentTitle(title)
            .setContentText(text)
            .setStyle(BigTextStyle().bigText(bigText))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setChannelId(channelId)
            .setOngoing(false)
            .setAutoCancel(false)
        notificationManagerCompat.notify(notificationId, builder.build())
    }

    fun updateWithPicture(
        targetNotificationActivity: Class<*>?,
        title: String?,
        text: String?,
        channelId: String?,
        notificationId: Int,
        pendingIntentflag: Int,
        drawableId: Int
    ) {
        val intent = Intent(context, targetNotificationActivity)
        intent.putExtra("count", title)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        val pendingIntent = PendingIntent.getActivity(context, 0, intent, pendingIntentflag)
        val builder: Builder = Builder(context, channelId!!)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setLargeIcon(BitmapFactory.decodeResource(context.resources, drawableId))
            .setContentTitle(title)
            .setContentText(text)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setChannelId(channelId)
            .setAutoCancel(true)
        //val androidImage = BitmapFactory.decodeResource(context.resources, drawable.alert_dark_frame)
        //builder.setStyle(BigPictureStyle().bigPicture(androidImage).setBigContentTitle(bigpictureString))
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

    init {
        notificationManagerCompat = NotificationManagerCompat.from(context)
        notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }
}