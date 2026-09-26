package com.kampplus.hava.feature.weather.data.repository

import com.kampplus.hava.core.common.dispatcher.IoDispatcher
import com.kampplus.hava.core.common.error.ErrorMapper
import com.kampplus.hava.core.common.result.AppResult
import com.kampplus.hava.core.common.result.runCatchingApp
import com.kampplus.hava.feature.weather.data.remote.CityRemoteDataSource
import com.kampplus.hava.feature.weather.domain.model.City
import com.kampplus.hava.feature.weather.domain.repository.CityRepository
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class CityRepositoryImpl @Inject constructor(
    private val remoteDataSource: CityRemoteDataSource,
    private val errorMapper: ErrorMapper,
    @param:IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : CityRepository {
    override suspend fun search(query: String): AppResult<List<City>> = withContext(ioDispatcher) {
        errorMapper.runCatchingApp { remoteDataSource.search(query) }
    }
}
