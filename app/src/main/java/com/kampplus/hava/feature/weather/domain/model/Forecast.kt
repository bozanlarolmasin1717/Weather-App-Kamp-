package com.kampplus.hava.feature.weather.domain.model

import java.time.LocalDate
import java.time.LocalDateTime

data class Forecast(
    val current: CurrentWeather,
    val hourly: List<HourlyForecast>,
    val daily: List<DailyForecast>,
    val timeZoneId: String? = null,
    val utcOffsetSeconds: Int? = null
)

data class HourlyForecast(
    val time: LocalDateTime,
    val temperatureC: Double,
    val weatherCode: WeatherCode,
    val precipitationProbability: Int? = null,
    val apparentTemperatureC: Double? = null,
    val windSpeedKmh: Double? = null,
    val precipitationMm: Double? = null,
    val isDay: Boolean? = null
)

data class DailyForecast(
    val date: LocalDate,
    val minTemperatureC: Double,
    val maxTemperatureC: Double,
    val weatherCode: WeatherCode,
    val precipitationProbability: Int? = null,
    val sunrise: LocalDateTime? = null,
    val sunset: LocalDateTime? = null
)
