package com.kampplus.hava.feature.weather.domain.model

/** Bir konum. [id] geocoding servisinin kimliğidir; favoriler bu kimlikle eşleşir. */
data class City(
    val id: Long,
    val name: String,
    val region: String?,
    val country: String?,
    val coordinates: Coordinates
)
