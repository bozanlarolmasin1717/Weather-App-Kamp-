package com.kampplus.hava.feature.favorites.data.local

import com.kampplus.hava.feature.favorites.data.local.dao.FavoriteCityDao
import com.kampplus.hava.feature.favorites.data.local.entity.FavoriteCityEntity
import com.kampplus.hava.feature.favorites.domain.model.FavoriteCity
import java.time.Clock
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/** Favori şehirleri diske yazar; uygulama tamamen kapatılıp açıldığında da korunur (CP4). */
class RoomFavoriteCityDataSource @Inject constructor(
    private val dao: FavoriteCityDao,
    private val clock: Clock
) : FavoriteCityLocalDataSource {

    override fun observeAll(): Flow<List<FavoriteCity>> = dao.observeAll().map { entities -> entities.map { it.toDomain() } }

    override suspend fun contains(id: Long): Boolean = dao.exists(id)

    override suspend fun upsert(city: FavoriteCity) = dao.upsert(city.toEntity(addedAtEpochMillis = clock.millis()))

    override suspend fun delete(id: Long) = dao.deleteById(id)

    private fun FavoriteCityEntity.toDomain() = FavoriteCity(
        id = id,
        name = name,
        region = region,
        country = country,
        latitude = latitude,
        longitude = longitude
    )

    private fun FavoriteCity.toEntity(addedAtEpochMillis: Long) = FavoriteCityEntity(
        id = id,
        name = name,
        region = region,
        country = country,
        latitude = latitude,
        longitude = longitude,
        addedAtEpochMillis = addedAtEpochMillis
    )
}
