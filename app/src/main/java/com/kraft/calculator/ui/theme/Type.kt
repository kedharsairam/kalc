package com.kraft.calculator.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.kraft.ui.tokens.KraftTypeScale
import androidx.compose.ui.unit.dp

val KraftTypography = Typography(
    displayLarge = TextStyle(fontSize = KraftTypeScale.LargeTitle, fontWeight = FontWeight.Normal),
    displayMedium = TextStyle(fontSize = KraftTypeScale.Title1, fontWeight = FontWeight.Normal),
    displaySmall = TextStyle(fontSize = KraftTypeScale.Title2, fontWeight = FontWeight.Normal),
    headlineLarge = TextStyle(fontSize = KraftTypeScale.Title3, fontWeight = FontWeight.SemiBold),
    headlineMedium = TextStyle(fontSize = KraftTypeScale.Headline, fontWeight = FontWeight.SemiBold),
    headlineSmall = TextStyle(fontSize = KraftTypeScale.Headline, fontWeight = FontWeight.Normal),
    titleLarge = TextStyle(fontSize = KraftTypeScale.Title3, fontWeight = FontWeight.SemiBold),
    titleMedium = TextStyle(fontSize = KraftTypeScale.Callout, fontWeight = FontWeight.Medium),
    titleSmall = TextStyle(fontSize = KraftTypeScale.Subheadline, fontWeight = FontWeight.Medium),
    bodyLarge = TextStyle(fontSize = KraftTypeScale.Body, fontWeight = FontWeight.Normal),
    bodyMedium = TextStyle(fontSize = KraftTypeScale.Subheadline, fontWeight = FontWeight.Normal),
    bodySmall = TextStyle(fontSize = KraftTypeScale.Footnote, fontWeight = FontWeight.Normal),
    labelLarge = TextStyle(fontSize = KraftTypeScale.Footnote, fontWeight = FontWeight.SemiBold),
    labelMedium = TextStyle(fontSize = KraftTypeScale.Caption1, fontWeight = FontWeight.SemiBold),
    labelSmall = TextStyle(fontSize = KraftTypeScale.Caption2, fontWeight = FontWeight.SemiBold),
)
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
    val badge = KraftTypeScale.Badge

  /**
   * Secondary money readouts — converter results, EMI and GST figures. Bold monospace at
   * 36sp: the same tabular figures as the 57sp hero, one step down because these screens
   * show several figures at once and five heroes would compete rather than reinforce.
   */
  val resultReadout = 36.sp

  /**
   * Past results in history — 24sp Bold monospace. Smaller than the live readouts because a
   * history card shows several entries and five 36sp figures would compete; larger than body
   * because the result is what the eye lands on. Same tabular figures, one step down.
   */
  val historyResult = 24.sp
}

/**
 * The history rationale line — the longest text on a history card, and the one the app
 * exists to preserve. 14sp with 20sp leading: 13 crowds stacked marks in the scripts this
 * app renders, 15 pushes the card past one screen on small phones, and the leading is what
 * keeps multi-line entries readable rather than the size.
 */
val HistoryRationale = TextStyle(fontSize = 14.sp, lineHeight = 20.sp)

/** Minimum height for the display column. Ensures the display area never collapses. */
object DisplayMinHeight {
  val minHeight = 180.dp
}

/**
 * Calculator-specific type scale for buttons.
 *
 * Sizes are chosen for calculator usability (large targets, clear hierarchy)
 * and do not map 1:1 to the standard KraftTypography body scale.
 * The standard KraftTypography scale is used for labels, headers, and sheet content;
 * this scale is for the keypad only.
 */
object CalculatorFontSizes {
    /** Primary number keys (large layout). */
    val numberLarge = 36.sp
    /** Primary number keys (standard layout). */
    val number = 30.sp
    /** Operator and equals keys (large layout). */
    val operatorLarge = KraftTypeScale.LargeTitle
    /** Operator and equals keys (standard layout). */
    val operator = 26.sp
    /** Utility keys (large layout). */
    val utilityLarge = 30.sp
    /** Utility keys (standard layout). */
    val utility = KraftTypeScale.Title2
    /** Scientific and shift-sci keys. */
    val scientific = KraftTypeScale.Title3
    /** Memory, toggle, and alpha keys (small labels). */
    val label = KraftTypeScale.Footnote
}
