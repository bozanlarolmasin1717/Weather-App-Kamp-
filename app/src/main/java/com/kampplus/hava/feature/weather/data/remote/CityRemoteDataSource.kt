package com.kampplus.hava.feature.weather.data.remote

import com.kampplus.hava.feature.weather.domain.model.City

interface CityRemoteDataSource {
    suspend fun search(query: String): List<City>
}
