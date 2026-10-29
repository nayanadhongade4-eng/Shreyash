package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = CrimsonPrimary,
    onPrimary = Color.White,
    primaryContainer = CrimsonDark,
    onPrimaryContainer = Color.White,
    secondary = VioletSecondary,
    onSecondary = Color.White,
    secondaryContainer = VioletDark,
    onSecondaryContainer = Color.White,
    tertiary = CyanAccent,
    onTertiary = Color.Black,
    background = NoteFlixBgDark,
    onBackground = TextPrimary,
    surface = NoteFlixSurfaceDark,
    onSurface = TextPrimary,
    surfaceVariant = NoteFlixSurfaceCard,
    onSurfaceVariant = TextSecondary,
    outline = NoteFlixBorder
)

private val LightColorScheme = darkColorScheme(
    primary = CrimsonPrimary,
    onPrimary = Color.White,
    secondary = VioletSecondary,
    onSecondary = Color.White,
    tertiary = CyanAccent,
    background = NoteFlixBgDark,
    onBackground = TextPrimary,
    surface = NoteFlixSurfaceDark,
    onSurface = TextPrimary,
    surfaceVariant = NoteFlixSurfaceCard,
    onSurfaceVariant = TextSecondary,
    outline = NoteFlixBorder
)

@Composable
fun NoteFlixTheme(
    darkTheme: Boolean = true, // Cinematic studio vibe is best in pro dark aesthetic
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
