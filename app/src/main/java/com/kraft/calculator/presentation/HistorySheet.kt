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
            .padding(top = 8.dp)
    ) {
        // Single clean header: title + count + clear
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "History",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.textPrimary,
                )
                if (history.isNotEmpty()) {
                    Spacer(Modifier.width(8.dp))
                    Surface(
                        color = colors.surfaceTertiary,
                        shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
                    ) {
                        Text(
                            text = history.size.toString(),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = colors.textSecondary,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 2.dp),
                        )
                    }
                }
            }
            if (history.isNotEmpty()) {
                TextButton(
                    onClick = onClearAll,
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                ) {
                    Text(
                        text = "Clear all",
                        fontSize = 15.sp,
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
                    .padding(vertical = 48.dp, horizontal = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Icon(
                    imageVector = Icons.Default.History,
                    contentDescription = null,
                    modifier = Modifier.size(56.dp),
                    tint = colors.textTertiary,
                )
                Text(
                    text = "No calculations yet",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = colors.textPrimary,
                )
                Text(
                    text = "Results you calculate will appear here.\nTap an entry to reload it.",
                    fontSize = 14.sp,
                    color = colors.textSecondary,
                    textAlign = TextAlign.Center,
                    lineHeight = 20.sp,
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(bottom = 24.dp),
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
                .padding(start = 20.dp, end = 8.dp, top = 14.dp, bottom = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.Start,
            ) {
            Text(
                text = entry.expression,
                fontSize = 15.sp,
                fontWeight = FontWeight.Normal,
                color = colors.textSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Start,
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = "= ${formatHistoryResult(entry.result)}",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                color = colors.textPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Start,
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = timeLabel,
                fontSize = 12.sp,
                color = colors.textTertiary,
                textAlign = TextAlign.Start,
                modifier = Modifier.fillMaxWidth(),
            )
            }
            IconButton(
                onClick = onDelete,
                modifier = Modifier.size(44.dp),
            ) {
                Icon(
                    imageVector = Icons.Default.DeleteOutline,
                    contentDescription = "Delete ${entry.expression}",
                    tint = colors.textTertiary,
                    modifier = Modifier.size(20.dp),
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
