package com.akundu.kkplayer.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.graphics.Color
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.akundu.kkplayer.R

class BackgroundSoundService : Service() {

    private val TAG = "Service"
    var player: MediaPlayer? = null
    var uriString: String = ""
    var songTitle: String = ""

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        uriString = intent.extras?.getString("uri") ?: ""
        songTitle = intent.extras?.getString("songTitle") ?: ""
        Log.d(TAG, "onStartCommand: $uriString")

        player = MediaPlayer.create(this, Uri.parse(uriString))
        if (player != null) {
            player?.isLooping = false // Set looping
            player?.setVolume(100f, 100f)
            player?.start()
            player?.setOnCompletionListener { mediaPlayer: MediaPlayer? -> stopSelf() }

            Log.i(TAG, "Playing: " + uriString.split("Music/").toTypedArray()[1])

            runAsForeground()
        }
        return START_STICKY
    }

    override fun onStart(intent: Intent, startId: Int) {}

    fun onUnBind(arg0: Intent?): IBinder? {
        // TO DO Auto-generated method
        return null
    }

    fun onStop() {}
    fun onPause() {}

    override fun onDestroy() {
        if (player != null) {
            player!!.stop()
            player!!.release()
        }
    }

    override fun onLowMemory() {}

    private fun runAsForeground() {
        val notificationIntent = Intent(this, this.javaClass)
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
        val pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE)
        val mNotificationManager = applicationContext.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= 26) {
            val channel = NotificationChannel("2", "Player channel", NotificationManager.IMPORTANCE_DEFAULT)
            channel.description = "Playing song notification"
            channel.setShowBadge(true)
            mNotificationManager.createNotificationChannel(channel)
        }
        val notification = NotificationCompat.Builder(this, "2")
            .setNumber(0)
            .setOngoing(true)
            .setColorized(true)
            .setColor(Color.parseColor("#606060"))
            .setSmallIcon(R.mipmap.ic_launcher)
            .setSubText("is playing...")
            .setContentTitle(songTitle)
            //.setContentText("Replace with your text")
            .setSilent(true)
            .setContentIntent(pendingIntent)
        startForeground(12345, notification.build())
    }
}