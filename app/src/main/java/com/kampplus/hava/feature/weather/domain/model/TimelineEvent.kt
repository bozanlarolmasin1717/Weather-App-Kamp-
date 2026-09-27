package com.kampplus.hava.feature.weather.domain.model

import java.time.LocalDateTime

data class TimelineEvent(
    val time: LocalDateTime,
    val type: TimelineEventType,
    val headline: TimelineEventHeadline,
    val advice: TimelineEventAdvice? = null,
    val apparentTemperatureC: Double? = null
)

enum class TimelineEventType {
    RainBegins,
    RainEases,
    OngoingRain,
    TemperatureRise,
    TemperatureDrop,
    StrongWindBegins,
    EveningCooling
}

enum class TimelineEventHeadline {
    RainBegins,
    RainEasing,
    RainContinuing,
    GettingWarmer,
    GettingCooler,
    StrongWindsBegin,
    CoolingAfterSunset
}

enum class TimelineEventAdvice {
    TakeUmbrella,
    ConditionsImproving,
    KeepRainProtectionReady,
    DressForWarmerConditions,
    ConsiderALightLayer,
    SecureLooseItems
}
