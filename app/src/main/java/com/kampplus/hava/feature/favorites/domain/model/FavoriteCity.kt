package com.kampplus.hava.feature.favorites.domain.model

data class FavoriteCity(
    val id: Long,
    val name: String,
    val region: String?,
    val country: String?,
    val latitude: Double,
    val longitude: Double
)
