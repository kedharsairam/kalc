package com.kraft.calculator.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kraft.calculator.domain.CalculationEntry
import com.kraft.calculator.ui.theme.ThemeColors
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit
import com.kraft.ui.tokens.KraftSpacing
import com.kraft.ui.tokens.KraftRadius
import com.kraft.calculator.ui.theme.HistoryRationale
import com.kraft.calculator.ui.theme.DisplayFontSizes

@Composable
fun HistorySheet(
    history: List<CalculationEntry>,
    onClearAll: () -> Unit,
    onDeleteEntry: (Long) -> Unit,
    onSelectEntry: (CalculationEntry) -> Unit,
    modifier: Modifier = Modifier,
    colors: ThemeColors,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = KraftSpacing.Spacing8)
    ) {
        // Single clean header: title + count + clear
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = KraftSpacing.Spacing20, vertical = KraftSpacing.Spacing12),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "History",
                      style = MaterialTheme.typography.displaySmall,
                      fontWeight = FontWeight.Bold,
                    color = colors.textPrimary,
                )
                if (history.isNotEmpty()) {
                    Spacer(Modifier.width(KraftSpacing.Spacing8))
                    Surface(
                        color = colors.surfaceTertiary,
                        shape = androidx.compose.foundation.shape.RoundedCornerShape(KraftSpacing.Spacing12),
                    ) {
                        Text(
                            text = history.size.toString(),
                            style = MaterialTheme.typography.labelLarge,
                            color = colors.textSecondary,
                            modifier = Modifier.padding(horizontal = KraftSpacing.Spacing8, vertical = KraftSpacing.Spacing2),
                        )
                    }
                }
            }
            if (history.isNotEmpty()) {
                TextButton(
                    onClick = onClearAll,
                    contentPadding = PaddingValues(horizontal = KraftSpacing.Spacing12, vertical = KraftSpacing.Spacing8),
                ) {
                    Text(
                        text = "Clear all",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        color = colors.accentRed,
                    )
                }
            }
        }

        if (history.isEmpty()) {
            // Empty state with CTA
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = KraftSpacing.Spacing48, horizontal = KraftSpacing.Spacing32),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(KraftSpacing.Spacing12),
            ) {
                Icon(
                    imageVector = Icons.Default.History,
                    contentDescription = null,
                    modifier = Modifier.size(KraftSpacing.Spacing56),
                    tint = colors.textTertiary,
                )
                Text(
                    text = "No calculations yet",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = colors.textPrimary,
                )
                Text(
                    text = "Results you calculate will appear here.\nTap an entry to reload it.",
                    style = HistoryRationale,
                    color = colors.textSecondary,
                    textAlign = TextAlign.Center,
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(bottom = KraftSpacing.Spacing24),
            ) {
                items(
                    items = history,
                    key = { "${it.timestamp}_${it.expression.hashCode()}" },
                ) { entry ->
                    HistoryTile(
                        entry = entry,
                        colors = colors,
                        onSelect = { onSelectEntry(entry) },
                        onDelete = { onDeleteEntry(entry.timestamp) },
                        modifier = Modifier.animateItem(),
                    )
                }
            }
        }
    }
}

@Composable
private fun HistoryTile(
    entry: CalculationEntry,
    onSelect: () -> Unit,
    onDelete: () -> Unit,
    colors: ThemeColors,
    modifier: Modifier = Modifier,
) {
    val timeLabel = remember(entry.timestamp) { relativeTime(entry.timestamp) }
    Surface(
        onClick = onSelect,
        color = Color.Transparent,
        modifier = modifier
            .fillMaxWidth()
            .semantics {
                contentDescription = "${entry.expression} equals ${entry.result}. Tap to reload."
            },
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = KraftSpacing.Spacing20, end = KraftSpacing.Spacing8, top = KraftSpacing.Spacing16, bottom = KraftSpacing.Spacing16),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.Start,
            ) {
            Text(
                text = entry.expression,
                style = MaterialTheme.typography.bodyMedium,
                color = colors.textSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Start,
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(Modifier.height(KraftSpacing.Spacing2))
            Text(
                text = "= ${formatHistoryResult(entry.result)}",
                fontSize = DisplayFontSizes.historyResult,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                color = colors.textPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Start,
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(Modifier.height(KraftSpacing.Spacing2))
            Text(
                text = timeLabel,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Normal,
                color = colors.textTertiary,
                textAlign = TextAlign.Start,
                modifier = Modifier.fillMaxWidth(),
            )
            }
            IconButton(
                onClick = onDelete,
                modifier = Modifier.size(KraftSpacing.TouchTarget),
            ) {
                Icon(
                    imageVector = Icons.Default.DeleteOutline,
                    contentDescription = "Delete ${entry.expression}",
                    tint = colors.textTertiary,
                    modifier = Modifier.size(KraftSpacing.Spacing20),
                )
            }
        }
    }
}

private fun formatHistoryResult(result: String): String {
    // Add thousand separators for display (engine output stays clean)
    return try {
        if (!result.matches(Regex("""^−?\d+(\.\d+)?$"""))) return result
        val isNeg = result.startsWith("−")
        val abs = if (isNeg) result.drop(1) else result
        val parts = abs.split(".")
        val intVal = parts[0].toLongOrNull() ?: return result
        if (intVal < 1000) return result
        val grouped = "%,d".format(java.util.Locale.US, intVal)
        val out = if (parts.size > 1) "$grouped.${parts[1]}" else grouped
        if (isNeg) "−$out" else out
    } catch (_: Exception) {
        result
    }
}

private fun relativeTime(timestamp: Long): String {
    val now = System.currentTimeMillis()
    val diff = now - timestamp
    if (diff < 0) return "just now"
    val mins = TimeUnit.MILLISECONDS.toMinutes(diff)
    if (mins < 1) return "just now"
    if (mins < 60) return "${mins}m ago"
    val hours = TimeUnit.MILLISECONDS.toHours(diff)
    if (hours < 24) return "${hours}h ago"
    val days = TimeUnit.MILLISECONDS.toDays(diff)
    if (days < 7) return "${days}d ago"
    return SimpleDateFormat("MMM d", Locale.getDefault()).format(Date(timestamp))
}
