package com.kampplus.hava.feature.weather.data.di

import com.kampplus.hava.feature.weather.data.local.CityCatalog
import com.kampplus.hava.feature.weather.data.local.TurkishCityCatalog
import com.kampplus.hava.feature.weather.data.remote.FakeWeatherRemoteDataSource
import com.kampplus.hava.feature.weather.data.remote.WeatherRemoteDataSource
import com.kampplus.hava.feature.weather.data.repository.WeatherRepositoryImpl
import com.kampplus.hava.feature.weather.domain.repository.WeatherRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class WeatherDataModule {

    @Binds
    @Singleton
    abstract fun bindWeatherRepository(
        impl: WeatherRepositoryImpl
    ): WeatherRepository

    @Binds
    abstract fun bindWeatherRemoteDataSource(
        impl: FakeWeatherRemoteDataSource
    ): WeatherRemoteDataSource

    @Binds
    abstract fun bindCityCatalog(
        impl: TurkishCityCatalog
    ): CityCatalog
}
