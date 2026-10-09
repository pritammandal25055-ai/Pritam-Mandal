package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = AuraCyan,
    onPrimary = Color(0xFF0D0F1D),
    primaryContainer = AuraVioletDark,
    onPrimaryContainer = AuraCyanLight,
    secondary = AuraViolet,
    onSecondary = Color.White,
    secondaryContainer = AuraDarkCardElevated,
    onSecondaryContainer = AuraTextPrimary,
    tertiary = AuraMagenta,
    background = AuraDarkBg,
    onBackground = AuraTextPrimary,
    surface = AuraDarkSurface,
    onSurface = AuraTextPrimary,
    surfaceVariant = AuraDarkCard,
    onSurfaceVariant = AuraTextSecondary,
    outline = AuraDarkBorder
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF007A8A),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFD6F6FF),
    onPrimaryContainer = Color(0xFF003840),
    secondary = Color(0xFF6D28D9),
    onSecondary = Color.White,
    background = AuraLightBg,
    onBackground = Color(0xFF0F172A),
    surface = AuraLightSurface,
    onSurface = Color(0xFF0F172A),
    surfaceVariant = AuraLightCard,
    onSurfaceVariant = Color(0xFF475569)
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true, // Default to sleek dark AI theme
    dynamicColor: Boolean = false, // Keep consistent branding colors
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
