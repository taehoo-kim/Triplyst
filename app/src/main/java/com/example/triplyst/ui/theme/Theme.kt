package com.example.triplyst.ui.theme

import android.app.Activity
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
    primary = Color(0xFFE6CBA8),
    secondary = Color(0xFFD4A373),
    tertiary = Color(0xFFFFD384),
    background = Color(0xFF3A2C18),
    surface = Color(0xFF5B4631),
    surfaceVariant = Color(0xFF5B4631),
    onBackground = Color(0xFFF6EEDD),
    onSurface = Color(0xFFE6CBA8)
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF6D5D4B),
    secondary = Color(0xFFBFA980),
    tertiary = Color(0xFFD4A373),
    background = Color(0xFFE6CBA8),
    surface = Color(0xFFF6EEDD),
    surfaceVariant = Color(0xFFF6EEDD),
    onBackground = Color(0xFF3A2C18),
    onSurface = Color(0xFF5B4631),
    primaryContainer = Color(0xFFFFD384),

    /* Other default colors to override
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    */
)

@Composable
fun TriplystTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false,
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