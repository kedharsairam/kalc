package com.kraft.calculator.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.kraft.calculator.domain.ConverterCategory
import com.kraft.calculator.domain.UnitConverter
import com.kraft.calculator.ui.theme.ThemeColors

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ConverterScreen(
    onBack: () -> Unit,
    colors: ThemeColors,
    modifier: Modifier = Modifier,
) {
    var tool by remember { mutableStateOf("Units") }
    var category by remember { mutableStateOf(ConverterCategory.LENGTH) }
    var input by remember { mutableStateOf("1") }
    var fromIdx by remember { mutableStateOf(0) }
    var toIdx by remember { mutableStateOf(1) }

    // Reset indices when category changes
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

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Unit Converter") },
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
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            // Category selector
            Text("Category", style = MaterialTheme.typography.labelLarge)
            @OptIn(ExperimentalMaterial3Api::class)
            ExposedDropdownMenuBox(
                expanded = false,
                onExpandedChange = {},
            ) {
                // Simple row of category chips instead
            }
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                ConverterCategory.entries.forEach { cat ->
                    FilterChip(
                        selected = category == cat,
                        onClick = { category = cat },
                        label = { Text(cat.title) },
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

            // From / To selectors
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

            // Result
            Card(
                modifier = Modifier.fillMaxWidth(),
            ) {
                Column(Modifier.padding(20.dp)) {
                    Text(
                        text = "Result",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = if (result == null) "—"
                        else {
                            val r = result!!
                            if (r == r.toLong().toDouble()) r.toLong().toString()
                            else "%.6g".format(r).trimEnd('0').trimEnd('.')
                        } + " ${to.symbol}",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }

            // ── EMI Calculator ──
            var loanAmt by remember { mutableStateOf("100000") }
            var loanRate by remember { mutableStateOf("9") }
            var loanMonths by remember { mutableStateOf("12") }
            val emiResult = remember(loanAmt, loanRate, loanMonths) {
                try {
                    val p = loanAmt.toDoubleOrNull() ?: return@remember null
                    val r = loanRate.toDoubleOrNull() ?: return@remember null
                    val n = loanMonths.toIntOrNull() ?: return@remember null
                    if (n <= 0) return@remember null
                    com.kraft.calculator.domain.FinanceCalculators.emi(p, r, n)
                } catch (_: Exception) { null }
            }
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Loan EMI", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    OutlinedTextField(value = loanAmt, onValueChange = { loanAmt = it.filter { c -> c.isDigit() || c == '.' } }, label = { Text("Principal") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal), singleLine = true, modifier = Modifier.fillMaxWidth())
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(value = loanRate, onValueChange = { loanRate = it.filter { c -> c.isDigit() || c == '.' } }, label = { Text("Rate %") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal), singleLine = true, modifier = Modifier.weight(1f))
                        OutlinedTextField(value = loanMonths, onValueChange = { loanMonths = it.filter { c -> c.isDigit() } }, label = { Text("Months") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), singleLine = true, modifier = Modifier.weight(1f))
                    }
                    if (emiResult != null) {
                        Text("EMI: ₹${"%.2f".format(emiResult.emi)}", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                        Text("Interest: ₹${"%.2f".format(emiResult.totalInterest)}  •  Total: ₹${"%.2f".format(emiResult.totalPayment)}", style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }

            // ── GST Calculator ──
            var gstAmt by remember { mutableStateOf("1000") }
            var gstRate by remember { mutableStateOf("18") }
            var gstForward by remember { mutableStateOf(true) }
            val gstResult = remember(gstAmt, gstRate, gstForward) {
                try {
                    val a = gstAmt.toDoubleOrNull() ?: return@remember null
                    val r = gstRate.toDoubleOrNull() ?: return@remember null
                    com.kraft.calculator.domain.FinanceCalculators.gst(a, r, gstForward, true)
                } catch (_: Exception) { null }
            }
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("GST (India)", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        FilterChip(selected = gstForward, onClick = { gstForward = true }, label = { Text("Add GST") })
                        Spacer(Modifier.width(8.dp))
                        FilterChip(selected = !gstForward, onClick = { gstForward = false }, label = { Text("Remove GST") })
                    }
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(value = gstAmt, onValueChange = { gstAmt = it.filter { c -> c.isDigit() || c == '.' } }, label = { Text("Amount") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal), singleLine = true, modifier = Modifier.weight(1f))
                        OutlinedTextField(value = gstRate, onValueChange = { gstRate = it.filter { c -> c.isDigit() || c == '.' } }, label = { Text("Rate %") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal), singleLine = true, modifier = Modifier.weight(1f))
                    }
                    if (gstResult != null) {
                        Text("Tax: ₹${"%.2f".format(gstResult.tax)}  •  Total: ₹${"%.2f".format(gstResult.gross)}", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                        Text("CGST: ₹${"%.2f".format(gstResult.cgst)}  •  SGST: ₹${"%.2f".format(gstResult.sgst)}", style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }
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

// Simple FlowRow replacement (no extra dep)
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun FlowRow(
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Start,
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    content: @Composable () -> Unit,
) {
    // Fallback to Column of Rows via BoxWithConstraints would be ideal,
    // but simplest reliable: use built-in FlowRow from foundation (Compose 1.6+)
    androidx.compose.foundation.layout.FlowRow(
        horizontalArrangement = horizontalArrangement,
        verticalArrangement = verticalArrangement,
        content = { content() },
    )
}
