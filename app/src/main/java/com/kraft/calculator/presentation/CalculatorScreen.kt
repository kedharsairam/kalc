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
import com.kraft.calculator.ui.theme.KraftThemeColors
import com.kraft.calculator.ui.theme.ThemeColors
import com.kraft.calculator.data.AppTheme
import com.kraft.ui.tokens.KraftSpacing
import com.kraft.ui.tokens.KraftRadius

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalculatorScreen(
    viewModel: CalculatorViewModel,
    modifier: Modifier = Modifier,
    colors: ThemeColors? = null,
) {
    val state by viewModel.state.collectAsState()
    val settings by viewModel.settings.collectAsState(initial = com.kraft.calculator.data.AppSettings())
    // Theme resolved from settings (SYSTEM → follows system dark mode)
    val activeColors: ThemeColors = when (settings.theme) {
        AppTheme.SYSTEM -> if (isSystemInDarkTheme()) KraftThemeColors.dark else KraftThemeColors.light
        AppTheme.LIGHT -> KraftThemeColors.light
        AppTheme.DARK -> KraftThemeColors.dark
        AppTheme.AMOLED -> KraftThemeColors.amoledGrey
    }
    var showSettings by remember { mutableStateOf(false) }
    var showConverter by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showHistory by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }

    if (showSettings) {
        SettingsScreen(
            viewModel = viewModel,
            onBack = { showSettings = false },
            colors = activeColors,
        )
        return
    }

    if (showConverter) {
        ConverterScreen(
            onBack = { showConverter = false },
            colors = activeColors,
        )
        return
    }

    if (showHistory) {
        ModalBottomSheet(
            onDismissRequest = { showHistory = false },
            sheetState = sheetState,
            containerColor = activeColors.background,
            shape = RoundedCornerShape(topStart = KraftRadius.Medium, topEnd = KraftRadius.Medium),
            dragHandle = {
                Box(
                    modifier = Modifier
                        .padding(top = KraftSpacing.Spacing8, bottom = KraftSpacing.Spacing4)
                        .width(KraftSpacing.DragHandleWidth)
                        .height(KraftSpacing.Spacing6)
                        .background(
                            color = activeColors.textPrimary.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(KraftRadius.Pill),
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
                colors = activeColors,
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
                            colors = activeColors,
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { showConverter = true }) {
                        Icon(
                            imageVector = Icons.Outlined.SwapHoriz,
                            contentDescription = "Unit converter",
                            tint = activeColors.accentBlue,
                        )
                    }
                    IconButton(onClick = { showSettings = true }) {
                        Icon(
                            imageVector = Icons.Outlined.Settings,
                            contentDescription = "Settings",
                            tint = activeColors.accentBlue,
                        )
                    }
                    IconButton(onClick = { showHistory = true }) {
                        Icon(
                            imageVector = Icons.Outlined.History,
                            contentDescription = "History",
                            tint = activeColors.accentBlue,
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = activeColors.background.copy(alpha = 0.85f),
                ),
            )
        },
        containerColor = activeColors.background,
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        modifier = modifier,
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
        ) {
            CalculatorDisplay(
                expression = state.expression,
                result = state.result,
                error = state.error,
                mode = state.mode,
                history = state.history,
                lastResult = state.lastResult,
                variables = state.variables,
                isSecondMode = state.isSecondMode,
                isAlphaMode = state.isAlphaMode,
                isHypMode = state.isHypMode,
                isEngMode = state.isEngMode,
                isSDMode = state.isSDMode,
                isDCMode = state.isDCMode,
                memory = state.memory,
                angleMode = state.angleMode,
                modifier = Modifier.weight(1f),
                colors = activeColors,
                snackbarHostState = snackbarHostState,
            )

            androidx.compose.runtime.CompositionLocalProvider(
                LocalHapticsEnabled provides settings.vibrationEnabled
            ) {
                when (state.mode) {
                    CalculatorMode.BASIC -> BasicKeypad(
                        onButtonPressed = viewModel::onButtonPressed,
                        colors = activeColors,
                    )
                    CalculatorMode.SCIENTIFIC -> ScientificKeypad(
                        onButtonPressed = viewModel::onButtonPressed,
                        angleMode = state.angleMode,
                        isSecondMode = state.isSecondMode,
                        isAlphaMode = state.isAlphaMode,
                        isHypMode = state.isHypMode,
                        isEngMode = state.isEngMode,
                        isSDMode = state.isSDMode,
                        colors = activeColors,
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
                shape = RoundedCornerShape(KraftRadius.Standard),
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
                    .defaultMinSize(minHeight = KraftSpacing.TouchTarget)
                    .clip(RoundedCornerShape(KraftRadius.Small))
                    .selectable(
                        selected = isSelected,
                        onClick = { onModeChange() },
                        role = Role.Tab,
                    )
                    .semantics { contentDescription = description }
                    .background(
                        color = if (isSelected) colors.accentBlue else Color.Transparent,
                        shape = RoundedCornerShape(KraftRadius.Small),
                    )
                    .padding(horizontal = KraftSpacing.Spacing16, vertical = KraftSpacing.Spacing8),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelLarge,
                    color = if (isSelected) Color.White
                    else colors.textTertiary,
                )
            }
        }
    }
}
