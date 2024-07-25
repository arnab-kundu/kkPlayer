package com.akundu.kkplayer.feature.splash.view

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import com.akundu.kkplayer.BuildConfig
import com.akundu.kkplayer.MainActivity
import com.akundu.kkplayer.ui.theme.KkPlayerTheme

class Splash2Activity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val version = BuildConfig.VERSION_NAME
        setContent {
            KkPlayerTheme {
                SplashPage(version = version)
            }
        }

        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this@Splash2Activity, MainActivity::class.java))
            finish()
        }, 10000)

    }
}