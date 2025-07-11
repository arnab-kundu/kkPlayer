package com.akundu.kkplayer.service

import android.app.ActivityManager
import android.app.ActivityManager.RunningServiceInfo
import android.content.Context
import com.akundu.kkplayer.Logg

object ServiceTools {
    @Suppress("unused")
    private val TAG = ServiceTools::class.java.name

    fun isServiceRunning(context: Context, serviceClassName: String?): Boolean {
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val services: List<RunningServiceInfo> = activityManager.getRunningServices(Int.MAX_VALUE)
        for (runningServiceInfo in services) {
            if (runningServiceInfo.service.className == serviceClassName) {
                Logg.i("isServiceRunning ${runningServiceInfo.service.shortClassName}: true")
                return true
            }
        }
        Logg.i("isServiceRunning: No service running")
        return false
    }
}
