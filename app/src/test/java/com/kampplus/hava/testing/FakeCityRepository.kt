package com.kampplus.hava.testing

import com.kampplus.hava.core.common.result.AppResult
import com.kampplus.hava.feature.weather.domain.model.City
import com.kampplus.hava.feature.weather.domain.repository.CityRepository

class FakeCityRepository(
    var searchResult: (String) -> AppResult<List<City>> = { AppResult.Success(emptyList()) }
) : CityRepository {
    val queries = mutableListOf<String>()

    override suspend fun search(query: String): AppResult<List<City>> {
        queries += query
        return searchResult(query)
    }
}
