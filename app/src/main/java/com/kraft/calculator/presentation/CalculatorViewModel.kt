package com.kraft.calculator.presentation

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlin.math.roundToInt
import com.kraft.calculator.data.HistoryRepository
import com.kraft.calculator.data.RoomHistoryRepository
import com.kraft.calculator.data.SettingsRepository
import com.kraft.calculator.data.AppTheme
import com.kraft.calculator.domain.CalculatorEngine
import com.kraft.calculator.domain.CalculatorMode
import com.kraft.calculator.domain.CalculatorState
import com.kraft.calculator.domain.AngleMode
import com.kraft.calculator.domain.CalculationEntry
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CalculatorViewModel(application: Application) : AndroidViewModel(application) {

    private val historyRepo = RoomHistoryRepository(application)
    private val settingsRepo = SettingsRepository(application)

    private val _state = MutableStateFlow(CalculatorState())
    val state: StateFlow<CalculatorState> = _state.asStateFlow()

    val settings = settingsRepo.settings

    init {
        viewModelScope.launch {
            val history = historyRepo.loadHistory(100)
            _state.update { it.copy(history = history) }
        }
        // Observe settings for history size changes
        viewModelScope.launch {
            settingsRepo.settings.collect { prefs ->
                // Trim history if size reduced
                if (prefs.historySize > 0) {
                    try {
                        val current = historyRepo.loadHistory(prefs.historySize + 10)
                        if (current.size > prefs.historySize) {
                            // Will trim on next save; force trim via reload
                            _state.update { it.copy(history = current.take(prefs.historySize)) }
                        }
                    } catch (_: Exception) { }
                }
            }
        }
    }

    fun setVibration(enabled: Boolean) {
        viewModelScope.launch { settingsRepo.setVibration(enabled) }
    }

    fun setTheme(theme: AppTheme) {
        viewModelScope.launch { settingsRepo.setTheme(theme) }
    }

    fun setPrecision(precision: Int) {
        viewModelScope.launch { settingsRepo.setPrecision(precision) }
    }

    fun setHistorySize(size: Int) {
        viewModelScope.launch { settingsRepo.setHistorySize(size) }
    }

    // ---------------------------------------------------------------------------
    // Button press dispatch
    // ---------------------------------------------------------------------------
    fun onButtonPressed(key: String) {
        val current = _state.value

        when (key) {
            "=" -> evaluateExpression(current)
            "AC" -> clearAll()
            "CE" -> clearEntry()
            "BS", "⌫", "DEL" -> backspace()
            "Ans" -> insertText("Ans")
            "÷", "×", "−", "+", "^" -> insertOperator(
                when (key) {
                    "÷" -> "÷"
                    "×" -> "×"
                    "−" -> "−"
                    "+" -> "+"
                    "^" -> "^"
                    else -> key
                }
            )
            "%" -> evaluateModulo(current)
            "²", "³", "⁻¹", "!" -> insertPostfix(key)
            "√" -> insertPrefix("√")
            "∛" -> insertPrefix("∛")
            "(" -> insertText("(")
            ")" -> insertText(")")
            "." -> insertText(".")
            "π", "τ", "e" -> insertConstant(key)
            "→DMS" -> convertToDms(current)
            "→Decimal" -> convertToDecimal(current)
            "→Frac" -> convertToFraction(current)
            "nCr", "nPr" -> insertBinaryIdent(key)
            "x" -> insertText("×")
            "Ran#" -> insertText("Ran#")

            // Scientific functions
            "sin", "cos", "tan", "ln", "log", "abs", "exp" -> insertPrefix(key)
            "asin", "acos", "atan" -> insertPrefix(
                when (key) {
                    "asin" -> "sin⁻¹"
                    "acos" -> "cos⁻¹"
                    "atan" -> "tan⁻¹"
                    else -> key
                }
            )
            "sinh", "cosh", "tanh" -> insertPrefix(key)
            "asinh", "acosh", "atanh" -> insertPrefix(
                when (key) {
                    "asinh" -> "sinh⁻¹"
                    "acosh" -> "cosh⁻¹"
                    "atanh" -> "tanh⁻¹"
                    else -> key
                }
            )
            "10^" -> insertPrefix("10^")
            "e^" -> insertPrefix("e^")

            // Mode toggles
            "MODE" -> toggleMode()
            "DEG" -> setAngleMode(AngleMode.DEGREE)
            "RAD" -> setAngleMode(AngleMode.RADIAN)
            "GRAD" -> setAngleMode(AngleMode.GRAD)
            "DC" -> toggleDcMode()
            "ENG" -> toggleEngMode()

            // Memory
            "MC" -> memoryClear()
            "MR" -> memoryRecall()
            "M+" -> memoryAdd()
            "M−" -> memorySubtract()

            // Toggles
            "ALPHA" -> toggleAlpha()

            // Number keys
            "0", "1", "2", "3", "4", "5", "6", "7", "8", "9" -> insertDigit(key)
            // Negative prefix
            "(−)", "±" -> insertPrefix("−")

            // Sci keypad aliases
            "SHIFT", "2nd" -> toggleSecond()
            "S⇔D", "SD" -> toggleSdMode()
            "hyp", "HYP" -> toggleHyp()
            "DRG▶" -> cycleAngleMode()

            // Postfix shorthands (x² means apply ² to current)
            "x²" -> insertPostfix("²")
            "x³" -> insertPostfix("³")
            "x⁻¹" -> insertPostfix("⁻¹")

            // Scientific notation entry
            "×10ˣ", "×10^" -> insertText("×10^(")

            // DMS entry
            "°′″", "°~" -> insertText("°")

            // Fraction shorthands (insert division template)
            "a b/c", "d/c" -> insertText("(")

            // Memory store/recall (map to M+ / MR for now)
            "STO" -> memoryAdd()
            "RCL" -> memoryRecall()

            // Inverse hyperbolic (from alpha layer if sent)
            "asinh" -> insertPrefix("sinh⁻¹")
            "acosh" -> insertPrefix("cosh⁻¹")
            "atanh" -> insertPrefix("tanh⁻¹")

            else -> { /* ignore */ }
        }
    }

    // ---------------------------------------------------------------------------
    // Actions
    // ---------------------------------------------------------------------------
    fun loadExpression(expression: String) {
        _state.update {
            it.copy(expression = expression, error = null, clearOnNextInput = false)
        }
    }

    private fun evaluateExpression(state: CalculatorState) {
        val normalized = CalculatorEngine.normalize(state.expression)
        if (normalized.isEmpty()) return

        val lastResult = state.lastResult
        try {
            val result = CalculatorEngine.evaluate(
                normalized, state.angleMode, lastResult, state.isEngMode
            )
            val entry = CalculationEntry(
                expression = normalized,
                result = result
            )
            val updatedHistory = listOf(entry) + state.history

            _state.update {
                it.copy(
                    expression = result,
                    result = result,
                    lastResult = result.toDoubleOrNull(),
                    lastOperand = null,
                    lastOperator = null,
                    history = updatedHistory,
                    error = null,
                    clearOnNextInput = true,
                )
            }
            saveHistory()
        } catch (e: Exception) {
            _state.update {
                it.copy(error = e.message, result = "Error")
            }
        }
    }

    private fun clearAll() {
        _state.update {
            it.copy(
                expression = "",
                result = "0",
                error = null,
                lastOperand = null,
                lastOperator = null,
                clearOnNextInput = false,
            )
        }
    }

    private fun clearEntry() {
        _state.update {
            if (it.error != null) {
                it.copy(error = null, result = if (it.expression.isNotEmpty()) it.expression else "0")
            } else {
                it.copy(expression = "", result = "0")
            }
        }
    }

    private fun backspace() {
        val current = _state.value
        if (current.clearOnNextInput) {
            _state.update { it.copy(expression = "", result = "0", clearOnNextInput = false) }
            return
        }
        if (current.expression.isEmpty()) return

        val expr = current.expression.trimEnd()
        val newExpr = if (expr.isNotEmpty()) expr.substring(0, expr.length - 1) else ""
        val newResult = if (newExpr.isEmpty()) "0" else tryEval(newExpr)
        _state.update { it.copy(expression = newExpr, result = newResult, error = null) }
    }

    private fun insertDigit(digit: String) {
        val current = _state.value
        val expr = if (current.clearOnNextInput) "" else current.expression
        val newExpr = expr + digit
        val newResult = tryEval(newExpr)
        _state.update { it.copy(expression = newExpr, result = newResult, clearOnNextInput = false) }
    }

    private fun insertOperator(op: String) {
        val current = _state.value
        val expr = if (current.clearOnNextInput) current.result else current.expression
        val newExpr = "$expr$op"
        _state.update { it.copy(expression = newExpr, clearOnNextInput = false) }
    }

    private fun insertPrefix(op: String) {
        val current = _state.value
        val expr = if (current.clearOnNextInput) "" else current.expression
        val newExpr = "$expr$op("
        _state.update { it.copy(expression = newExpr, clearOnNextInput = false) }
    }

    private fun insertPostfix(op: String) {
        val current = _state.value
        val expr = if (current.clearOnNextInput) current.result else current.expression
        val newExpr = "$expr$op"
        val newResult = tryEval(newExpr)
        _state.update { it.copy(expression = newExpr, result = newResult, clearOnNextInput = false) }
    }

    private fun insertText(text: String) {
        val current = _state.value
        val expr = if (current.clearOnNextInput) "" else current.expression
        val newExpr = "$expr$text"
        val newResult = tryEval(newExpr)
        _state.update { it.copy(expression = newExpr, result = newResult, clearOnNextInput = false) }
    }

    private fun insertConstant(text: String) {
        val current = _state.value
        val expr = if (current.clearOnNextInput) "" else current.expression
        val newExpr = "$expr$text"
        val newResult = tryEval(newExpr)
        _state.update { it.copy(expression = newExpr, result = newResult, clearOnNextInput = false) }
    }

    private fun insertBinaryIdent(op: String) {
        val current = _state.value
        val expr = if (current.clearOnNextInput) current.result else current.expression
        val newExpr = "$expr$op"
        val newResult = tryEval(newExpr)
        _state.update { it.copy(expression = newExpr, result = newResult, clearOnNextInput = false) }
    }

    private fun evaluateModulo(state: CalculatorState) {
        val expr = state.expression.trim()
        if (expr.isEmpty()) return
        try {
            // Find last number in expression
            val numRegex = Regex("""(\d+\.?\d*)\s*$""")
            val match = numRegex.find(expr) ?: return
            val lastNum = match.groupValues[1].toDoubleOrNull() ?: return
            val prefix = expr.substring(0, match.range.first).trim()

            val newExpr = if (prefix.isEmpty()) {
                // Just a number: X% -> X/100
                "(${match.groupValues[1]}÷100)"
            } else {
                val lastOp = prefix.lastOrNull()
                val baseExpr = prefix.dropLast(1).trim()
                if ((lastOp == '+' || lastOp == '−') && baseExpr.isNotEmpty()) {
                    // A+B% -> A+A*B/100, A-B% -> A-A*B/100
                    try {
                        val base = CalculatorEngine.evaluate(
                            CalculatorEngine.normalize(baseExpr),
                            state.angleMode, state.lastResult, state.isEngMode
                        ).toDoubleOrNull() ?: lastNum
                        "($baseExpr$lastOp$base×${match.groupValues[1]}÷100)"
                    } catch (_: Exception) {
                        "($prefix${match.groupValues[1]}÷100)"
                    }
                } else {
                    // A×B% or A÷B% -> A×(B/100)
                    "($prefix(${match.groupValues[1]}÷100))"
                }
            }
            _state.update { it.copy(expression = newExpr) }
        } catch (_: Exception) { }
    }

    private fun convertToDms(state: CalculatorState) {
        val value = state.result.toDoubleOrNull() ?: return
        val degrees = value.toInt()
        val minutesRem = (value - degrees) * 60
        val minutes = minutesRem.toInt()
        val seconds = (minutesRem - minutes) * 60
        _state.update { it.copy(result = "$degrees°${minutes}′${String.format("%.1f", seconds)}″") }
    }

    private fun convertToDecimal(state: CalculatorState) {
        val value = state.result.toDoubleOrNull() ?: return
        _state.update { it.copy(result = value.toString()) }
    }

    private fun convertToFraction(state: CalculatorState) {
        val value = state.result.toDoubleOrNull() ?: return
        val frac = CalculatorEngine.formatAsFraction(value, mixedFraction = true)
        if (frac != null) {
            _state.update { it.copy(result = frac) }
        }
    }

    // Mode toggles
    private fun toggleMode() {
        _state.update {
            it.copy(mode = if (it.mode == CalculatorMode.BASIC) CalculatorMode.SCIENTIFIC else CalculatorMode.BASIC)
        }
    }

    private fun setAngleMode(mode: AngleMode) {
        _state.update { it.copy(angleMode = mode) }
    }

    private fun cycleAngleMode() {
        val next = when (_state.value.angleMode) {
            AngleMode.DEGREE -> AngleMode.RADIAN
            AngleMode.RADIAN -> AngleMode.GRAD
            AngleMode.GRAD -> AngleMode.DEGREE
        }
        setAngleMode(next)
    }

    private fun toggleSdMode() {
        _state.update { it.copy(isSDMode = !it.isSDMode) }
    }

    private fun toggleDcMode() {
        _state.update { it.copy(isDCMode = !it.isDCMode) }
    }

    private fun toggleEngMode() {
        _state.update { it.copy(isEngMode = !it.isEngMode) }
    }

    private fun toggleAlpha() {
        _state.update { it.copy(isAlphaMode = !it.isAlphaMode) }
    }

    private fun toggleHyp() {
        _state.update { it.copy(isHypMode = !it.isHypMode) }
    }

    private fun toggleSecond() {
        _state.update { it.copy(isSecondMode = !it.isSecondMode) }
    }

    // Memory
    private fun memoryClear() {
        _state.update { it.copy(memory = 0.0) }
    }

    private fun memoryRecall() {
        _state.update {
            val memStr = formatMemory(it.memory)
            it.copy(expression = memStr, result = memStr, clearOnNextInput = true)
        }
    }

    private fun memoryAdd() {
        val current = _state.value
        val value = current.result.toDoubleOrNull() ?: return
        _state.update { it.copy(memory = it.memory + value) }
    }

    private fun memorySubtract() {
        val current = _state.value
        val value = current.result.toDoubleOrNull() ?: return
        _state.update { it.copy(memory = it.memory - value) }
    }

    private fun formatMemory(value: Double): String {
        if (value == value.roundToInt().toDouble()) return value.toInt().toString()
        return CalculatorEngine.normalize(value.toString())
    }

    // History (Room-backed with stable IDs)
    private fun saveHistory() {
        // Persist latest entry to Room (state already updated optimistically)
        viewModelScope.launch {
            try {
                val latest = _state.value.history.firstOrNull() ?: return@launch
                // Get max size from settings (default 100)
                historyRepo.saveEntry(latest.expression, latest.result, 100)
            } catch (_: Exception) { }
        }
    }

    fun deleteHistoryEntry(timestamp: Long) {
        val entry = _state.value.history.firstOrNull { it.timestamp == timestamp }
        _state.update {
            it.copy(history = it.history.filter { e -> e.timestamp != timestamp })
        }
        viewModelScope.launch {
            try {
                if (entry != null) {
                    historyRepo.deleteByExpression(entry.expression, entry.timestamp)
                }
            } catch (_: Exception) { }
        }
    }

    fun clearHistory() {
        _state.update { it.copy(history = emptyList()) }
        viewModelScope.launch {
            try {
                historyRepo.clearAll()
            } catch (_: Exception) { }
        }
    }

    // Utility
    private fun tryEval(expr: String): String {
        if (expr.isEmpty()) return "0"
        val normalized = CalculatorEngine.normalize(expr)
        return try {
            CalculatorEngine.evaluate(normalized, _state.value.angleMode, _state.value.lastResult, _state.value.isEngMode)
        } catch (_: Exception) { _state.value.result }
    }
}
