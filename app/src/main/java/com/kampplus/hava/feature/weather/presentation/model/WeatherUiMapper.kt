package com.kampplus.hava.feature.weather.presentation.model

import com.kampplus.hava.R
import com.kampplus.hava.core.ui.text.UiText
import com.kampplus.hava.feature.weather.domain.model.City
import com.kampplus.hava.feature.weather.domain.model.CityWeather
import com.kampplus.hava.feature.weather.domain.model.Forecast
import com.kampplus.hava.feature.weather.domain.model.WeatherCode
import com.kampplus.hava.feature.weather.domain.policy.WeatherConditionClassifier
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Locale
import javax.inject.Inject
import kotlin.math.roundToInt

class WeatherUiMapper @Inject constructor(
    private val conditionClassifier: WeatherConditionClassifier,
    private val conditionUiRegistry: WeatherConditionUiRegistry
) {
    fun toListItem(cityWeather: CityWeather, isFavorite: Boolean = false): CityWeatherUiModel = with(cityWeather) {
        val condition = conditionClassifier.classify(current.weatherCode)
        val conditionUi = conditionUiRegistry.resolve(condition)
        CityWeatherUiModel(
            cityId = city.id,
            title = city.name,
            subtitle = subtitle(city),
            temperatureText = degrees(current.temperatureC),
            temperatureC = current.temperatureC,
            conditionEmoji = conditionUi.emoji,
            conditionLabel = conditionUi.label,
            isFavorite = isFavorite,
            condition = condition
        )
    }

    fun toForecast(city: City, forecast: Forecast, isFavorite: Boolean = false): ForecastUiModel = with(forecast) {
        val condition = conditionClassifier.classify(current.weatherCode)
        val conditionUi = conditionUiRegistry.resolve(condition)
        val currentHour = current.observedAt.truncatedTo(ChronoUnit.HOURS)
        ForecastUiModel(
            cityId = city.id,
            cityName = city.name,
            subtitle = subtitle(city),
            temperatureText = degrees(current.temperatureC),
            temperatureC = current.temperatureC,
            conditionEmoji = conditionUi.emoji,
            conditionLabel = conditionUi.label,
            feelsLikeText = current.apparentTemperatureC?.let(::degrees),
            humidityText = current.humidityPercent?.let { "%$it" },
            windText = current.windSpeedKmh?.let { "${it.roundToInt()} km/sa" },
            hourly = hourly.filter { !it.time.isBefore(currentHour) }.take(HOURLY_COUNT).map { hour ->
                val hourCondition = conditionClassifier.classify(hour.weatherCode)
                HourlyUiModel(
                    timeText = hour.time.format(HOUR_FORMAT),
                    emoji = conditionUiRegistry.resolve(hourCondition).emoji,
                    temperatureText = degrees(hour.temperatureC),
                    precipitationText = percent(hour.precipitationProbability)
                )
            },
            daily = daily.mapIndexed { index, day ->
                val dayCondition = conditionClassifier.classify(day.weatherCode)
                DailyUiModel(
                    dayLabel = if (index == 0) {
                        UiText.Resource(R.string.today)
                    } else {
                        UiText.Dynamic(day.date.format(DAY_FORMAT).replaceFirstChar(Char::titlecase))
                    },
                    emoji = conditionUiRegistry.resolve(dayCondition).emoji,
                    minText = degrees(day.minTemperatureC),
                    maxText = degrees(day.maxTemperatureC),
                    precipitationText = percent(day.precipitationProbability)
                )
            },
            isFavorite = isFavorite,
            condition = condition
        )
    }

    private fun subtitle(city: City) = listOfNotNull(city.region, city.country).distinct().joinToString(", ")

    private fun degrees(celsius: Double) = "${celsius.roundToInt()}°"

    private fun percent(value: Int?) = value?.takeIf { it > 0 }?.let { "%$it" }

    private companion object {
        const val HOURLY_COUNT = 24
        val HOUR_FORMAT: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm")
        val DAY_FORMAT: DateTimeFormatter = DateTimeFormatter.ofPattern("EEEE", Locale.forLanguageTag("tr"))
    }
}
