package com.kraft.calculator.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kraft.calculator.domain.ConverterCategory
import com.kraft.calculator.domain.FinanceCalculators
import com.kraft.calculator.domain.UnitConverter
import com.kraft.calculator.ui.theme.KraftRadius
import com.kraft.calculator.ui.theme.ThemeColors

private enum class ConverterTool { UNITS, EMI, GST }

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ConverterScreen(
    onBack: () -> Unit,
    colors: ThemeColors,
    modifier: Modifier = Modifier,
) {
    var tool by remember { mutableStateOf(ConverterTool.UNITS) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Tools", fontWeight = FontWeight.SemiBold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = colors.accentBlue,
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = colors.background,
                ),
            )
        },
        containerColor = colors.background,
        modifier = modifier,
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
        ) {
            // Tool tabs
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(KraftRadius.standard))
                    .background(colors.surfaceSecondary)
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                ConverterTool.entries.forEach { t ->
                    val selected = tool == t
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(KraftRadius.small))
                            .background(if (selected) colors.accentBlue else androidx.compose.ui.graphics.Color.Transparent)
                            .padding(vertical = 10.dp)
                            .clickable { tool = t },
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = when (t) {
                                ConverterTool.UNITS -> "Units"
                                ConverterTool.EMI -> "EMI"
                                ConverterTool.GST -> "GST"
                            },
                            fontSize = 15.sp,
                            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
                            color = if (selected) androidx.compose.ui.graphics.Color.White else colors.textSecondary,
                        )
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            when (tool) {
                ConverterTool.UNITS -> UnitsTab(colors)
                ConverterTool.EMI -> EmiTab(colors)
                ConverterTool.GST -> GstTab(colors)
            }
        }
    }
}

@Composable
private fun UnitsTab(colors: ThemeColors) {
    var category by remember { mutableStateOf(ConverterCategory.LENGTH) }
    var input by remember { mutableStateOf("1") }
    var fromIdx by remember { mutableStateOf(0) }
    var toIdx by remember { mutableStateOf(1) }

    LaunchedEffect(category) {
        fromIdx = 0
        toIdx = 1
    }

    val units = remember(category) { UnitConverter.unitsFor(category) }
    val from = units.getOrElse(fromIdx) { units.first() }
    val to = units.getOrElse(toIdx) { units.getOrElse(1) { units.first() } }

    val result = remember(input, from, to) {
        val v = input.toDoubleOrNull()
        if (v == null) null
        else try {
            UnitConverter.convert(v, from, to)
        } catch (_: Exception) {
            null
        }
    }

    Column(
        modifier = Modifier.verticalScroll(rememberScrollState()).imePadding(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        // Category chips
        androidx.compose.foundation.layout.FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            ConverterCategory.entries.forEach { cat ->
                val selected = category == cat
                FilterChip(
                    selected = selected,
                    onClick = { category = cat },
                    label = { Text(cat.title) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = colors.accentBlue,
                        selectedLabelColor = androidx.compose.ui.graphics.Color.White,
                    ),
                )
            }
        }

        // Result hero
        Card(
            colors = CardDefaults.cardColors(containerColor = colors.surface),
            shape = RoundedCornerShape(KraftRadius.large),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Column(Modifier.padding(24.dp), horizontalAlignment = Alignment.End) {
                Text(
                    text = "$input ${from.symbol}",
                    fontSize = 16.sp,
                    color = colors.textSecondary,
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = if (result == null) "—"
                    else {
                        val r = result!!
                        (if (r == r.toLong().toDouble()) r.toLong().toString()
                        else "%.6g".format(r).trimEnd('0').trimEnd('.')) + " ${to.symbol}"
                    },
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                    color = colors.textPrimary,
                )
            }
        }

        // Input
        OutlinedTextField(
            value = input,
            onValueChange = { input = it.filter { c -> c.isDigit() || c == '.' || c == '-' } },
            label = { Text("Value") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
        )

        // From/To
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            UnitDropdown(
                label = "From",
                units = units.map { "${it.name} (${it.symbol})" },
                selected = fromIdx,
                onSelect = { fromIdx = it },
                modifier = Modifier.weight(1f),
            )
            IconButton(onClick = {
                val tmp = fromIdx
                fromIdx = toIdx
                toIdx = tmp
            }) {
                Icon(
                    imageVector = Icons.Filled.SwapVert,
                    contentDescription = "Swap units",
                    tint = colors.accentBlue,
                )
            }
            UnitDropdown(
                label = "To",
                units = units.map { "${it.name} (${it.symbol})" },
                selected = toIdx,
                onSelect = { toIdx = it },
                modifier = Modifier.weight(1f),
            )
        }
        Spacer(Modifier.height(16.dp))
    }
}

@Composable
private fun EmiTab(colors: ThemeColors) {
    var loanAmt by remember { mutableStateOf("100000") }
    var loanRate by remember { mutableStateOf("9") }
    var loanMonths by remember { mutableStateOf("12") }
    val emiResult = remember(loanAmt, loanRate, loanMonths) {
        try {
            val p = loanAmt.toDoubleOrNull() ?: return@remember null
            val r = loanRate.toDoubleOrNull() ?: return@remember null
            val n = loanMonths.toIntOrNull() ?: return@remember null
            if (n <= 0) return@remember null
            FinanceCalculators.emi(p, r, n)
        } catch (_: Exception) { null }
    }

    Column(
        modifier = Modifier.verticalScroll(rememberScrollState()).imePadding(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Card(
            colors = CardDefaults.cardColors(containerColor = colors.surface),
            shape = RoundedCornerShape(KraftRadius.large),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Column(Modifier.padding(24.dp), horizontalAlignment = Alignment.End) {
                Text("Monthly EMI", fontSize = 14.sp, color = colors.textSecondary)
                Text(
                    text = if (emiResult == null) "—" else "₹${"%,.2f".format(emiResult.emi)}",
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                    color = colors.textPrimary,
                )
                if (emiResult != null) {
                    Spacer(Modifier.height(8.dp))
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Column(horizontalAlignment = Alignment.Start) {
                            Text("Interest", fontSize = 12.sp, color = colors.textTertiary)
                            Text("₹${"%,.0f".format(emiResult.totalInterest)}", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = colors.textPrimary)
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text("Total", fontSize = 12.sp, color = colors.textTertiary)
                            Text("₹${"%,.0f".format(emiResult.totalPayment)}", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = colors.textPrimary)
                        }
                    }
                }
            }
        }
        OutlinedTextField(value = loanAmt, onValueChange = { loanAmt = it.filter { c -> c.isDigit() || c == '.' } }, label = { Text("Loan amount (₹)") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal), singleLine = true, modifier = Modifier.fillMaxWidth())
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedTextField(value = loanRate, onValueChange = { loanRate = it.filter { c -> c.isDigit() || c == '.' } }, label = { Text("Rate %") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal), singleLine = true, modifier = Modifier.weight(1f))
            OutlinedTextField(value = loanMonths, onValueChange = { loanMonths = it.filter { c -> c.isDigit() } }, label = { Text("Months") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), singleLine = true, modifier = Modifier.weight(1f))
        }
        Spacer(Modifier.height(16.dp))
    }
}

@Composable
private fun GstTab(colors: ThemeColors) {
    var gstAmt by remember { mutableStateOf("1000") }
    var gstRate by remember { mutableStateOf("18") }
    var gstForward by remember { mutableStateOf(true) }
    val gstResult = remember(gstAmt, gstRate, gstForward) {
        try {
            val a = gstAmt.toDoubleOrNull() ?: return@remember null
            val r = gstRate.toDoubleOrNull() ?: return@remember null
            FinanceCalculators.gst(a, r, gstForward, true)
        } catch (_: Exception) { null }
    }

    Column(
        modifier = Modifier.verticalScroll(rememberScrollState()).imePadding(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterChip(selected = gstForward, onClick = { gstForward = true }, label = { Text("Add GST") })
            FilterChip(selected = !gstForward, onClick = { gstForward = false }, label = { Text("Remove GST") })
        }
        Card(
            colors = CardDefaults.cardColors(containerColor = colors.surface),
            shape = RoundedCornerShape(KraftRadius.large),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Column(Modifier.padding(24.dp), horizontalAlignment = Alignment.End) {
                Text(if (gstForward) "Total incl. tax" else "Net amount", fontSize = 14.sp, color = colors.textSecondary)
                Text(
                    text = if (gstResult == null) "—"
                    else "₹${"%,.2f".format(if (gstForward) gstResult.gross else gstResult.net)}",
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                    color = colors.textPrimary,
                )
                if (gstResult != null) {
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "Tax ₹${"%.2f".format(gstResult.tax)} (CGST ₹${"%.2f".format(gstResult.cgst)} + SGST ₹${"%.2f".format(gstResult.sgst)})",
                        fontSize = 14.sp,
                        color = colors.textSecondary,
                    )
                }
            }
        }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedTextField(value = gstAmt, onValueChange = { gstAmt = it.filter { c -> c.isDigit() || c == '.' } }, label = { Text("Amount (₹)") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal), singleLine = true, modifier = Modifier.weight(1f))
            OutlinedTextField(value = gstRate, onValueChange = { gstRate = it.filter { c -> c.isDigit() || c == '.' } }, label = { Text("Rate %") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal), singleLine = true, modifier = Modifier.weight(1f))
        }
        Spacer(Modifier.height(16.dp))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun UnitDropdown(
    label: String,
    units: List<String>,
    selected: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = modifier,
    ) {
        OutlinedTextField(
            value = units.getOrElse(selected) { "" },
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.menuAnchor().fillMaxWidth(),
            singleLine = true,
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            units.forEachIndexed { i, name ->
                DropdownMenuItem(
                    text = { Text(name, maxLines = 1) },
                    onClick = {
                        onSelect(i)
                        expanded = false
                    },
                )
            }
        }
    }
}

