package com.kampplus.hava.feature.weather.presentation.model

import androidx.compose.ui.graphics.Color
import com.kampplus.hava.core.ui.theme.TemperaturePalette

fun temperatureColor(celsius: Double): Color = when {
    celsius < 0 -> TemperaturePalette.Freezing
    celsius < 12 -> TemperaturePalette.Cold
    celsius < 22 -> TemperaturePalette.Mild
    celsius < 30 -> TemperaturePalette.Warm
    else -> TemperaturePalette.Hot
}
