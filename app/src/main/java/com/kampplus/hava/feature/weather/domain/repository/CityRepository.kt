package com.kampplus.hava.feature.weather.domain.repository

import com.kampplus.hava.core.common.result.AppResult
import com.kampplus.hava.feature.weather.domain.model.City

interface CityRepository {

    suspend fun search(query: String): AppResult<List<City>>
}
