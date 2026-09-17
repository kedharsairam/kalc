package com.kraft.calculator.presentation

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.LaunchedEffect
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

@OptIn(androidx.compose.foundation.ExperimentalFoundationApi::class)
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
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

    fun copyText(text: String, label: String) {
        val clip = ClipData.newPlainText(label, text)
        clipboard.setPrimaryClip(clip)
        scope.launch {
            snackbarHostState?.showSnackbar("Copied $label")
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = 180.dp)
            .padding(start = 20.dp, end = 16.dp, top = 8.dp, bottom = 8.dp),
        verticalArrangement = Arrangement.Bottom,
    ) {
        // ── Zone 1: Badges (sci mode only, compact) ──
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
                modifier = Modifier.padding(bottom = 4.dp),
            )
        }

        // ── Zone 2: Ticker tape (fills available space above, like Zeevy InlineTape) ──
        if (history.isNotEmpty()) {
            TickerTape(
                history = history,
                colors = colors,
                modifier = Modifier
                    .weight(1f, fill = false)
                    .padding(bottom = 4.dp),
            )
        } else {
            Spacer(Modifier.weight(1f, fill = false))
        }

        // ── Zone 3: Fixed 2-line display (bottom-anchored, never shifts) ──
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.Bottom,
        ) {
            // Expression line (subordinate, muted, shrinks) — Zeevy pattern
            // Long-press copies expression
            val heroScroll = rememberScrollState()
            LaunchedEffect(expression, result, error) {
                heroScroll.scrollTo(heroScroll.maxValue)
            }
            // Auto-shrink expression based on length (24sp → 14sp floor)
            val exprSize = remember(expression) {
                when {
                    expression.length > 30 -> 14.sp
                    expression.length > 20 -> 18.sp
                    expression.length > 12 -> 21.sp
                    else -> 24.sp
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(heroScroll)
                    .combinedClickable(
                        onClick = {},
                        onLongClick = {
                            val text = if (expression.isNotEmpty()) expression else result
                            if (text.isNotEmpty()) copyText(text, "expression")
                        },
                    ),
                horizontalArrangement = Arrangement.End,
            ) {
                Text(
                    text = when {
                        error != null -> error
                        expression.isNotEmpty() -> expression
                        else -> result
                    },
                    fontSize = exprSize,
                    fontWeight = FontWeight.Normal,
                    color = when {
                        error != null -> colors.accentRed
                        else -> colors.textSecondary
                    },
                    textAlign = TextAlign.End,
                    maxLines = 2,
                    softWrap = false,
                    overflow = TextOverflow.Ellipsis,
                )
            }

            // Result line — HERO (57sp fixed, dominant) — Zeevy pattern
            // Only show when numerically different from expression
            // (compares values, not strings, so "123456" vs "123,456" doesn't duplicate)
            // Long-press copies result
            val showResult = remember(expression, result, error) {
                if (expression.isEmpty() || error != null) false
                else {
                    // Strip grouping separators and compare numerically
                    val cleanExpr = expression.replace(",", "").replace("−", "-")
                    val cleanResult = result.replace(",", "").replace("−", "-")
                    // If expression is a single number equal to result, hide duplicate
                    val exprNum = cleanExpr.toDoubleOrNull()
                    val resNum = cleanResult.toDoubleOrNull()
                    if (exprNum != null && resNum != null) {
                        // Same value (within epsilon) = don't duplicate
                        kotlin.math.abs(exprNum - resNum) > 1e-12
                    } else {
                        // Expression has operators, always show result
                        cleanExpr != cleanResult
                    }
                }
            }
            if (showResult) {
                Text(
                    text = remember(result) { formatWithGrouping(result) },
                    fontSize = 57.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.textPrimary,
                    textAlign = TextAlign.End,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 6.dp)
                        .combinedClickable(
                            onClick = {},
                            onLongClick = { copyText(result, "result") },
                        ),
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
