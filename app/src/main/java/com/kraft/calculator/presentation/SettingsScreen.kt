package com.kraft.calculator.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kraft.calculator.data.AppSettings
import com.kraft.calculator.data.AppTheme
import com.kraft.calculator.ui.theme.ThemeColors
import kotlinx.coroutines.launch
import com.kraft.ui.tokens.KraftSpacing
import com.kraft.ui.tokens.KraftRadius

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: CalculatorViewModel,
    onBack: () -> Unit,
    colors: ThemeColors,
    modifier: Modifier = Modifier,
) {
    val settings by viewModel.settings.collectAsState(
        initial = AppSettings()
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                        )
                    }
                },
            )
        },
        containerColor = colors.background,
        modifier = modifier,
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(
                    horizontal = KraftSpacing.Spacing16,
                    vertical = KraftSpacing.Spacing8,
                ),
            verticalArrangement = Arrangement.spacedBy(KraftSpacing.Spacing12),
        ) {
            // Theme
            SettingsSectionHeader(title = "Appearance", colors = colors)
            SettingsGroup(colors = colors) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            horizontal = KraftSpacing.Spacing16,
                            vertical = KraftSpacing.Spacing12,
                        ),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                ) {
                    val themeOptions = listOf(
                        "System" to 0,
                        "Dark" to 1,
                        "Light" to 2,
                        "Amoled" to 3,
                    )
                    themeOptions.forEach { (label, index) ->
                        val isSelected = settings.theme.ordinal == index
                        FilterChip(
                            selected = isSelected,
                            onClick = { viewModel.setTheme(AppTheme.entries[index]) },
                            label = { Text(label) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = colors.accentBlue,
                                selectedLabelColor = androidx.compose.ui.graphics.Color.White,
                            ),
                        )
                    }
                }
            }

            // Vibration
            SettingsSectionHeader(title = "Feedback", colors = colors)
            SettingsGroup(colors = colors) {
                SettingsSwitch(
                    title = "Vibration",
                    subtitle = "Haptic feedback on button press",
                    checked = settings.vibrationEnabled,
                    onCheckedChange = { viewModel.setVibration(it) },
                    colors = colors,
                )
            }

            // Precision
            SettingsSectionHeader(title = "Calculation", colors = colors)
            SettingsGroup(colors = colors) {
                Text(
                    text = "Decimal precision: ${settings.decimalPrecision}",
                    style = MaterialTheme.typography.titleSmall,
                    color = colors.textPrimary,
                    modifier = Modifier.padding(
                        horizontal = KraftSpacing.Spacing16,
                        vertical = KraftSpacing.Spacing8,
                    ),
                )
                Slider(
                    value = settings.decimalPrecision.toFloat(),
                    onValueChange = { viewModel.setPrecision(it.toInt()) },
                    valueRange = 2f..15f,
                    steps = 12,
                    colors = SliderDefaults.colors(
                        thumbColor = colors.accentBlue,
                        activeTrackColor = colors.accentBlue,
                    ),
                    modifier = Modifier.padding(horizontal = KraftSpacing.Spacing16),
                )
                SettingsInsetDivider(colors = colors)
                Text(
                    text = "History size: ${if (settings.historySize == 0) "Disabled" else settings.historySize.toString()}",
                    style = MaterialTheme.typography.titleSmall,
                    color = colors.textPrimary,
                    modifier = Modifier.padding(
                        horizontal = KraftSpacing.Spacing16,
                        vertical = KraftSpacing.Spacing8,
                    ),
                )
                Slider(
                    value = settings.historySize.toFloat(),
                    onValueChange = { viewModel.setHistorySize(it.toInt()) },
                    valueRange = 0f..200f,
                    steps = 19,
                    colors = SliderDefaults.colors(
                        thumbColor = colors.accentBlue,
                        activeTrackColor = colors.accentBlue,
                    ),
                    modifier = Modifier.padding(horizontal = KraftSpacing.Spacing16),
                )
                Spacer(Modifier.height(KraftSpacing.Spacing8))
            }

            // About
            SettingsSectionHeader(title = "About", colors = colors)
            SettingsGroup(colors = colors) {
                SettingsInfoRow(title = "Kalc", subtitle = "Private calculator — no ads, no trackers", colors = colors)
                SettingsInsetDivider(colors = colors)
                SettingsInfoRow(
                    title = "Version",
                    subtitle = "${com.kraft.calculator.BuildConfig.VERSION_NAME} (${com.kraft.calculator.BuildConfig.VERSION_CODE})",
                    colors = colors,
                )
                SettingsInsetDivider(colors = colors)
                SettingsInfoRow(title = "Developer", subtitle = "Kedhar Sairam", colors = colors)
                SettingsInsetDivider(colors = colors)
                SettingsInfoRow(title = "License", subtitle = "MIT — open source", colors = colors)
                SettingsInsetDivider(colors = colors)
                SettingsInfoRow(title = "Source code", subtitle = "github.com/kedharsairam/kalc", colors = colors)
            }
            Spacer(Modifier.height(KraftSpacing.Spacing8))
        }
    }
}

/** Muted small-caps header above each grouped card. */
@Composable
private fun SettingsSectionHeader(title: String, colors: ThemeColors) {
    Text(
        text = title.uppercase(),
        style = MaterialTheme.typography.labelMedium.copy(letterSpacing = 0.8.sp), // @kraft-lint-ignore type.no-raw-sp — uppercase header tracking, twice the shared 0.4
        color = colors.textTertiary,
        modifier = Modifier.padding(
            start = KraftSpacing.Spacing4,
            end = KraftSpacing.Spacing4,
            top = KraftSpacing.Spacing8,
            bottom = KraftSpacing.Spacing4,
        ),
    )
}

/** Grouped card: surface fill, 16dp radius, hairline separator outline. */
@Composable
private fun SettingsGroup(colors: ThemeColors, content: @Composable () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(KraftRadius.Medium))
            .background(colors.surface)
            .border(
                width = KraftSpacing.BorderWidth,
                color = colors.separator.copy(alpha = 0.55f),
                shape = RoundedCornerShape(KraftRadius.Medium),
            ),
    ) {
        content()
    }
}

/** Inset divider between grouped rows — never full-bleed. */
@Composable
private fun SettingsInsetDivider(colors: ThemeColors) {
    HorizontalDivider(
        color = colors.separator.copy(alpha = 0.55f),
        modifier = Modifier.padding(start = KraftSpacing.Spacing16),
    )
}

@Composable
private fun SettingsInfoRow(title: String, subtitle: String, colors: ThemeColors) {
    ListItem(
        headlineContent = { Text(title, color = colors.textPrimary) },
        supportingContent = { Text(subtitle, color = colors.textSecondary) },
        colors = ListItemDefaults.colors(containerColor = colors.surface),
        modifier = Modifier.heightIn(min = KraftSpacing.Spacing56),
    )
}

@Composable
private fun SettingsSwitch(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    colors: ThemeColors,
) {
    val haptics = LocalHapticFeedback.current
    ListItem(
        headlineContent = { Text(title, color = colors.textPrimary) },
        supportingContent = { Text(subtitle, color = colors.textSecondary) },
        trailingContent = {
            Switch(
                checked = checked,
                onCheckedChange = {
                    haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    onCheckedChange(it)
                },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = androidx.compose.ui.graphics.Color.White,
                    checkedTrackColor = colors.accentBlue,
                ),
            )
        },
        colors = ListItemDefaults.colors(containerColor = colors.surface),
        modifier = Modifier
            .heightIn(min = KraftSpacing.Spacing56)
            .clickable(
                role = Role.Switch,
                onClickLabel = "Toggle $title",
                onClick = {
                    haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    onCheckedChange(!checked)
                },
            ),
    )
}
