package com.kraft.calculator.domain

import kotlin.math.*

object CalculatorEngine {

    // ---------------------------------------------------------------------------
    // Token types for Shunting-Yard
    // ---------------------------------------------------------------------------
    private enum class TokenType { NUMBER, BINARY_OP, PREFIX_OP, POSTFIX_OP, LEFT_PAREN, RIGHT_PAREN }

    private data class Token(
        val type: TokenType,
        val value: String,
        val number: Double? = null,
        val precedence: Int = 0,
        val isRightAssociative: Boolean = false,
    )

    // ---------------------------------------------------------------------------
    // Constants
    // ---------------------------------------------------------------------------
    private val binaryOps = mapOf(
        "+" to 2, "−" to 2, "×" to 3, "÷" to 3, "%" to 3, "^" to 4, "√" to 4
    )
    private val rightAssocOps = setOf("^", "√")
    private val unaryPrefixOps = setOf(
        "√", "∛", "sin", "cos", "tan", "asin", "acos", "atan",
        "ln", "log", "exp", "abs", "sin⁻¹", "cos⁻¹", "tan⁻¹",
        "10^", "e^", "sinh", "cosh", "tanh", "sinh⁻¹", "cosh⁻¹", "tanh⁻¹",
    )
    private val unaryPostfixOps = setOf("²", "³", "!", "⁻¹")
    private val identBinaryOps = setOf("nCr", "nPr")
    private val constants = mapOf(
        "π" to PI, "e" to E, "τ" to PI * 2
    )

    // ---------------------------------------------------------------------------
    // Main evaluation entry point
    // ---------------------------------------------------------------------------
    fun evaluate(
        expression: String,
        angleMode: AngleMode = AngleMode.DEGREE,
        lastResult: Double? = null,
        isEngMode: Boolean = false,
    ): String {
        val trimmed = expression.trim()
        if (trimmed.isEmpty()) return "0"

        try {
            val tokens = tokenize(trimmed, lastResult)
            if (tokens.isEmpty()) return "0"

            val rpn = shuntingYard(tokens)
            val result = evaluateRpn(rpn, angleMode)
            return formatNumber(result, isEngMode)
        } catch (e: CalculatorException) {
            throw e
        } catch (e: Exception) {
            throw CalculatorException(e.message ?: "Syntax Error")
        }
    }

    fun normalize(expression: String): String {
        if (expression.isEmpty()) return expression

        var open = 0
        for (ch in expression) {
            when (ch) {
                '(' -> open++
                ')' -> open--
            }
        }

        var result = expression
        repeat(open) { result += ")" }

        while (result.isNotEmpty() && isOperatorChar(result.last())) {
            result = result.dropLast(1)
        }

        return result.trim()
    }

    // ---------------------------------------------------------------------------
    // Tokenization
    // ---------------------------------------------------------------------------
    private fun tokenize(input: String, lastResult: Double?): List<Token> {
        val tokens = mutableListOf<Token>()
        var i = 0
        val len = input.length

        fun lastWasOperand(): Boolean = tokens.isNotEmpty() && (
                tokens.last().type == TokenType.NUMBER ||
                        tokens.last().type == TokenType.POSTFIX_OP ||
                        tokens.last().type == TokenType.RIGHT_PAREN
                )

        fun isDigit(ch: Char) = ch in '0'..'9'
        fun isLetter(ch: Char) = ch in 'a'..'z' || ch in 'A'..'Z'

        while (i < len) {
            val ch = input[i]

            // Whitespace
            if (ch == ' ') { i++; continue }

            // Numbers
            if (isDigit(ch) || ch == '.') {
                val numStr = buildString {
                    while (i < len && (isDigit(input[i]) || input[i] == '.')) {
                        append(input[i]); i++
                    }
                    // Scientific notation
                    if (i < len && (input[i] == 'e' || input[i] == 'E')) {
                        val next = i + 1
                        if (next < len && (isDigit(input[next]) || input[next] == '+' || input[next] == '-')) {
                            append(input[i]); i++
                            if (input[i] == '+' || input[i] == '-') { append(input[i]); i++ }
                            while (i < len && isDigit(input[i])) { append(input[i]); i++ }
                        }
                    }
                }

                val value = numStr.toDoubleOrNull() ?: throw CalculatorException("Syntax Error")

                // DMS parsing: DD°MM′SS″
                if (i < len && input[i] == '°') {
                    i++ // consume °
                    val minuteStr = buildString {
                        while (i < len && (isDigit(input[i]) || input[i] == '.')) {
                            append(input[i]); i++
                        }
                    }
                    val minutes = minuteStr.toDoubleOrNull() ?: 0.0

                    if (i < len && input[i] == '′') {
                        i++
                        val secondStr = buildString {
                            while (i < len && (isDigit(input[i]) || input[i] == '.')) {
                                append(input[i]); i++
                            }
                        }
                        val seconds = secondStr.toDoubleOrNull() ?: 0.0
                        if (i < len && input[i] == '″') i++
                        val combined = value + minutes / 60 + seconds / 3600
                        tokens.add(Token(TokenType.NUMBER, combined.toString(), combined))
                    } else {
                        val combined = value + minutes / 60
                        tokens.add(Token(TokenType.NUMBER, combined.toString(), combined))
                    }
                } else {
                    tokens.add(Token(TokenType.NUMBER, numStr, value))
                }
                continue
            }

            // Constants π, τ
            if (ch == 'π' || ch == 'τ') {
                if (lastWasOperand()) tokens.add(Token(TokenType.BINARY_OP, "×", precedence = 3))
                tokens.add(Token(TokenType.NUMBER, ch.toString(), constants[ch.toString()]!!))
                i++
                continue
            }

            // Identifiers
            if (isLetter(ch)) {
                val ident = buildString {
                    while (i < len && isLetter(input[i])) { append(input[i]); i++ }
                    // Check for ⁻¹ suffix
                    if (i + 1 < len && input[i] == '⁻' && input[i + 1] == '¹') {
                        append("⁻¹"); i += 2
                    }
                }

                // Ans variable
                if (ident == "Ans") {
                    val ans = lastResult ?: 0.0
                    if (lastWasOperand()) tokens.add(Token(TokenType.BINARY_OP, "×", precedence = 3))
                    tokens.add(Token(TokenType.NUMBER, "Ans", ans))
                    continue
                }

                // Ran# random
                if (ident == "Ran" && i < len && input[i] == '#') {
                    i++
                    val r = Math.random()
                    if (lastWasOperand()) tokens.add(Token(TokenType.BINARY_OP, "×", precedence = 3))
                    tokens.add(Token(TokenType.NUMBER, "Ran#", r))
                    continue
                }

                // Named constants
                if (constants.containsKey(ident)) {
                    if (lastWasOperand()) tokens.add(Token(TokenType.BINARY_OP, "×", precedence = 3))
                    tokens.add(Token(TokenType.NUMBER, ident, constants[ident]!!))
                    continue
                }

                // Inverse functions (ident ends with ⁻¹)
                if (ident.endsWith("⁻¹")) {
                    val base = ident.dropLast(2)
                    if (unaryPrefixOps.contains("$base⁻¹")) {
                        if (lastWasOperand()) tokens.add(Token(TokenType.BINARY_OP, "×", precedence = 3))
                        tokens.add(Token(TokenType.PREFIX_OP, "$base⁻¹"))
                        continue
                    }
                }

                // Unary prefix functions
                if (unaryPrefixOps.contains(ident)) {
                    if (lastWasOperand()) tokens.add(Token(TokenType.BINARY_OP, "×", precedence = 3))
                    tokens.add(Token(TokenType.PREFIX_OP, ident))
                    continue
                }

                // nCr / nPr binary ops
                if (identBinaryOps.contains(ident)) {
                    if (!lastWasOperand()) throw CalculatorException("Syntax Error")
                    tokens.add(Token(TokenType.BINARY_OP, ident, precedence = 4))
                    continue
                }

                throw CalculatorException("Unknown: $ident")
            }

            // Minus — binary or unary
            if (ch == '−' || ch == '-') {
                if (!lastWasOperand()) {
                    tokens.add(Token(TokenType.PREFIX_OP, "−"))
                } else {
                    tokens.add(Token(TokenType.BINARY_OP, "−", precedence = 2))
                }
                i++
                continue
            }

            // DMS separators — skip if found independently
            if (ch == '°' || ch == '′' || ch == '″') { i++; continue }

            // Square root / cube root
            if (ch == '√' || ch == '∛') {
                if (ch == '√' && lastWasOperand()) {
                    tokens.add(Token(TokenType.BINARY_OP, "√", precedence = binaryOps["√"]!!, isRightAssociative = true))
                } else {
                    tokens.add(Token(TokenType.PREFIX_OP, ch.toString()))
                }
                i++
                continue
            }

            // Binary operators: +, ×, ÷, ^, %
            if (ch == '+' || ch == '×' || ch == '÷' || ch == '%' || ch == '^') {
                if (lastWasOperand()) {
                    tokens.add(Token(
                        TokenType.BINARY_OP, ch.toString(),
                        precedence = binaryOps[ch.toString()] ?: 2,
                        isRightAssociative = rightAssocOps.contains(ch.toString())
                    ))
                }
                i++
                continue
            }

            // Left paren
            if (ch == '(') {
                if (lastWasOperand()) tokens.add(Token(TokenType.BINARY_OP, "×", precedence = 3))
                tokens.add(Token(TokenType.LEFT_PAREN, "("))
                i++; continue
            }

            // Right paren
            if (ch == ')') {
                tokens.add(Token(TokenType.RIGHT_PAREN, ")"))
                i++; continue
            }

            // Postfix operators
            val isMinusOne = ch == '⁻' && i + 1 < len && input[i + 1] == '¹'
            val chStr = ch.toString()
            if (unaryPostfixOps.contains(chStr) || isMinusOne) {
                if (lastWasOperand()) {
                    tokens.add(Token(TokenType.POSTFIX_OP, if (isMinusOne) "⁻¹" else chStr))
                }
                i += if (isMinusOne) 2 else 1
                continue
            }

            // / as division
            if (ch == '/') {
                if (lastWasOperand()) tokens.add(Token(TokenType.BINARY_OP, "÷", precedence = 3))
                i++; continue
            }

            // * as multiplication
            if (ch == '*') {
                if (lastWasOperand()) tokens.add(Token(TokenType.BINARY_OP, "×", precedence = 3))
                i++; continue
            }

            throw CalculatorException("Unexpected: $ch")
        }

        return tokens
    }

    // ---------------------------------------------------------------------------
    // Shunting-Yard → RPN
    // ---------------------------------------------------------------------------
    private fun shuntingYard(tokens: List<Token>): List<Token> {
        val output = mutableListOf<Token>()
        val opStack = mutableListOf<Token>()

        for (token in tokens) {
            when (token.type) {
                TokenType.NUMBER -> output.add(token)
                TokenType.POSTFIX_OP -> output.add(token)
                TokenType.PREFIX_OP -> opStack.add(token)
                TokenType.BINARY_OP -> {
                    while (opStack.isNotEmpty() && opStack.last().type == TokenType.BINARY_OP &&
                        (token.precedence < opStack.last().precedence ||
                                (token.precedence == opStack.last().precedence && !token.isRightAssociative))
                    ) {
                        output.add(opStack.removeAt(opStack.lastIndex))
                    }
                    opStack.add(token)
                }
                TokenType.LEFT_PAREN -> opStack.add(token)
                TokenType.RIGHT_PAREN -> {
                    while (opStack.isNotEmpty() && opStack.last().type != TokenType.LEFT_PAREN) {
                        output.add(opStack.removeAt(opStack.lastIndex))
                    }
                    if (opStack.isEmpty() || opStack.last().type != TokenType.LEFT_PAREN) {
                        throw CalculatorException("Mismatched parentheses")
                    }
                    opStack.removeAt(opStack.lastIndex)
                    if (opStack.isNotEmpty() && opStack.last().type == TokenType.PREFIX_OP) {
                        output.add(opStack.removeAt(opStack.lastIndex))
                    }
                }
            }
        }

        while (opStack.isNotEmpty()) {
            val op = opStack.removeAt(opStack.lastIndex)
            if (op.type == TokenType.LEFT_PAREN) throw CalculatorException("Mismatched parentheses")
            output.add(op)
        }

        return output
    }

    // ---------------------------------------------------------------------------
    // RPN Evaluation
    // ---------------------------------------------------------------------------
    private fun evaluateRpn(rpn: List<Token>, angleMode: AngleMode): Double {
        fun toRadians(a: Double): Double = when (angleMode) {
            AngleMode.DEGREE -> a * PI / 180
            AngleMode.RADIAN -> a
            AngleMode.GRAD -> a * PI / 200
        }
        fun fromRadians(a: Double): Double = when (angleMode) {
            AngleMode.DEGREE -> a * 180 / PI
            AngleMode.RADIAN -> a
            AngleMode.GRAD -> a * 200 / PI
        }
        val stack = mutableListOf<Double>()

        for (token in rpn) {
            when (token.type) {
                TokenType.NUMBER -> {
                    stack.add(token.number ?: throw CalculatorException("Syntax Error"))
                }
                TokenType.POSTFIX_OP -> {
                    val a = stack.removeAt(stack.lastIndex)
                    when (token.value) {
                        "²" -> stack.add(a * a)
                        "³" -> stack.add(a * a * a)
                        "!" -> {
                            if (a < 0 || a != a.roundToInt().toDouble()) throw CalculatorException("Domain Error")
                            stack.add(factorial(a.toInt()))
                        }
                        "⁻¹" -> {
                            if (a == 0.0) throw CalculatorException("Cannot divide by zero")
                            stack.add(1.0 / a)
                        }
                        else -> throw CalculatorException("Unknown op")
                    }
                }
                TokenType.BINARY_OP -> {
                    val b = stack.removeAt(stack.lastIndex)
                    val a = stack.removeAt(stack.lastIndex)
                    when (token.value) {
                        "+" -> stack.add(a + b)
                        "−" -> stack.add(a - b)
                        "×" -> stack.add(a * b)
                        "÷" -> {
                            if (b == 0.0) throw CalculatorException("Cannot divide by zero")
                            stack.add(a / b)
                        }
                        "%" -> {
                            if (b == 0.0) throw CalculatorException("Cannot divide by zero")
                            stack.add(a * b / 100)
                        }
                        "^" -> stack.add(a.pow(b))
                        "√" -> {
                            if (a <= 0) throw CalculatorException("Domain Error")
                            if (b < 0) throw CalculatorException("Domain Error")
                            stack.add(b.pow(1.0 / a))
                        }
                        "nCr" -> {
                            if (a < 0 || b < 0 || a != a.roundToInt().toDouble() || b != b.roundToInt().toDouble())
                                throw CalculatorException("Domain Error")
                            if (b > a) throw CalculatorException("Domain Error")
                            stack.add(nCr(a.toInt(), b.toInt()))
                        }
                        "nPr" -> {
                            if (a < 0 || b < 0 || a != a.roundToInt().toDouble() || b != b.roundToInt().toDouble())
                                throw CalculatorException("Domain Error")
                            if (b > a) throw CalculatorException("Domain Error")
                            stack.add(nPr(a.toInt(), b.toInt()))
                        }
                        else -> throw CalculatorException("Unknown op")
                    }
                }
                TokenType.PREFIX_OP -> {
                    val a = stack.removeAt(stack.lastIndex)
                    when (token.value) {
                        "−" -> stack.add(-a)
                        "√" -> {
                            if (a < 0) throw CalculatorException("Domain Error")
                            stack.add(sqrt(a))
                        }
                        "∛" -> {
                            if (a < 0) throw CalculatorException("Domain Error")
                            stack.add(a.pow(1.0 / 3))
                        }
                        "sin" -> stack.add(sin(toRadians(a)))
                        "cos" -> stack.add(cos(toRadians(a)))
                        "tan" -> {
                            val angle = toRadians(a)
                            if ((angle % PI - PI / 2).absoluteValue < 1e-12 && (angle % PI).absoluteValue > 1e-12)
                                throw CalculatorException("Domain Error")
                            stack.add(tan(angle))
                        }
                        "asin" -> {
                            if (a < -1 || a > 1) throw CalculatorException("Domain Error")
                            stack.add(fromRadians(asin(a)))
                        }
                        "acos" -> {
                            if (a < -1 || a > 1) throw CalculatorException("Domain Error")
                            stack.add(fromRadians(acos(a)))
                        }
                        "atan" -> stack.add(fromRadians(atan(a)))
                        "sin⁻¹" -> {
                            if (a < -1 || a > 1) throw CalculatorException("Domain Error")
                            stack.add(fromRadians(asin(a)))
                        }
                        "cos⁻¹" -> {
                            if (a < -1 || a > 1) throw CalculatorException("Domain Error")
                            stack.add(fromRadians(acos(a)))
                        }
                        "tan⁻¹" -> stack.add(fromRadians(atan(a)))
                        "ln" -> {
                            if (a <= 0) throw CalculatorException("Domain Error")
                            stack.add(ln(a))
                        }
                        "log" -> {
                            if (a <= 0) throw CalculatorException("Domain Error")
                            stack.add(ln(a) / ln(10.0))
                        }
                        "exp" -> stack.add(exp(a))
                        "abs" -> stack.add(a.absoluteValue)
                        "10^" -> stack.add(10.0.pow(a))
                        "e^" -> stack.add(exp(a))
                        "sinh" -> stack.add((exp(a) - exp(-a)) / 2)
                        "cosh" -> stack.add((exp(a) + exp(-a)) / 2)
                        "tanh" -> {
                            val ex = exp(2 * a)
                            stack.add((ex - 1) / (ex + 1))
                        }
                        "sinh⁻¹" -> stack.add(ln(a + sqrt(a * a + 1)))
                        "cosh⁻¹" -> {
                            if (a < 1) throw CalculatorException("Domain Error")
                            stack.add(ln(a + sqrt(a * a - 1)))
                        }
                        "tanh⁻¹" -> {
                            if (a <= -1 || a >= 1) throw CalculatorException("Domain Error")
                            stack.add(0.5 * ln((1 + a) / (1 - a)))
                        }
                        else -> throw CalculatorException("Unknown function")
                    }
                }
                TokenType.LEFT_PAREN, TokenType.RIGHT_PAREN -> throw CalculatorException("Syntax Error")
            }
        }

        if (stack.size != 1) throw CalculatorException("Syntax Error")

        val result = stack.first()
        if (result.isInfinite()) throw CalculatorException("Overflow")
        if (result.isNaN()) throw CalculatorException("Undefined")

        return result
    }

    // ---------------------------------------------------------------------------
    // Formatting
    // ---------------------------------------------------------------------------
    private fun factorial(n: Int): Double {
        if (n < 0) throw CalculatorException("Domain Error")
        if (n > 170) throw CalculatorException("Overflow")
        var result = 1.0
        for (i in 2..n) result *= i
        return result
    }

    private fun nCr(n: Int, r: Int): Double {
        if (r > n) return 0.0
        val rr = if (r < n - r) r else n - r
        var result = 1.0
        for (i in 1..rr) {
            result = result * (n - rr + i) / i
        }
        return result
    }

    private fun nPr(n: Int, r: Int): Double {
        if (r > n) return 0.0
        var result = 1.0
        for (i in n downTo n - r + 1) result *= i
        return result
    }

    private fun formatNumber(value: Double, isEngMode: Boolean): String {
        if (value == Double.POSITIVE_INFINITY) return "Infinity"
        if (value == Double.NEGATIVE_INFINITY) return "−Infinity"
        if (value.isNaN()) return "Undefined"

        val rounded = if (value.absoluteValue < 1e-12) 0.0 else value
        val absValue = rounded.absoluteValue

        val formatted: String = when {
            // Engineering notation
            isEngMode && (absValue >= 1e3 || absValue < 1e-3) && absValue != 0.0 -> {
                var exp = 0
                var mantissa = rounded
                if (absValue >= 1) {
                    while (mantissa.absoluteValue >= 1000) { mantissa /= 1000; exp += 3 }
                } else {
                    while (mantissa.absoluteValue < 1) { mantissa *= 1000; exp -= 3 }
                }
                "${trimTrailingZeros(mantissa.toString().take(8))}e$exp"
            }
            // Auto-scientific for very large/small
            (absValue >= 1e10 || (absValue < 1e-10 && absValue > 0)) && absValue != 0.0 -> {
                val parts = rounded.formatExponential(8)
                "${trimTrailingZeros(parts.first)}e${parts.second}"
            }
            else -> {
                if (rounded == rounded.roundToInt().toDouble()) {
                    rounded.toInt().toString()
                } else {
                    trimTrailingZeros(String.format("%.10f", rounded).replace(',', '.'))
                }
            }
        }

        // Normalize hyphen-minus to unicode minus
        return formatted.replace('-', '−')
    }

    private fun Double.formatExponential(precision: Int): Pair<String, String> {
        val str = String.format("%.${precision}e", this).replace(',', '.')
        val parts = str.split("e")
        val exp = parts[1].replace("+", "")
        return Pair(parts[0], exp)
    }

    private fun trimTrailingZeros(s: String): String {
        if (!s.contains('.')) return s
        var trimmed = s
        while (trimmed.endsWith("0")) trimmed = trimmed.dropLast(1)
        if (trimmed.endsWith(".")) trimmed = trimmed.dropLast(1)
        return trimmed
    }

    fun formatAsFraction(value: Double, maxDenominator: Int = 1000, mixedFraction: Boolean = false): String? {
        if (value.isInfinite() || value.isNaN()) return null
        if (value == 0.0) return "0"

        val negative = value < 0
        val absValue = value.absoluteValue

        if (absValue == absValue.roundToInt().toDouble()) {
            return value.toInt().toString()
        }

        // Continued fraction convergents
        var hPrev = 0.0; var kPrev = 1.0
        var h = 1.0; var k = 0.0
        var x = absValue

        while (true) {
            val a = x.toInt()
            val hNext = a * h + hPrev
            val kNext = a * k + kPrev

            if (kNext > maxDenominator) break

            hPrev = kPrev.also { kPrev = k }
            h = hNext; k = kNext
            x = 1.0 / (x - a)

            if ((absValue - h / k).absoluteValue < 1e-14) break
        }

        if (k == 0.0) return null

        val numerator = h.toInt()
        val denominator = k.toInt()
        val sign = if (negative) "−" else ""

        return if (!mixedFraction || numerator < denominator) {
            "$sign$numerator/$denominator"
        } else {
            val whole = numerator / denominator
            val rem = numerator % denominator
            if (rem == 0) "$sign$whole" else "$sign$whole $rem/$denominator"
        }
    }

    private fun isOperatorChar(ch: Char) = ch in setOf('+', '−', '×', '÷', '^', '%')
}

class CalculatorException(message: String) : Exception(message)
