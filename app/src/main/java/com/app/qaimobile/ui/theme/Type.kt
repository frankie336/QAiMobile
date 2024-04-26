package com.app.qaimobile.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp

val CustomRegularTextStyle = TextStyle().copy(fontFamily = RobotoRegular)
val CustomBoldTextStyle = TextStyle().copy(fontFamily = RobotoBold)
val CustomMediumTextStyle = TextStyle().copy(fontFamily = RobotoMedium)
val CustomItalicTextStyle = TextStyle().copy(fontFamily = RobotoItalic)
val CustomBlackTextStyle = TextStyle().copy(fontFamily = RobotoBlack)

// Set of Material typography styles to start with
val Typography = Typography(
    bodyLarge = TextStyle(
        fontFamily = CustomRegularTextStyle.fontFamily,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    )
    /* Other default text styles to override
    titleLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
    */
)

