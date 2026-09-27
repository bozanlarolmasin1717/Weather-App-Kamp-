package com.kampplus.hava.core.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.font.FontWeight

private val base = Typography()

internal val HavaTypography = base.copy(
    displayLarge = base.displayLarge.copy(fontWeight = FontWeight.Light),
    displayMedium = base.displayMedium.copy(fontWeight = FontWeight.Light),
    headlineLarge = base.headlineLarge.copy(fontWeight = FontWeight.SemiBold),
    titleLarge = base.titleLarge.copy(fontWeight = FontWeight.SemiBold),
    titleMedium = base.titleMedium.copy(fontWeight = FontWeight.SemiBold),
    labelLarge = base.labelLarge.copy(fontWeight = FontWeight.SemiBold),
    labelMedium = base.labelMedium.copy(fontWeight = FontWeight.SemiBold)
)
