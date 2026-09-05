package com.akundu.kkplayer.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class StopServiceReceiver : BroadcastReceiver() {
    override fun onReceive(
        context: Context,
        intent: Intent,
    ) {
        val isStopService: Boolean = intent.extras?.getBoolean("isStopService") ?: false
        val isPauseService: Boolean = intent.extras?.getBoolean("isPauseService") ?: false
        val isNextSong: Boolean = intent.extras?.getBoolean("isNextSong") ?: false
        val isPreviousSong: Boolean = intent.extras?.getBoolean("isPreviousSong") ?: false

        if (isStopService) {
            context.stopService(Intent(context, BackgroundSoundService::class.java))
        }

        if (isPauseService) {
            BackgroundSoundService.getServiceObject()?.playPausePlayer()
        }

        if (isNextSong) {
            BackgroundSoundService.getServiceObject()?.nextSong()
        }

        if (isPreviousSong) {
            BackgroundSoundService.getServiceObject()?.previousSong()
        }
    }
}
