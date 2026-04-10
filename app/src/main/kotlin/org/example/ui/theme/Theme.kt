package org.example.ui.theme

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val PinkColorScheme = lightColorScheme(
    primary = Color(0xFFE91E63),
    secondary = Color(0xFFF48FB1),
    tertiary = Color(0xFFFCE4EC),
    background = Color(0xFFFFF0F5),
    surface = Color(0xFFFFF0F5)
)

@Composable
fun AppTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = PinkColorScheme,
        typography = Typography(),
        content = content
    )
}