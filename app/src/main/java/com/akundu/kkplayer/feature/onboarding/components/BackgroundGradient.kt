package com.akundu.kkplayer.feature.onboarding.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.akundu.kkplayer.ui.theme.KkPlayerTheme


@Preview(showBackground = false)
@Composable
fun BackgroundGradientPreview() {
    KkPlayerTheme {
        BackgroundGradient(modifier = Modifier, startColor =  Color(0xFF02325E), endColor = Color(0xFF00172C))
    }
}

@Composable
fun BackgroundGradient(modifier: Modifier, startColor: Color, endColor: Color) {
    Box(
        modifier = Modifier
            .fillMaxSize(1F)
            .background(brush = Brush.verticalGradient(colors = listOf(startColor, endColor)))
    )
}