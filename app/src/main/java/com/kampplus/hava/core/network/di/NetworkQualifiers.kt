package com.kampplus.hava.core.network.di

import javax.inject.Qualifier

/** Open-Meteo iki ayrı host kullanır; her biri için ayrı Retrofit örneği vardır. */
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class ForecastRetrofit

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class GeocodingRetrofit
