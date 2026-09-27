package com.kampplus.hava.feature.weather.presentation.visual

import com.kampplus.hava.feature.weather.domain.model.WeatherCode
import com.kampplus.hava.feature.weather.domain.policy.WeatherCondition
import com.kampplus.hava.feature.weather.domain.policy.WeatherConditionClassifier
import java.time.LocalDateTime
import javax.inject.Inject

class WeatherVisualStateResolver @Inject constructor(
    private val classifier: WeatherConditionClassifier
) {

    fun resolve(weatherCode: WeatherCode, isDay: Boolean?, cityLocalTime: LocalDateTime): WeatherVisualState {
        val condition = classifier.classify(weatherCode)

        return when (condition) {
            WeatherCondition.Thunderstorm -> WeatherVisualState.Thunderstorm
            WeatherCondition.Rain,
            WeatherCondition.RainShowers,
            WeatherCondition.Drizzle -> WeatherVisualState.Rainy
            WeatherCondition.Snow,
            WeatherCondition.SnowShowers -> WeatherVisualState.Snowy
            WeatherCondition.Fog -> WeatherVisualState.Foggy
            WeatherCondition.Clear,
            WeatherCondition.MainlyClear,
            WeatherCondition.PartlyCloudy -> resolveClearState(isDay, cityLocalTime)
            WeatherCondition.Overcast,
            WeatherCondition.Unknown -> WeatherVisualState.Cloudy
        }
    }

    private fun resolveClearState(isDay: Boolean?, cityLocalTime: LocalDateTime): WeatherVisualState = when {
        isDay == true -> WeatherVisualState.ClearSunny
        isDay == false && cityLocalTime.hour in EVENING_HOURS -> WeatherVisualState.SunsetEvening
        isDay == false -> WeatherVisualState.ClearNight
        else -> WeatherVisualState.Cloudy
    }

    private companion object {
        val EVENING_HOURS = 16..20
    }
}
