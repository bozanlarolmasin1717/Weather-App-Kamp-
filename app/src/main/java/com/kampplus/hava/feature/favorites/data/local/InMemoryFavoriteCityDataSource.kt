package com.kampplus.hava.feature.favorites.data.local

import com.kampplus.hava.feature.favorites.domain.model.FavoriteCity
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * Uygulama açık kaldığı sürece favorileri bellekte tutar (CP3).
 * `@Singleton` olduğu için tüm ekranlar aynı state'i paylaşır; uygulama kapanınca veri kaybolur.
 */
@Singleton
class InMemoryFavoriteCityDataSource @Inject constructor() : FavoriteCityLocalDataSource {

    private val favorites = MutableStateFlow<List<FavoriteCity>>(emptyList())

    override fun observeAll(): Flow<List<FavoriteCity>> = favorites.asStateFlow()

    override suspend fun contains(id: Long): Boolean = favorites.value.any { it.id == id }

    override suspend fun upsert(city: FavoriteCity) {
        favorites.update { current -> listOf(city) + current.filterNot { it.id == city.id } }
    }

    override suspend fun delete(id: Long) {
        favorites.update { current -> current.filterNot { it.id == id } }
    }
}
