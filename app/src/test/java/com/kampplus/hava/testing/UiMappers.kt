package com.kampplus.hava.testing

import com.kampplus.hava.feature.weather.domain.policy.WmoWeatherConditionClassifier
import com.kampplus.hava.feature.weather.presentation.model.WeatherConditionUiRegistry
import com.kampplus.hava.feature.weather.presentation.model.WeatherUiMapper
import com.kampplus.hava.feature.weather.presentation.visual.WeatherVisualStateResolver

fun testUiMapper() = WeatherUiMapper(
    conditionClassifier =
    WmoWeatherConditionClassifier(),
    conditionUiRegistry =
    WeatherConditionUiRegistry(
        emptyMap()
    ),
    visualStateResolver = WeatherVisualStateResolver(
        WmoWeatherConditionClassifier()
    )
)
