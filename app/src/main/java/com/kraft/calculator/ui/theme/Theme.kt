package com.kraft.calculator.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

private val LightColorScheme = lightColorScheme(
    primary = AccentBlue,
    onPrimary = Color.White,
    secondary = AccentPurple,
    onSecondary = Color.White,
    error = AccentRed,
    onError = Color.White,
    surface = SurfaceLight,
    onSurface = TextPrimaryLight,
    surfaceVariant = SurfaceSecondaryLight,
    onSurfaceVariant = TextSecondaryLight,
    outline = SeparatorLight,
    background = BackgroundLight,
    onBackground = TextPrimaryLight,
)

private val DarkColorScheme = darkColorScheme(
    primary = AccentBlueDark,
    onPrimary = Color.White,
    secondary = AccentPurpleDark,
    onSecondary = Color.White,
    error = AccentRedDark,
    onError = Color.White,
    surface = SurfaceDark,
    onSurface = TextPrimaryDark,
    surfaceVariant = SurfaceSecondaryDark,
    onSurfaceVariant = TextSecondaryDark,
    outline = SeparatorDark,
    background = BackgroundDark,
    onBackground = TextPrimaryDark,
)

val KraftShapes = Shapes(
    small = RoundedCornerShape(8.dp),
    medium = RoundedCornerShape(12.dp),
    large = RoundedCornerShape(16.dp),
    extraLarge = RoundedCornerShape(20.dp),
)

object KraftRadius {
    val small = 8.dp
    val standard = 12.dp
    val large = 16.dp
    val hero = 20.dp
}

object KraftSpacing {
    val spacing2 = 2.dp
    val spacing4 = 4.dp
    val spacing8 = 8.dp
    val spacing12 = 12.dp
    val spacing14 = 14.dp
    val spacing16 = 16.dp
    val spacing20 = 20.dp
    val spacing24 = 24.dp
    val spacing32 = 32.dp
    val spacing40 = 40.dp
    val spacing48 = 48.dp
    val spacing64 = 64.dp
}

@Composable
fun KraftTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = KraftTypography,
        shapes = KraftShapes,
        content = content
    )
}
