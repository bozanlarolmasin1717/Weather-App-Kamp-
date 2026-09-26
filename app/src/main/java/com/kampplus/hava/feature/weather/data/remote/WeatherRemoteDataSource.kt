package com.kampplus.hava.feature.weather.data.remote

import com.kampplus.hava.feature.weather.domain.model.City
import com.kampplus.hava.feature.weather.domain.model.CityWeather
import com.kampplus.hava.feature.weather.domain.model.Forecast

/**
 * Uzak hava verisi sözleşmesi. Implementasyonlar domain modeli döner;
 * DTO'lar implementasyonun içinde kalır.
 */
interface WeatherRemoteDataSource {
    suspend fun getCurrentWeather(cities: List<City>): List<CityWeather>

    suspend fun getForecast(city: City): Forecast
}
