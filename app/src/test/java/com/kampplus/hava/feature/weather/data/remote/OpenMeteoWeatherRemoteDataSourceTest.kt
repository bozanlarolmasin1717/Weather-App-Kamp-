package com.kampplus.hava.feature.weather.data.remote

import com.kampplus.hava.core.network.di.NetworkModule
import com.kampplus.hava.feature.weather.data.remote.api.OpenMeteoForecastApi
import com.kampplus.hava.feature.weather.domain.model.Coordinates
import com.kampplus.hava.feature.weather.domain.model.WeatherCode
import com.kampplus.hava.testing.city
import java.time.LocalDate
import java.time.LocalDateTime
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.SerializationException
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit

class OpenMeteoWeatherRemoteDataSourceTest {

    private val server =
        MockWebServer()

    private lateinit var dataSource:
        OpenMeteoWeatherRemoteDataSource

    private val istanbul =
        city(
            id = 745044,
            name = "İstanbul",
            region = "İstanbul",
            coordinates =
            Coordinates(
                41.0138,
                28.9497
            )
        )

    private val ankara =
        city()

    @Before
    fun setUp() {
        server.start()

        val api =
            Retrofit.Builder()
                .baseUrl(
                    server.url(
                        "/v1/"
                    )
                )
                .addConverterFactory(
                    NetworkModule
                        .provideConverterFactory(
                            NetworkModule
                                .provideJson()
                        )
                )
                .build()
                .create(
                    OpenMeteoForecastApi::class.java
                )

        dataSource =
            OpenMeteoWeatherRemoteDataSource(
                api
            )
    }

    @After
    fun tearDown() {
        server.shutdown()
    }

    @Test
    fun `multiple cities are fetched in one request and matched by order`() = runTest {
        server.enqueue(
            MockResponse()
                .setBody(
                    readResource(
                        "forecast_multi.json"
                    )
                )
        )

        val result =
            dataSource
                .getCurrentWeather(
                    listOf(
                        istanbul,
                        ankara
                    )
                )

        assertEquals(
            1,
            server.requestCount
        )

        val url =
            server
                .takeRequest()
                .requestUrl!!

        assertEquals(
            "41.0138,39.92",
            url.queryParameter(
                "latitude"
            )
        )

        assertEquals(
            "auto",
            url.queryParameter(
                "timezone"
            )
        )

        assertEquals(
            listOf(
                "İstanbul",
                "Ankara"
            ),
            result.map {
                it.city.name
            }
        )

        assertEquals(
            18.4,
            result
                .first()
                .current
                .temperatureC,
            0.0
        )

        assertEquals(
            WeatherCode(2),
            result
                .last()
                .current
                .weatherCode
        )
    }

    @Test
    fun `single city uses object response`() = runTest {
        server.enqueue(
            MockResponse()
                .setBody(
                    readResource(
                        "forecast_single.json"
                    )
                )
        )

        val result =
            dataSource
                .getCurrentWeather(
                    listOf(
                        ankara
                    )
                )

        assertEquals(
            14.5,
            result
                .single()
                .current
                .temperatureC,
            0.0
        )

        assertEquals(
            LocalDateTime.of(
                2026,
                9,
                24,
                11,
                30
            ),
            result
                .single()
                .current
                .observedAt
        )
    }

    @Test
    fun `forecast maps columnar hourly and daily data and skips incomplete rows`() = runTest {
        server.enqueue(
            MockResponse()
                .setBody(
                    readResource(
                        "forecast_single.json"
                    )
                )
        )

        val forecast =
            dataSource
                .getForecast(
                    ankara
                )

        with(
            forecast.current
        ) {
            assertEquals(
                48,
                humidityPercent
            )

            assertEquals(
                9.7,
                windSpeedKmh!!,
                0.0
            )

            assertEquals(
                true,
                isDay
            )
        }

        assertEquals(
            "Europe/Istanbul",
            forecast.timeZoneId
        )

        assertEquals(
            10_800,
            forecast.utcOffsetSeconds
        )

        assertEquals(
            listOf(
                0,
                2
            ),
            forecast
                .hourly
                .map {
                    it.time.hour
                }
        )

        assertEquals(
            35,
            forecast
                .hourly
                .last()
                .precipitationProbability
        )

        with(forecast.hourly.last()) {
            assertEquals(10.1, apparentTemperatureC!!, 0.0)
            assertEquals(13.6, windSpeedKmh!!, 0.0)
            assertEquals(0.8, precipitationMm!!, 0.0)
            assertEquals(false, isDay)
        }

        assertEquals(
            LocalDate.of(
                2026,
                9,
                25
            ),
            forecast
                .daily
                .last()
                .date
        )

        assertEquals(
            6.2,
            forecast
                .daily
                .last()
                .minTemperatureC,
            0.0
        )

        assertEquals(
            LocalDateTime.of(2026, 9, 25, 6, 43),
            forecast.daily.last().sunrise
        )

        assertEquals(
            LocalDateTime.of(2026, 9, 25, 18, 45),
            forecast.daily.last().sunset
        )

        val url =
            server
                .takeRequest()
                .requestUrl!!

        assertEquals(
            "7",
            url.queryParameter(
                "forecast_days"
            )
        )

        assertTrue(
            checkNotNull(url.queryParameter("hourly"))
                .contains("wind_speed_10m")
        )

        assertTrue(
            checkNotNull(url.queryParameter("daily"))
                .contains("sunset")
        )
    }

    @Test
    fun `missing day state stays unknown instead of defaulting to daytime`() = runTest {
        server.enqueue(
            MockResponse().setBody(
                """
                    {
                      "latitude":52.52,
                      "longitude":13.41,
                      "timezone":"Europe/Berlin",
                      "current": {
                        "time":"2026-10-25T02:30",
                        "temperature_2m":9.0,
                        "weather_code":3
                      }
                    }
                """.trimIndent()
            )
        )

        val result = dataSource.getCurrentWeather(
            listOf(ankara)
        )

        assertNull(result.single().current.isDay)
    }

    @Test
    fun `forecast preserves city timezone and utc offset across daylight saving transition`() = runTest {
        server.enqueue(
            MockResponse().setBody(
                """
                    {
                      "latitude":52.52,
                      "longitude":13.41,
                      "utc_offset_seconds":3600,
                      "timezone":"Europe/Berlin",
                      "current": {
                        "time":"2026-10-25T02:30",
                        "temperature_2m":9.0,
                        "weather_code":3,
                        "is_day":0
                      },
                      "hourly": {
                        "time":["2026-10-25T02:00","not-a-time","2026-10-26T02:00"],
                        "temperature_2m":[9.1,9.0,7.4],
                        "weather_code":[3,3,2]
                      },
                      "daily": {
                        "time":["2026-10-25","not-a-date","2026-10-26"],
                        "temperature_2m_max":[12.0,11.0,10.0],
                        "temperature_2m_min":[7.0,6.0,5.0],
                        "weather_code":[3,3,2]
                      }
                    }
                """.trimIndent()
            )
        )

        val forecast = dataSource.getForecast(ankara)

        assertEquals("Europe/Berlin", forecast.timeZoneId)
        assertEquals(3_600, forecast.utcOffsetSeconds)
        assertEquals(LocalDateTime.of(2026, 10, 25, 2, 30), forecast.current.observedAt)
        assertEquals(
            listOf(
                LocalDateTime.of(2026, 10, 25, 2, 0),
                LocalDateTime.of(2026, 10, 26, 2, 0)
            ),
            forecast.hourly.map { it.time }
        )
        assertEquals(
            listOf(
                LocalDate.of(2026, 10, 25),
                LocalDate.of(2026, 10, 26)
            ),
            forecast.daily.map { it.date }
        )
    }

    @Test(
        expected =
        SerializationException::class
    )
    fun `missing current block is treated as malformed response`() = runTest {
        server.enqueue(
            MockResponse()
                .setBody(
                    """
                        {
                          "latitude":39.9,
                          "longitude":32.8
                        }
                    """.trimIndent()
                )
        )

        dataSource
            .getForecast(
                ankara
            )
    }

    private fun readResource(name: String): String = checkNotNull(
        javaClass
            .classLoader
            ?.getResource(
                name
            )
    )
        .readText()
}
