package com.vantechinformatics.easycargo.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.datastore.preferences.core.stringPreferencesKey

// --- Theme mode ---

enum class ThemeMode { DARK, LIGHT }

val THEME_MODE_KEY = stringPreferencesKey("theme_mode")

// --- Custom color palette ---

data class EasyCargoColors(
    // Surfaces
    val glassSurface: Color,
    val glassCard: Color,
    val glassBorder: Color,
    // Text
    val contentPrimary: Color,
    val textSecondary: Color,
    val textMuted: Color,
    // Accents
    val greenLight: Color,
    val orangeLight: Color,
    val red700: Color,
    // Overlay gradient
    val overlayGradientTop: Color,
    val overlayGradientMid: Color,
    val overlayGradientBottom: Color,
    // Track color for progress bars
    val progressTrack: Color,
)

// --- Dark palette (current values) ---

private val Orange700 = Color(0xFFE65100)
private val Orange500 = Color(0xFFFF6D00)
private val Orange400 = Color(0xFFFF9100)

private val Green500 = Color(0xFF4CAF50)
private val Green700 = Color(0xFF2E7D32)
private val Green900 = Color(0xFF1B5E20)
private val Amber600 = Color(0xFFFFA000)
private val RedLight = Color(0xFFEF5350)

private val GlassDark = Color(0xFF0D1B2A)
private val TextPrimary = Color(0xFFF5F5F5)

val DarkEasyCargoColors = EasyCargoColors(
    glassSurface = Color(0xCC1B2838),
    glassCard = Color(0xB31E2D3D),
    glassBorder = Color(0x33FFFFFF),
    contentPrimary = Color(0xFFF5F5F5),
    textSecondary = Color(0xFFB0BEC5),
    textMuted = Color(0xFF78909C),
    greenLight = Color(0xFF81C784),
    orangeLight = Color(0xFFFFAB40),
    red700 = Color(0xFFD32F2F),
    overlayGradientTop = Color(0xAA0D1B2A),
    overlayGradientMid = Color(0xDD0D1B2A),
    overlayGradientBottom = Color(0xEE0D1B2A),
    progressTrack = Color.White.copy(alpha = 0.15f),
)

// --- Light palette (frosted white glass) ---

val LightEasyCargoColors = EasyCargoColors(
    glassSurface = Color(0xCCF5F5F5),
    glassCard = Color(0xB3FFFFFF),
    glassBorder = Color(0x33000000),
    contentPrimary = Color(0xFF1B1B1B),
    textSecondary = Color(0xFF546E7A),
    textMuted = Color(0xFF78909C),
    greenLight = Color(0xFF388E3C),
    orangeLight = Color(0xFFE65100),
    red700 = Color(0xFFC62828),
    overlayGradientTop = Color(0x77FFFFFF),
    overlayGradientMid = Color(0xAAFFFFFF),
    overlayGradientBottom = Color(0xCCFFFFFF),
    progressTrack = Color.Black.copy(alpha = 0.10f),
)

// --- CompositionLocal ---

val LocalEasyCargoColors = staticCompositionLocalOf { DarkEasyCargoColors }

// --- Material color schemes ---

private val DarkMaterialColorScheme = darkColorScheme(
    primary = Orange500,
    onPrimary = Color.White,
    primaryContainer = Orange700,
    onPrimaryContainer = Color.White,
    secondary = DarkEasyCargoColors.textSecondary,
    onSecondary = GlassDark,
    secondaryContainer = Color(0x991B2838),
    onSecondaryContainer = TextPrimary,
    background = Color.Transparent,
    onBackground = TextPrimary,
    surface = DarkEasyCargoColors.glassCard,
    onSurface = TextPrimary,
    surfaceVariant = DarkEasyCargoColors.glassBorder,
    onSurfaceVariant = DarkEasyCargoColors.textSecondary,
    error = RedLight,
    onError = Color.White,
    outline = DarkEasyCargoColors.glassBorder,
)

private val LightMaterialColorScheme = lightColorScheme(
    primary = Orange500,
    onPrimary = Color.White,
    primaryContainer = Orange400,
    onPrimaryContainer = Color.White,
    secondary = LightEasyCargoColors.textSecondary,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFE0E0E0),
    onSecondaryContainer = Color(0xFF1B1B1B),
    background = Color.Transparent,
    onBackground = Color(0xFF1B1B1B),
    surface = LightEasyCargoColors.glassCard,
    onSurface = Color(0xFF1B1B1B),
    surfaceVariant = LightEasyCargoColors.glassBorder,
    onSurfaceVariant = LightEasyCargoColors.textSecondary,
    error = RedLight,
    onError = Color.White,
    outline = LightEasyCargoColors.glassBorder,
)

val EasyCargoShapes = Shapes(
    small = RoundedCornerShape(8.dp),
    medium = RoundedCornerShape(12.dp),
    large = RoundedCornerShape(16.dp),
    extraLarge = RoundedCornerShape(20.dp),
)

// --- Theme composable ---

@Composable
fun EasyCargoTheme(darkTheme: Boolean = true, content: @Composable () -> Unit) {
    val colorScheme = if (darkTheme) DarkMaterialColorScheme else LightMaterialColorScheme
    val easyCargoColors = if (darkTheme) DarkEasyCargoColors else LightEasyCargoColors

    CompositionLocalProvider(LocalEasyCargoColors provides easyCargoColors) {
        MaterialTheme(
            colorScheme = colorScheme,
            shapes = EasyCargoShapes,
            content = content
        )
    }
}

// --- Convenience accessor ---

object EasyCargoTheme {
    val colors: EasyCargoColors
        @Composable
        @ReadOnlyComposable
        get() = LocalEasyCargoColors.current
}
