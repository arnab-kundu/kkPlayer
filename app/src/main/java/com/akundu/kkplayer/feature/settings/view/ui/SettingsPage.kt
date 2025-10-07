package com.akundu.kkplayer.feature.settings.view.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.akundu.kkplayer.R
import com.akundu.kkplayer.feature.settings.viewModel.SettingsViewModel

@Preview
@Composable
fun SettingsScreenContainer(
    modifier: Modifier = Modifier,
    viewModel: SettingsViewModel = viewModel(),
    backClick: () -> Unit = {},
) {
    val theme by viewModel.theme.collectAsState()
    val repeatMode by viewModel.repeatMode.collectAsState()

    SettingsScreen(
        modifier = modifier,
        backClick = backClick,
        selectedTheme = theme,
        onThemeSelected = viewModel::onThemeSelected,
        selectedRepeatMode = repeatMode,
        onRepeatModeSelected = viewModel::onRepeatModeSelected,
        selectedDisplayMode = "", // TODO
        onDisplayModeSelected = { /* TODO */ },
        onClearCache = { /* Clear Cache Logic */ },
        onClearDatabase = { /* Clear DB Logic */ },
        onClearData = { /* Clear All Data Logic */ },
    )
}

@Composable
fun SettingsScreen(
    modifier: Modifier,
    backClick: () -> Unit,
    selectedTheme: String,
    onThemeSelected: (String) -> Unit,
    selectedRepeatMode: String,
    onRepeatModeSelected: (String) -> Unit,
    selectedDisplayMode: String = "All Songs",
    onDisplayModeSelected: (String) -> Unit,
    onClearCache: () -> Unit,
    onClearDatabase: () -> Unit,
    onClearData: () -> Unit,
) {
    val glassModifier =
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color.White.copy(alpha = 0.15f),
                        Color.White.copy(alpha = 0.05f),
                    ),
                ),
            ).border(
                width = 1.dp,
                color = Color.White.copy(alpha = 0.3f),
                shape = RoundedCornerShape(16.dp),
            ).padding(16.dp)

    Image(
        modifier = Modifier.blur(16.dp),
        painter = painterResource(id = R.drawable.background),
        contentDescription = null,
        contentScale = ContentScale.FillBounds,
    )
    Column(
        modifier =
            modifier
                .fillMaxSize()
                .padding(16.dp),
    ) {
        Row {
            Icon(
                painter = painterResource(id = R.drawable.baseline_west_24),
                contentDescription = null,
                modifier =
                    Modifier
                        .size(24.dp)
                        .clickable { backClick.invoke() },
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Column(
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(
                    "Settings",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White,
                    fontSize = 26.sp,
                    fontFamily = FontFamily.Cursive,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                )
            }
        }
        Spacer(modifier = Modifier.height(24.dp))

        // 🎨 Theme Section
        Box(modifier = glassModifier) {
            Column {
                Text("Theme", style = MaterialTheme.typography.titleMedium, color = Color.White)
                Spacer(Modifier.height(8.dp))
                listOf("Default", "Dark", "Light").forEach { theme ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(
                            selected = theme == selectedTheme,
                            onClick = { onThemeSelected(theme) },
                        )
                        Text(theme, modifier = Modifier.padding(start = 8.dp), color = Color.White)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 🔁 Repeat Mode Section
        Box(modifier = glassModifier) {
            Column {
                Text("Repeat Mode", style = MaterialTheme.typography.titleMedium, color = Color.White)
                Spacer(Modifier.height(8.dp))
                listOf("Repeat One", "Repeat All", "Repeat None").forEach { mode ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(
                            selected = mode == selectedRepeatMode,
                            onClick = { onRepeatModeSelected(mode) },
                        )
                        Text(mode, modifier = Modifier.padding(start = 8.dp), color = Color.White)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // 🎵 Display Songs Section
        Box(modifier = glassModifier) {
            Column {
                Text(text = "Display Options", style = MaterialTheme.typography.titleMedium, color = Color.White)
                Spacer(Modifier.height(8.dp))
                listOf("All Songs", "Downloaded Songs Only").forEach { mode ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(
                            selected = mode == selectedDisplayMode,
                            onClick = { onDisplayModeSelected(mode) },
                        )
                        Text(mode, modifier = Modifier.padding(start = 8.dp), color = Color.White)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // 🧹 Clear Options
        GlassActionButton(
            icon = Icons.Outlined.Delete, // Delete Cache
            label = "Clear Cache",
            onClick = onClearCache,
        )

        GlassActionButton(
            icon = Icons.Default.DateRange, // DB icon
            label = "Clear Database",
            onClick = onClearDatabase,
        )

        GlassActionButton(
            icon = Icons.Default.Delete, // Delete all data,
            label = "Clear All Data",
            color = Color.Red,
            onClick = onClearData,
        )
    }
}

@Composable
fun GlassActionButton(
    icon: ImageVector,
    label: String,
    color: Color = Color.White,
    onClick: () -> Unit,
) {
    Box(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color.White.copy(alpha = 0.15f),
                            Color.White.copy(alpha = 0.05f),
                        ),
                    ),
                ).border(1.dp, Color.White.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
                .clickable { onClick() }
                .padding(horizontal = 16.dp, vertical = 12.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = color,
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(label, color = color, style = MaterialTheme.typography.bodyLarge)
        }
    }
}
