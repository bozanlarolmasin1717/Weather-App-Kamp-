package com.kampplus.hava.feature.weather.presentation.model

import com.kampplus.hava.core.ui.text.UiText
import com.kampplus.hava.feature.weather.presentation.visual.WeatherVisualState
import java.time.LocalDate
import java.time.LocalDateTime

data class ForecastUiModel(
    val cityId: Long,
    val cityName: String,
    val subtitle: String,
    val temperatureText: String,
    val temperatureC: Double,
    val conditionEmoji: String,
    val conditionLabel: UiText,
    val isDay: Boolean?,
    val timeZoneId: String?,
    val visualState: WeatherVisualState = WeatherVisualState.Cloudy,
    val feelsLikeText: String?,
    val humidityText: String?,
    val windText: String?,
    val highText: String? = null,
    val lowText: String? = null,
    val sunriseText: String? = null,
    val sunsetText: String? = null,
    val insight: WeatherInsightUiModel? = null,
    val timeline: List<TimelineEventUiModel> = emptyList(),
    val hourly: List<HourlyUiModel>,
    val daily: List<DailyUiModel>,
    val isFavorite: Boolean = false
)

data class WeatherInsightUiModel(
    val headline: UiText,
    val advice: UiText?,
    val timeText: String?
)

data class TimelineEventUiModel(
    val time: LocalDateTime,
    val timeText: String,
    val headline: UiText,
    val detail: UiText?
)

data class HourlyUiModel(
    val time: LocalDateTime,
    val timeText: String,
    val emoji: String,
    val conditionLabel: UiText? = null,
    val temperatureText: String,
    val precipitationText: String?
)

data class DailyUiModel(
    val date: LocalDate,
    val dateText: String = "",
    val dayLabel: UiText,
    val emoji: String,
    val conditionLabel: UiText? = null,
    val minText: String,
    val maxText: String,
    val precipitationText: String?
)
