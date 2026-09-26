package com.kampplus.hava.feature.weather.domain.policy

import com.kampplus.hava.feature.weather.domain.model.WeatherCode

class WmoWeatherConditionClassifierTest {

    private val classifier =
        WmoWeatherConditionClassifier()

    @Test
    fun `maps wmo code groups to conditions`() {

        mapOf(
            0 to WeatherCondition.Clear,
            2 to WeatherCondition.PartlyCloudy,
            45 to WeatherCondition.Fog,
            55 to WeatherCondition.Drizzle,
            63 to WeatherCondition.Rain,
            75 to WeatherCondition.Snow,
            81 to WeatherCondition.RainShowers,
            86 to WeatherCondition.SnowShowers,
            99 to WeatherCondition.Thunderstorm
        ).forEach {
                code,
                expected ->

            assertEquals(
                "code $code",
                expected,
                classifier.classify(
                    WeatherCode(code)
                )
            )
        }
    }

    @Test
    fun `unknown code does not crash`() {

        assertEquals(
            WeatherCondition.Unknown,
            classifier.classify(
                WeatherCode(42)
            )
        )
    }
}
