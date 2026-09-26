package com.kampplus.hava.core.navigation

import kotlinx.serialization.Serializable

@Serializable
data object ListDestination

@Serializable
data class ForecastDestination(
    val cityId: Long,
    val name: String,
    val region: String?,
    val country: String?,
    val latitude: Double,
    val longitude: Double
) {
    companion object {
        const val ARG_CITY_ID = "cityId"
        const val ARG_NAME = "name"
        const val ARG_REGION = "region"
        const val ARG_COUNTRY = "country"
        const val ARG_LATITUDE = "latitude"
        const val ARG_LONGITUDE = "longitude"
    }
}
