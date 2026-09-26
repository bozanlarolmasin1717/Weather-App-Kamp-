package com.kampplus.hava.feature.weather.data.remote.api

import com.kampplus.hava.feature.weather.data.remote.dto.ForecastResponseDto
import retrofit2.http.GET
import retrofit2.http.Query

interface OpenMeteoForecastApi {

    @GET("forecast")
    suspend fun getForecast(
        @Query("latitude")
        latitude: String,
        @Query("longitude")
        longitude: String,
        @Query("current")
        current: String,
        @Query("hourly")
        hourly: String? = null,
        @Query("daily")
        daily: String? = null,
        @Query("forecast_days")
        forecastDays: Int? = null,
        @Query("timezone")
        timezone: String = "auto"
    ): ForecastResponseDto

    @GET("forecast")
    suspend fun getForecasts(
        @Query("latitude")
        latitudes: String,
        @Query("longitude")
        longitudes: String,
        @Query("current")
        current: String,
        @Query("timezone")
        timezone: String = "auto"
    ): List<ForecastResponseDto>
}
