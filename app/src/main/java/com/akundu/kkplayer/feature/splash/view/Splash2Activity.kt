package com.akundu.kkplayer.feature.splash.view

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.akundu.kkplayer.BuildConfig
import com.akundu.kkplayer.feature.main.view.MainActivity
import com.akundu.kkplayer.feature.splash.viewModel.SplashViewModel
import com.akundu.kkplayer.ui.theme.KkPlayerTheme
import kotlinx.coroutines.launch

class Splash2Activity : AppCompatActivity() {

    private lateinit var viewModel: SplashViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = SplashViewModel()
        // viewModel.reverseAnimation()
        val version = BuildConfig.VERSION_NAME

        lifecycleScope.launch {
            viewModel.uiState.collect { uiState ->
                setContent {
                    KkPlayerTheme {
                        SplashPage(viewModel = viewModel, version = version, isAnimationEndFlow = true, uiState = uiState, loginButtonClick = {
                            viewModel.loginButtonClickStateChangeEvent()

                            Handler(Looper.getMainLooper()).postDelayed({
                                startActivity(Intent(this@Splash2Activity, MainActivity::class.java))
                                finish()
                            }, 1500)
                        })
                    }
                }

            }
        }
    }
}