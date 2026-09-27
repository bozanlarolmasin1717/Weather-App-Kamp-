package com.kampplus.hava.feature.weather.presentation.model

import com.kampplus.hava.core.ui.text.UiText
import com.kampplus.hava.feature.weather.presentation.visual.WeatherVisualState

data class CityWeatherUiModel(
    val cityId: Long,
    val title: String,
    val subtitle: String,
    val temperatureText: String,
    val temperatureC: Double,
    val conditionEmoji: String,
    val conditionLabel: UiText,
    val visualState: WeatherVisualState = WeatherVisualState.Cloudy,
    val isFavorite: Boolean = false
)
