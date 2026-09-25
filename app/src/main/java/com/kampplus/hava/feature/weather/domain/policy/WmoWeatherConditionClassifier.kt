package com.kampplus.hava.feature.weather.domain.policy

import com.kampplus.hava.feature.weather.domain.model.WeatherCode
import javax.inject.Inject

class WmoWeatherConditionClassifier @Inject constructor() : WeatherConditionClassifier {

    override fun classify(code: WeatherCode): WeatherCondition = when (code.value) {
        0 -> WeatherCondition.Clear
        1 -> WeatherCondition.MainlyClear
        2 -> WeatherCondition.PartlyCloudy
        3 -> WeatherCondition.Overcast

        45, 48 -> WeatherCondition.Fog

        51, 53, 55, 56, 57 -> WeatherCondition.Drizzle

        61, 63, 65, 66, 67 -> WeatherCondition.Rain

        71, 73, 75, 77 -> WeatherCondition.Snow

        80, 81, 82 -> WeatherCondition.RainShowers

        85, 86 -> WeatherCondition.SnowShowers

        95, 96, 99 -> WeatherCondition.Thunderstorm

        else -> WeatherCondition.Unknown
    }
}
