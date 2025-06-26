package com.example.stulogandroidapp.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.graphics.Color

// Custom color values based on your palette
private val Plum = Color(0xFF66344C)
private val Mauve = Color(0xFF664F69)
private val LavenderGrey = Color(0xFF9A7B9A)
private val PastelPurple = Color(0xFFCA9CE1)
private val LilacPink = Color(0xFFDFA5EB)

private val DarkColorScheme = darkColorScheme(
    primary = Plum,
    onPrimary = Color.White,
    secondary = Mauve,
    onSecondary = Color.White,
    background = Color(0xFF121212),      // Very dark background
    onBackground = Color.White,          // White text on dark background
    surface = LavenderGrey,              // Light purple surface
    onSurface = Color.Black              // Black text on light surface
)

private val LightColorScheme = lightColorScheme(
    primary = Plum,
    onPrimary = Color.White,
    secondary = Mauve,
    onSecondary = Color.White,
    background = Color(0xFFFFFFFF),      // Light background
    onBackground = Color.Black,          // Black text on light background
    surface = LilacPink,                 // Light pink surface
    onSurface = Color.Black              // Black text on light surface
)

@Composable
fun StuLogAndroidAppTheme(
    useDarkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            if (useDarkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        useDarkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}
