package com.kraft.calculator.domain

import org.junit.Test
import org.junit.Assert.*
import kotlin.math.PI
import kotlin.math.E

/**
 * Comprehensive JUnit4 test suite for [CalculatorEngine].
 *
 * Conventions:
 * - [CalculatorEngine.evaluate] returns a formatted String, so numeric tests
 *   parse it back to Double via [eval]. The engine renders negative numbers
 *   with U+2212 MINUS SIGN, which is normalized before parsing.
 * - Double comparisons use a delta (1e-9) because formatting rounds to 10 dp.
 * - Error tests use try/catch + fail (compatible with JUnit 4.12 which has
 *   no assertThrows) and assert on the exact exception message.
 */
class CalculatorEngineTest {

    private fun eval(expression: String, angleMode: AngleMode = AngleMode.DEGREE): Double {
        val raw = CalculatorEngine.evaluate(expression, angleMode = angleMode)
        return raw.replace("−", "-").toDouble()
    }

    private fun expectError(expression: String, expectedMessage: String, angleMode: AngleMode = AngleMode.DEGREE) {
        try {
            CalculatorEngine.evaluate(expression, angleMode = angleMode)
            fail("Expected CalculatorException(\"$expectedMessage\") for: $expression")
        } catch (e: CalculatorException) {
            assertEquals(expectedMessage, e.message)
        }
    }

    // ------------------------------------------------------------------
    // 1. Basic arithmetic, precedence, parentheses
    // ------------------------------------------------------------------

    @Test fun addition() {
        assertEquals(5.0, eval("2+3"), 1e-9)
    }

    @Test fun subtraction() {
        assertEquals(5.0, eval("10−5"), 1e-9)
    }

    @Test fun subtractionAsciiMinus() {
        assertEquals(5.0, eval("10-5"), 1e-9)
    }

    @Test fun multiplication() {
        assertEquals(42.0, eval("6×7"), 1e-9)
    }

    @Test fun multiplicationAsciiStar() {
        assertEquals(42.0, eval("6*7"), 1e-9)
    }

    @Test fun division() {
        assertEquals(5.0, eval("20÷4"), 1e-9)
    }

    @Test fun divisionAsciiSlash() {
        assertEquals(5.0, eval("20/4"), 1e-9)
    }

    @Test fun precedenceMulBeforeAdd() {
        // 2+3×4 = 14, not 20
        assertEquals(14.0, eval("2+3×4"), 1e-9)
    }

    @Test fun precedenceMulBeforeSub() {
        assertEquals(4.0, eval("10−2×3"), 1e-9)
    }

    @Test fun parenthesesOverridePrecedence() {
        assertEquals(20.0, eval("(2+3)×4"), 1e-9)
    }

    @Test fun nestedParentheses() {
        // ((2+3)×(4−1))÷5 = (5×3)÷5 = 3
        assertEquals(3.0, eval("((2+3)×(4−1))÷5"), 1e-9)
    }

    @Test fun deeplyNestedParentheses() {
        // 2×(3+(4×5)) = 2×23 = 46
        assertEquals(46.0, eval("2×(3+(4×5))"), 1e-9)
    }

    @Test fun decimalAddition() {
        assertEquals(0.3, eval("0.1+0.2"), 1e-9)
    }

    @Test fun unaryMinus() {
        assertEquals(-2.0, eval("−5+3"), 1e-9)
    }

    // ------------------------------------------------------------------
    // 2. Trigonometry in DEGREE mode
    // ------------------------------------------------------------------

    @Test fun sin30Deg() {
        assertEquals(0.5, eval("sin(30)", AngleMode.DEGREE), 1e-9)
    }

    @Test fun cos60Deg() {
        assertEquals(0.5, eval("cos(60)", AngleMode.DEGREE), 1e-9)
    }

    @Test fun tan45Deg() {
        assertEquals(1.0, eval("tan(45)", AngleMode.DEGREE), 1e-9)
    }

    @Test fun asinHalfDeg() {
        assertEquals(30.0, eval("asin(0.5)", AngleMode.DEGREE), 1e-9)
    }

    @Test fun acosHalfDeg() {
        assertEquals(60.0, eval("acos(0.5)", AngleMode.DEGREE), 1e-9)
    }

    @Test fun atanOneDeg() {
        assertEquals(45.0, eval("atan(1)", AngleMode.DEGREE), 1e-9)
    }

    // ------------------------------------------------------------------
    // 3. Trigonometry in RADIAN mode
    // ------------------------------------------------------------------

    @Test fun sinPiOver6Rad() {
        assertEquals(0.5, eval("sin(π÷6)", AngleMode.RADIAN), 1e-9)
    }

    @Test fun cosPiOver3Rad() {
        assertEquals(0.5, eval("cos(π÷3)", AngleMode.RADIAN), 1e-9)
    }

    @Test fun tanPiOver4Rad() {
        assertEquals(1.0, eval("tan(π÷4)", AngleMode.RADIAN), 1e-9)
    }

    @Test fun asinHalfRad() {
        // asin(0.5) = π/6 ≈ 0.5235987756
        assertEquals(PI / 6, eval("asin(0.5)", AngleMode.RADIAN), 1e-9)
    }

    // ------------------------------------------------------------------
    // 4. Trigonometry in GRAD mode (200 grad = 180°)
    // ------------------------------------------------------------------

    @Test fun sin100GradIsOne() {
        // 100 grad = 90° → sin = 1.0
        assertEquals(1.0, eval("sin(100)", AngleMode.GRAD), 1e-9)
    }

    @Test fun cos100GradIsZero() {
        // 100 grad = 90° → cos ≈ 0 (engine snaps |x| < 1e-12 to 0)
        assertEquals(0.0, eval("cos(100)", AngleMode.GRAD), 1e-9)
    }

    // ------------------------------------------------------------------
    // 5. Hyperbolic functions
    // ------------------------------------------------------------------

    @Test fun sinhZero() {
        assertEquals(0.0, eval("sinh(0)"), 1e-9)
    }

    @Test fun coshZero() {
        assertEquals(1.0, eval("cosh(0)"), 1e-9)
    }

    @Test fun tanhZero() {
        assertEquals(0.0, eval("tanh(0)"), 1e-9)
    }

    @Test fun sinhOne() {
        // WolframAlpha: sinh(1) = 1.1752011936438014
        assertEquals(1.1752011936, eval("sinh(1)"), 1e-9)
    }

    @Test fun coshOne() {
        // WolframAlpha: cosh(1) = 1.543080634815244
        assertEquals(1.5430806348, eval("cosh(1)"), 1e-9)
    }

    // ------------------------------------------------------------------
    // 6. Logarithms and exponentials
    // ------------------------------------------------------------------

    @Test fun lnOfE() {
        assertEquals(1.0, eval("ln(e)"), 1e-9)
    }

    @Test fun log100() {
        assertEquals(2.0, eval("log(100)"), 1e-9)
    }

    @Test fun log1000() {
        assertEquals(3.0, eval("log(1000)"), 1e-9)
    }

    @Test fun expZero() {
        assertEquals(1.0, eval("exp(0)"), 1e-9)
    }

    @Test fun expOneIsE() {
        assertEquals(E, eval("exp(1)"), 1e-9)
    }

    // ------------------------------------------------------------------
    // 7. Powers and roots
    // ------------------------------------------------------------------

    @Test fun twoToTheTen() {
        assertEquals(1024.0, eval("2^10"), 1e-9)
    }

    @Test fun sqrt16() {
        assertEquals(4.0, eval("√16"), 1e-9)
    }

    @Test fun cbrt27() {
        assertEquals(3.0, eval("∛27"), 1e-9)
    }

    @Test fun squarePostfix() {
        assertEquals(25.0, eval("5²"), 1e-9)
    }

    @Test fun cubePostfix() {
        assertEquals(125.0, eval("5³"), 1e-9)
    }

    @Test fun reciprocalPostfix() {
        assertEquals(0.2, eval("5⁻¹"), 1e-9)
    }

    @Test fun binaryNthRoot() {
        // 3√27 = 27^(1/3) = 3
        assertEquals(3.0, eval("3√27"), 1e-9)
    }

    // ------------------------------------------------------------------
    // 8. Factorial
    // ------------------------------------------------------------------

    @Test fun zeroFactorial() {
        assertEquals(1.0, eval("0!"), 1e-9)
    }

    @Test fun fiveFactorial() {
        assertEquals(120.0, eval("5!"), 1e-9)
    }

    @Test fun tenFactorial() {
        assertEquals(3628800.0, eval("10!"), 1e-9)
    }

    // ------------------------------------------------------------------
    // 9. nCr / nPr combinations and permutations
    // ------------------------------------------------------------------

    @Test fun nCrBasic() {
        assertEquals(10.0, eval("5nCr2"), 1e-9)
    }

    @Test fun nPrBasic() {
        assertEquals(20.0, eval("5nPr2"), 1e-9)
    }

    @Test fun nCrChooseZero() {
        assertEquals(1.0, eval("7nCr0"), 1e-9)
    }

    @Test fun nCrChooseSelf() {
        assertEquals(1.0, eval("7nCr7"), 1e-9)
    }

    @Test fun nCrLarger() {
        // 10C3 = 120
        assertEquals(120.0, eval("10nCr3"), 1e-9)
    }

    @Test fun nPrLarger() {
        // 10P3 = 720
        assertEquals(720.0, eval("10nPr3"), 1e-9)
    }

    // ------------------------------------------------------------------
    // 10. Constants
    // ------------------------------------------------------------------

    @Test fun piConstant() {
        // WolframAlpha: π = 3.141592653589793
        assertEquals(PI, eval("π"), 1e-9)
    }

    @Test fun eConstant() {
        // WolframAlpha: e = 2.718281828459045
        assertEquals(E, eval("e"), 1e-9)
    }

    @Test fun tauConstant() {
        // WolframAlpha: τ = 2π = 6.283185307179586
        assertEquals(2 * PI, eval("τ"), 1e-9)
    }

    // ------------------------------------------------------------------
    // 11. DMS (degrees-minutes-seconds) input
    // ------------------------------------------------------------------

    @Test fun dmsDegreesMinutes() {
        // 30°30′ = 30 + 30/60 = 30.5
        assertEquals(30.5, eval("30°30′"), 1e-9)
    }

    @Test fun dmsDegreesMinutesSeconds() {
        // 45°30′30″ = 45 + 30/60 + 30/3600 = 45.508333...
        assertEquals(45.0 + 30.0 / 60 + 30.0 / 3600, eval("45°30′30″"), 1e-9)
    }

    // ------------------------------------------------------------------
    // 12. Error cases
    // ------------------------------------------------------------------

    @Test fun divideByZeroThrows() {
        expectError("1÷0", "Cannot divide by zero")
    }

    @Test fun sqrtNegativeThrows() {
        expectError("√(−1)", "Domain Error")
    }

    @Test fun lnZeroThrows() {
        expectError("ln(0)", "Domain Error")
    }

    @Test fun asinOutOfRangeThrows() {
        expectError("asin(2)", "Domain Error")
    }

    @Test fun unbalancedOpenParenThrows() {
        expectError("(2+3", "Mismatched parentheses")
    }

    @Test fun unbalancedCloseParenThrows() {
        expectError("2+3)", "Mismatched parentheses")
    }

    // ------------------------------------------------------------------
    // 13. Edge cases
    // ------------------------------------------------------------------

    @Test fun emptyStringReturnsZero() {
        assertEquals("0", CalculatorEngine.evaluate(""))
    }

    @Test fun blankStringReturnsZero() {
        assertEquals("0", CalculatorEngine.evaluate("   "))
    }

    @Test fun overflowThrows() {
        // 1e308 × 10 overflows Double → Infinity → "Overflow"
        expectError("1e308×10", "Overflow")
    }

    @Test fun zeroDividedByZeroThrows() {
        // Engine reports 0/0 as "Cannot divide by zero" (divisor check fires first)
        expectError("0÷0", "Cannot divide by zero")
    }
}
