package com.akundu.kkplayer.service;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.IBinder;
import android.util.Log;


public class BackgroundSoundService extends Service {

    private static final String TAG = null;
    MediaPlayer player;
    String uriString;

    public IBinder onBind(Intent intent) {

        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        uriString = intent.getExtras().getString("uri");
        Log.d(TAG, "onBind: " + uriString);

        player = MediaPlayer.create(this, Uri.parse(uriString));
        player.setLooping(false); // Set looping
        player.setVolume(100, 100);
        player.start();
        return START_NOT_STICKY;
    }

    public void onStart(Intent intent, int startId) {
        // TODO
    }

    public IBinder onUnBind(Intent arg0) {
        // TO DO Auto-generated method
        return null;
    }

    public void onStop() {

    }

    public void onPause() {

    }

    @Override
    public void onDestroy() {
        player.stop();
        player.release();
    }

    @Override
    public void onLowMemory() {

    }

}