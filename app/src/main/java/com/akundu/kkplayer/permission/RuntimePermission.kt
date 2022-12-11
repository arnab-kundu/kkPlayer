package com.akundu.kkplayer.permission

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale
import androidx.core.content.ContextCompat
import com.akundu.kkplayer.Logg

object RuntimePermission {

    /**
     * Request Notification permission at runtime
     * for Android 13 and above
     */
    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    fun askNotificationPermission(activity: Activity, requestPermissionLauncher: ActivityResultLauncher<String>) {
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
            Logg.i("NOTIFICATION PERMISSION GRANTED")
        } else if (shouldShowRequestPermissionRationale(activity, Manifest.permission.POST_NOTIFICATIONS)) {
            Toast.makeText(activity, "APP NOTIFICATION NOT ALLOWED", Toast.LENGTH_SHORT).show()
        } else {
            Logg.e("Ask for permissions")
            requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }
}