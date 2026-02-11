package com.danvaly.alfabet.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    primary = PrimaryPink,
    onPrimary = SurfaceWhite,
    primaryContainer = PrimaryPinkDark,
    secondary = SecondaryPurple,
    onSecondary = SurfaceWhite,
    secondaryContainer = SecondaryPurpleLight,
    background = BackgroundCream,
    onBackground = OnBackground,
    surface = SurfaceWhite,
    onSurface = OnBackground,
    surfaceVariant = BackgroundLight,
    onSurfaceVariant = OnBackgroundLight,
    error = WrongRed,
    onError = SurfaceWhite,
    tertiary = CorrectGreen,
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
