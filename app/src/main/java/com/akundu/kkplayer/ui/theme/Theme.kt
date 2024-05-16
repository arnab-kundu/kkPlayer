package com.akundu.kkplayer.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorPalette = darkColorScheme(
    primary = DarkPrimary,
    onPrimary = DarkPrimary,
    primaryContainer = DarkPrimary,

    secondary = DarkSecondary,
    onSecondary = DarkSecondary,

    background = DarkBackground,
    onBackground = DarkThemeFontColor,

    surface = DarkSurface,
    onSurface = DarkOnSurface,

    onError = Color.Red
)

private val LightColorPalette = lightColorScheme(
    primary = LightPrimary,
    onPrimary = LightPrimary,
    primaryContainer = LightPrimary,

    secondary = LightSecondary,
    onSecondary = LightSecondary,

    background = LightBackground,
    onBackground = LightThemeFontColor,

    surface = LightSurface,
    onSurface = LightOnSurface,

    onError = Color.Red
)

@Composable
fun KkPlayerTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    val colors = if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }

    MaterialTheme(
        typography = Typography,
        shapes = Shapes,
        content = content,
        colorScheme = colors
    )
}