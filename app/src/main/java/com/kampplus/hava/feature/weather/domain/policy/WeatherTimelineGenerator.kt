package com.kampplus.hava.feature.weather.domain.policy

import com.kampplus.hava.feature.weather.domain.model.Forecast
import com.kampplus.hava.feature.weather.domain.model.TimelineEvent

fun interface WeatherTimelineGenerator {
    fun generate(forecast: Forecast): List<TimelineEvent>
}
