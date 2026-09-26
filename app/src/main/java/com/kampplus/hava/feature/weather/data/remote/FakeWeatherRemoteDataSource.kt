package com.kampplus.hava.feature.weather.data.remote

import com.kampplus.hava.feature.weather.domain.model.City
import com.kampplus.hava.feature.weather.domain.model.CityWeather
import com.kampplus.hava.feature.weather.domain.model.CurrentWeather
import com.kampplus.hava.feature.weather.domain.model.DailyForecast
import com.kampplus.hava.feature.weather.domain.model.Forecast
import com.kampplus.hava.feature.weather.domain.model.HourlyForecast
import com.kampplus.hava.feature.weather.domain.model.WeatherCode
import java.time.LocalDateTime
import javax.inject.Inject
import kotlinx.coroutines.delay

class FakeWeatherRemoteDataSource @Inject constructor() :
    WeatherRemoteDataSource {

    override suspend fun getCurrentWeather(
        cities: List<City>
    ): List<CityWeather> {

        delay(FAKE_LATENCY_MS)

        return cities.mapIndexed { index, city ->

            CityWeather(
                city = city,
                current = CurrentWeather(
                    temperatureC =
                        TEMPERATURES[
                            index % TEMPERATURES.size
                        ],
                    weatherCode =
                        WeatherCode(
                            CODES[
                                index % CODES.size
                            ]
                        ),
                    observedAt = OBSERVED_AT,
                    apparentTemperatureC =
                        TEMPERATURES[
                            index % TEMPERATURES.size
                        ] - 1.5,
                    humidityPercent =
                        40 + (index * 3) % 50,
                    windSpeedKmh =
                        5.0 + index % 15
                )
            )
        }
    }

    override suspend fun getForecast(
        city: City
    ): Forecast {

        delay(FAKE_LATENCY_MS)

        val base =
            TEMPERATURES[
                (city.id % TEMPERATURES.size)
                    .toInt()
            ]

        val current =
            CurrentWeather(
                temperatureC = base,
                weatherCode =
                    WeatherCode(
                        CODES[
                            (city.id % CODES.size)
                                .toInt()
                        ]
                    ),
                observedAt = OBSERVED_AT,
                apparentTemperatureC =
                    base - 1.5,
                humidityPercent = 55,
                windSpeedKmh = 12.0
            )

        val hourly =
            List(HOURS) { hour ->

                HourlyForecast(
                    time =
                        OBSERVED_AT.plusHours(
                            hour.toLong()
                        ),
                    temperatureC =
                        base +
                            DAILY_CURVE[
                                (
                                    OBSERVED_AT.hour +
                                        hour
                                    ) %
                                    DAILY_CURVE.size
                            ],
                    weatherCode =
                        WeatherCode(
                            CODES[
                                (
                                    hour +
                                        city.id.toInt()
                                    ) %
                                    CODES.size
                            ]
                        ),
                    precipitationProbability =
                        (hour * 7) % 60
                )
            }

        val daily =
            List(DAYS) { day ->

                DailyForecast(
                    date =
                        OBSERVED_AT
                            .toLocalDate()
                            .plusDays(
                                day.toLong()
                            ),
                    minTemperatureC =
                        base - 6 + day % 3,
                    maxTemperatureC =
                        base + 3 - day % 2,
                    weatherCode =
                        WeatherCode(
                            CODES[
                                (
                                    day * 3 +
                                        city.id.toInt()
                                    ) %
                                    CODES.size
                            ]
                        ),
                    precipitationProbability =
                        (day * 13) % 80
                )
            }

        return Forecast(
            current = current,
            hourly = hourly,
            daily = daily
        )
    }

    private companion object {

        const val HOURS = 24
        const val DAYS = 7
        const val FAKE_LATENCY_MS = 300L

        val OBSERVED_AT:
            LocalDateTime =
            LocalDateTime.of(
                2026,
                9,
                24,
                12,
                0
            )

        val DAILY_CURVE =
            listOf(
                -5.0,
                -5.5,
                -6.0,
                -6.0,
                -5.5,
                -5.0,
                -4.0,
                -2.5,
                -1.0,
                0.5,
                1.5,
                2.5,
                3.0,
                3.5,
                3.5,
                3.0,
                2.0,
                1.0,
                0.0,
                -1.0,
                -2.0,
                -3.0,
                -4.0,
                -4.5
            )

        val TEMPERATURES =
            listOf(
                18.4,
                21.0,
                26.3,
                19.7,
                29.1,
                30.2,
                22.8,
                27.5,
                31.4,
                20.1,
                28.0,
                25.6,
                17.9,
                16.4,
                19.2,
                18.8,
                11.3,
                13.7,
                23.5,
                27.0
            )

        val CODES =
            listOf(
                2,
                0,
                1,
                3,
                0,
                1,
                0,
                2,
                0,
                61,
                1,
                0,
                3,
                45,
                80,
                63,
                2,
                71,
                1,
                95
            )
    }
}
