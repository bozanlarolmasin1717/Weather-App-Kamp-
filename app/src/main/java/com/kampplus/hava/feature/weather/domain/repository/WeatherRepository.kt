package com.kampplus.hava.feature.weather.domain.repository

import com.kampplus.hava.core.common.result.AppResult
import com.kampplus.hava.feature.weather.domain.model.CityWeather
import kotlinx.coroutines.flow.Flow

interface WeatherRepository {

    fun getCityWeathers(): Flow<AppResult<List<CityWeather>>>
}
