package com.kraft.calculator.domain

/**
 * Unit converter following Fossify pattern: each unit defines
 * factor to convert TO canonical base unit, then FROM base to target.
 * Temperature overrides for affine (offset) conversions.
 */
enum class ConverterCategory(val title: String) {
    LENGTH("Length"),
    AREA("Area"),
    VOLUME("Volume"),
    MASS("Mass"),
    TEMPERATURE("Temperature"),
    TIME("Time"),
    SPEED("Speed"),
    PRESSURE("Pressure"),
    ENERGY("Energy"),
}

data class ConverterUnit(
    val name: String,
    val symbol: String,
    val toBase: Double,
    val offsetToBase: Double = 0.0,
)

object UnitConverter {

    private val length = listOf(
        ConverterUnit("Kilometers", "km", 1000.0),
        ConverterUnit("Meters", "m", 1.0),
        ConverterUnit("Centimeters", "cm", 0.01),
        ConverterUnit("Millimeters", "mm", 0.001),
        ConverterUnit("Micrometers", "µm", 1e-6),
        ConverterUnit("Nanometers", "nm", 1e-9),
        ConverterUnit("Miles", "mi", 1609.344),
        ConverterUnit("Yards", "yd", 0.9144),
        ConverterUnit("Feet", "ft", 0.3048),
        ConverterUnit("Inches", "in", 0.0254),
        ConverterUnit("Nautical miles", "nmi", 1852.0),
    )

    private val area = listOf(
        ConverterUnit("Square km", "km²", 1e6),
        ConverterUnit("Square m", "m²", 1.0),
        ConverterUnit("Square cm", "cm²", 1e-4),
        ConverterUnit("Hectares", "ha", 1e4),
        ConverterUnit("Acres", "ac", 4046.8564224),
        ConverterUnit("Square miles", "mi²", 2589988.110336),
        ConverterUnit("Square feet", "ft²", 0.09290304),
        ConverterUnit("Square inches", "in²", 6.4516e-4),
    )

    private val volume = listOf(
        ConverterUnit("Liters", "L", 1.0),
        ConverterUnit("Milliliters", "mL", 1e-3),
        ConverterUnit("Cubic meters", "m³", 1000.0),
        ConverterUnit("Gallons (US)", "gal", 3.785411784),
        ConverterUnit("Quarts (US)", "qt", 0.946352946),
        ConverterUnit("Pints (US)", "pt", 0.473176473),
        ConverterUnit("Cups (US)", "cup", 0.24),
        ConverterUnit("Fluid oz (US)", "fl oz", 0.0295735295625),
    )

    private val mass = listOf(
        ConverterUnit("Kilograms", "kg", 1.0),
        ConverterUnit("Grams", "g", 1e-3),
        ConverterUnit("Milligrams", "mg", 1e-6),
        ConverterUnit("Tonnes", "t", 1000.0),
        ConverterUnit("Pounds", "lb", 0.45359237),
        ConverterUnit("Ounces", "oz", 0.028349523125),
    )

    private val temperature = listOf(
        ConverterUnit("Celsius", "°C", 1.0, 273.15),
        ConverterUnit("Fahrenheit", "°F", 5.0 / 9.0, 459.67 * 5.0 / 9.0),
        ConverterUnit("Kelvin", "K", 1.0, 0.0),
    )

    private val time = listOf(
        ConverterUnit("Seconds", "s", 1.0),
        ConverterUnit("Milliseconds", "ms", 1e-3),
        ConverterUnit("Minutes", "min", 60.0),
        ConverterUnit("Hours", "h", 3600.0),
        ConverterUnit("Days", "d", 86400.0),
        ConverterUnit("Weeks", "wk", 604800.0),
    )

    private val speed = listOf(
        ConverterUnit("Meters/sec", "m/s", 1.0),
        ConverterUnit("Km/hour", "km/h", 1.0 / 3.6),
        ConverterUnit("Miles/hour", "mph", 0.44704),
        ConverterUnit("Feet/sec", "ft/s", 0.3048),
        ConverterUnit("Knots", "kn", 0.514444444444),
    )

    private val pressure = listOf(
        ConverterUnit("Pascals", "Pa", 1.0),
        ConverterUnit("Kilopascals", "kPa", 1000.0),
        ConverterUnit("Bar", "bar", 1e5),
        ConverterUnit("PSI", "psi", 6894.757293168),
        ConverterUnit("Atmospheres", "atm", 101325.0),
        ConverterUnit("mmHg", "mmHg", 133.322387415),
    )

    private val energy = listOf(
        ConverterUnit("Joules", "J", 1.0),
        ConverterUnit("Kilojoules", "kJ", 1000.0),
        ConverterUnit("Calories", "cal", 4.184),
        ConverterUnit("Kilocalories", "kcal", 4184.0),
        ConverterUnit("Watt-hours", "Wh", 3600.0),
        ConverterUnit("BTU", "BTU", 1055.05585262),
        ConverterUnit("Electronvolts", "eV", 1.602176634e-19),
    )

    fun unitsFor(category: ConverterCategory): List<ConverterUnit> = when (category) {
        ConverterCategory.LENGTH -> length
        ConverterCategory.AREA -> area
        ConverterCategory.VOLUME -> volume
        ConverterCategory.MASS -> mass
        ConverterCategory.TEMPERATURE -> temperature
        ConverterCategory.TIME -> time
        ConverterCategory.SPEED -> speed
        ConverterCategory.PRESSURE -> pressure
        ConverterCategory.ENERGY -> energy
    }

    fun convert(value: Double, from: ConverterUnit, to: ConverterUnit): Double {
        val base = value * from.toBase + from.offsetToBase
        return (base - to.offsetToBase) / to.toBase
    }
}
