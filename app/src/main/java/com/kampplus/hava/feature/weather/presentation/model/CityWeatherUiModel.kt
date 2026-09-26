package com.kampplus.hava.feature.weather.presentation.model

import com.kampplus.hava.core.ui.text.UiText

/** Liste satırında gösterilmeye hazır, biçimlendirilmiş şehir havası. */
data class CityWeatherUiModel(
    val cityId: Long,
    val title: String,
    val subtitle: String,
    val temperatureText: String,
    val temperatureC: Double,
    val conditionEmoji: String,
    val conditionLabel: UiText,
    val isFavorite: Boolean = false
)
