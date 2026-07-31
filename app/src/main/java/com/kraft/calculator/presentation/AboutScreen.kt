package com.kraft.calculator.presentation

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kraft.calculator.ui.theme.KraftThemeColors
import com.kraft.calculator.ui.theme.ThemeColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen(
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    colors: ThemeColors = if (isSystemInDarkTheme()) KraftThemeColors.dark else KraftThemeColors.light,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("About") },
                navigationIcon = {
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = colors.background,
                ),
            )
        },
        modifier = modifier,
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                text = "Kalc",
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                color = colors.textPrimary,
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = "Version 1.0.0",
                fontSize = 13.sp,
                color = colors.textSecondary,
            )
            Spacer(Modifier.height(12.dp))
            Text(
                text = "A Kraft calculator app",
                fontSize = 12.sp,
                color = colors.textTertiary,
            )
        }
    }
}
