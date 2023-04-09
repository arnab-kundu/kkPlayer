package com.akundu.kkplayer

import android.app.Application
import com.akundu.kkplayer.network.ConnectionStateMonitor

class KkPlayerApp : Application() {

    var appsNotificationManager: AppsNotificationManager? = null

    override fun onCreate() {
        super.onCreate()

        appsNotificationManager = AppsNotificationManager.getInstance(this@KkPlayerApp)
        appsNotificationManager?.registerNotificationChannel(
            channelId = "1",
            channelName = "Download channel",
            channelDescription = "Song download notifications"
        )
        appsNotificationManager?.registerNotificationChannel(
            channelId = "2",
            channelName = "Player channel",
            channelDescription = "Playing song notification"
        )

        ConnectionStateMonitor().enable(this)
    }
}