package com.kampplus.hava.feature.weather.data.remote

import com.kampplus.hava.feature.weather.domain.model.City
import com.kampplus.hava.feature.weather.domain.model.CityWeather
import com.kampplus.hava.feature.weather.domain.model.CurrentWeather
import com.kampplus.hava.feature.weather.domain.model.WeatherCode
import java.time.LocalDateTime
import javax.inject.Inject
import kotlinx.coroutines.delay

/**
 * CP1–CP3 için koda gömülü sabit hava verisi. İnternet gerektirmez; aynı şehir için
 * her zaman aynı değeri üretir. CP4'te DI binding'i değiştirilerek gerçek API ile yer değiştirir.
 */
class FakeWeatherRemoteDataSource @Inject constructor() : WeatherRemoteDataSource {

    override suspend fun getCurrentWeather(
        cities: List<City>
    ): List<CityWeather> {

        delay(FAKE_LATENCY_MS)

        return cities.mapIndexed { index, city ->

            CityWeather(
                city = city,
                current = CurrentWeather(
                    temperatureC = TEMPERATURES[index % TEMPERATURES.size],
                    weatherCode = WeatherCode(
                        CODES[index % CODES.size]
                    ),
                    observedAt = OBSERVED_AT,
                    apparentTemperatureC =
                        TEMPERATURES[index % TEMPERATURES.size] - 1.5,
                    humidityPercent = 40 + (index * 3) % 50,
                    windSpeedKmh = 5.0 + index % 15
                )
            )
        }
    }

    private companion object {

        const val FAKE_LATENCY_MS = 300L

        val OBSERVED_AT: LocalDateTime =
            LocalDateTime.of(2026, 9, 24, 12, 0)

        val TEMPERATURES = listOf(
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

        val CODES = listOf(
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
