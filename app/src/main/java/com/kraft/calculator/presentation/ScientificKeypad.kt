package com.kraft.calculator.presentation

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.kraft.calculator.domain.AngleMode
import com.kraft.calculator.ui.theme.KraftThemeColors
import com.kraft.calculator.ui.theme.ThemeColors
import com.kraft.ui.tokens.KraftSpacing

private data class SciKey(
    val primary: String,
    val shifted: String? = null,
    val alpha: String? = null,
    val action: String? = null,
    val shiftedAction: String? = null,
    val style: CalculatorButtonStyle = CalculatorButtonStyle.SCIENTIFIC,
    val isToggle: Boolean = false,
) {
    fun label(isSecond: Boolean, isAlpha: Boolean): String {
        if (isAlpha && alpha != null) return alpha!!
        if (isSecond && shifted != null) return shifted!!
        return primary
    }

    fun resolveAction(isSecond: Boolean, isAlpha: Boolean): String {
        if (isAlpha && alpha != null) return alpha!!
        if (isSecond && shifted != null) return shiftedAction ?: shifted!!
        return action ?: primary
    }

    fun resolveStyle(isSecond: Boolean, isAlpha: Boolean): CalculatorButtonStyle {
        if (isAlpha && alpha != null) return CalculatorButtonStyle.ALPHA
        if (isSecond && shifted != null) return CalculatorButtonStyle.SHIFT_SCI
        return style
    }
}

private val keyRows = listOf(
    listOf(
        SciKey("sin", shifted = "sin⁻¹", alpha = "A"),
        SciKey("cos", shifted = "cos⁻¹", alpha = "B"),
        SciKey("tan", shifted = "tan⁻¹", alpha = "C"),
        SciKey("(", alpha = "D", style = CalculatorButtonStyle.UTILITY),
        SciKey(")", alpha = "E", style = CalculatorButtonStyle.UTILITY),
    ),
    listOf(
        SciKey("S⇔D", alpha = "F", isToggle = true),
        SciKey("log", shifted = "10^", shiftedAction = "10^", alpha = "G"),
        SciKey("ln", shifted = "e^", shiftedAction = "e^", alpha = "H"),
        SciKey("(−)", alpha = "I", style = CalculatorButtonStyle.UTILITY),
        SciKey("hyp", alpha = "J", isToggle = true),
    ),
    listOf(
        SciKey("a b/c", shifted = "d/c", alpha = "K"),
        SciKey("√", action = "√", shifted = "∛", alpha = "L"),
        SciKey("x²", action = "x²", alpha = "M"),
        SciKey("x³", action = "x³", alpha = "N"),
        SciKey("x⁻¹", action = "x⁻¹", shifted = "nCr", alpha = "O"),
    ),
    listOf(
        SciKey("xʸ", action = "^", shifted = "nPr", alpha = "P"),
        SciKey("×10ˣ", action = "×10^", shifted = "Ran#", alpha = "Q"),
        SciKey("!", shifted = "°′″", shiftedAction = "°~", alpha = "R"),
        SciKey("π", shifted = "τ", alpha = "S"),
        SciKey("e", alpha = "T"),
    ),
    listOf(
        SciKey("STO", alpha = "U", style = CalculatorButtonStyle.MEMORY),
        SciKey("RCL", alpha = "V", style = CalculatorButtonStyle.MEMORY),
        SciKey("DRG▶", alpha = "W", style = CalculatorButtonStyle.MEMORY, isToggle = true),
        SciKey("ENG", alpha = "X", style = CalculatorButtonStyle.MEMORY, isToggle = true),
        SciKey("Ans", alpha = "Y"),
    ),
    listOf(
        SciKey("7", style = CalculatorButtonStyle.NUMBER),
        SciKey("8", style = CalculatorButtonStyle.NUMBER),
        SciKey("9", style = CalculatorButtonStyle.NUMBER),
        SciKey("DEL", style = CalculatorButtonStyle.UTILITY),
        SciKey("AC", style = CalculatorButtonStyle.UTILITY),
    ),
    listOf(
        SciKey("4", style = CalculatorButtonStyle.NUMBER),
        SciKey("5", style = CalculatorButtonStyle.NUMBER),
        SciKey("6", style = CalculatorButtonStyle.NUMBER),
        SciKey("×", style = CalculatorButtonStyle.OPERATOR),
        SciKey("÷", style = CalculatorButtonStyle.OPERATOR),
    ),
    listOf(
        SciKey("1", style = CalculatorButtonStyle.NUMBER),
        SciKey("2", style = CalculatorButtonStyle.NUMBER),
        SciKey("3", style = CalculatorButtonStyle.NUMBER),
        SciKey("+", style = CalculatorButtonStyle.OPERATOR),
        SciKey("−", style = CalculatorButtonStyle.OPERATOR),
    ),
    listOf(
        SciKey("SHIFT", style = CalculatorButtonStyle.TOGGLE, isToggle = true),
        SciKey("ALPHA", style = CalculatorButtonStyle.TOGGLE, isToggle = true),
        SciKey("0", style = CalculatorButtonStyle.NUMBER),
        SciKey(".", style = CalculatorButtonStyle.NUMBER),
        SciKey("=", style = CalculatorButtonStyle.EQUALS),
    ),
)

@Composable
fun ScientificKeypad(
    onButtonPressed: (String) -> Unit,
    isSecondMode: Boolean = false,
    isAlphaMode: Boolean = false,
    isEngMode: Boolean = false,
    isSDMode: Boolean = false,
    isHypMode: Boolean = false,
    angleMode: AngleMode = AngleMode.DEGREE,
    modifier: Modifier = Modifier,
    colors: ThemeColors = if (isSystemInDarkTheme()) KraftThemeColors.dark else KraftThemeColors.light,
) {
    val gap = KraftSpacing.Spacing6
    val sectionGap = KraftSpacing.Spacing12
    val bottomGap = KraftSpacing.Spacing8
    val cols = 5

    BoxWithConstraints(modifier = modifier.fillMaxWidth()) {
        val w = maxWidth
        val btnW = (w - gap * (cols + 1)) / cols
        if (btnW <= 0.dp) return@BoxWithConstraints

        val functionBtnH = maxOf(btnW * 0.5f, KraftSpacing.TouchTarget)
        val numberBtnH = maxOf(btnW * 0.8f, KraftSpacing.Spacing48)

        // Total height = scientific keypad total (same calculation as KeypadSizing)
        // We just use the values directly here for the rows.

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = gap, end = gap, bottom = bottomGap),
        ) {
            for (i in 0..4) {
                SciRow(
                    keys = keyRows[i],
                    btnH = functionBtnH,
                    gap = gap,
                    isSecondMode = isSecondMode,
                    isAlphaMode = isAlphaMode,
                    isHypMode = isHypMode,
                    isEngMode = isEngMode,
                    isSDMode = isSDMode,
                    onButtonPressed = onButtonPressed,
                    colors = colors,
                )
                if (i < 4) Spacer(Modifier.height(gap))
            }

            Spacer(Modifier.height(sectionGap - gap))

            for (i in 5..8) {
                SciRow(
                    keys = keyRows[i],
                    btnH = numberBtnH,
                    gap = gap,
                    isSecondMode = isSecondMode,
                    isAlphaMode = isAlphaMode,
                    isHypMode = isHypMode,
                    isEngMode = isEngMode,
                    isSDMode = isSDMode,
                    onButtonPressed = onButtonPressed,
                    colors = colors,
                )
                if (i < 8) Spacer(Modifier.height(gap))
            }
        }
    }
}

@Composable
private fun SciRow(
    keys: List<SciKey>,
    btnH: Dp,
    gap: Dp,
    isSecondMode: Boolean,
    isAlphaMode: Boolean,
    isHypMode: Boolean,
    isEngMode: Boolean,
    isSDMode: Boolean,
    onButtonPressed: (String) -> Unit,
    colors: ThemeColors,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(btnH),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Spacer(Modifier.width(gap))
        for (key in keys) {
            val (label, action, style) = if (isHypMode && (key.primary == "sin" || key.primary == "cos" || key.primary == "tan")) {
                val hypPrefix = "${key.primary}h"
                val invHypPrefix = "${key.primary}h⁻¹"
                Triple(
                    if (isSecondMode) invHypPrefix else hypPrefix,
                    if (isSecondMode) invHypPrefix else hypPrefix,
                    if (isSecondMode) CalculatorButtonStyle.SHIFT_SCI else CalculatorButtonStyle.SCIENTIFIC,
                )
            } else {
                Triple(
                    key.label(isSecondMode, isAlphaMode),
                    key.resolveAction(isSecondMode, isAlphaMode),
                    key.resolveStyle(isSecondMode, isAlphaMode),
                )
            }

            val isActive = (key.primary == "SHIFT" && isSecondMode) ||
                    (key.primary == "ALPHA" && isAlphaMode) ||
                    (key.primary == "ENG" && key.isToggle && isEngMode) ||
                    (key.primary == "S⇔D" && key.isToggle && isSDMode) ||
                    (key.primary == "hyp" && key.isToggle && isHypMode)

            val effectiveStyle = if (key.primary == "SHIFT" || key.primary == "ALPHA")
                CalculatorButtonStyle.TOGGLE else style

            CalculatorButton(
                label = label,
                style = effectiveStyle,
                onClick = { onButtonPressed(action) },
                isActive = isActive,
                colors = colors,
                modifier = Modifier.weight(1f),
            )
            Spacer(Modifier.width(gap))
        }
    }
}
