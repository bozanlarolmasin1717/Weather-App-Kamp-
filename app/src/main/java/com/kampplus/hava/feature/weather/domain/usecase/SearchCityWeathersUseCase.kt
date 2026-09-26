package com.kampplus.hava.feature.weather.domain.usecase

import com.kampplus.hava.core.common.result.AppResult
import com.kampplus.hava.feature.weather.domain.model.CityWeather
import com.kampplus.hava.feature.weather.domain.repository.CityRepository
import com.kampplus.hava.feature.weather.domain.repository.WeatherRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow

/**
 * İki repository'yi birleştiren iş akışı: önce şehirleri bul, sonra bulunan şehirlerin
 * anlık havasını tek istekte getir. ViewModel bu orkestrasyonu bilmez.
 */
class SearchCityWeathersUseCase @Inject constructor(
    private val cityRepository: CityRepository,
    private val weatherRepository: WeatherRepository
) {
    operator fun invoke(query: String): Flow<AppResult<List<CityWeather>>> = flow {
        when (val cities = cityRepository.search(query.trim())) {
            is AppResult.Failure -> emit(cities)
            is AppResult.Success ->
                if (cities.data.isEmpty()) {
                    emit(AppResult.Success(emptyList()))
                } else {
                    emitAll(weatherRepository.getCurrentWeather(cities.data))
                }
        }
    }
}
