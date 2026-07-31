package com.kraft.calculator.presentation

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlin.math.roundToInt
import com.kraft.calculator.data.HistoryRepository
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

    private val historyRepo = HistoryRepository(application)

    private val _state = MutableStateFlow(
        CalculatorState(history = historyRepo.loadHistory())
    )
    val state: StateFlow<CalculatorState> = _state.asStateFlow()

    // ---------------------------------------------------------------------------
    // Button press dispatch
    // ---------------------------------------------------------------------------
    fun onButtonPressed(key: String) {
        val current = _state.value

        when (key) {
            "=" -> evaluateExpression(current)
            "AC" -> clearAll()
            "CE" -> clearEntry()
            "BS" -> backspace()
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
            "SD" -> toggleSdMode()
            "DC" -> toggleDcMode()
            "ENG" -> toggleEngMode()

            // Memory
            "MC" -> memoryClear()
            "MR" -> memoryRecall()
            "M+" -> memoryAdd()
            "M−" -> memorySubtract()

            // Toggles
            "ALPHA" -> toggleAlpha()
            "HYP" -> toggleHyp()
            "2nd" -> toggleSecond()

            // Number keys
            "0", "1", "2", "3", "4", "5", "6", "7", "8", "9" -> insertDigit(key)
            // Negative prefix
            "(−)" -> insertPrefix("−")

            else -> { /* ignore */ }
        }
    }

    // ---------------------------------------------------------------------------
    // Actions
    // ---------------------------------------------------------------------------
    private fun evaluateExpression(state: CalculatorState) {
        val normalized = CalculatorEngine.normalize(state.expression)
        if (normalized.isEmpty()) return

        val lastResult = state.lastResult
        try {
            val result = CalculatorEngine.evaluate(
                normalized, state.isDegreeMode, lastResult, state.isEngMode
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
        val expr = state.expression
        val normalized = CalculatorEngine.normalize(expr)
        if (normalized.isEmpty()) return
        try {
            val evaluated = CalculatorEngine.evaluate(normalized, state.isDegreeMode, state.lastResult, state.isEngMode)
            val newExpr = "$expr×100"
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

    // History
    private fun saveHistory() {
        viewModelScope.launch {
            historyRepo.saveHistory(_state.value.history)
        }
    }

    fun deleteHistoryEntry(timestamp: Long) {
        _state.update {
            it.copy(history = it.history.filter { entry -> entry.timestamp != timestamp })
        }
        saveHistory()
    }

    fun clearHistory() {
        _state.update { it.copy(history = emptyList()) }
        viewModelScope.launch {
            historyRepo.clearHistory()
        }
    }

    // Utility
    private fun tryEval(expr: String): String {
        if (expr.isEmpty()) return "0"
        val normalized = CalculatorEngine.normalize(expr)
        return try {
            CalculatorEngine.evaluate(normalized, _state.value.isDegreeMode, _state.value.lastResult, _state.value.isEngMode)
        } catch (_: Exception) { _state.value.result }
    }
}
