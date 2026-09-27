package com.kampplus.hava.feature.weather.domain.model

import java.time.LocalDateTime

data class WeatherInsight(
    val type: WeatherInsightType,
    val priority: WeatherInsightPriority,
    val headline: WeatherInsightHeadline,
    val advice: WeatherInsightAdvice? = null,
    val relevantFrom: LocalDateTime? = null,
    val relevantTo: LocalDateTime? = null
)

enum class WeatherInsightType {
    Thunderstorm,
    Rain,
    Snow,
    StrongWind,
    Cooling,
    WarmAndClear
}

enum class WeatherInsightPriority {
    Critical,
    High,
    Medium,
    Low
}

enum class WeatherInsightHeadline {
    ThunderstormsExpected,
    RainExpected,
    SnowExpected,
    StrongWindsExpected,
    ColderThisEvening,
    WarmAndClearDay
}

enum class WeatherInsightAdvice {
    AvoidExposedAreas,
    TakeUmbrella,
    AllowExtraTravelTime,
    SecureLooseItems,
    TakeLightJacket,
    GoodForOutdoorPlans
}
