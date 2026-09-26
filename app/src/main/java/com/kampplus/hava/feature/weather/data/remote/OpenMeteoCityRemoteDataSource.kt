package com.kampplus.hava.feature.weather.data.remote

import com.kampplus.hava.feature.weather.data.mapper.toDomain
import com.kampplus.hava.feature.weather.data.remote.api.OpenMeteoGeocodingApi
import com.kampplus.hava.feature.weather.domain.model.City
import javax.inject.Inject

class OpenMeteoCityRemoteDataSource @Inject constructor(
    private val api: OpenMeteoGeocodingApi
) : CityRemoteDataSource {
    override suspend fun search(query: String): List<City> = api.search(name = query).results.map { it.toDomain() }
}
