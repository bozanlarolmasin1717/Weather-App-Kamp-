package com.kampplus.hava.feature.weather.presentation.visual

import com.kampplus.hava.feature.weather.domain.model.WeatherCode
import com.kampplus.hava.feature.weather.domain.policy.WmoWeatherConditionClassifier
import java.time.LocalDateTime
import org.junit.Assert.assertEquals
import org.junit.Test

class WeatherVisualStateResolverTest {

    private val resolver = WeatherVisualStateResolver(
        WmoWeatherConditionClassifier()
    )
    private val noon = LocalDateTime.of(2026, 9, 24, 12, 0)

    @Test
    fun `weather families map to atmospheric states`() {
        mapOf(
            3 to WeatherVisualState.Cloudy,
            61 to WeatherVisualState.Rainy,
            73 to WeatherVisualState.Snowy,
            45 to WeatherVisualState.Foggy,
            95 to WeatherVisualState.Thunderstorm
        ).forEach { (code, expected) ->
            assertEquals(expected, resolver.resolve(WeatherCode(code), true, noon))
        }
    }

    @Test
    fun `clear condition follows explicit day night and evening state`() {
        assertEquals(
            WeatherVisualState.ClearSunny,
            resolver.resolve(WeatherCode(0), true, noon)
        )
        assertEquals(
            WeatherVisualState.SunsetEvening,
            resolver.resolve(WeatherCode(0), false, noon.withHour(19))
        )
        assertEquals(
            WeatherVisualState.ClearNight,
            resolver.resolve(WeatherCode(0), false, noon.withHour(23))
        )
    }

    @Test
    fun `missing day state never defaults to sunny`() {
        assertEquals(
            WeatherVisualState.Cloudy,
            resolver.resolve(WeatherCode(0), null, noon)
        )
    }

    @Test
    fun `unknown weather code has safe cloudy fallback`() {
        assertEquals(
            WeatherVisualState.Cloudy,
            resolver.resolve(WeatherCode(42), true, noon)
        )
    }
}
