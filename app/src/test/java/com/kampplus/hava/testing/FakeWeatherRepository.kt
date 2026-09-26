package com.kampplus.hava.testing

import com.kampplus.hava.core.common.result.AppResult
import com.kampplus.hava.feature.weather.domain.model.CityWeather
import com.kampplus.hava.feature.weather.domain.repository.WeatherRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/** Sonuçları testten ayarlanabilen sahte repository. */
class FakeWeatherRepository(
    var cityWeathersResult: () -> AppResult<List<CityWeather>> = { AppResult.Success(emptyList()) }
) : WeatherRepository {
    override fun getCityWeathers(): Flow<AppResult<List<CityWeather>>> = flow { emit(cityWeathersResult()) }
}
