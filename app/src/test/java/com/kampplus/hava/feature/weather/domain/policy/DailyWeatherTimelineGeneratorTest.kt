package com.kampplus.hava.feature.weather.domain.policy

import com.kampplus.hava.feature.weather.domain.model.DailyForecast
import com.kampplus.hava.feature.weather.domain.model.HourlyForecast
import com.kampplus.hava.feature.weather.domain.model.TimelineEventType
import com.kampplus.hava.feature.weather.domain.model.WeatherCode
import com.kampplus.hava.testing.forecast
import java.time.LocalDateTime
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class DailyWeatherTimelineGeneratorTest {

    private val generator = DailyWeatherTimelineGenerator(
        WmoWeatherConditionClassifier()
    )
    private val now = LocalDateTime.of(2026, 9, 24, 10, 30)

    @Test
    fun `rain beginning is detected from consecutive local hours`() {
        val events = generate(
            hour(11, rainChance = 10),
            hour(12, rainChance = 70),
            hour(13, rainChance = 80)
        )

        assertEquals(listOf(TimelineEventType.RainBegins), events.map { it.type })
        assertEquals(now.toLocalDate().atTime(12, 0), events.single().time)
    }

    @Test
    fun `rain ending is reported at first dry hour`() {
        val events = generate(
            hour(11, code = 61, rainChance = 90),
            hour(12, code = 61, rainChance = 80),
            hour(13, code = 2, rainChance = 10)
        )

        assertEquals(
            listOf(TimelineEventType.RainBegins, TimelineEventType.RainEases),
            events.map { it.type }
        )
        assertEquals(now.toLocalDate().atTime(13, 0), events.last().time)
    }

    @Test
    fun `rain already occurring produces one ongoing event`() {
        val base = forecast(now)
        val events = generator.generate(
            base.copy(
                current = base.current.copy(weatherCode = WeatherCode(61)),
                hourly = listOf(
                    hour(11, code = 61, rainChance = 90),
                    hour(12, code = 61, rainChance = 90)
                )
            )
        )

        assertEquals(listOf(TimelineEventType.OngoingRain), events.map { it.type })
    }

    @Test
    fun `meaningful apparent temperature drop is detected`() {
        val events = generate(
            hour(11, temperatureC = 20.0, apparentTemperatureC = 20.0),
            hour(12, temperatureC = 18.0, apparentTemperatureC = 17.0),
            hour(13, temperatureC = 15.0, apparentTemperatureC = 14.0)
        )

        assertEquals(listOf(TimelineEventType.TemperatureDrop), events.map { it.type })
        assertEquals(14.0, events.single().apparentTemperatureC!!, 0.0)
    }

    @Test
    fun `strong wind beginning requires a known threshold crossing`() {
        val base = forecast(now)
        val events = generator.generate(
            base.copy(
                current = base.current.copy(windSpeedKmh = 18.0),
                hourly = listOf(
                    hour(11, windKmh = 25.0),
                    hour(12, windKmh = 42.0),
                    hour(13, windKmh = 45.0)
                )
            )
        )

        assertEquals(listOf(TimelineEventType.StrongWindBegins), events.map { it.type })
        assertEquals(now.toLocalDate().atTime(12, 0), events.single().time)
    }

    @Test
    fun `sunset cooling replaces nearby generic temperature event`() {
        val base = forecast(now)
        val events = generator.generate(
            base.copy(
                hourly = listOf(
                    hour(17, temperatureC = 19.0, apparentTemperatureC = 18.0),
                    hour(18, temperatureC = 16.0, apparentTemperatureC = 15.0),
                    hour(19, temperatureC = 13.0, apparentTemperatureC = 12.0)
                ),
                daily = listOf(
                    DailyForecast(
                        date = now.toLocalDate(),
                        minTemperatureC = 10.0,
                        maxTemperatureC = 22.0,
                        weatherCode = WeatherCode(1),
                        sunset = now.toLocalDate().atTime(17, 45)
                    )
                )
            )
        )

        assertEquals(listOf(TimelineEventType.EveningCooling), events.map { it.type })
        assertEquals(now.toLocalDate().atTime(18, 0), events.single().time)
    }

    @Test
    fun `events are returned in chronological order`() {
        val base = forecast(now)
        val events = generator.generate(
            base.copy(
                current = base.current.copy(windSpeedKmh = 10.0),
                hourly = listOf(
                    hour(14, rainChance = 80, windKmh = 20.0),
                    hour(11, rainChance = 0, windKmh = 20.0),
                    hour(15, rainChance = 10, windKmh = 45.0)
                )
            )
        )

        assertEquals(events.map { it.time }.sorted(), events.map { it.time })
    }

    @Test
    fun `repetitive temperature changes are suppressed`() {
        val events = generate(
            hour(11, temperatureC = 22.0),
            hour(12, temperatureC = 16.0),
            hour(13, temperatureC = 15.0),
            hour(14, temperatureC = 14.0)
        )

        assertEquals(1, events.count { it.type == TimelineEventType.TemperatureDrop })
    }

    @Test
    fun `forecast gap does not create false rain wind or temperature transitions`() {
        val base = forecast(now)
        val events = generator.generate(
            base.copy(
                current = base.current.copy(windSpeedKmh = 10.0),
                hourly = listOf(
                    hour(11, temperatureC = 22.0, rainChance = 0, windKmh = 10.0),
                    hour(16, temperatureC = 12.0, rainChance = 90, windKmh = 50.0)
                )
            )
        )

        assertTrue(events.isEmpty())
    }

    @Test
    fun `next day rows never become midnight transitions`() {
        val late = LocalDateTime.of(2026, 9, 24, 23, 30)
        val base = forecast(late)
        val events = generator.generate(
            base.copy(
                current = base.current.copy(windSpeedKmh = 10.0),
                hourly = listOf(
                    HourlyForecast(
                        time = late.toLocalDate().atTime(23, 0),
                        temperatureC = 18.0,
                        weatherCode = WeatherCode(2),
                        windSpeedKmh = 10.0
                    ),
                    HourlyForecast(
                        time = late.toLocalDate().plusDays(1).atStartOfDay(),
                        temperatureC = 10.0,
                        weatherCode = WeatherCode(61),
                        precipitationProbability = 90,
                        windSpeedKmh = 50.0
                    )
                )
            )
        )

        assertTrue(events.isEmpty())
    }

    @Test
    fun `timeline stays concise when many meaningful transitions exist`() {
        val base = forecast(now)
        val events = generator.generate(
            base.copy(
                current = base.current.copy(windSpeedKmh = 10.0),
                hourly = listOf(
                    hour(11, temperatureC = 22.0, rainChance = 0, windKmh = 10.0),
                    hour(12, temperatureC = 16.0, rainChance = 80, windKmh = 10.0),
                    hour(13, temperatureC = 15.0, rainChance = 0, windKmh = 45.0),
                    hour(14, temperatureC = 22.0, rainChance = 80, windKmh = 20.0),
                    hour(15, temperatureC = 21.0, rainChance = 0, windKmh = 20.0)
                )
            )
        )

        assertTrue(events.size <= DailyWeatherTimelineGenerator.MAX_EVENTS)
    }

    private fun generate(vararg hours: HourlyForecast) = forecast(now).copy(
        hourly = hours.toList(),
        daily = emptyList()
    ).let(generator::generate)

    private fun hour(
        hour: Int,
        code: Int = 2,
        temperatureC: Double = 18.0,
        apparentTemperatureC: Double? = null,
        rainChance: Int? = null,
        precipitationMm: Double? = null,
        windKmh: Double? = null
    ) = HourlyForecast(
        time = now.toLocalDate().atTime(hour, 0),
        temperatureC = temperatureC,
        weatherCode = WeatherCode(code),
        precipitationProbability = rainChance,
        apparentTemperatureC = apparentTemperatureC,
        windSpeedKmh = windKmh,
        precipitationMm = precipitationMm
    )
}
