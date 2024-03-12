package com.akundu.kkplayer.feature.onboarding.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.akundu.kkplayer.R
import com.akundu.kkplayer.ui.theme.KkPlayerTheme


@Preview(showBackground = false)
@Composable
fun BackButtonPreview() {
    KkPlayerTheme {
        BackButton(modifier = Modifier, color = Color.White)
    }
}


@Composable
@Suppress("UNUSED_PARAMETER")
fun BackButton(modifier: Modifier, color: Color) {
    Row(modifier = Modifier.size(width = 120.dp, height = 48.dp), verticalAlignment = Alignment.CenterVertically) {
        Image(
            modifier = Modifier
                .fillMaxHeight(1F)
                .size(48.dp)
                .padding(vertical = 8.dp, horizontal = 8.dp),
            painter = painterResource(id = R.drawable.ic_arrow_back),
            contentDescription = stringResource(id = R.string.app_name),
            colorFilter = ColorFilter.tint(color),
        )
        Text(
            modifier = Modifier.padding(0.dp, 0.dp, 0.dp, 0.dp),
            text = "Back",
            fontSize = 20.sp,
            fontWeight = FontWeight.Normal,
            color = color
        )
    }
}

