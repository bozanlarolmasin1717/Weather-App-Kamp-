package com.kampplus.hava.feature.weather.data.remote

import com.kampplus.hava.feature.weather.data.mapper.requireCurrent
import com.kampplus.hava.feature.weather.data.mapper.toDomain
import com.kampplus.hava.feature.weather.data.mapper.toForecast
import com.kampplus.hava.feature.weather.data.remote.api.OpenMeteoForecastApi
import com.kampplus.hava.feature.weather.domain.model.City
import com.kampplus.hava.feature.weather.domain.model.CityWeather
import com.kampplus.hava.feature.weather.domain.model.Forecast
import javax.inject.Inject

/** Open-Meteo'dan gerçek veri (CP4). */
class OpenMeteoWeatherRemoteDataSource @Inject constructor(
    private val api: OpenMeteoForecastApi
) : WeatherRemoteDataSource {

    /** Tüm şehirler tek istekte sorgulanır; yanıt dizisi istek sırasıyla döner. */
    override suspend fun getCurrentWeather(cities: List<City>): List<CityWeather> {
        if (cities.isEmpty()) return emptyList()
        val responses = if (cities.size == 1) {
            val city = cities.single()
            listOf(api.getForecast(city.latitude(), city.longitude(), current = CURRENT_FIELDS))
        } else {
            api.getForecasts(
                latitudes = cities.joinToString(",") { it.latitude() },
                longitudes = cities.joinToString(",") { it.longitude() },
                current = CURRENT_FIELDS
            )
        }
        return cities.zip(responses) { city, response -> CityWeather(city = city, current = response.requireCurrent().toDomain()) }
    }

    override suspend fun getForecast(city: City): Forecast = api.getForecast(
        latitude = city.latitude(),
        longitude = city.longitude(),
        current = CURRENT_FIELDS,
        hourly = HOURLY_FIELDS,
        daily = DAILY_FIELDS,
        forecastDays = FORECAST_DAYS
    ).toForecast()

    private fun City.latitude() = coordinates.latitude.toString()

    private fun City.longitude() = coordinates.longitude.toString()

    private companion object {
        // Yeni bir değişken (ör. uv_index) = buraya alan adı + DTO/domain alanı.
        const val CURRENT_FIELDS = "temperature_2m,weather_code,apparent_temperature,relative_humidity_2m,wind_speed_10m,is_day"
        const val HOURLY_FIELDS = "temperature_2m,weather_code,precipitation_probability"
        const val DAILY_FIELDS = "weather_code,temperature_2m_max,temperature_2m_min,precipitation_probability_max"
        const val FORECAST_DAYS = 7
    }
}
