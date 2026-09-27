package com.kampplus.hava.feature.weather.domain.policy

import com.kampplus.hava.feature.weather.domain.model.Forecast
import com.kampplus.hava.feature.weather.domain.model.HourlyForecast
import com.kampplus.hava.feature.weather.domain.model.TimelineEvent
import com.kampplus.hava.feature.weather.domain.model.TimelineEventAdvice
import com.kampplus.hava.feature.weather.domain.model.TimelineEventHeadline
import com.kampplus.hava.feature.weather.domain.model.TimelineEventType
import java.time.Duration
import java.time.LocalDateTime
import javax.inject.Inject

class DailyWeatherTimelineGenerator @Inject constructor(
    private val conditionClassifier: WeatherConditionClassifier
) : WeatherTimelineGenerator {

    override fun generate(forecast: Forecast): List<TimelineEvent> {
        val currentTime = forecast.current.observedAt
        val hours = forecast.hourly
            .asSequence()
            .filter { it.time.toLocalDate() == currentTime.toLocalDate() }
            .filter { !it.time.isBefore(currentTime.withMinute(0).withSecond(0).withNano(0)) }
            .sortedBy { it.time }
            .distinctBy { it.time }
            .toList()

        if (hours.isEmpty()) {
            return emptyList()
        }

        val candidates = buildList {
            addAll(rainEvents(forecast, hours))
            addAll(windEvents(forecast, hours))
            addAll(temperatureEvents(hours))
            eveningEvent(forecast, hours)?.let(::add)
        }

        return candidates
            .sortedWith(compareBy<TimelineEvent> { it.time }.thenByDescending(::priority))
            .fold(emptyList<TimelineEvent>()) { accepted, candidate ->
                if (accepted.any { isDuplicate(it, candidate) }) {
                    accepted
                } else {
                    accepted + candidate
                }
            }
            .let(::preferSpecificEveningEvent)
            .take(MAX_EVENTS)
    }

    private fun rainEvents(forecast: Forecast, hours: List<HourlyForecast>): List<TimelineEvent> = buildList {
        val first = hours.first()
        val currentRain = conditionClassifier.classify(forecast.current.weatherCode) in RAIN_CONDITIONS
        val firstRain = isRain(first)

        if (firstRain && isNear(forecast.current.observedAt, first.time)) {
            add(
                rainEvent(
                    time = first.time,
                    ongoing = currentRain
                )
            )
        }

        hours.zipWithNext().forEach { (previous, current) ->
            if (!isNear(previous.time, current.time)) {
                return@forEach
            }

            val wasRaining = isRain(previous)
            val isRaining = isRain(current)
            when {
                !wasRaining && isRaining -> add(rainEvent(current.time))
                wasRaining && !isRaining -> add(rainEasingEvent(current.time))
            }
        }
    }

    private fun windEvents(forecast: Forecast, hours: List<HourlyForecast>): List<TimelineEvent> {
        val events = mutableListOf<TimelineEvent>()
        var previousTime = forecast.current.observedAt
        var previousWind = forecast.current.windSpeedKmh

        hours.forEach { hour ->
            val wind = hour.windSpeedKmh
            if (
                isNear(previousTime, hour.time) &&
                previousWind != null &&
                wind != null &&
                previousWind < STRONG_WIND_KMH &&
                wind >= STRONG_WIND_KMH
            ) {
                events += TimelineEvent(
                    time = hour.time,
                    type = TimelineEventType.StrongWindBegins,
                    headline = TimelineEventHeadline.StrongWindsBegin,
                    advice = TimelineEventAdvice.SecureLooseItems
                )
            }

            previousTime = hour.time
            previousWind = wind
        }

        return events
    }

    private fun temperatureEvents(hours: List<HourlyForecast>): List<TimelineEvent> {
        val events = mutableListOf<TimelineEvent>()

        hours.forEachIndexed { index, current ->
            val earlierIndex = (0 until index)
                .firstOrNull { earlierIndex ->
                    val duration = Duration.between(hours[earlierIndex].time, current.time)
                    duration > Duration.ZERO && duration <= TEMPERATURE_WINDOW
                } ?: return@forEachIndexed
            val earlier = hours[earlierIndex]
            val segment = hours.subList(earlierIndex, index + 1)
            if (segment.zipWithNext().any { !isNear(it.first.time, it.second.time) }) {
                return@forEachIndexed
            }

            val earlierFeels = feelsLike(earlier)
            val currentFeels = feelsLike(current)
            val change = currentFeels - earlierFeels
            if (kotlin.math.abs(change) < TEMPERATURE_CHANGE_C) {
                return@forEachIndexed
            }

            events += TimelineEvent(
                time = current.time,
                type = if (change > 0) {
                    TimelineEventType.TemperatureRise
                } else {
                    TimelineEventType.TemperatureDrop
                },
                headline = if (change > 0) {
                    TimelineEventHeadline.GettingWarmer
                } else {
                    TimelineEventHeadline.GettingCooler
                },
                advice = if (change > 0) {
                    TimelineEventAdvice.DressForWarmerConditions
                } else {
                    TimelineEventAdvice.ConsiderALightLayer
                },
                apparentTemperatureC = currentFeels
            )
        }

        return events
    }

    private fun eveningEvent(forecast: Forecast, hours: List<HourlyForecast>): TimelineEvent? {
        val sunset = forecast.daily
            .firstOrNull { it.date == forecast.current.observedAt.toLocalDate() }
            ?.sunset
            ?: return null
        val afterSunsetIndex = hours.indexOfFirst { !it.time.isBefore(sunset) }
        if (afterSunsetIndex <= 0) {
            return null
        }

        val before = hours[afterSunsetIndex - 1]
        val after = hours[afterSunsetIndex]
        if (!isNear(before.time, after.time) || feelsLike(before) - feelsLike(after) < EVENING_COOLING_C) {
            return null
        }

        return TimelineEvent(
            time = after.time,
            type = TimelineEventType.EveningCooling,
            headline = TimelineEventHeadline.CoolingAfterSunset,
            advice = TimelineEventAdvice.ConsiderALightLayer,
            apparentTemperatureC = feelsLike(after)
        )
    }

    private fun preferSpecificEveningEvent(events: List<TimelineEvent>): List<TimelineEvent> {
        val evening = events.firstOrNull { it.type == TimelineEventType.EveningCooling }
            ?: return events
        return events.filterNot {
            it.type == TimelineEventType.TemperatureDrop &&
                kotlin.math.abs(Duration.between(it.time, evening.time).toHours()) <= DUPLICATE_HOURS
        }
    }

    private fun isDuplicate(existing: TimelineEvent, candidate: TimelineEvent): Boolean {
        val sameFamily = existing.type == candidate.type ||
            existing.type in TEMPERATURE_TYPES && candidate.type in TEMPERATURE_TYPES
        return sameFamily &&
            kotlin.math.abs(Duration.between(existing.time, candidate.time).toHours()) < DUPLICATE_HOURS
    }

    private fun rainEvent(time: LocalDateTime, ongoing: Boolean = false) = TimelineEvent(
        time = time,
        type = if (ongoing) TimelineEventType.OngoingRain else TimelineEventType.RainBegins,
        headline = if (ongoing) {
            TimelineEventHeadline.RainContinuing
        } else {
            TimelineEventHeadline.RainBegins
        },
        advice = if (ongoing) {
            TimelineEventAdvice.KeepRainProtectionReady
        } else {
            TimelineEventAdvice.TakeUmbrella
        }
    )

    private fun rainEasingEvent(time: LocalDateTime) = TimelineEvent(
        time = time,
        type = TimelineEventType.RainEases,
        headline = TimelineEventHeadline.RainEasing,
        advice = TimelineEventAdvice.ConditionsImproving
    )

    private fun isRain(hour: HourlyForecast): Boolean {
        val condition = conditionClassifier.classify(hour.weatherCode)
        return condition in RAIN_CONDITIONS ||
            (
                condition !in SNOW_CONDITIONS &&
                    (
                        (hour.precipitationProbability ?: 0) >= RAIN_PROBABILITY_PERCENT ||
                            (hour.precipitationMm ?: 0.0) >= MEASURABLE_RAIN_MM
                        )
                )
    }

    private fun feelsLike(hour: HourlyForecast) = hour.apparentTemperatureC
        ?: hour.temperatureC

    private fun isNear(first: LocalDateTime, second: LocalDateTime): Boolean {
        val gap = Duration.between(first, second).abs()
        return gap <= MAX_FORECAST_GAP && gap >= Duration.ZERO
    }

    private fun priority(event: TimelineEvent): Int = when (event.type) {
        TimelineEventType.RainBegins,
        TimelineEventType.OngoingRain -> 4
        TimelineEventType.StrongWindBegins -> 3
        TimelineEventType.RainEases,
        TimelineEventType.EveningCooling -> 2
        TimelineEventType.TemperatureRise,
        TimelineEventType.TemperatureDrop -> 1
    }

    companion object {
        const val RAIN_PROBABILITY_PERCENT = 60
        const val MEASURABLE_RAIN_MM = 0.2
        const val STRONG_WIND_KMH = 40.0
        const val TEMPERATURE_CHANGE_C = 5.0
        const val EVENING_COOLING_C = 2.0
        const val DUPLICATE_HOURS = 3L
        const val MAX_EVENTS = 4

        private val MAX_FORECAST_GAP = Duration.ofHours(2)
        private val TEMPERATURE_WINDOW = Duration.ofHours(3)
        private val RAIN_CONDITIONS = setOf(
            WeatherCondition.Drizzle,
            WeatherCondition.Rain,
            WeatherCondition.RainShowers,
            WeatherCondition.Thunderstorm
        )
        private val SNOW_CONDITIONS = setOf(
            WeatherCondition.Snow,
            WeatherCondition.SnowShowers
        )
        private val TEMPERATURE_TYPES = setOf(
            TimelineEventType.TemperatureRise,
            TimelineEventType.TemperatureDrop
        )
    }
}
