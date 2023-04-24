package com.akundu.kkplayer.feature.onboarding.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.akundu.kkplayer.ui.theme.KkPlayerTheme


@Preview(showBackground = false)
@Composable
fun ViewPagerItemPreview() {
    KkPlayerTheme() {
        ViewPagerItem(modifier = Modifier, fontColor = Color.White)
    }
}


@Composable
fun ViewPagerItem(modifier: Modifier, fontColor: Color) {
    Column(modifier = Modifier.background(color = Color.Transparent)) {
        Text(
            text = "User guide",
            fontStyle = FontStyle.Normal,
            fontWeight = FontWeight.Normal,
            fontSize = 32.sp,
            color = fontColor,
            modifier = Modifier
                .fillMaxWidth(1F)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            textAlign = TextAlign.Center,
        )
        Text(
            text = "Let's get few things setup, so you can maximize your exercise.",
            fontStyle = FontStyle.Normal,
            fontWeight = FontWeight.Normal,
            fontSize = 20.sp,
            color = fontColor,
            modifier = Modifier
                .fillMaxWidth(1F)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            textAlign = TextAlign.Center,
        )
        Text(
            text = "If you are new 'Take the tour' otherwise 'Start' your exercise.",
            fontStyle = FontStyle.Normal,
            fontWeight = FontWeight.Normal,
            fontSize = 20.sp,
            color = fontColor,
            modifier = Modifier
                .fillMaxWidth(1F)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            textAlign = TextAlign.Center,
        )
    }
}
