package com.kampplus.hava.feature.favorites.data.repository

import com.kampplus.hava.feature.favorites.data.local.FavoriteCityLocalDataSource
import com.kampplus.hava.feature.favorites.domain.model.FavoriteCity
import com.kampplus.hava.feature.favorites.domain.repository.FavoriteCityRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class FavoriteCityRepositoryImpl @Inject constructor(
    private val localDataSource: FavoriteCityLocalDataSource
) : FavoriteCityRepository {

    override fun observeFavorites(): Flow<List<FavoriteCity>> = localDataSource.observeAll()

    override suspend fun isFavorite(id: Long): Boolean = localDataSource.contains(id)

    override suspend fun add(city: FavoriteCity) = localDataSource.upsert(city)

    override suspend fun remove(id: Long) = localDataSource.delete(id)
}
