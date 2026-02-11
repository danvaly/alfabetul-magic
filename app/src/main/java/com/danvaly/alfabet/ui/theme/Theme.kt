package com.danvaly.alfabet.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    primary = DuoGreen,
    onPrimary = SurfaceWhite,
    primaryContainer = DuoGreenDark,
    secondary = DuoBlue,
    onSecondary = SurfaceWhite,
    secondaryContainer = DuoPurpleLight,
    background = BackgroundCream,
    onBackground = OnBackground,
    surface = SurfaceWhite,
    onSurface = OnBackground,
    surfaceVariant = BackgroundLight,
    onSurfaceVariant = OnBackgroundLight,
    error = WrongRed,
    onError = SurfaceWhite,
    tertiary = DuoOrange,
    onTertiary = SurfaceWhite
)

@Composable
fun AlfabetulMagicTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = AppTypography,
        content = content
    )
}
