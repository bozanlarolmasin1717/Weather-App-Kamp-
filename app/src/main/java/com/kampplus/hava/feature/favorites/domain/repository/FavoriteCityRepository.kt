package com.kampplus.hava.feature.favorites.domain.repository

import com.kampplus.hava.feature.favorites.domain.model.FavoriteCity
import kotlinx.coroutines.flow.Flow

interface FavoriteCityRepository {

    fun observeFavorites():
        Flow<List<FavoriteCity>>

    suspend fun isFavorite(
        id: Long
    ): Boolean

    suspend fun add(
        city: FavoriteCity
    )

    suspend fun remove(
        id: Long
    )
}
