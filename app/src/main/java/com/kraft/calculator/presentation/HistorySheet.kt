package com.kraft.calculator.presentation

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.background
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kraft.calculator.domain.CalculationEntry
import com.kraft.calculator.ui.theme.KraftThemeColors
import com.kraft.calculator.ui.theme.ThemeColors

@Composable
fun HistorySheet(
    history: List<CalculationEntry>,
    onClearAll: () -> Unit,
    onDeleteEntry: (Long) -> Unit,
    onSelectEntry: (CalculationEntry) -> Unit,
    modifier: Modifier = Modifier,
    colors: ThemeColors = if (isSystemInDarkTheme()) KraftThemeColors.dark else KraftThemeColors.light,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 4.dp)
    ) {
        // Section header: HISTORY
        SectionLabel(
            text = "HISTORY",
            color = colors.textPrimary.copy(alpha = 0.5f),
            modifier = Modifier.padding(
                start = 16.dp, top = 24.dp, end = 16.dp, bottom = 8.dp,
            ),
        )

        // Header row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "History",
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = colors.textSecondary,
            )
            if (history.isNotEmpty()) {
                TextButton(
                    onClick = onClearAll,
                    contentPadding = PaddingValues(0.dp),
                ) {
                    Text(
                        text = "Clear",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = colors.accentRed,
                    )
                }
            }
        }

        if (history.isEmpty()) {
            // Empty state
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                contentAlignment = Alignment.Center,
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Icon(
                        imageVector = Icons.Default.History,
                        contentDescription = null,
                        modifier = Modifier.size(48.dp),
                        tint = colors.textTertiary,
                    )
                    Text(
                        text = "No history yet",
                        fontSize = 17.sp,
                        color = colors.textSecondary,
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
            ) {
                items(
                    items = history,
                    key = { "${it.timestamp}_${it.expression.hashCode()}" },
                ) { entry ->
                    val dismissState = androidx.compose.material3.rememberSwipeToDismissBoxState(
                        confirmValueChange = { value ->
                            if (value == androidx.compose.material3.SwipeToDismissBoxValue.EndToStart) {
                                onDeleteEntry(entry.timestamp)
                                true
                            } else false
                        }
                    )
                    androidx.compose.material3.SwipeToDismissBox(
                        state = dismissState,
                        enableDismissFromStartToEnd = false,
                        backgroundContent = {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(colors.accentRed)
                                    .padding(end = 20.dp),
                                contentAlignment = Alignment.CenterEnd,
                            ) {
                                Text(
                                    text = "Delete",
                                    color = Color.White,
                                    fontWeight = FontWeight.SemiBold,
                                )
                            }
                        },
                    ) {
                        HistoryTile(
                            entry = entry,
                            colors = colors,
                            onSelect = { onSelectEntry(entry) },
                        )
                    }
                    HorizontalDivider(
                        thickness = 0.5.dp,
                        color = colors.separator,
                        modifier = Modifier.padding(start = 20.dp),
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
    colors: ThemeColors,
) {
    Surface(
        onClick = onSelect,
        color = Color.Transparent,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.End,
            ) {
                Text(
                    text = entry.expression,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Normal,
                    color = colors.textSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = androidx.compose.ui.text.style.TextAlign.End,
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "= ${entry.result}",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = colors.textPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = androidx.compose.ui.text.style.TextAlign.End,
                )
            }
            Spacer(Modifier.width(12.dp))
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = colors.textTertiary,
            )
        }
    }
}

@Composable
private fun SectionLabel(
    modifier: Modifier = Modifier,
    text: String,
    color: Color,
) {
    Text(
        text = text,
        fontSize = 12.sp,
        fontWeight = FontWeight.SemiBold,
        color = color,
        letterSpacing = 0.5.sp,
        modifier = modifier,
    )
}
