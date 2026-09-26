package com.kampplus.hava.feature.weather.presentation.model

import com.kampplus.hava.core.ui.text.UiText

/**
 * Liste ekranında gösterilecek biçimlendirilmiş şehir hava durumu UI modeli.
 */
data class CityWeatherUiModel(
    val cityId: Long,
    val title: String,
    val subtitle: String,
    val temperatureText: String,
    val temperatureC: Double,
    val conditionEmoji: String,
    val conditionLabel: UiText
)
