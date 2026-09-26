package com.kampplus.hava.feature.weather.domain.policy

import com.kampplus.hava.feature.weather.domain.model.WeatherCode

/** Ham hava kodunu uygulamanın konuştuğu hava koşuluna eşleyen iş kuralı (strategy). */
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
