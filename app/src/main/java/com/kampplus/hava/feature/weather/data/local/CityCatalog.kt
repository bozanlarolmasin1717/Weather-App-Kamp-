package com.kampplus.hava.feature.weather.data.local

import com.kampplus.hava.feature.weather.domain.model.City

fun interface CityCatalog {
    fun cities(): List<City>
}
