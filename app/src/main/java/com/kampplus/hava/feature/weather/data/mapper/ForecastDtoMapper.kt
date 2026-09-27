package com.kampplus.hava.feature.weather.data.mapper

import com.kampplus.hava.feature.weather.data.remote.dto.CurrentDto
import com.kampplus.hava.feature.weather.data.remote.dto.DailyDto
import com.kampplus.hava.feature.weather.data.remote.dto.ForecastResponseDto
import com.kampplus.hava.feature.weather.data.remote.dto.HourlyDto
import com.kampplus.hava.feature.weather.domain.model.CurrentWeather
import com.kampplus.hava.feature.weather.domain.model.DailyForecast
import com.kampplus.hava.feature.weather.domain.model.Forecast
import com.kampplus.hava.feature.weather.domain.model.HourlyForecast
import com.kampplus.hava.feature.weather.domain.model.WeatherCode
import java.time.LocalDate
import java.time.LocalDateTime
import kotlinx.serialization.SerializationException

fun ForecastResponseDto.requireCurrent(): CurrentDto = current
    ?: throw SerializationException(
        "Forecast response has no current block"
    )

fun ForecastResponseDto.toForecast(): Forecast = Forecast(
    current =
    requireCurrent()
        .toDomain(),
    hourly =
    hourly
        ?.toDomain()
        .orEmpty(),
    daily =
    daily
        ?.toDomain()
        .orEmpty(),
    timeZoneId = timezone,
    utcOffsetSeconds = utcOffsetSeconds
)

fun CurrentDto.toDomain() = CurrentWeather(
    temperatureC =
    temperature,
    weatherCode =
    WeatherCode(
        weatherCode
    ),
    observedAt =
    LocalDateTime.parse(
        time
    ),
    apparentTemperatureC =
    apparentTemperature,
    humidityPercent =
    relativeHumidity,
    windSpeedKmh =
    windSpeed,
    isDay =
    isDay?.let {
        it != 0
    }
)

fun HourlyDto.toDomain(): List<HourlyForecast> = time.indices
    .mapNotNull { index ->

        val temperature =
            temperature
                .getOrNull(index)
                ?: return@mapNotNull null

        val code =
            weatherCode
                .getOrNull(index)
                ?: return@mapNotNull null

        val parsedTime =
            time
                .getOrNull(index)
                ?.let(::parseDateTimeOrNull)
                ?: return@mapNotNull null

        HourlyForecast(
            time =
            parsedTime,
            temperatureC =
            temperature,
            weatherCode =
            WeatherCode(code),
            precipitationProbability =
            precipitationProbability
                .getOrNull(index),
            apparentTemperatureC =
            apparentTemperature
                .getOrNull(index),
            windSpeedKmh =
            windSpeed
                .getOrNull(index),
            precipitationMm =
            precipitation
                .getOrNull(index),
            isDay =
            isDay
                .getOrNull(index)
                ?.let {
                    it != 0
                }
        )
    }

fun DailyDto.toDomain(): List<DailyForecast> = time.indices
    .mapNotNull { index ->

        val max =
            temperatureMax
                .getOrNull(index)
                ?: return@mapNotNull null

        val min =
            temperatureMin
                .getOrNull(index)
                ?: return@mapNotNull null

        val code =
            weatherCode
                .getOrNull(index)
                ?: return@mapNotNull null

        val parsedDate =
            time
                .getOrNull(index)
                ?.let(::parseDateOrNull)
                ?: return@mapNotNull null

        DailyForecast(
            date =
            parsedDate,
            minTemperatureC =
            min,
            maxTemperatureC =
            max,
            weatherCode =
            WeatherCode(code),
            precipitationProbability =
            precipitationProbabilityMax
                .getOrNull(index),
            sunrise =
            sunrise
                .getOrNull(index)
                ?.let(::parseDateTimeOrNull),
            sunset =
            sunset
                .getOrNull(index)
                ?.let(::parseDateTimeOrNull)
        )
    }

private fun parseDateTimeOrNull(value: String): LocalDateTime? = runCatching {
    LocalDateTime.parse(value)
}.getOrNull()

private fun parseDateOrNull(value: String): LocalDate? = runCatching {
    LocalDate.parse(value)
}.getOrNull()
