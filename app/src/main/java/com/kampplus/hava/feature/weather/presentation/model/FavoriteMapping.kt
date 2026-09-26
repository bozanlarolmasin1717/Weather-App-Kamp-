package com.kampplus.hava.feature.weather.presentation.model

import com.kampplus.hava.feature.favorites.domain.model.FavoriteCity
import com.kampplus.hava.feature.weather.domain.model.City
import com.kampplus.hava.feature.weather.domain.model.Coordinates

/** Hava özelliği, favoriler özelliğinin domain modelini tanır; tersi geçerli değildir. */
fun City.toFavorite() = FavoriteCity(
    id = id,
    name = name,
    region = region,
    country = country,
    latitude = coordinates.latitude,
    longitude = coordinates.longitude
)

fun FavoriteCity.toCity() = City(
    id = id,
    name = name,
    region = region,
    country = country,
    coordinates = Coordinates(latitude = latitude, longitude = longitude)
)
