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
    variables: Map<String, Double> = emptyMap(),
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
        // ── Zone 1: Ticker tape (fills available space above) ──
        // Status badges (SHIFT/DEG/ENG/etc.) live ONLY in PreviewBar below.
        // BadgeRow removed — was duplicating the same info twice.
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
            // Determine display mode:
            // - Typing a number (or empty): show it LARGE (no size jump)
            // - Full expression with result: expression small + result hero
            // - Error: show error medium red
            val isSingleNumber = remember(expression, result) {
                if (expression.isEmpty()) true
                else {
                    val cleanExpr = expression.replace(",", "").replace("−", "-")
                    val cleanResult = result.replace(",", "").replace("−", "-")
                    val exprNum = cleanExpr.toDoubleOrNull()
                    val resNum = cleanResult.toDoubleOrNull()
                    exprNum != null && resNum != null &&
                        kotlin.math.abs(exprNum - resNum) <= 1e-12
                }
            }

            if (error != null) {
                // Error state: medium red, single line
                Text(
                    text = error,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Medium,
                    color = colors.accentRed,
                    textAlign = TextAlign.End,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.fillMaxWidth(),
                )
            } else if (isSingleNumber) {
                // Typing or empty: show current value LARGE (no jump when result appears)
                val displayValue = if (expression.isNotEmpty()) expression else result
                val largeScroll = rememberScrollState()
                LaunchedEffect(displayValue) {
                    largeScroll.scrollTo(largeScroll.maxValue)
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(largeScroll)
                        .combinedClickable(
                            onClick = {},
                            onLongClick = { if (displayValue.isNotEmpty()) copyText(displayValue, "value") },
                        ),
                    horizontalArrangement = Arrangement.End,
                ) {
                    Text(
                        text = remember(displayValue) { formatWithGrouping(displayValue) },
                        fontSize = 57.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.textPrimary,
                        textAlign = TextAlign.End,
                        maxLines = 2,
                        softWrap = false,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            } else {
                // Full expression: small muted on top, hero result below
                val heroScroll = rememberScrollState()
                LaunchedEffect(expression) {
                    heroScroll.scrollTo(heroScroll.maxValue)
                }
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
                            onLongClick = { if (expression.isNotEmpty()) copyText(expression, "expression") },
                        ),
                    horizontalArrangement = Arrangement.End,
                ) {
                    Text(
                        text = expression,
                        fontSize = exprSize,
                        fontWeight = FontWeight.Normal,
                        color = colors.textSecondary,
                        textAlign = TextAlign.End,
                        maxLines = 2,
                        softWrap = false,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
                Text(
                    text = remember(result) { formatWithGrouping(result) },
                    fontSize = 57.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
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
            variables = variables,
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
    variables: Map<String, Double> = emptyMap(),
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
            // Subtle indicator for stored variables (up to 3, e.g. "rent=1200 · tax=5")
            if (variables.isNotEmpty()) {
                val summary = remember(variables) {
                    variables.entries.take(3)
                        .joinToString(" · ") { "${it.key}=${formatAns(it.value)}" }
                }
                Text(
                    text = if (showAns) "  $summary" else summary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Normal,
                    color = colors.textTertiary,
                    maxLines = 1,
                    softWrap = false,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }

        // Right: status badges
        // Text colors fixed for contrast on accent backgrounds (not theme-dependent)
        Row(horizontalArrangement = Arrangement.End, verticalAlignment = Alignment.CenterVertically) {
            if (memory != 0.0) {
                MiniBadge("M", colors.accentYellow, Color.Black)
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
                if (isSecondMode) MiniBadge("SHIFT", colors.accentOrange, Color.Black)
                if (isAlphaMode) MiniBadge("ALPHA", colors.accentRed, Color.White)
                if (isHypMode) MiniBadge("HYP", colors.accentOrange, Color.Black)
                if (isEngMode) MiniBadge("ENG", colors.surfaceTertiary, colors.textTertiary)
                if (isSDMode) MiniBadge("SD", colors.accentGreen, Color.Black)
                if (isDCMode) MiniBadge("d/c", colors.accentGreen, Color.Black)
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
