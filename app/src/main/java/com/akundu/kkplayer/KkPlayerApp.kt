package com.akundu.kkplayer

import android.app.Application

class KkPlayerApp : Application() {


    var appsNotificationManager: AppsNotificationManager? = null

    override fun onCreate() {
        super.onCreate()

        appsNotificationManager = AppsNotificationManager.getInstance(this@KkPlayerApp)
        appsNotificationManager?.registerNotificationChannelChannel(
            "CHANNEL_ID",
            "CHANNEL_MESSAGING",
            "CHANNEL_DESCRIPTION"
        )

    }
}