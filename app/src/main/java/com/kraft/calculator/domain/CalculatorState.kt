package com.kraft.calculator.domain

enum class CalculatorMode { BASIC, SCIENTIFIC }

enum class AngleMode { DEGREE, RADIAN, GRAD }

data class CalculatorState(
    val expression: String = "",
    val result: String = "0",
    val mode: CalculatorMode = CalculatorMode.BASIC,
    val angleMode: AngleMode = AngleMode.DEGREE,
    val isSecondMode: Boolean = false,
    val isAlphaMode: Boolean = false,
    val isEngMode: Boolean = false,
    val isSDMode: Boolean = false,
    val isDCMode: Boolean = false,
    val isHypMode: Boolean = false,
    val memory: Double = 0.0,
    val lastResult: Double? = null,
    val lastOperator: String? = null,
    val lastOperand: Double? = null,
    val history: List<CalculationEntry> = emptyList(),
    val error: String? = null,
    val clearOnNextInput: Boolean = false,
) {
    val isDegreeMode: Boolean get() = angleMode == AngleMode.DEGREE

    val angleLabel: String get() = when (angleMode) {
        AngleMode.DEGREE -> "DEG"
        AngleMode.RADIAN -> "RAD"
        AngleMode.GRAD -> "GRAD"
    }
}
