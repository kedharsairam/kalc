package com.kraft.calculator.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

val KraftTypography = Typography(
    displayLarge = TextStyle(fontSize = 34.sp, fontWeight = FontWeight.Normal),
    displayMedium = TextStyle(fontSize = 28.sp, fontWeight = FontWeight.Normal),
    displaySmall = TextStyle(fontSize = 22.sp, fontWeight = FontWeight.Normal),
    headlineLarge = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.SemiBold),
    headlineMedium = TextStyle(fontSize = 17.sp, fontWeight = FontWeight.SemiBold),
    headlineSmall = TextStyle(fontSize = 17.sp, fontWeight = FontWeight.Normal),
    titleLarge = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.SemiBold),
    titleMedium = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Medium),
    titleSmall = TextStyle(fontSize = 15.sp, fontWeight = FontWeight.Medium),
    bodyLarge = TextStyle(fontSize = 17.sp, fontWeight = FontWeight.Normal),
    bodyMedium = TextStyle(fontSize = 15.sp, fontWeight = FontWeight.Normal),
    bodySmall = TextStyle(fontSize = 13.sp, fontWeight = FontWeight.Normal),
    labelLarge = TextStyle(fontSize = 13.sp, fontWeight = FontWeight.SemiBold),
    labelMedium = TextStyle(fontSize = 12.sp, fontWeight = FontWeight.SemiBold),
    labelSmall = TextStyle(fontSize = 11.sp, fontWeight = FontWeight.SemiBold),
)

// Named tokens matching the Flutter KraftTypography
object KraftFontSizes {
    val largeTitle = 34.sp
    val title1 = 28.sp
    val title2 = 22.sp
    val title3 = 20.sp
    val headline = 17.sp
    val body = 17.sp
    val callout = 16.sp
    val subheadline = 15.sp
    val footnote = 13.sp
    val caption1 = 12.sp
    val caption2 = 11.sp
}
