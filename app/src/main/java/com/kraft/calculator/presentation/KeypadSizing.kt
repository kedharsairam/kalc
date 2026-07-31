package com.kraft.calculator.presentation

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/// Shared sizing logic so Basic and Scientific keypads have the same total height,
/// keeping the display area stable when switching modes.
object KeypadSizing {

    // Basic keypad constants
    private const val BASIC_COLS = 4
    private const val BASIC_GAP = 8
    private const val BASIC_ROWS = 5

    // Scientific keypad constants
    private const val SCI_COLS = 5
    private const val SCI_GAP = 6
    private const val SCI_SECTION_GAP = 12
    private const val SCI_BOTTOM_GAP = 8
    private const val SCI_FUNCTION_ROWS = 5
    private const val SCI_NUMBER_ROWS = 4

    /// Total height of the scientific keypad for a given available width.
    fun scientificTotalHeight(availableWidth: Dp): Dp {
        val btnW = (availableWidth - SCI_GAP.dp * (SCI_COLS + 1)) / SCI_COLS
        if (btnW <= 0.dp) return 0.dp

        val functionBtnH = btnW * 0.5f
        val numberBtnH = btnW * 0.8f

        // Gaps: (5 func rows → 4 gaps) + (4 number rows → 3 gaps) = 7 regular gaps
        val regularGaps = (SCI_FUNCTION_ROWS - 1) + (SCI_NUMBER_ROWS - 1)
        return functionBtnH * SCI_FUNCTION_ROWS +
                numberBtnH * SCI_NUMBER_ROWS +
                SCI_GAP.dp * regularGaps +
                (SCI_SECTION_GAP - SCI_GAP).dp +
                SCI_BOTTOM_GAP.dp
    }

    /// Row height for the basic keypad so its total matches the scientific keypad's.
    fun basicRowHeight(availableWidth: Dp): Dp {
        val total = scientificTotalHeight(availableWidth)
        if (total <= 0.dp) return 0.dp

        val gaps = BASIC_GAP.dp * (BASIC_ROWS - 1) + BASIC_GAP.dp // vertical gaps + bottom
        return (total - gaps) / BASIC_ROWS
    }

    /// Basic keypad button width for a given available width.
    fun basicButtonWidth(availableWidth: Dp): Dp {
        return (availableWidth - BASIC_GAP.dp * (BASIC_COLS + 1)) / BASIC_COLS
    }
}
