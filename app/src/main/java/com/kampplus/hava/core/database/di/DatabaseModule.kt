package com.kampplus.hava.core.database.di

import android.content.Context
import androidx.room.Room
import com.kampplus.hava.core.database.HavaDatabase
import com.kampplus.hava.feature.favorites.data.local.dao.FavoriteCityDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): HavaDatabase =
        Room.databaseBuilder(context, HavaDatabase::class.java, HavaDatabase.NAME).build()

    @Provides
    fun provideFavoriteCityDao(database: HavaDatabase): FavoriteCityDao = database.favoriteCityDao()
}
