package com.kampplus.hava.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.kampplus.hava.feature.favorites.data.local.dao.FavoriteCityDao
import com.kampplus.hava.feature.favorites.data.local.entity.FavoriteCityEntity

/**
 * Uygulamanın tek veritabanı. Yeni tablo (ör. offline tahmin cache'i) eklemek için
 * entity listesine eklenir, sürüm artırılır ve bir Migration yazılır.
 */
@Database(entities = [FavoriteCityEntity::class], version = 1, exportSchema = true)
abstract class HavaDatabase : RoomDatabase() {
    abstract fun favoriteCityDao(): FavoriteCityDao

    companion object {
        const val NAME = "hava.db"
    }
}
