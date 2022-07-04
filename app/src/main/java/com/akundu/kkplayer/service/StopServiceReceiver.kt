package com.akundu.kkplayer.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class StopServiceReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {

        val isStopService: Boolean = intent.extras?.getBoolean("isStopService") ?: false
        val isPauseService: Boolean = intent.extras?.getBoolean("isPauseService") ?: false

        if (isStopService) {
            context.stopService(Intent(context, BackgroundSoundService::class.java))
        }

        if (isPauseService) {
            val soundService = BackgroundSoundService.getServiceObject() as BackgroundSoundService
            soundService.playPausePlayer()
        }
    }
}