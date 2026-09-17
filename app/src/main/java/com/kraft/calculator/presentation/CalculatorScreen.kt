package com.kraft.calculator.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.SwapHoriz
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kraft.calculator.domain.CalculatorMode
import com.kraft.calculator.ui.theme.KraftRadius
import com.kraft.calculator.ui.theme.KraftThemeColors
import com.kraft.calculator.ui.theme.ThemeColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalculatorScreen(
    viewModel: CalculatorViewModel,
    modifier: Modifier = Modifier,
    colors: ThemeColors? = null,
) {
    val state by viewModel.state.collectAsState()
    val settings by viewModel.settings.collectAsState(initial = com.kraft.calculator.data.AppSettings())
    // Dark theme only — no theme picker
    val colors = KraftThemeColors.dark
    var showSettings by remember { mutableStateOf(false) }
    var showConverter by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showHistory by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }

    if (showSettings) {
        SettingsScreen(
            viewModel = viewModel,
            onBack = { showSettings = false },
            colors = colors,
        )
        return
    }

    if (showConverter) {
        ConverterScreen(
            onBack = { showConverter = false },
            colors = colors,
        )
        return
    }

    if (showHistory) {
        ModalBottomSheet(
            onDismissRequest = { showHistory = false },
            sheetState = sheetState,
            containerColor = colors.background,
            shape = RoundedCornerShape(topStart = KraftRadius.large, topEnd = KraftRadius.large),
            dragHandle = {
                Box(
                    modifier = Modifier
                        .padding(top = 8.dp, bottom = 4.dp)
                        .width(36.dp)
                        .height(6.dp)
                        .background(
                            color = colors.textPrimary.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(3.dp),
                        ),
                )
            },
        ) {
            HistorySheet(
                history = state.history,
                onClearAll = { viewModel.clearHistory() },
                onDeleteEntry = { viewModel.deleteHistoryEntry(it) },
                onSelectEntry = { entry ->
                    viewModel.loadExpression(entry.expression)
                    showHistory = false
                },
                colors = colors,
            )
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center,
                    ) {
                        ModePill(
                            currentMode = state.mode,
                            onModeChange = { viewModel.onButtonPressed("MODE") },
                            colors = colors,
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { showConverter = true }) {
                        Icon(
                            imageVector = Icons.Outlined.SwapHoriz,
                            contentDescription = "Unit converter",
                            tint = colors.accentBlue,
                        )
                    }
                    IconButton(onClick = { showSettings = true }) {
                        Icon(
                            imageVector = Icons.Outlined.Settings,
                            contentDescription = "Settings",
                            tint = colors.accentBlue,
                        )
                    }
                    IconButton(onClick = { showHistory = true }) {
                        Icon(
                            imageVector = Icons.Outlined.History,
                            contentDescription = "History",
                            tint = colors.accentBlue,
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = colors.background.copy(alpha = 0.85f),
                ),
            )
        },
        containerColor = colors.background,
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        modifier = modifier,
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
        ) {
            // Display area (fills remaining space)
            CalculatorDisplay(
                expression = state.expression,
                result = state.result,
                error = state.error,
                mode = state.mode,
                history = state.history,
                lastResult = state.lastResult,
                isSecondMode = state.isSecondMode,
                isAlphaMode = state.isAlphaMode,
                isHypMode = state.isHypMode,
                isEngMode = state.isEngMode,
                isSDMode = state.isSDMode,
                isDCMode = state.isDCMode,
                memory = state.memory,
                angleMode = state.angleMode,
                modifier = Modifier.weight(1f),
                colors = colors,
                snackbarHostState = snackbarHostState,
            )

            // Keypad (haptics provided via CompositionLocal)
            androidx.compose.runtime.CompositionLocalProvider(
                LocalHapticsEnabled provides settings.vibrationEnabled
            ) {
                when (state.mode) {
                    CalculatorMode.BASIC -> BasicKeypad(
                        onButtonPressed = viewModel::onButtonPressed,
                        colors = colors,
                    )
                    CalculatorMode.SCIENTIFIC -> ScientificKeypad(
                        onButtonPressed = viewModel::onButtonPressed,
                        angleMode = state.angleMode,
                        isSecondMode = state.isSecondMode,
                        isAlphaMode = state.isAlphaMode,
                        isHypMode = state.isHypMode,
                        isEngMode = state.isEngMode,
                        isSDMode = state.isSDMode,
                        colors = colors,
                    )
                }
            }
        }
    }
}

/// Apple-style capsule toggle for Basic / Sci mode in the nav bar center.
@Composable
private fun ModePill(
    currentMode: CalculatorMode,
    onModeChange: () -> Unit,
    colors: ThemeColors,
) {
    Row(
        modifier = Modifier
            .background(
                color = colors.surfaceTertiary,
                shape = RoundedCornerShape(KraftRadius.standard),
            ),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        for (mode in CalculatorMode.entries) {
            val isSelected = currentMode == mode
            val label = if (mode == CalculatorMode.BASIC) "Basic" else "Sci"
            val description = if (mode == CalculatorMode.BASIC) {
                if (isSelected) "Basic mode, selected" else "Switch to Basic mode"
            } else {
                if (isSelected) "Scientific mode, selected" else "Switch to Scientific mode"
            }

            Box(
                modifier = Modifier
                    .defaultMinSize(minHeight = 44.dp)
                    .clip(RoundedCornerShape(KraftRadius.standard - 2.dp))
                    .selectable(
                        selected = isSelected,
                        onClick = { onModeChange() },
                        role = Role.Tab,
                    )
                    .semantics { contentDescription = description }
                    .background(
                        color = if (isSelected) colors.accentBlue else Color.Transparent,
                        shape = RoundedCornerShape(KraftRadius.standard - 2.dp),
                    )
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = label,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = if (isSelected) Color.White
                    else colors.textTertiary,
                )
            }
        }
    }
}
