package com.kraft.calculator.presentation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kraft.calculator.ui.theme.ThemeColors
import com.kraft.calculator.ui.theme.CalculatorFontSizes
import com.kraft.ui.tokens.KraftSpacing
import com.kraft.ui.tokens.KraftRadius

/** Styles a calculator button by its role in the UI. */
enum class CalculatorButtonStyle {
    /** Numeric input (0-9, decimal). Largest type, neutral background. */
    NUMBER,
    /** Arithmetic operators (+, −, ×, ÷, ^). Accent blue, medium weight. */
    OPERATOR,
    /** Utility keys (AC, CE, %, √, ⋯). Surface tertiary, medium type. */
    UTILITY,
    /** Equals key. Distinct green confirm color, semi-bold. */
    EQUALS,
    /** Scientific functions (sin, cos, ln, log ⋯). Orange accent on surface. */
    SCIENTIFIC,
    /** Memory keys (MC, MR, M+, M−). Purple accent on surface. */
    MEMORY,
    /** Toggle keys (DEG/RAD/GRAD, ALPHA, 2nd). Green when active. */
    TOGGLE,
    /** Alpha / variable keys. Red accent to signal alternate layer. */
    ALPHA,
    /** Shift-Sci keys (sinh, cosh, tanh on 2nd layer). Orange accent. */
    SHIFT_SCI
}

/** Pair of background and foreground colors for a button. */
data class ButtonColors(val background: androidx.compose.ui.graphics.Color, val foreground: androidx.compose.ui.graphics.Color)

/**
 * Returns the background/foreground pair for [style], accounting for [isActive]
 * (used by toggle-style buttons like DEG/RAD/GRAD).
 *
 * Color assignment follows Apple's guidance: one visual accent per button type,
 * white text on all colored backgrounds (semantic contrast, not theme color),
 * and destructive red reserved exclusively for the alpha/variable layer.
 */
fun buttonColors(style: CalculatorButtonStyle, isActive: Boolean, colors: ThemeColors): ButtonColors {
    return when (style) {
        CalculatorButtonStyle.NUMBER -> ButtonColors(
            background = colors.surfaceSecondary,
            foreground = colors.textPrimary,
        )
        CalculatorButtonStyle.OPERATOR -> ButtonColors(
            background = colors.accentBlue,
            foreground = androidx.compose.ui.graphics.Color.White,
        )
        CalculatorButtonStyle.UTILITY -> ButtonColors(
            background = colors.surfaceTertiary,
            foreground = colors.textPrimary,
        )
        CalculatorButtonStyle.EQUALS -> ButtonColors(
            background = colors.accentEquals,
            foreground = androidx.compose.ui.graphics.Color.White,
        )
        CalculatorButtonStyle.SCIENTIFIC -> ButtonColors(
            background = colors.surfaceSecondary,
            foreground = colors.accentOrange,
        )
        CalculatorButtonStyle.MEMORY -> ButtonColors(
            background = colors.surfaceSecondary,
            foreground = colors.accentPurple,
        )
        CalculatorButtonStyle.TOGGLE -> if (isActive) ButtonColors(
            background = colors.accentGreen,
            foreground = androidx.compose.ui.graphics.Color.White,
        ) else ButtonColors(
            background = colors.surfaceTertiary,
            foreground = colors.textTertiary,
        )
        CalculatorButtonStyle.ALPHA -> ButtonColors(
            background = colors.surfaceSecondary,
            foreground = colors.accentRed,
        )
        CalculatorButtonStyle.SHIFT_SCI -> ButtonColors(
            background = colors.surfaceSecondary,
            foreground = colors.accentOrange,
        )
    }
}



/** Returns the font size for [style], using calculator-specific scale. */
fun buttonFontSize(style: CalculatorButtonStyle, largeFont: Boolean = false): androidx.compose.ui.unit.TextUnit {
    return when (style) {
        CalculatorButtonStyle.NUMBER -> if (largeFont) CalculatorFontSizes.numberLarge else CalculatorFontSizes.number
        CalculatorButtonStyle.OPERATOR, CalculatorButtonStyle.EQUALS -> if (largeFont) CalculatorFontSizes.operatorLarge else CalculatorFontSizes.operator
        CalculatorButtonStyle.UTILITY -> if (largeFont) CalculatorFontSizes.utilityLarge else CalculatorFontSizes.utility
        CalculatorButtonStyle.SCIENTIFIC, CalculatorButtonStyle.SHIFT_SCI -> CalculatorFontSizes.scientific
        CalculatorButtonStyle.MEMORY, CalculatorButtonStyle.TOGGLE, CalculatorButtonStyle.ALPHA -> CalculatorFontSizes.label
    }
}

/** Returns the font weight for [style]. Only EQUALS is semi-bold; all others medium or normal. */
fun buttonFontWeight(style: CalculatorButtonStyle): FontWeight {
    return when (style) {
        CalculatorButtonStyle.NUMBER -> FontWeight.Normal
        CalculatorButtonStyle.EQUALS -> FontWeight.SemiBold
        else -> FontWeight.Medium
    }
}

/**
 * Composition local that controls whether haptic feedback is enabled for button presses.
 * Set from settings via `LocalHapticsEnabled provides ...` in the screen.
 */
val LocalHapticsEnabled = staticCompositionLocalOf { true }

/**
 * Returns an accessibility content description for [label].
 *
 * Maps symbolic labels (⌫, ±, ÷, ×, sin⁻¹, etc.) to spoken descriptions
 * so TalkBack reads meaningful text instead of the symbol character.
 */
fun buttonDescription(label: String): String = when (label) {
    "⌫", "DEL" -> "Delete"
    "±", "−\u2212" -> "Plus minus, toggle sign"
    "÷" -> "Divide"
    "×" -> "Multiply"
    "−" -> "Minus"
    "+" -> "Plus"
    "=" -> "Equals"
    "AC" -> "All clear"
    "CE" -> "Clear entry"
    "%" -> "Percent"
    "√" -> "Square root"
    "∛" -> "Cube root"
    "π" -> "Pi"
    "τ" -> "Tau"
    "²" -> "Squared"
    "³" -> "Cubed"
    "⁻¹" -> "Reciprocal"
    "!" -> "Factorial"
    "^", "xʸ" -> "Power"
    "S⇔D" -> "Toggle fraction decimal"
    "DRG▶" -> "Cycle angle mode, degrees radians gradians"
    "×10ˣ" -> "Scientific notation"
    "a b/c" -> "Fraction"
    "d/c" -> "Decimal to fraction"
    "STO" -> "Store to memory"
    "RCL" -> "Recall memory"
    "SHIFT", "2nd" -> "Second function"
    "HYP", "hyp" -> "Hyperbolic"
    "ENG" -> "Engineering notation"
    "Ans" -> "Last answer"
    "Ran#" -> "Random number"
    "→DMS" -> "Convert to degrees minutes seconds"
    "→Decimal" -> "Convert to decimal"
    "→Frac" -> "Convert to fraction"
    "x²" -> "Square"
    "x³" -> "Cube"
    "x⁻¹" -> "Reciprocal"
    "sin⁻¹", "cos⁻¹", "tan⁻¹" -> "Inverse ${label.dropLast(2)}"
    "sinh⁻¹", "cosh⁻¹", "tanh⁻¹" -> "Inverse hyperbolic ${label.dropLast(2)}"
    else -> label
}

/**
 * A single calculator keypad button.
 *
 * Uses [CalculatorButtonStyle] to determine color, font size, and weight.
 * Provides semantic labels for accessibility and haptic feedback when enabled.
 * Minimum touch target is 44dp per Apple HIG.
 */
@Composable
fun CalculatorButton(
    label: String,
    style: CalculatorButtonStyle,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isActive: Boolean = false,
    largeFont: Boolean = false,
    colors: ThemeColors = com.kraft.calculator.ui.theme.KraftThemeColors.light,
) {
    val (bg, fg) = buttonColors(style, isActive, colors)
    val fontSize = buttonFontSize(style, largeFont)
    val fontWeight = buttonFontWeight(style)
    val haptics = LocalHapticFeedback.current
    val hapticsEnabled = LocalHapticsEnabled.current

    Button(
        onClick = {
            if (hapticsEnabled) {
                haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
            }
            onClick()
        },
        modifier = modifier
            .fillMaxSize()
            .defaultMinSize(minWidth = KraftSpacing.TouchTarget, minHeight = KraftSpacing.TouchTarget),
        shape = RoundedCornerShape(KraftRadius.Standard),
        colors = ButtonDefaults.buttonColors(
            containerColor = bg,
            contentColor = fg,
        ),
        contentPadding = PaddingValues(0.dp),
    ) {
        Text(
            text = label,
            fontSize = fontSize,
            fontWeight = fontWeight,
            textAlign = TextAlign.Center,
            maxLines = 1,
        )
    }
}
