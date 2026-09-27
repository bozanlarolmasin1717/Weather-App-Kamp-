package com.kampplus.hava.feature.weather.domain.policy

import com.kampplus.hava.feature.weather.domain.model.DailyForecast
import com.kampplus.hava.feature.weather.domain.model.HourlyForecast
import com.kampplus.hava.feature.weather.domain.model.WeatherCode
import com.kampplus.hava.feature.weather.domain.model.WeatherInsightAdvice
import com.kampplus.hava.feature.weather.domain.model.WeatherInsightHeadline
import com.kampplus.hava.feature.weather.domain.model.WeatherInsightPriority
import com.kampplus.hava.feature.weather.domain.model.WeatherInsightType
import com.kampplus.hava.testing.forecast
import java.time.LocalDateTime
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class RuleBasedWeatherInsightGeneratorTest {

    private val generator = RuleBasedWeatherInsightGenerator(
        WmoWeatherConditionClassifier()
    )
    private val now = LocalDateTime.of(2026, 9, 24, 10, 30)

    @Test
    fun `thunderstorm takes priority over rain and wind`() {
        val result = generator.generate(
            forecast(now).copy(
                hourly = listOf(
                    hour(11, code = 61, rainChance = 90, windKmh = 55.0),
                    hour(12, code = 95, rainChance = 90, windKmh = 55.0)
                )
            )
        )

        assertEquals(WeatherInsightType.Thunderstorm, result?.type)
        assertEquals(WeatherInsightPriority.Critical, result?.priority)
        assertEquals(WeatherInsightHeadline.ThunderstormsExpected, result?.headline)
        assertEquals(WeatherInsightAdvice.AvoidExposedAreas, result?.advice)
        assertEquals(now.toLocalDate().atTime(12, 0), result?.relevantFrom)
    }

    @Test
    fun `rain probability creates expected rain insight with bounded window`() {
        val result = generator.generate(
            forecast(now).copy(
                hourly = listOf(
                    hour(11, rainChance = 20),
                    hour(13, rainChance = 70),
                    hour(14, rainChance = 80),
                    hour(15, rainChance = 10)
                )
            )
        )

        assertEquals(WeatherInsightType.Rain, result?.type)
        assertEquals(WeatherInsightAdvice.TakeUmbrella, result?.advice)
        assertEquals(now.toLocalDate().atTime(13, 0), result?.relevantFrom)
        assertEquals(now.toLocalDate().atTime(14, 0), result?.relevantTo)
    }

    @Test
    fun `measurable precipitation creates rain insight when probability is missing`() {
        val result = generator.generate(
            forecast(now).copy(
                hourly = listOf(
                    hour(12, precipitationMm = 0.3)
                )
            )
        )

        assertEquals(WeatherInsightType.Rain, result?.type)
    }

    @Test
    fun `snow is not mislabeled as rain when precipitation probability is high`() {
        val result = generator.generate(
            forecast(now).copy(
                hourly = listOf(
                    hour(12, code = 73, rainChance = 90)
                )
            )
        )

        assertEquals(WeatherInsightType.Snow, result?.type)
        assertEquals(WeatherInsightAdvice.AllowExtraTravelTime, result?.advice)
    }

    @Test
    fun `strong wind is reported when no precipitation hazard exists`() {
        val result = generator.generate(
            forecast(now).copy(
                hourly = listOf(
                    hour(12, windKmh = 41.0)
                )
            )
        )

        assertEquals(WeatherInsightType.StrongWind, result?.type)
        assertEquals(WeatherInsightAdvice.SecureLooseItems, result?.advice)
    }

    @Test
    fun `evening apparent temperature drop suggests a light jacket`() {
        val base = forecast(now)
        val result = generator.generate(
            base.copy(
                current = base.current.copy(
                    temperatureC = 20.0,
                    apparentTemperatureC = 19.0
                ),
                hourly = listOf(
                    hour(16, temperatureC = 18.0, apparentTemperatureC = 17.0),
                    hour(18, temperatureC = 14.0, apparentTemperatureC = 13.0)
                ),
                daily = listOf(
                    DailyForecast(
                        date = now.toLocalDate(),
                        minTemperatureC = 10.0,
                        maxTemperatureC = 20.0,
                        weatherCode = WeatherCode(1),
                        sunset = now.toLocalDate().atTime(17, 45)
                    )
                )
            )
        )

        assertEquals(WeatherInsightType.Cooling, result?.type)
        assertEquals(WeatherInsightHeadline.ColderThisEvening, result?.headline)
        assertEquals(WeatherInsightAdvice.TakeLightJacket, result?.advice)
        assertEquals(now.toLocalDate().atTime(18, 0), result?.relevantFrom)
    }

    @Test
    fun `warm mostly clear dry day produces low priority positive insight`() {
        val result = generator.generate(
            forecast(now).copy(
                hourly = listOf(
                    hour(11, code = 0, temperatureC = 21.0, isDay = true),
                    hour(12, code = 1, temperatureC = 23.0, isDay = true),
                    hour(13, code = 2, temperatureC = 24.0, isDay = true),
                    hour(14, code = 3, temperatureC = 24.0, isDay = true)
                )
            )
        )

        assertEquals(WeatherInsightType.WarmAndClear, result?.type)
        assertEquals(WeatherInsightPriority.Low, result?.priority)
        assertEquals(WeatherInsightAdvice.GoodForOutdoorPlans, result?.advice)
    }

    @Test
    fun `rain suppresses positive outdoor advice`() {
        val result = generator.generate(
            forecast(now).copy(
                hourly = listOf(
                    hour(11, code = 0, temperatureC = 24.0, isDay = true),
                    hour(12, code = 1, temperatureC = 25.0, isDay = true),
                    hour(13, code = 61, temperatureC = 23.0, rainChance = 90, isDay = true)
                )
            )
        )

        assertEquals(WeatherInsightType.Rain, result?.type)
        assertEquals(WeatherInsightAdvice.TakeUmbrella, result?.advice)
    }

    @Test
    fun `past and next-day hazards do not affect today's insight`() {
        val result = generator.generate(
            forecast(now).copy(
                hourly = listOf(
                    hour(8, code = 95),
                    hour(12, code = 3),
                    hour(12, code = 95, dayOffset = 1)
                )
            )
        )

        assertNull(result)
    }

    @Test
    fun `missing optional hourly data returns no advice instead of guessing`() {
        val result = generator.generate(
            forecast(now).copy(
                hourly = listOf(
                    hour(11, code = 0, temperatureC = 24.0),
                    hour(12, code = 1, temperatureC = 25.0)
                ),
                daily = emptyList()
            )
        )

        assertNull(result)
    }

    @Test
    fun `empty remaining forecast returns no insight`() {
        val result = generator.generate(
            forecast(now).copy(
                hourly = emptyList()
            )
        )

        assertNull(result)
    }

    private fun hour(
        hour: Int,
        code: Int = 3,
        temperatureC: Double = 18.0,
        apparentTemperatureC: Double? = null,
        rainChance: Int? = null,
        precipitationMm: Double? = null,
        windKmh: Double? = null,
        isDay: Boolean? = null,
        dayOffset: Long = 0
    ) = HourlyForecast(
        time = now.toLocalDate().plusDays(dayOffset).atTime(hour, 0),
        temperatureC = temperatureC,
        weatherCode = WeatherCode(code),
        precipitationProbability = rainChance,
        apparentTemperatureC = apparentTemperatureC,
        windSpeedKmh = windKmh,
        precipitationMm = precipitationMm,
        isDay = isDay
    )
}
