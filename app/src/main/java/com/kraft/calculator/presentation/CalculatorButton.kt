package com.kraft.calculator.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kraft.calculator.ui.theme.KraftThemeColors
import com.kraft.calculator.ui.theme.KraftRadius
import com.kraft.calculator.ui.theme.ThemeColors

enum class CalculatorButtonStyle {
    number, operator, utility, equals, scientific, memory, toggle, alpha, shiftSci
}

data class ButtonColors(val background: Color, val foreground: Color)

fun buttonColors(style: CalculatorButtonStyle, isActive: Boolean, colors: ThemeColors): ButtonColors {
    return when (style) {
        CalculatorButtonStyle.number -> ButtonColors(
            background = colors.surfaceSecondary,
            foreground = colors.textPrimary,
        )
        CalculatorButtonStyle.operator -> ButtonColors(
            background = colors.accentBlue,
            foreground = Color.White,
        )
        CalculatorButtonStyle.utility -> ButtonColors(
            background = colors.surfaceTertiary,
            foreground = colors.textPrimary,
        )
        CalculatorButtonStyle.equals -> ButtonColors(
            background = colors.accentEquals,
            foreground = Color.White,
        )
        CalculatorButtonStyle.scientific -> ButtonColors(
            background = colors.surfaceSecondary,
            foreground = colors.accentOrange,
        )
        CalculatorButtonStyle.memory -> ButtonColors(
            background = colors.surfaceSecondary,
            foreground = colors.accentPurple,
        )
        CalculatorButtonStyle.toggle -> if (isActive) ButtonColors(
            background = colors.accentGreen,
            foreground = Color.White,
        ) else ButtonColors(
            background = colors.surfaceTertiary,
            foreground = colors.textTertiary,
        )
        CalculatorButtonStyle.alpha -> ButtonColors(
            background = colors.surfaceSecondary,
            foreground = colors.accentRed,
        )
        CalculatorButtonStyle.shiftSci -> ButtonColors(
            background = colors.surfaceSecondary,
            foreground = colors.accentOrange,
        )
    }
}

fun buttonFontSize(style: CalculatorButtonStyle, largeFont: Boolean = false): Int {
    return when (style) {
        CalculatorButtonStyle.number -> if (largeFont) 36 else 30
        CalculatorButtonStyle.operator, CalculatorButtonStyle.equals -> if (largeFont) 34 else 26
        CalculatorButtonStyle.utility -> if (largeFont) 30 else 22
        CalculatorButtonStyle.scientific, CalculatorButtonStyle.shiftSci -> 20
        CalculatorButtonStyle.memory, CalculatorButtonStyle.toggle, CalculatorButtonStyle.alpha -> 13
    }
}

fun buttonFontWeight(style: CalculatorButtonStyle): FontWeight {
    return when (style) {
        CalculatorButtonStyle.number -> FontWeight.Normal
        CalculatorButtonStyle.equals -> FontWeight.SemiBold
        else -> FontWeight.Medium
    }
}

val LocalHapticsEnabled = androidx.compose.runtime.staticCompositionLocalOf { true }

/**
 * Maps symbol labels to spoken descriptions for accessibility.
 */
fun buttonDescription(label: String): String = when (label) {
    "⌫", "DEL" -> "Delete"
    "±", "(−)" -> "Plus minus, toggle sign"
    "÷" -> "Divide"
    "×" -> "Multiply"
    "−" -> "Minus"
    "+" -> "Plus"
    "=" -> "Equals"
    "AC" -> "All clear"
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
    "sin⁻¹", "cos⁻¹", "tan⁻¹" -> "Inverse ${label.dropLast(2)}"
    "sinh⁻¹", "cosh⁻¹", "tanh⁻¹" -> "Inverse hyperbolic ${label.dropLast(2)}"
    else -> label
}

@Composable
fun CalculatorButton(
    label: String,
    style: CalculatorButtonStyle,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isActive: Boolean = false,
    largeFont: Boolean = false,
    colors: ThemeColors = KraftThemeColors.light,
) {
    val (bg, fg) = buttonColors(style, isActive, colors)
    val fontSize = buttonFontSize(style, largeFont).sp
    val fontWeight = buttonFontWeight(style)

    val haptics = androidx.compose.ui.platform.LocalHapticFeedback.current
    val hapticsEnabled = LocalHapticsEnabled.current

    Button(
        onClick = {
            if (hapticsEnabled) {
                haptics.performHapticFeedback(androidx.compose.ui.hapticfeedback.HapticFeedbackType.TextHandleMove)
            }
            onClick()
        },
        modifier = modifier
            .fillMaxSize()
            .defaultMinSize(minWidth = 44.dp, minHeight = 44.dp)
            .semantics {
                contentDescription = buttonDescription(label)
                if (isActive) selected = true
            },
        shape = RoundedCornerShape(KraftRadius.standard),
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
