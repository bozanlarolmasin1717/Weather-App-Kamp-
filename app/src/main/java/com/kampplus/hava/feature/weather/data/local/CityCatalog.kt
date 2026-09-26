package com.kampplus.hava.feature.weather.data.local

import com.kampplus.hava.feature.weather.domain.model.City

/** Liste ekranında gösterilecek öne çıkan şehirler. Farklı bir liste = yeni implementasyon. */
fun interface CityCatalog {
    fun cities(): List<City>
}
