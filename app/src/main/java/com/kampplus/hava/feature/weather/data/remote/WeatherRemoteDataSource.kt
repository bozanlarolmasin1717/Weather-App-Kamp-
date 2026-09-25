package com.kampplus.hava.feature.weather.data.remote

import com.kampplus.hava.feature.weather.domain.model.City
import com.kampplus.hava.feature.weather.domain.model.CityWeather

interface WeatherRemoteDataSource {

    suspend fun getCurrentWeather(
        cities: List<City>
    ): List<CityWeather>
}
