package com.kampplus.hava.feature.favorites.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.kampplus.hava.feature.favorites.data.local.entity.FavoriteCityEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteCityDao {
    @Query("SELECT * FROM favorite_cities ORDER BY added_at DESC")
    fun observeAll(): Flow<List<FavoriteCityEntity>>

    @Query("SELECT EXISTS(SELECT 1 FROM favorite_cities WHERE id = :id)")
    suspend fun exists(id: Long): Boolean

    @Upsert
    suspend fun upsert(entity: FavoriteCityEntity)

    @Query("DELETE FROM favorite_cities WHERE id = :id")
    suspend fun deleteById(id: Long)
}
