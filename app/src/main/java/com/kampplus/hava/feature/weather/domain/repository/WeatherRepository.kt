package com.kampplus.hava.feature.weather.domain.repository

import com.kampplus.hava.core.common.result.AppResult
import com.kampplus.hava.feature.weather.domain.model.City
import com.kampplus.hava.feature.weather.domain.model.CityWeather
import com.kampplus.hava.feature.weather.domain.model.Forecast
import kotlinx.coroutines.flow.Flow

/**
 * Hava verisinin tek giriş noktası. Verinin sabit listeden mi, ağdan mı, cache'ten mi
 * geldiği bu sözleşmenin arkasında kalır.
 */
interface WeatherRepository {
    /** Öne çıkan şehirlerin anlık hava durumu. */
    fun getCityWeathers(): Flow<AppResult<List<CityWeather>>>

    /** Verilen şehirlerin anlık hava durumu (ör. arama sonuçları). */
    fun getCurrentWeather(cities: List<City>): Flow<AppResult<List<CityWeather>>>

    suspend fun getForecast(city: City): AppResult<Forecast>
}
