package com.kampplus.hava.testing

import com.kampplus.hava.feature.weather.domain.model.City
import com.kampplus.hava.feature.weather.domain.model.CityWeather
import com.kampplus.hava.feature.weather.domain.model.Coordinates
import com.kampplus.hava.feature.weather.domain.model.CurrentWeather
import com.kampplus.hava.feature.weather.domain.model.WeatherCode
import java.time.LocalDateTime

fun city(id: Long = 323786, name: String = "Ankara", region: String? = "Ankara", country: String? = "Türkiye") =
    City(id = id, name = name, region = region, country = country, coordinates = Coordinates(39.92, 32.85))

fun cityWeather(city: City = city(), temperatureC: Double = 21.4, code: Int = 0) = CityWeather(
    city = city,
    current = CurrentWeather(
        temperatureC = temperatureC,
        weatherCode = WeatherCode(code),
        observedAt = LocalDateTime.of(2026, 9, 24, 12, 0)
    )
)
