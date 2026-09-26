package com.kampplus.hava.feature.weather.domain.policy

import com.kampplus.hava.feature.weather.domain.model.WeatherCode

/**
 * WMO hava kodunu uygulamanın kullandığı hava koşulu durumuna çeviren kural sözleşmesi.
 */
fun interface WeatherConditionClassifier {
    fun classify(code: WeatherCode): WeatherCondition
}

enum class WeatherCondition {
    Clear,
    MainlyClear,
    PartlyCloudy,
    Overcast,
    Fog,
    Drizzle,
    Rain,
    Snow,
    RainShowers,
    SnowShowers,
    Thunderstorm,
    Unknown
}
