package com.kampplus.hava.testing

import com.kampplus.hava.core.common.error.AppError
import com.kampplus.hava.core.common.result.AppResult
import com.kampplus.hava.feature.weather.domain.model.City
import com.kampplus.hava.feature.weather.domain.model.CityWeather
import com.kampplus.hava.feature.weather.domain.model.Forecast
import com.kampplus.hava.feature.weather.domain.repository.WeatherRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakeWeatherRepository(
    var cityWeathersResult:
        () -> AppResult<
        List<CityWeather>
        > =
        {
            AppResult.Success(
                emptyList()
            )
        },
    var forecastResult:
        (City) -> AppResult<Forecast> =
        {
            AppResult.Failure(
                AppError.NotFound
            )
        }
) : WeatherRepository {

    val requestedForecasts =
        mutableListOf<City>()

    override fun getCityWeathers():
        Flow<
            AppResult<
                List<CityWeather>
                >
            > =
        flow {
            emit(
                cityWeathersResult()
            )
        }

    override suspend fun getForecast(
        city: City
    ): AppResult<Forecast> {

        requestedForecasts +=
            city

        return forecastResult(
            city
        )
    }
}
