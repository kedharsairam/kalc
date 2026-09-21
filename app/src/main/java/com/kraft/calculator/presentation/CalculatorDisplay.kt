package com.kraft.calculator.presentation

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.foundation.ExperimentalFoundationApi
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kraft.calculator.domain.AngleMode
import com.kraft.calculator.domain.CalculationEntry
import com.kraft.calculator.domain.CalculatorMode
import com.kraft.calculator.ui.theme.KraftFontSizes
import com.kraft.calculator.ui.theme.KraftRadius
import com.kraft.calculator.ui.theme.KraftSpacing
import com.kraft.calculator.ui.theme.KraftThemeColors
import com.kraft.calculator.ui.theme.ThemeColors
import kotlinx.coroutines.launch
import kotlin.math.roundToInt
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

/**
 * The calculator's main display area.
 *
 * Shows three zones stacked vertically:
 * 1. Ticker tape — last 2 entries in compact form (when history is non-empty).
 * 2. Main display — either a single large number (typing/empty state) or
 *    an expression line + hero result (full expression state).
 * 3. Preview bar — contextual info: Ans value, stored variables, mode badges.
 *
 * All sizing uses [KraftSpacing] for layout and [DisplayFontSizes] / [KraftFontSizes]
 * for typography. No magic numbers.
 */
@OptIn(ExperimentalFoundationApi::class)
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

    /** Copies [text] to clipboard and shows a snackbar confirmation. */
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
            .defaultMinSize(minHeight = DisplayMinHeight.minHeight)
            .padding(
                start = KraftSpacing.spacing20,
                end = KraftSpacing.spacing16,
                top = KraftSpacing.spacing8,
                bottom = KraftSpacing.spacing8,
            ),
        verticalArrangement = Arrangement.Bottom,
    ) {
        // ── Ticker tape ──────────────────────────────────────────────────────
        // Last 2 calculation entries in compact form.
        // (A separate BadgeRow was removed — it duplicated this information.)
        if (history.isNotEmpty()) {
            TickerTape(
                history = history,
                colors = colors,
                modifier = Modifier
                    .weight(1f, fill = false)
                    .padding(bottom = KraftSpacing.spacing4),
            )
        } else {
            Spacer(Modifier.weight(1f, fill = false))
        }

        // ── Main display ──────────────────────────────────────────────────────
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.Bottom,
        ) {
            // Single large number when typing or empty — no size jump when result appears.
            // Full expression shown as small muted line + hero result below.
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
                // Error state — single line, title-1 size, red.
                Text(
                    text = error,
                    fontSize = KraftFontSizes.title1,
                    fontWeight = FontWeight.Medium,
                    color = colors.accentRed,
                    textAlign = TextAlign.End,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.fillMaxWidth(),
                )
            } else if (isSingleNumber) {
                // Typing/empty: show current value large, right-aligned, scrollable.
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
                        fontSize = DisplayFontSizes.heroResult,
                        fontWeight = FontWeight.Bold,
                        color = colors.textPrimary,
                        textAlign = TextAlign.End,
                        maxLines = 2,
                        softWrap = false,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            } else {
                // Full expression: adaptive-size expression line + hero result below.
                val heroScroll = rememberScrollState()
                LaunchedEffect(expression) {
                    heroScroll.scrollTo(heroScroll.maxValue)
                }
                val exprSize = remember(expression) {
                    when {
                        expression.length > 30 -> DisplayFontSizes.expressionVeryLong
                        expression.length > 20 -> DisplayFontSizes.expressionLong
                        expression.length > 12 -> DisplayFontSizes.expressionMedium
                        else -> DisplayFontSizes.expressionShort
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
                    fontSize = DisplayFontSizes.heroResult,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    color = colors.textPrimary,
                    textAlign = TextAlign.End,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = KraftSpacing.spacing6)
                        .combinedClickable(
                            onClick = {},
                            onLongClick = { copyText(result, "result") },
                        ),
                )
            }
        }

        // ── Preview / status bar ──────────────────────────────────────────────
        // Left: Ans value + stored variable summary.
        // Right: mode/status badges (DEG, SHIFT, ALPHA, HYP, ENG, SD, d/c, M).
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

        // ── Divider ───────────────────────────────────────────────────────────
        HairlineDivider(colors)
    }
}

// ─── Display font sizes ──────────────────────────────────────────────────────
// Calculator-display-specific type scale.
// The hero result (57sp) is intentionally larger than the standard KraftTypography
// scale — it is the primary read-out element, not body text.
// Expression sizes are adaptive: shorter expressions render larger for visual weight.
object DisplayFontSizes {
    val heroResult = 57.sp
    val expressionShort = 24.sp
    val expressionMedium = 21.sp
    val expressionLong = 18.sp
    val expressionVeryLong = 14.sp
    val badge = 10.sp
}

/** Minimum height for the display column. Ensures the display area never collapses. */
object DisplayMinHeight {
    val minHeight = 180.dp
}

// ─── Ticker tape ─────────────────────────────────────────────────────────────
/** Last 2 calculation entries, compact right-aligned format. */
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
            .padding(top = KraftSpacing.spacing2),
        horizontalAlignment = Alignment.End,
    ) {
        entries.reversed().forEach { entry ->
            Text(
                text = "${entry.expression} = ${entry.result}",
                fontSize = KraftFontSizes.caption1,
                fontWeight = FontWeight.Normal,
                color = colors.textTertiary,
                textAlign = TextAlign.End,
                maxLines = 1,
                softWrap = false,
                modifier = Modifier.padding(vertical = KraftSpacing.spacing1),
            )
        }
    }
}

// ─── Preview / status bar ────────────────────────────────────────────────────
/** Contextual info strip: Ans + variable summary (left), mode badges (right). */
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
            .padding(top = KraftSpacing.spacing4, bottom = KraftSpacing.spacing2),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // Left: Ans + scrub context
        Row(horizontalArrangement = Arrangement.Start, verticalAlignment = Alignment.CenterVertically) {
            if (showAns) {
                Text(
                    text = "Ans: ${formatAns(lastResult!!)}",
                    fontSize = KraftFontSizes.caption2,
                    fontWeight = FontWeight.Medium,
                    color = colors.textTertiary,
                )
            }
            // Stored variable summary (up to 3, e.g. "rent=1200 · tax=5")
            if (variables.isNotEmpty()) {
                val summary = remember(variables) {
                    variables.entries.take(3)
                        .joinToString(" · ") { "${it.key}=${formatAns(it.value)}" }
                }
                Text(
                    text = if (showAns) "  $summary" else summary,
                    fontSize = KraftFontSizes.caption2,
                    fontWeight = FontWeight.Normal,
                    color = colors.textTertiary,
                    maxLines = 1,
                    softWrap = false,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }

        // Right: status badges
        Row(horizontalArrangement = Arrangement.End, verticalAlignment = Alignment.CenterVertically) {
            if (memory != 0.0) {
                MiniBadge("M", colors.accentYellow, androidx.compose.ui.graphics.Color.Black)
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
                if (isSecondMode) MiniBadge("SHIFT", colors.accentOrange, androidx.compose.ui.graphics.Color.Black)
                if (isAlphaMode) MiniBadge("ALPHA", colors.accentRed, androidx.compose.ui.graphics.Color.White)
                if (isHypMode) MiniBadge("HYP", colors.accentOrange, androidx.compose.ui.graphics.Color.Black)
                if (isEngMode) MiniBadge("ENG", colors.surfaceTertiary, colors.textTertiary)
                if (isSDMode) MiniBadge("SD", colors.accentGreen, androidx.compose.ui.graphics.Color.Black)
                if (isDCMode) MiniBadge("d/c", colors.accentGreen, androidx.compose.ui.graphics.Color.Black)
            }
        }
    }
}

// ─── Mini badge ──────────────────────────────────────────────────────────────
/** Small pill badge for status indicators (M, DEG, SHIFT, ALPHA, etc.). */
@Composable
private fun MiniBadge(label: String, bg: androidx.compose.ui.graphics.Color, fg: androidx.compose.ui.graphics.Color) {
    Box(
        modifier = Modifier
            .padding(start = KraftSpacing.spacing4)
            .background(
                color = bg,
                shape = RoundedCornerShape(KraftRadius.tiny),
            )
            .padding(horizontal = KraftSpacing.spacing4, vertical = KraftSpacing.spacing2),
    ) {
        Text(
            text = label,
            fontSize = DisplayFontSizes.badge,
            fontWeight = FontWeight.SemiBold,
            color = fg,
            letterSpacing = 0.3.sp,
        )
    }
}

// ─── Formatting helpers ──────────────────────────────────────────────────────

/**
 * Adds thousand separators to a numeric string for display.
 *
 * Only formats pure numbers (not expressions). Uses locale grouping.
 * Engine output stays clean for parsing; this is display-layer only.
 */
fun formatWithGrouping(value: String): String {
    if (!value.matches(Regex("""^−?\d+(\.\d+)?$"""))) return value
    try {
        val isNegative = value.startsWith("−")
        val abs = if (isNegative) value.drop(1) else value
        val parts = abs.split(".")
        val intPart = parts[0].toLongOrNull() ?: return value
        if (intPart < 1000) return value
        val symbols = DecimalFormatSymbols(Locale.getDefault())
        val df = DecimalFormat("#,###", symbols)
        df.isGroupingUsed = true
        val grouped = df.format(intPart)
        val result = if (parts.size > 1) "$grouped.${parts[1]}" else grouped
        return if (isNegative) "−$result" else result
    } catch (_: Exception) {
        return value
    }
}

/** Formats a [Double] for the Ans preview: integers as-is, otherwise up to 4 decimals. */
private fun formatAns(value: Double): String {
    return if (value == value.roundToInt().toDouble()) {
        value.toInt().toString()
    } else {
        val formatted = String.format(Locale.US, "%.4f", value).trimEnd('0')
        formatted.trimEnd('.')
    }
}

// ─── Divider ─────────────────────────────────────────────────────────────────

/** Hairline separator between the display and the preview bar. */
@Composable
private fun HairlineDivider(colors: ThemeColors) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(KraftSpacing.spacing1)
            .padding(top = KraftSpacing.spacing2)
            .background(colors.separator),
    )
}
