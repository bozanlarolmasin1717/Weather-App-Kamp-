package com.kampplus.hava.feature.weather.domain.repository

import com.kampplus.hava.core.common.result.AppResult
import com.kampplus.hava.feature.weather.domain.model.City
import com.kampplus.hava.feature.weather.domain.model.CityWeather
import com.kampplus.hava.feature.weather.domain.model.Forecast
import kotlinx.coroutines.flow.Flow

interface WeatherRepository {

    fun getCityWeathers():
        Flow<AppResult<List<CityWeather>>>

    suspend fun getForecast(
        city: City
    ): AppResult<Forecast>
}
