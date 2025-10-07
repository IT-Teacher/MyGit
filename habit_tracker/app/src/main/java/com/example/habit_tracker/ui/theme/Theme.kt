package com.example.habit_tracker.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp

// Custom color palette (vibrant but clean)
private val LightColors = lightColorScheme(
    primary = Color(0xFF3A86FF),      // Indigo-Blue
    onPrimary = Color.White,
    primaryContainer = Color(0xFFDBE8FF),
    onPrimaryContainer = Color(0xFF00214D),

    secondary = Color(0xFF00BFA6),    // Teal
    onSecondary = Color.Black,
    secondaryContainer = Color(0xFFBFF6EE),
    onSecondaryContainer = Color(0xFF003D35),

    tertiary = Color(0xFFFF6F91),     // Coral/Pink accent
    onTertiary = Color.White,

    background = Color(0xFFF7F9FC),
    onBackground = Color(0xFF0F172A),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF0F172A)
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFF7AB2FF),
    onPrimary = Color(0xFF001633),
    primaryContainer = Color(0xFF0E2A56),
    onPrimaryContainer = Color(0xFFDBE8FF),

    secondary = Color(0xFF5CEAD7),
    onSecondary = Color(0xFF00201B),
    secondaryContainer = Color(0xFF004E45),
    onSecondaryContainer = Color(0xFFBFF6EE),

    tertiary = Color(0xFFFF98B0),
    onTertiary = Color(0xFF3F0012),

    background = Color(0xFF0B1220),
    onBackground = Color(0xFFE6EEF9),
    surface = Color(0xFF0F172A),
    onSurface = Color(0xFFE6EEF9)
)

@Composable
fun AppTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        typography = Typography,
        content = content
    )
}