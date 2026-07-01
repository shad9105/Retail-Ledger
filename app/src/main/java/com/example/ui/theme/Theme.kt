package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = DarkPrimaryTeal,
    secondary = DarkSecondaryBlue,
    tertiary = DarkAccentGreen,
    background = DarkCanvas,
    surface = DarkSurface,
    onPrimary = Color.Black,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = DarkTextPrimary,
    onSurface = DarkTextPrimary,
    error = DarkAccentRed,
    primaryContainer = DarkSurface,
    onPrimaryContainer = DarkTextPrimary,
    secondaryContainer = DarkBorder,
    onSecondaryContainer = DarkTextPrimary
)

private val LightColorScheme = DarkColorScheme // Always premium dark mode for beautiful minimalist branding

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true, // Default to true for the stunning premium dark look
    content: @Composable () -> Unit
) {
    val colorScheme = DarkColorScheme


    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
