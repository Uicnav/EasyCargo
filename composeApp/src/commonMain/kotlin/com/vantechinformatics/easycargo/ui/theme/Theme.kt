package com.vantechinformatics.easycargo.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

// Primary palette - orange accents
val Orange700 = Color(0xFFE65100)
val Orange500 = Color(0xFFFF6D00)
val Orange400 = Color(0xFFFF9100)
val OrangeLight = Color(0xFFFFAB40)

// Semantic colors
val Green500 = Color(0xFF4CAF50)
val Green700 = Color(0xFF2E7D32)
val Green900 = Color(0xFF1B5E20)
val GreenLight = Color(0xFF81C784)
val Amber600 = Color(0xFFFFA000)
val Red700 = Color(0xFFD32F2F)
val RedLight = Color(0xFFEF5350)

// Glass surfaces - semi-transparent dark
val GlassDark = Color(0xFF0D1B2A)
val GlassSurface = Color(0xCC1B2838) // ~80% opacity dark blue-gray
val GlassSurfaceLight = Color(0x991B2838) // ~60% opacity
val GlassCard = Color(0xB31E2D3D) // ~70% opacity
val GlassBorder = Color(0x33FFFFFF) // subtle white border

// Text
val TextPrimary = Color(0xFFF5F5F5)
val TextSecondary = Color(0xFFB0BEC5)
val TextMuted = Color(0xFF78909C)

private val EasyCargoColorScheme = darkColorScheme(
    primary = Orange500,
    onPrimary = Color.White,
    primaryContainer = Orange700,
    onPrimaryContainer = Color.White,
    secondary = TextSecondary,
    onSecondary = GlassDark,
    secondaryContainer = GlassSurfaceLight,
    onSecondaryContainer = TextPrimary,
    background = Color.Transparent,
    onBackground = TextPrimary,
    surface = GlassCard,
    onSurface = TextPrimary,
    surfaceVariant = GlassBorder,
    onSurfaceVariant = TextSecondary,
    error = RedLight,
    onError = Color.White,
    outline = GlassBorder,
)

val EasyCargoShapes = Shapes(
    small = RoundedCornerShape(8.dp),
    medium = RoundedCornerShape(12.dp),
    large = RoundedCornerShape(16.dp),
    extraLarge = RoundedCornerShape(20.dp),
)

@Composable
fun EasyCargoTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = EasyCargoColorScheme,
        shapes = EasyCargoShapes,
        content = content
    )
}
