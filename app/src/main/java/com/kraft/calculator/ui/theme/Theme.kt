package com.kraft.calculator.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.kraft.calculator.data.AppTheme
import com.kraft.calculator.ui.theme.ThemeColors
import com.kraft.ui.tokens.KraftRadius

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
    small = RoundedCornerShape(KraftRadius.Small),
    medium = RoundedCornerShape(KraftRadius.Standard),
    large = RoundedCornerShape(KraftRadius.Medium),
    extraLarge = RoundedCornerShape(KraftRadius.Hero),
)

/** Maps [AppTheme] + system dark mode to the correct [ThemeColors] instance. */
fun ThemeColors.fromTheme(theme: AppTheme, systemIsDark: Boolean): ThemeColors {
    return when (theme) {
        AppTheme.SYSTEM -> if (systemIsDark) KraftThemeColors.dark else KraftThemeColors.light
        AppTheme.LIGHT -> KraftThemeColors.light
        AppTheme.DARK -> KraftThemeColors.dark
        AppTheme.AMOLED -> KraftThemeColors.amoledGrey
    }
}

/// Compose-local theme colors, backed by KraftThemeColors.
/// Provides the active theme (light/dark/amoled) to any composable in the tree.
val LocalThemeColors = compositionLocalOf { KraftThemeColors.dark }

@Composable
fun KraftTheme(
    colors: ThemeColors = KraftThemeColors.dark,
    content: @Composable () -> Unit,
) {
    val colorScheme = if (colors == KraftThemeColors.dark || colors == KraftThemeColors.amoledGrey)
        DarkColorScheme else LightColorScheme

    CompositionLocalProvider(LocalThemeColors provides colors) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = KraftTypography,
            shapes = KraftShapes,
            content = content,
        )
    }
}
