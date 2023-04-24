package com.akundu.kkplayer.feature.onboarding.screens

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.akundu.kkplayer.feature.onboarding.components.AppUsagesGuideline
import com.akundu.kkplayer.feature.onboarding.components.BackButton
import com.akundu.kkplayer.feature.onboarding.components.BackgroundGradient
import com.akundu.kkplayer.feature.onboarding.components.PrimaryButton
import com.akundu.kkplayer.feature.onboarding.components.SecondaryButton
import com.akundu.kkplayer.feature.onboarding.components.ViewPagerItem
import com.akundu.kkplayer.ui.theme.KkPlayerTheme


@Preview(showBackground = true)
@Composable
fun SetupGuideScreenPreview() {
    KkPlayerTheme {
        SetupGuideScreen()
    }
}


@Composable
fun SetupGuideScreen() {
    Surface(modifier = Modifier.fillMaxSize(), color = Color.Transparent) {

        BackgroundGradient(modifier = Modifier, startColor = MaterialTheme.colors.surface, endColor = MaterialTheme.colors.onSurface)
        Column(modifier = Modifier.background(color = Color.Transparent)) {
            Spacer(modifier = Modifier.size(32.dp))
            BackButton(
                modifier = Modifier.clickable {
                    Log.d("click event", "Back button")
                },
                color = Color.White
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth(1F)
                    .height(200.dp)
                    .background(MaterialTheme.colors.background)
            ) {
                Image(
                    modifier = Modifier
                        .size(192.dp)
                        .align(Alignment.Center),
                    painter = painterResource(id = com.akundu.kkplayer.R.drawable.ic_launcher_foreground),
                    contentDescription = stringResource(id = com.akundu.kkplayer.R.string.app_name),
                )
            }

            Spacer(modifier = Modifier.size(32.dp))
            ViewPagerItem(modifier = Modifier, fontColor = Color.White)

            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth(1F)) {
                Spacer(modifier = Modifier.size(48.dp))
                AppUsagesGuideline(color = MaterialTheme.colors.primary)
                Spacer(modifier = Modifier.size(48.dp))
                SecondaryButton(buttonText = "'Skip'", modifier = Modifier, fontColor = MaterialTheme.colors.primary, strokeColor = MaterialTheme.colors.primary)
                Spacer(modifier = Modifier.size(16.dp))
                PrimaryButton(buttonText = "Next", modifier = Modifier, backgroundColor = MaterialTheme.colors.primary)
                Spacer(modifier = Modifier.size(16.dp))
            }
        }
    }
}