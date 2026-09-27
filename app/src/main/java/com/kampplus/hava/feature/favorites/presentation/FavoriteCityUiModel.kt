package com.kampplus.hava.feature.favorites.presentation

import com.kampplus.hava.core.ui.text.UiText
import com.kampplus.hava.feature.weather.presentation.visual.WeatherVisualState

data class FavoriteCityUiModel(
    val id: Long,
    val title: String,
    val subtitle: String,
    val temperatureText: String? = null,
    val conditionEmoji: String? = null,
    val conditionLabel: UiText? = null,
    val visualState: WeatherVisualState = WeatherVisualState.ClearNight
)
