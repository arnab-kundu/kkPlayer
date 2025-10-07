package com.akundu.kkplayer.feature.settings.view

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.akundu.kkplayer.feature.settings.view.ui.SettingsScreenContainer
import com.akundu.kkplayer.ui.theme.KkPlayerTheme

class SettingsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            KkPlayerTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    SettingsScreenContainer(
                        modifier = Modifier.padding(innerPadding),
                        backClick = { finish() },
                    )
                }
            }
        }
    }
}
