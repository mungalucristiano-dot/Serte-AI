package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = SerteGreenPrimary,
    onPrimary = Color(0xFF003919),
    primaryContainer = SerteGreenDark,
    onPrimaryContainer = SerteGreenNeon,
    secondary = SerteGreenNeon,
    onSecondary = Color(0xFF003919),
    secondaryContainer = SerteGreenSubtle,
    onSecondaryContainer = SerteGreenPrimary,
    tertiary = SerteAccentCyan,
    onTertiary = Color(0xFF00363D),
    background = SerteDarkBackground,
    onBackground = SerteTextPrimary,
    surface = SerteDarkSurface,
    onSurface = SerteTextPrimary,
    surfaceVariant = SerteDarkSurfaceVariant,
    onSurfaceVariant = SerteTextSecondary,
    surfaceContainer = SerteDarkSurfaceContainer,
    outline = SerteDarkBorder,
    outlineVariant = Color(0xFF384352)
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF008945),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFB9F6CA),
    onPrimaryContainer = Color(0xFF00210B),
    secondary = Color(0xFF00A854),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFD7FBE8),
    onSecondaryContainer = Color(0xFF003919),
    tertiary = Color(0xFF00838F),
    onTertiary = Color.White,
    background = SerteLightBackground,
    onBackground = SerteLightText,
    surface = SerteLightSurface,
    onSurface = SerteLightText,
    surfaceVariant = SerteLightSurfaceVariant,
    onSurfaceVariant = Color(0xFF49454F),
    outline = Color(0xFFCFD8DC)
)

@Composable
fun SerteAiTheme(
    darkTheme: Boolean = true, // Default to sleek futuristic dark mode
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
