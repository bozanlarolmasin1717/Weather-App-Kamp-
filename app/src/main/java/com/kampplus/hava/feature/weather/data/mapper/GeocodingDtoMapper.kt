package com.kampplus.hava.feature.weather.data.mapper

import com.kampplus.hava.feature.weather.data.remote.dto.GeocodingResultDto
import com.kampplus.hava.feature.weather.domain.model.City
import com.kampplus.hava.feature.weather.domain.model.Coordinates

fun GeocodingResultDto.toDomain() = City(
    id = id,
    name = name,
    region = region,
    country = country,
    coordinates = Coordinates(latitude = latitude, longitude = longitude)
)
