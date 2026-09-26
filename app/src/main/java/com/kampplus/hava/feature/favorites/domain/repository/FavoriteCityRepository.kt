package com.kampplus.hava.feature.favorites.domain.repository

import com.kampplus.hava.feature.favorites.domain.model.FavoriteCity
import kotlinx.coroutines.flow.Flow

interface FavoriteCityRepository {
    /** Favorileri en son eklenen başta olacak şekilde yayınlar. */
    fun observeFavorites(): Flow<List<FavoriteCity>>

    suspend fun isFavorite(id: Long): Boolean

    suspend fun add(city: FavoriteCity)

    suspend fun remove(id: Long)
}
