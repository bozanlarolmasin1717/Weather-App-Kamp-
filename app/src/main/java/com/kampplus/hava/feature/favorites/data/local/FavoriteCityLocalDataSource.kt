package com.kampplus.hava.feature.favorites.data.local

import com.kampplus.hava.feature.favorites.domain.model.FavoriteCity
import kotlinx.coroutines.flow.Flow

interface FavoriteCityLocalDataSource {
    fun observeAll(): Flow<List<FavoriteCity>>

    suspend fun contains(id: Long): Boolean

    suspend fun upsert(city: FavoriteCity)

    suspend fun delete(id: Long)
}
