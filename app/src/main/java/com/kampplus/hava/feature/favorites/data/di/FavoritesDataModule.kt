package com.kampplus.hava.feature.favorites.data.di

import com.kampplus.hava.feature.favorites.data.local.FavoriteCityLocalDataSource
import com.kampplus.hava.feature.favorites.data.local.InMemoryFavoriteCityDataSource
import com.kampplus.hava.feature.favorites.data.repository.FavoriteCityRepositoryImpl
import com.kampplus.hava.feature.favorites.domain.repository.FavoriteCityRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class FavoritesDataModule {
    @Binds
    abstract fun bindFavoriteCityRepository(impl: FavoriteCityRepositoryImpl): FavoriteCityRepository

    @Binds
    abstract fun bindFavoriteCityLocalDataSource(impl: InMemoryFavoriteCityDataSource): FavoriteCityLocalDataSource
}
