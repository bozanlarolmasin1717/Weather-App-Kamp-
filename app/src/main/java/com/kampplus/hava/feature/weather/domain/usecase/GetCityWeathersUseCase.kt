package com.kampplus.hava.feature.weather.domain.usecase

import com.kampplus.hava.core.common.result.AppResult
import com.kampplus.hava.feature.weather.domain.model.CityWeather
import com.kampplus.hava.feature.weather.domain.repository.WeatherRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class GetCityWeathersUseCase @Inject constructor(
    private val repository: WeatherRepository
) {

    operator fun invoke(): Flow<AppResult<List<CityWeather>>> =
        repository.getCityWeathers()
}
