package com.kraft.calculator.presentation

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.kraft.calculator.ui.theme.KraftThemeColors
import com.kraft.calculator.ui.theme.ThemeColors

@Composable
fun BasicKeypad(
    onButtonPressed: (String) -> Unit,
    modifier: Modifier = Modifier,
    colors: ThemeColors = if (isSystemInDarkTheme()) KraftThemeColors.dark else KraftThemeColors.light,
) {
    val gap = 8.dp

    BoxWithConstraints(modifier = modifier.fillMaxWidth()) {
        val btnH = KeypadSizing.basicRowHeight(maxWidth)
        if (btnH <= 0.dp) return@BoxWithConstraints

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = gap, end = gap, bottom = gap),
        ) {
            BasicRow(gap, btnH, onButtonPressed, "⌫", "AC", "%", "÷", colors)
            Spacer(Modifier.height(gap))
            BasicRow(gap, btnH, onButtonPressed, "7", "8", "9", "×", colors)
            Spacer(Modifier.height(gap))
            BasicRow(gap, btnH, onButtonPressed, "4", "5", "6", "−", colors)
            Spacer(Modifier.height(gap))
            BasicRow(gap, btnH, onButtonPressed, "1", "2", "3", "+", colors)
            Spacer(Modifier.height(gap))
            BasicRow(gap, btnH, onButtonPressed, "±", "0", ".", "=", colors)
        }
    }
}

@Composable
private fun BasicRow(
    gap: androidx.compose.ui.unit.Dp,
    height: androidx.compose.ui.unit.Dp,
    onButtonPressed: (String) -> Unit,
    l1: String, l2: String, l3: String, l4: String,
    colors: ThemeColors,
) {
    fun style(label: String) = when (label) {
        "⌫", "AC", "%", "±" -> CalculatorButtonStyle.utility
        "÷", "×", "−", "+" -> CalculatorButtonStyle.operator
        "=" -> CalculatorButtonStyle.equals
        else -> CalculatorButtonStyle.number
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(height),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Spacer(Modifier.width(gap))
        CalculatorButton(l1, style(l1), onClick = { onButtonPressed(l1) }, largeFont = true, colors = colors, modifier = Modifier.weight(1f))
        Spacer(Modifier.width(gap))
        CalculatorButton(l2, style(l2), onClick = { onButtonPressed(l2) }, largeFont = true, colors = colors, modifier = Modifier.weight(1f))
        Spacer(Modifier.width(gap))
        CalculatorButton(l3, style(l3), onClick = { onButtonPressed(l3) }, largeFont = true, colors = colors, modifier = Modifier.weight(1f))
        Spacer(Modifier.width(gap))
        CalculatorButton(l4, style(l4), onClick = { onButtonPressed(l4) }, largeFont = true, colors = colors, modifier = Modifier.weight(1f))
        Spacer(Modifier.width(gap))
    }
}
