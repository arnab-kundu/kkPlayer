package com.akundu.kkplayer.feature.onboarding.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.akundu.kkplayer.ui.theme.KkPlayerTheme

@Preview(showBackground = false)
@Composable
fun SecondaryButtonPreview() {
    KkPlayerTheme() {
        SecondaryButton(buttonText = "'Skip'", modifier = Modifier, fontColor = Color(0xFF26C9EB), strokeColor = Color(0xFF26C9EB))
    }
}


@Composable
fun SecondaryButton(
    buttonText: String,
    modifier: Modifier,
    fontColor: Color = Color(0xFFFFFFFF),
    strokeColor: Color = Color(0xFFFFFFFF)
) {
    Card(
        shape = RoundedCornerShape(30.dp),
        border = BorderStroke(width = 2.dp, color = strokeColor),
        modifier = modifier
            .size(width = 160.dp, height = 48.dp),
        backgroundColor = Color.Transparent,
    ) {
        Box(
            modifier = Modifier,
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = buttonText,
                modifier = Modifier.padding(0.dp, 0.dp, 0.dp, 0.dp),
                fontWeight = FontWeight.Normal,
                fontStyle = FontStyle.Normal,
                fontSize = 20.sp,
                color = fontColor
            )
        }
    }
}