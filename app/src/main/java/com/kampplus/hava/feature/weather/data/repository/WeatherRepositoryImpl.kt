package com.kampplus.hava.feature.weather.data.repository

import com.kampplus.hava.core.common.dispatcher.IoDispatcher
import com.kampplus.hava.core.common.error.ErrorMapper
import com.kampplus.hava.core.common.result.AppResult
import com.kampplus.hava.core.common.result.runCatchingApp
import com.kampplus.hava.feature.weather.data.local.CityCatalog
import com.kampplus.hava.feature.weather.data.remote.WeatherRemoteDataSource
import com.kampplus.hava.feature.weather.domain.model.City
import com.kampplus.hava.feature.weather.domain.model.CityWeather
import com.kampplus.hava.feature.weather.domain.model.Forecast
import com.kampplus.hava.feature.weather.domain.repository.WeatherRepository
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext

class WeatherRepositoryImpl @Inject constructor(
    private val cityCatalog: CityCatalog,
    private val remoteDataSource:
    WeatherRemoteDataSource,
    private val errorMapper: ErrorMapper,
    @param:IoDispatcher
    private val ioDispatcher:
    CoroutineDispatcher
) : WeatherRepository {

    override fun getCityWeathers():
        Flow<AppResult<List<CityWeather>>> =
        flow {
            emit(
                errorMapper.runCatchingApp {
                    remoteDataSource
                        .getCurrentWeather(
                            cityCatalog.cities()
                        )
                }
            )
        }.flowOn(ioDispatcher)

    override suspend fun getForecast(
        city: City
    ): AppResult<Forecast> =
        withContext(ioDispatcher) {

            errorMapper.runCatchingApp {
                remoteDataSource
                    .getForecast(city)
            }
        }
}
