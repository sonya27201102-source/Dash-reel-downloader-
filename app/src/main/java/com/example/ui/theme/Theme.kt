package com.example.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = DashVioletPrimary,
    onPrimary = DashTextPrimary,
    primaryContainer = DashVioletDark,
    onPrimaryContainer = DashVioletLight,
    secondary = DashCoralAccent,
    onSecondary = DashTextPrimary,
    secondaryContainer = DashDarkSurfaceVariant,
    onSecondaryContainer = DashCoralLight,
    tertiary = DashCyanAccent,
    background = DashDarkBg,
    onBackground = DashTextPrimary,
    surface = DashDarkSurface,
    onSurface = DashTextPrimary,
    surfaceVariant = DashDarkSurfaceVariant,
    onSurfaceVariant = DashTextSecondary,
    outline = DashDarkBorder,
    error = DashRedError
)

private val LightColorScheme = DarkColorScheme // Default to sleek dark aesthetic for media/video app

@Composable
fun DashReelTheme(
    darkTheme: Boolean = true,
    dynamicColor: Boolean = false, // Keep signature Dash neon aesthetic
    content: @Composable () -> Unit
) {
    val colorScheme = DarkColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                window.statusBarColor = DashDarkBg.toArgb()
                window.navigationBarColor = DashDarkBg.toArgb()
                WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
                WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = false
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
