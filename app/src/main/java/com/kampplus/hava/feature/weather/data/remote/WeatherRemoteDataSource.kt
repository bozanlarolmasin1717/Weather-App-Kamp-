package com.kampplus.hava.feature.weather.data.remote

import com.kampplus.hava.feature.weather.domain.model.City
import com.kampplus.hava.feature.weather.domain.model.CityWeather

/**
 * Uzak hava verisi sözleşmesi. Implementasyonlar domain modeli döner;
 * DTO'lar implementasyonun içinde kalır.
 */
interface WeatherRemoteDataSource {

    suspend fun getCurrentWeather(
        cities: List<City>
    ): List<CityWeather>
}
