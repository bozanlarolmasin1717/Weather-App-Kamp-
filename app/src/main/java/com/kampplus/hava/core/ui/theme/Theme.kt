package com.kampplus.hava.core.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColors = lightColorScheme(
    primary = Teal40,
    onPrimary = Neutral99,
    primaryContainer = Teal90,
    onPrimaryContainer = Teal10,
    secondary = Amber40,
    secondaryContainer = Amber90,
    background = Neutral99,
    onBackground = Neutral10,
    surface = Neutral99,
    onSurface = Neutral10,
    surfaceVariant = NeutralVariant90,
    onSurfaceVariant = NeutralVariant30,
    surfaceContainer = Neutral95,
    error = Red40
)

private val DarkColors = darkColorScheme(
    primary = HavaColors.Ice,
    onPrimary = HavaColors.Ink,
    primaryContainer = HavaColors.DeepBlue,
    onPrimaryContainer = HavaColors.TextPrimary,
    secondary = HavaColors.Sun,
    background = HavaColors.Ink,
    onBackground = HavaColors.TextPrimary,
    surface = HavaColors.Midnight,
    onSurface = HavaColors.TextPrimary,
    surfaceVariant = HavaColors.Storm,
    onSurfaceVariant = HavaColors.TextSecondary,
    error = Red80
)

@Composable
fun HavaTheme(darkTheme: Boolean = true, content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        typography = HavaTypography,
        content = content
    )
}
