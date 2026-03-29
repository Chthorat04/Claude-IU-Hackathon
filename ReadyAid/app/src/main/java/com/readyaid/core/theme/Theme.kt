package com.readyaid.core.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = ReadyAidTeal,
    secondary = ReadyAidAmber,
    tertiary = ReadyAidGreen,
    background = BackgroundPrimary,
    surface = BackgroundCard,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = TextPrimary,
    onSurface = TextPrimary,
    error = ReadyAidRed,
    errorContainer = ReadyAidRed.copy(alpha = 0.1f),
    onErrorContainer = ReadyAidRed
)

@Composable
fun ReadyAidTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = ReadyAidTypography,
        content = content
    )
}
