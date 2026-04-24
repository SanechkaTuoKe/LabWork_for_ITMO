package org.example.ui.theme

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp


val ColorPrimary     = Color(0xFF2B5278)
val ColorPrimaryVar  = Color(0xFF1E3A52)
val ColorSecondary   = Color(0xFF4A7FA5)
val ColorBackground  = Color(0xFFF0F2F5)
val ColorSurface     = Color(0xFFFFFFFF)
val ColorSurfaceVar  = Color(0xFFE4E8EE)
val ColorOnPrimary   = Color(0xFFFFFFFF)
val ColorOnSurface   = Color(0xFF1A1A2E)
val ColorOnSurfaceVar= Color(0xFF4A5568)
val ColorError       = Color(0xFFB91C1C)
val ColorDivider     = Color(0xFFCBD5E1)
val ColorPrimaryContainer = Color(0xFFD6E8F7)

private val AppColorScheme = lightColorScheme(
    primary          = ColorPrimary,
    onPrimary        = ColorOnPrimary,
    primaryContainer = ColorPrimaryContainer,
    secondary        = ColorSecondary,
    onSecondary      = ColorOnPrimary,
    background       = ColorBackground,
    onBackground     = ColorOnSurface,
    surface          = ColorSurface,
    onSurface        = ColorOnSurface,
    surfaceVariant   = ColorSurfaceVar,
    onSurfaceVariant = ColorOnSurfaceVar,
    error            = ColorError,
    outline          = ColorDivider
)

private val AppTypography = Typography(
    titleLarge = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = ColorOnSurface),
    titleMedium = TextStyle(fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = ColorOnSurface),
    titleSmall = TextStyle(fontSize = 13.sp, fontWeight = FontWeight.Medium, color = ColorOnSurfaceVar),
    bodyMedium = TextStyle(fontSize = 13.sp, fontWeight = FontWeight.Normal, color = ColorOnSurface),
    bodySmall = TextStyle(fontSize = 12.sp, fontWeight = FontWeight.Normal, color = ColorOnSurfaceVar),
    labelSmall = TextStyle(fontSize = 11.sp, fontWeight = FontWeight.Medium, color = ColorOnSurfaceVar)
)

@Composable
fun EquipmentAppTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = AppColorScheme,
        typography  = AppTypography,
        content     = content
    )
}
