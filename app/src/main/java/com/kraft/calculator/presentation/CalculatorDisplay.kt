package com.kraft.calculator.presentation

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kraft.calculator.domain.AngleMode
import com.kraft.calculator.domain.CalculationEntry
import com.kraft.calculator.domain.CalculatorMode
import com.kraft.calculator.ui.theme.KraftRadius
import com.kraft.calculator.ui.theme.KraftThemeColors
import com.kraft.calculator.ui.theme.ThemeColors
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
fun CalculatorDisplay(
    expression: String,
    result: String,
    error: String? = null,
    mode: CalculatorMode = CalculatorMode.BASIC,
    history: List<CalculationEntry> = emptyList(),
    lastResult: Double? = null,
    isSecondMode: Boolean = false,
    isAlphaMode: Boolean = false,
    isEngMode: Boolean = false,
    isSDMode: Boolean = false,
    isDCMode: Boolean = false,
    isHypMode: Boolean = false,
    memory: Double = 0.0,
    angleMode: AngleMode = AngleMode.DEGREE,
    modifier: Modifier = Modifier,
    colors: ThemeColors = if (isSystemInDarkTheme()) KraftThemeColors.dark else KraftThemeColors.light,
    snackbarHostState: SnackbarHostState? = null,
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 20.dp, end = 16.dp, top = 8.dp, bottom = 8.dp),
    ) {
        // ── Zone 1: Ticker tape + badges ──
        if (mode == CalculatorMode.SCIENTIFIC) {
            BadgeRow(
                isSecondMode = isSecondMode,
                isAlphaMode = isAlphaMode,
                isSDMode = isSDMode,
                isDCMode = isDCMode,
                isHypMode = isHypMode,
                isEngMode = isEngMode,
                memory = memory,
                angleMode = angleMode,
                colors = colors,
                modifier = Modifier.padding(bottom = 2.dp),
            )
        }

        if (history.isNotEmpty()) {
            TickerTape(
                history = history,
                colors = colors,
                modifier = Modifier.padding(bottom = 2.dp),
            )
        }

        // ── Zone 2: Hero expression + result ──
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            verticalArrangement = Arrangement.Center,
        ) {
            // Hero: the current expression (or result if no expression)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.End,
            ) {
                Text(
                    text = when {
                        error != null -> error
                        expression.isNotEmpty() -> expression
                        else -> result
                    },
                    fontSize = if (mode == CalculatorMode.BASIC) 32.sp else 28.sp,
                    fontWeight = when {
                        error != null -> FontWeight.Medium
                        else -> FontWeight.Normal
                    },
                    color = when {
                        error != null -> colors.accentRed
                        else -> colors.textPrimary
                    },
                    textAlign = TextAlign.End,
                    maxLines = 1,
                    softWrap = false,
                )
            }

            // Result line — only when it differs from the hero expression
            if (expression.isNotEmpty() && result != expression && error == null) {
                Text(
                    text = remember(result) { formatWithGrouping(result) },
                    fontSize = if (mode == CalculatorMode.BASIC) 38.sp else 34.sp,
                    fontWeight = FontWeight.Light,
                    color = colors.textPrimary,
                    textAlign = TextAlign.End,
                    maxLines = 1,
                    overflow = TextOverflow.Visible,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 6.dp),
                )
            }

            // Error message below hero
            if (error != null) {
                Text(
                    text = error,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = colors.accentRed,
                    textAlign = TextAlign.End,
                    maxLines = 1,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                )
            }
        }

        // ── Zone 3: Preview / status bar ──
        PreviewBar(
            expression = expression,
            result = result,
            lastResult = lastResult,
            memory = memory,
            angleMode = angleMode,
            mode = mode,
            isSecondMode = isSecondMode,
            isAlphaMode = isAlphaMode,
            isHypMode = isHypMode,
            isEngMode = isEngMode,
            isSDMode = isSDMode,
            isDCMode = isDCMode,
            colors = colors,
        )

        // ── Zone 4: Divider ──
        HairlineDivider(colors)
    }
}

// ─── Ticker Tape ───────────────────────────────────────────────────────────
// Shows the last 2 calculation entries in compact format.
// Each entry: "expression = result"

@Composable
private fun TickerTape(
    history: List<CalculationEntry>,
    colors: ThemeColors,
    modifier: Modifier = Modifier,
) {
    val entries = history.take(2)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 2.dp),
        horizontalAlignment = Alignment.End,
    ) {
        entries.reversed().forEach { entry ->
            Text(
                text = "${entry.expression} = ${entry.result}",
                fontSize = 12.sp,
                fontWeight = FontWeight.Normal,
                color = colors.textTertiary,
                textAlign = TextAlign.End,
                maxLines = 1,
                softWrap = false,
                modifier = Modifier.padding(vertical = 1.dp),
            )
        }
    }
}

// ─── Preview / Status Bar ──────────────────────────────────────────────────
// Shows contextual info in a thin strip:
//   Left: "Ans: X" when lastResult is defined
//   Right: active badges (sci mode), memory indicator

@Composable
private fun PreviewBar(
    expression: String,
    result: String,
    lastResult: Double?,
    memory: Double,
    angleMode: AngleMode,
    mode: CalculatorMode,
    isSecondMode: Boolean,
    isAlphaMode: Boolean,
    isHypMode: Boolean,
    isEngMode: Boolean,
    isSDMode: Boolean,
    isDCMode: Boolean,
    colors: ThemeColors,
) {
    val showAns = lastResult != null

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 4.dp, bottom = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // Left: Ans + scrub context
        Row(horizontalArrangement = Arrangement.Start, verticalAlignment = Alignment.CenterVertically) {
            if (showAns) {
                Text(
                    text = "Ans: ${formatAns(lastResult!!)}",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = colors.textTertiary,
                )
            }
        }

        // Right: status badges
        Row(horizontalArrangement = Arrangement.End, verticalAlignment = Alignment.CenterVertically) {
            if (memory != 0.0) {
                MiniBadge("M", colors.accentYellow, if (isSystemInDarkTheme()) Color.Black else Color.White)
            }

            if (mode == CalculatorMode.SCIENTIFIC) {
                MiniBadge(
                    when (angleMode) {
                        AngleMode.DEGREE -> "DEG"
                        AngleMode.RADIAN -> "RAD"
                        AngleMode.GRAD -> "GRAD"
                    },
                    colors.surfaceTertiary,
                    colors.textTertiary,
                )
                if (isSecondMode) MiniBadge("SHIFT", colors.accentOrange, if (isSystemInDarkTheme()) Color.Black else Color.White)
                if (isAlphaMode) MiniBadge("ALPHA", colors.accentRed, Color.White)
                if (isHypMode) MiniBadge("HYP", colors.accentOrange, if (isSystemInDarkTheme()) Color.Black else Color.White)
                if (isEngMode) MiniBadge("ENG", colors.surfaceTertiary, colors.textTertiary)
                if (isSDMode) MiniBadge("SD", colors.accentGreen, Color.White)
                if (isDCMode) MiniBadge("d/c", colors.accentGreen, Color.White)
            }
        }
    }
}

@Composable
private fun MiniBadge(label: String, bg: Color, fg: Color) {
    Box(
        modifier = Modifier
            .padding(start = 4.dp)
            .background(
                color = bg,
                shape = RoundedCornerShape(KraftRadius.small / 2),
            )
            .padding(horizontal = 5.dp, vertical = 1.dp),
    ) {
        Text(
            text = label,
            fontSize = 10.sp,
            fontWeight = FontWeight.SemiBold,
            color = fg,
            letterSpacing = 0.3.sp,
        )
    }
}

/**
 * Adds thousand separators to a numeric string for display.
 * Only formats pure numbers (not expressions). Uses locale grouping.
 * Engine output stays clean for parsing; this is display-layer only.
 */
fun formatWithGrouping(value: String): String {
    // Only format if it's a pure number (optional minus, digits, optional decimal)
    if (!value.matches(Regex("""^−?\d+(\.\d+)?$"""))) return value
    try {
        val isNegative = value.startsWith("−")
        val abs = if (isNegative) value.drop(1) else value
        val parts = abs.split(".")
        val intPart = parts[0].toLongOrNull() ?: return value
        // Don't group small numbers
        if (intPart < 1000) return value
        val symbols = DecimalFormatSymbols(Locale.getDefault())
        val df = DecimalFormat("#,###", symbols)
        df.isGroupingUsed = true
        val grouped = df.format(intPart)
        // Normalize minus sign
        val result = if (parts.size > 1) "$grouped.${parts[1]}" else grouped
        return if (isNegative) "−$result" else result
    } catch (_: Exception) {
        return value
    }
}

private fun formatAns(value: Double): String {
    return if (value == value.roundToInt().toDouble()) {
        value.toInt().toString()
    } else {
        // Show up to 4 decimal places, strip trailing zeros
        val formatted = String.format(Locale.US, "%.4f", value).trimEnd('0')
        formatted.trimEnd('.')
    }
}

// ─── Divider ───────────────────────────────────────────────────────────────

@Composable
private fun HairlineDivider(colors: ThemeColors) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .padding(top = 2.dp)
            .background(colors.separator),
    )
}

// ─── Badge Row (scientific mode header) ────────────────────────────────────

@Composable
private fun BadgeRow(
    isSecondMode: Boolean,
    isAlphaMode: Boolean,
    isSDMode: Boolean,
    isDCMode: Boolean,
    isHypMode: Boolean,
    isEngMode: Boolean,
    memory: Double,
    angleMode: AngleMode,
    colors: ThemeColors,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End,
    ) {
        if (isSecondMode) Badge("SHIFT", colors.accentOrange, if (isSystemInDarkTheme()) Color.Black else Color.White)
        if (isAlphaMode) Badge("ALPHA", colors.accentRed, Color.White)
        if (isSDMode) Badge("SD", colors.accentGreen, Color.White)
        if (isDCMode) Badge("d/c", colors.accentGreen, Color.White)
        if (isHypMode) Badge("HYP", colors.accentOrange, if (isSystemInDarkTheme()) Color.Black else Color.White)
        if (memory != 0.0) Badge("M", colors.accentYellow, if (isSystemInDarkTheme()) Color.Black else Color.White)
        Badge(
            when (angleMode) {
                AngleMode.DEGREE -> "DEG"
                AngleMode.RADIAN -> "RAD"
                AngleMode.GRAD -> "GRAD"
            },
            colors.surfaceTertiary,
            colors.textTertiary,
        )
        if (isEngMode) Badge("ENG", colors.surfaceTertiary, colors.textTertiary)
    }
}

@Composable
private fun Badge(
    label: String,
    bg: Color,
    fg: Color,
) {
    Box(
        modifier = Modifier
            .padding(end = 4.dp)
            .background(
                color = bg,
                shape = RoundedCornerShape(KraftRadius.small / 2),
            )
            .padding(horizontal = 6.dp, vertical = 1.dp),
    ) {
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            color = fg,
            letterSpacing = 0.3.sp,
        )
    }
}
