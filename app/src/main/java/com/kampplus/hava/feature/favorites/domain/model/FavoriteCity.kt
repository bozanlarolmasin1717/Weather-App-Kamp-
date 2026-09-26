package com.kampplus.hava.feature.favorites.domain.model

/**
 * Favoriye eklenen şehrin kendi başına saklanan özeti. Favoriler ekranı hava verisi
 * olmadan da (ağ yokken) listelenebilir.
 */
data class FavoriteCity(
    val id: Long,
    val name: String,
    val region: String?,
    val country: String?,
    val latitude: Double,
    val longitude: Double
)
