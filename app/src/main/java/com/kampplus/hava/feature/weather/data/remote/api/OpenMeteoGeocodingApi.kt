package com.kampplus.hava.feature.weather.data.remote.api

import com.kampplus.hava.feature.weather.data.remote.dto.GeocodingResponseDto
import retrofit2.http.GET
import retrofit2.http.Query

interface OpenMeteoGeocodingApi {
    @GET("search")
    suspend fun search(
        @Query("name") name: String,
        @Query("count") count: Int = 10,
        @Query("language") language: String = "tr",
        @Query("format") format: String = "json"
    ): GeocodingResponseDto
}
