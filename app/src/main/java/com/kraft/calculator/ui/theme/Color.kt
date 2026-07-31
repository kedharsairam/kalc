package com.kraft.calculator.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

// -- Accents --
val AccentBlue = Color(0xFF007AFF)
val AccentBlueDark = Color(0xFF0A84FF)
val AccentGreen = Color(0xFF34C759)
val AccentGreenDark = Color(0xFF30D158)
val AccentRed = Color(0xFFFF3B30)
val AccentRedDark = Color(0xFFFF453A)
val AccentOrange = Color(0xFFFF9500)
val AccentOrangeDark = Color(0xFFFF9F0A)
val AccentYellow = Color(0xFFFFCC00)
val AccentYellowDark = Color(0xFFFFD60A)
val AccentPurple = Color(0xFFAF52DE)
val AccentPurpleDark = Color(0xFFBF5AF2)

// -- Backgrounds & Surfaces --
val BackgroundLight = Color(0xFFF2F2F7)
val BackgroundDark = Color(0xFF000000)
val SurfaceLight = Color(0xFFFFFFFF)
val SurfaceDark = Color(0xFF1C1C1E)
val SurfaceSecondaryLight = Color(0xFFF2F2F7)
val SurfaceSecondaryDark = Color(0xFF2C2C2E)
val SurfaceTertiaryLight = Color(0xFFE5E5EA)
val SurfaceTertiaryDark = Color(0xFF3A3A3C)

// -- Text --
val TextPrimaryLight = Color(0xFF000000)
val TextPrimaryDark = Color(0xFFFFFFFF)
val TextSecondaryLight = Color(0xFF3A3A3C)
val TextSecondaryDark = Color(0xFFEBEBF5)
val TextTertiaryLight = Color(0xFF8E8E93)
val TextTertiaryDark = Color(0xFFEBEBF5)

// -- Separators & Fills --
val SeparatorLight = Color(0xFFC6C6C8)
val SeparatorDark = Color(0xFF38383A)
val FillPrimaryLight = Color(0xFF787880)
val FillPrimaryDark = Color(0xFF787880)

// -- Theme-aware color selector --
@Immutable
data class ThemeColors(
    val background: Color,
    val surface: Color,
    val surfaceSecondary: Color,
    val surfaceTertiary: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val textTertiary: Color,
    val separator: Color,
    val accentBlue: Color,
    val accentGreen: Color,
    val accentRed: Color,
    val accentOrange: Color,
    val accentYellow: Color,
    val accentPurple: Color,
)

object KraftThemeColors {
    val light = ThemeColors(
        background = BackgroundLight,
        surface = SurfaceLight,
        surfaceSecondary = SurfaceSecondaryLight,
        surfaceTertiary = SurfaceTertiaryLight,
        textPrimary = TextPrimaryLight,
        textSecondary = TextSecondaryLight,
        textTertiary = TextTertiaryLight,
        separator = SeparatorLight,
        accentBlue = AccentBlue,
        accentGreen = AccentGreen,
        accentRed = AccentRed,
        accentOrange = AccentOrange,
        accentYellow = AccentYellow,
        accentPurple = AccentPurple,
    )

    val dark = ThemeColors(
        background = BackgroundDark,
        surface = SurfaceDark,
        surfaceSecondary = SurfaceSecondaryDark,
        surfaceTertiary = SurfaceTertiaryDark,
        textPrimary = TextPrimaryDark,
        textSecondary = TextSecondaryDark,
        textTertiary = TextTertiaryDark,
        separator = SeparatorDark,
        accentBlue = AccentBlueDark,
        accentGreen = AccentGreenDark,
        accentRed = AccentRedDark,
        accentOrange = AccentOrangeDark,
        accentYellow = AccentYellowDark,
        accentPurple = AccentPurpleDark,
    )
}
