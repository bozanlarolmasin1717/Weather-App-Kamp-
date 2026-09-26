package com.kampplus.hava.feature.weather.presentation.model

import com.kampplus.hava.core.ui.text.UiText

data class ForecastUiModel(
    val cityId: Long,
    val cityName: String,
    val subtitle: String,
    val temperatureText: String,
    val temperatureC: Double,
    val conditionEmoji: String,
    val conditionLabel: UiText,
    val feelsLikeText: String?,
    val humidityText: String?,
    val windText: String?,
    val hourly: List<HourlyUiModel>,
    val daily: List<DailyUiModel>
)

data class HourlyUiModel(
    val timeText: String,
    val emoji: String,
    val temperatureText: String,
    val precipitationText: String?
)

data class DailyUiModel(
    val dayLabel: UiText,
    val emoji: String,
    val minText: String,
    val maxText: String,
    val precipitationText: String?
)
