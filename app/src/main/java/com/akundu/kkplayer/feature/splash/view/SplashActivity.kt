package com.akundu.kkplayer.feature.splash.view

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.akundu.kkplayer.MainActivity
import com.akundu.kkplayer.feature.splash.viewModel.SplashViewModel

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    private lateinit var viewModel: SplashViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = SplashViewModel()

        // Splash screen loading
        installSplashScreen().apply {

            // Conditional delay in SplashScreen. SplashScreen will remain until this condition gets succeeded
            setKeepOnScreenCondition {
                viewModel.isLoading.value
            }

            // Animation works for Android 12 and later versions
            // After complete SplashScreen animation, resume Main/UI Thread operation.
            setOnExitAnimationListener {
                startActivity(Intent(this@SplashActivity, MainActivity::class.java))
                finish()
            }
        }

    }
}