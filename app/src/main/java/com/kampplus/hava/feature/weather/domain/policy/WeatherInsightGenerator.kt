package com.kampplus.hava.feature.weather.domain.policy

import com.kampplus.hava.feature.weather.domain.model.Forecast
import com.kampplus.hava.feature.weather.domain.model.WeatherInsight

fun interface WeatherInsightGenerator {
    fun generate(forecast: Forecast): WeatherInsight?
}
