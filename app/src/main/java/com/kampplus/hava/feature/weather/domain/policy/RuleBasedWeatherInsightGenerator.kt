package com.kampplus.hava.feature.weather.domain.policy

import com.kampplus.hava.feature.weather.domain.model.Forecast
import com.kampplus.hava.feature.weather.domain.model.HourlyForecast
import com.kampplus.hava.feature.weather.domain.model.WeatherInsight
import com.kampplus.hava.feature.weather.domain.model.WeatherInsightAdvice
import com.kampplus.hava.feature.weather.domain.model.WeatherInsightHeadline
import com.kampplus.hava.feature.weather.domain.model.WeatherInsightPriority
import com.kampplus.hava.feature.weather.domain.model.WeatherInsightType
import javax.inject.Inject

class RuleBasedWeatherInsightGenerator @Inject constructor(
    private val conditionClassifier: WeatherConditionClassifier
) : WeatherInsightGenerator {

    override fun generate(forecast: Forecast): WeatherInsight? {
        val currentTime = forecast.current.observedAt
        val remainingToday = forecast.hourly
            .asSequence()
            .filter { it.time.toLocalDate() == currentTime.toLocalDate() }
            .filter { !it.time.isBefore(currentTime.withMinute(0).withSecond(0).withNano(0)) }
            .sortedBy { it.time }
            .toList()

        if (remainingToday.isEmpty()) {
            return null
        }

        return thunderstormInsight(remainingToday)
            ?: rainInsight(remainingToday)
            ?: snowInsight(remainingToday)
            ?: windInsight(remainingToday)
            ?: coolingInsight(forecast, remainingToday)
            ?: warmAndClearInsight(remainingToday)
    }

    private fun thunderstormInsight(hours: List<HourlyForecast>): WeatherInsight? {
        val first = hours.firstOrNull {
            conditionClassifier.classify(it.weatherCode) == WeatherCondition.Thunderstorm
        } ?: return null

        return insight(
            type = WeatherInsightType.Thunderstorm,
            priority = WeatherInsightPriority.Critical,
            headline = WeatherInsightHeadline.ThunderstormsExpected,
            advice = WeatherInsightAdvice.AvoidExposedAreas,
            first = first,
            hours = hours,
            matching = {
                conditionClassifier.classify(it.weatherCode) == WeatherCondition.Thunderstorm
            }
        )
    }

    private fun rainInsight(hours: List<HourlyForecast>): WeatherInsight? {
        val first = hours.firstOrNull(::isRainExpected) ?: return null

        return insight(
            type = WeatherInsightType.Rain,
            priority = WeatherInsightPriority.High,
            headline = WeatherInsightHeadline.RainExpected,
            advice = WeatherInsightAdvice.TakeUmbrella,
            first = first,
            hours = hours,
            matching = ::isRainExpected
        )
    }

    private fun snowInsight(hours: List<HourlyForecast>): WeatherInsight? {
        val first = hours.firstOrNull {
            conditionClassifier.classify(it.weatherCode) in SNOW_CONDITIONS
        } ?: return null

        return insight(
            type = WeatherInsightType.Snow,
            priority = WeatherInsightPriority.High,
            headline = WeatherInsightHeadline.SnowExpected,
            advice = WeatherInsightAdvice.AllowExtraTravelTime,
            first = first,
            hours = hours,
            matching = {
                conditionClassifier.classify(it.weatherCode) in SNOW_CONDITIONS
            }
        )
    }

    private fun windInsight(hours: List<HourlyForecast>): WeatherInsight? {
        val first = hours.firstOrNull {
            (it.windSpeedKmh ?: 0.0) >= STRONG_WIND_KMH
        } ?: return null

        return insight(
            type = WeatherInsightType.StrongWind,
            priority = WeatherInsightPriority.High,
            headline = WeatherInsightHeadline.StrongWindsExpected,
            advice = WeatherInsightAdvice.SecureLooseItems,
            first = first,
            hours = hours,
            matching = {
                (it.windSpeedKmh ?: 0.0) >= STRONG_WIND_KMH
            }
        )
    }

    private fun coolingInsight(forecast: Forecast, hours: List<HourlyForecast>): WeatherInsight? {
        val baseline = forecast.current.apparentTemperatureC
            ?: forecast.current.temperatureC
        val sunset = forecast.daily
            .firstOrNull { it.date == forecast.current.observedAt.toLocalDate() }
            ?.sunset
        val eveningStart = sunset ?: forecast.current.observedAt
            .toLocalDate()
            .atTime(EVENING_HOUR, 0)
        val first = hours.firstOrNull {
            !it.time.isBefore(eveningStart) &&
                baseline - (it.apparentTemperatureC ?: it.temperatureC) >= COOLING_DROP_C
        } ?: return null

        return WeatherInsight(
            type = WeatherInsightType.Cooling,
            priority = WeatherInsightPriority.Medium,
            headline = WeatherInsightHeadline.ColderThisEvening,
            advice = WeatherInsightAdvice.TakeLightJacket,
            relevantFrom = first.time
        )
    }

    private fun warmAndClearInsight(hours: List<HourlyForecast>): WeatherInsight? {
        val daytime = hours.filter { it.isDay == true }
        if (daytime.isEmpty()) {
            return null
        }

        val clearCount = daytime.count {
            conditionClassifier.classify(it.weatherCode) in CLEAR_CONDITIONS
        }
        val isMostlyClear = clearCount.toDouble() / daytime.size >= MOSTLY_CLEAR_RATIO
        val isWarm = daytime.maxOf { it.temperatureC } >= WARM_TEMPERATURE_C
        val staysDry = daytime.none(::isRainExpected)

        if (!isMostlyClear || !isWarm || !staysDry) {
            return null
        }

        return WeatherInsight(
            type = WeatherInsightType.WarmAndClear,
            priority = WeatherInsightPriority.Low,
            headline = WeatherInsightHeadline.WarmAndClearDay,
            advice = WeatherInsightAdvice.GoodForOutdoorPlans,
            relevantFrom = daytime.first().time,
            relevantTo = daytime.last().time
        )
    }

    private fun isRainExpected(hour: HourlyForecast): Boolean {
        val condition = conditionClassifier.classify(hour.weatherCode)
        if (condition in SNOW_CONDITIONS) {
            return false
        }

        return condition in RAIN_CONDITIONS ||
            (hour.precipitationProbability ?: 0) >= RAIN_PROBABILITY_PERCENT ||
            (hour.precipitationMm ?: 0.0) >= MEASURABLE_RAIN_MM
    }

    private fun insight(
        type: WeatherInsightType,
        priority: WeatherInsightPriority,
        headline: WeatherInsightHeadline,
        advice: WeatherInsightAdvice,
        first: HourlyForecast,
        hours: List<HourlyForecast>,
        matching: (HourlyForecast) -> Boolean
    ): WeatherInsight {
        val last = hours
            .dropWhile { it.time < first.time }
            .takeWhile(matching)
            .lastOrNull()

        return WeatherInsight(
            type = type,
            priority = priority,
            headline = headline,
            advice = advice,
            relevantFrom = first.time,
            relevantTo = last?.time
        )
    }

    companion object {
        const val RAIN_PROBABILITY_PERCENT = 60
        const val MEASURABLE_RAIN_MM = 0.2
        const val STRONG_WIND_KMH = 40.0
        const val COOLING_DROP_C = 5.0
        const val WARM_TEMPERATURE_C = 22.0
        const val MOSTLY_CLEAR_RATIO = 0.6
        const val EVENING_HOUR = 17

        private val RAIN_CONDITIONS = setOf(
            WeatherCondition.Drizzle,
            WeatherCondition.Rain,
            WeatherCondition.RainShowers
        )
        private val SNOW_CONDITIONS = setOf(
            WeatherCondition.Snow,
            WeatherCondition.SnowShowers
        )
        private val CLEAR_CONDITIONS = setOf(
            WeatherCondition.Clear,
            WeatherCondition.MainlyClear,
            WeatherCondition.PartlyCloudy
        )
    }
}
