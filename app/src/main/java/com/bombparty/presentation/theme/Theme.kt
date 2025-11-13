package com.bombparty.presentation.theme

import android.app.Activity
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
    primary = BombRed,
    onPrimary = TextOnDark,
    primaryContainer = BombRedDark,
    onPrimaryContainer = TextOnDark,
    secondary = BombOrange,
    onSecondary = TextOnDark,
    secondaryContainer = BombYellow,
    onSecondaryContainer = TextPrimary,
    tertiary = AccentPurple,
    onTertiary = TextOnDark,
    background = BackgroundDark,
    onBackground = TextOnDark,
    surface = SurfaceDark,
    onSurface = TextOnDark,
    error = ErrorRed,
    onError = TextOnDark
)

private val LightColorScheme = lightColorScheme(
    primary = BombRed,
    onPrimary = TextOnDark,
    primaryContainer = BombRedDark,
    onPrimaryContainer = TextOnDark,
    secondary = BombOrange,
    onSecondary = TextOnDark,
    secondaryContainer = BombYellow,
    onSecondaryContainer = TextPrimary,
    tertiary = AccentBlue,
    onTertiary = TextOnDark,
    background = BackgroundLight,
    onBackground = TextPrimary,
    surface = SurfaceLight,
    onSurface = TextPrimary,
    error = ErrorRed,
    onError = TextOnDark
)

@Composable
fun BombPartyTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
