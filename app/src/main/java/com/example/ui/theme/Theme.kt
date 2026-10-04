package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.example.data.models.ThemeType

private val DarkColorScheme = darkColorScheme(
    primary = NeonGreen,
    onPrimary = Color(0xFF032611),
    primaryContainer = Color(0xFF0B381E),
    onPrimaryContainer = EmeraldLight,
    secondary = MintAccent,
    onSecondary = Color(0xFF003822),
    secondaryContainer = Color(0xFF07482D),
    onSecondaryContainer = Color(0xFFA7F3D0),
    tertiary = GoldPrimary,
    onTertiary = Color(0xFF382C00),
    tertiaryContainer = GoldContainer,
    onTertiaryContainer = Color(0xFFFFE082),
    background = DarkBackground,
    onBackground = DarkTextPrimary,
    surface = DarkSurface,
    onSurface = DarkTextPrimary,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = DarkTextSecondary,
    outline = DarkCardBorder,
    error = ErrorRed,
    onError = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF059669),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFA7F3D0),
    onPrimaryContainer = Color(0xFF022C22),
    secondary = Color(0xFF0D9488),
    onSecondary = Color.White,
    tertiary = Color(0xFFD97706),
    onTertiary = Color.White,
    background = LightBackground,
    onBackground = LightTextPrimary,
    surface = LightSurface,
    onSurface = LightTextPrimary,
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = LightTextSecondary,
    outline = Color(0xFFCBD5E1),
    error = ErrorRed,
    onError = Color.White
)

private val NeonColorScheme = darkColorScheme(
    primary = NeonThemePrimary,
    onPrimary = Color(0xFF003632),
    primaryContainer = Color(0xFF004D40),
    onPrimaryContainer = Color(0xFF80CBC4),
    secondary = NeonThemeSecondary,
    onSecondary = Color.White,
    tertiary = Color(0xFFFF007F),
    background = NeonThemeBg,
    onBackground = Color(0xFFF3E8FF),
    surface = NeonThemeSurface,
    onSurface = Color(0xFFF3E8FF),
    surfaceVariant = Color(0xFF240E3E),
    outline = Color(0xFF3C1361),
    error = ErrorRed
)

private val SpaceColorScheme = darkColorScheme(
    primary = SpaceThemePrimary,
    onPrimary = Color(0xFF003355),
    primaryContainer = Color(0xFF0369A1),
    onPrimaryContainer = Color(0xFFBAE6FD),
    secondary = SpaceThemeSecondary,
    tertiary = GoldPrimary,
    background = SpaceThemeBg,
    onBackground = Color(0xFFF8FAFC),
    surface = SpaceThemeSurface,
    onSurface = Color(0xFFF8FAFC),
    surfaceVariant = Color(0xFF1E293B),
    outline = Color(0xFF334155),
    error = ErrorRed
)

private val ForestColorScheme = darkColorScheme(
    primary = ForestThemePrimary,
    onPrimary = Color(0xFF003822),
    primaryContainer = Color(0xFF065F46),
    onPrimaryContainer = ForestThemeSecondary,
    secondary = Color(0xFF34D399),
    tertiary = GoldPrimary,
    background = ForestThemeBg,
    onBackground = Color(0xFFECFDF5),
    surface = ForestThemeSurface,
    onSurface = Color(0xFFECFDF5),
    surfaceVariant = Color(0xFF134E3E),
    outline = Color(0xFF1C6B55),
    error = ErrorRed
)

@Composable
fun SnakeGameTheme(
    themeType: ThemeType = ThemeType.DARK,
    content: @Composable () -> Unit
) {
    val colorScheme = when (themeType) {
        ThemeType.DARK -> DarkColorScheme
        ThemeType.LIGHT -> LightColorScheme
        ThemeType.NEON -> NeonColorScheme
        ThemeType.SPACE -> SpaceColorScheme
        ThemeType.FOREST -> ForestColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
