package com.kraft.calculator.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.kraft.calculator.data.AppSettings
import com.kraft.calculator.data.AppTheme
import com.kraft.calculator.ui.theme.ThemeColors

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
        modifier = modifier,
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            // Vibration
            SettingsSection(title = "Feedback") {
                SettingsSwitch(
                    title = "Vibration",
                    subtitle = "Haptic feedback on button press",
                    checked = settings.vibrationEnabled,
                    onCheckedChange = { viewModel.setVibration(it) },
                )
            }

            // Precision
            SettingsSection(title = "Calculation") {
                Text(
                    text = "Decimal precision: ${settings.decimalPrecision}",
                    style = MaterialTheme.typography.titleSmall,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                )
                Slider(
                    value = settings.decimalPrecision.toFloat(),
                    onValueChange = { viewModel.setPrecision(it.toInt()) },
                    valueRange = 2f..15f,
                    steps = 12,
                    modifier = Modifier.padding(horizontal = 16.dp),
                )
                Text(
                    text = "History size: ${if (settings.historySize == 0) "Disabled" else settings.historySize.toString()}",
                    style = MaterialTheme.typography.titleSmall,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                )
                Slider(
                    value = settings.historySize.toFloat(),
                    onValueChange = { viewModel.setHistorySize(it.toInt()) },
                    valueRange = 0f..200f,
                    steps = 19,
                    modifier = Modifier.padding(horizontal = 16.dp),
                )
            }

            // About
            SettingsSection(title = "About") {
                ListItem(
                    headlineContent = { Text("Kalc") },
                    supportingContent = { Text("Private calculator — no ads, no trackers") },
                )
                ListItem(
                    headlineContent = { Text("Version") },
                    supportingContent = { Text("${com.kraft.calculator.BuildConfig.VERSION_NAME} (${com.kraft.calculator.BuildConfig.VERSION_CODE})") },
                )
                ListItem(
                    headlineContent = { Text("Developer") },
                    supportingContent = { Text("Kedhar Sairam") },
                )
                ListItem(
                    headlineContent = { Text("License") },
                    supportingContent = { Text("MIT — open source") },
                )
                ListItem(
                    headlineContent = { Text("Source code") },
                    supportingContent = { Text("github.com/kedharsairam/kalc") },
                )
            }
        }
    }
}

@Composable
private fun SettingsSection(
    title: String,
    content: @Composable () -> Unit,
) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
    )
    content()
}

@Composable
private fun SettingsSwitch(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    ListItem(
        headlineContent = { Text(title) },
        supportingContent = { Text(subtitle) },
        trailingContent = {
            Switch(checked = checked, onCheckedChange = onCheckedChange)
        },
    )
}
