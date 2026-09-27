package com.kampplus.hava.testing

import com.kampplus.hava.feature.weather.domain.policy.DailyWeatherTimelineGenerator
import com.kampplus.hava.feature.weather.domain.policy.RuleBasedWeatherInsightGenerator
import com.kampplus.hava.feature.weather.domain.policy.WmoWeatherConditionClassifier
import com.kampplus.hava.feature.weather.presentation.model.WeatherConditionUiRegistry
import com.kampplus.hava.feature.weather.presentation.model.WeatherUiMapper
import com.kampplus.hava.feature.weather.presentation.visual.WeatherVisualStateResolver

fun testUiMapper(): WeatherUiMapper {
    val classifier = WmoWeatherConditionClassifier()
    return WeatherUiMapper(
        conditionClassifier =
        classifier,
        conditionUiRegistry =
        WeatherConditionUiRegistry(
            emptyMap()
        ),
        visualStateResolver = WeatherVisualStateResolver(
            classifier
        ),
        insightGenerator = RuleBasedWeatherInsightGenerator(classifier),
        timelineGenerator = DailyWeatherTimelineGenerator(classifier)
    )
}
