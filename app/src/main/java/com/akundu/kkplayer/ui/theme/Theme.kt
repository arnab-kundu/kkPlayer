package com.akundu.kkplayer.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorPalette = darkColors(
    primary = DarkPrimary,
    onPrimary = DarkPrimary,
    primaryVariant = DarkPrimary,

    secondary = DarkSecondary,
    onSecondary = DarkSecondary,

    background = DarkBackground,
    onBackground = DarkBackground,

    surface = DarkSurface,
    onSurface = DarkOnSurface,

    onError = Color.Red
)

private val LightColorPalette = lightColors(
    primary = LightPrimary,
    onPrimary = LightPrimary,
    primaryVariant = LightPrimary,

    secondary = LightSecondary,
    onSecondary = LightSecondary,

    background = LightBackground,
    onBackground = LightBackground,

    surface = LightSurface,
    onSurface = LightOnSurface,

    onError = Color.Red
)

@Composable
fun KkPlayerTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable() () -> Unit) {
    val colors = if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }

    MaterialTheme(
        colors = colors,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}