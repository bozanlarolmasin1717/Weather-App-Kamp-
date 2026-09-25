package com.kampplus.hava.feature.weather.data.local

import com.kampplus.hava.feature.weather.domain.model.City
import com.kampplus.hava.feature.weather.domain.model.Coordinates
import javax.inject.Inject

class TurkishCityCatalog @Inject constructor() : CityCatalog {

    override fun cities(): List<City> = CITIES

    private companion object {

        val CITIES = listOf(
            city(745044, "İstanbul", "İstanbul", 41.0138, 28.9497),
            city(323786, "Ankara", "Ankara", 39.9199, 32.8543),
            city(311046, "İzmir", "İzmir", 38.4127, 27.1384),
            city(750269, "Bursa", "Bursa", 40.1956, 29.0601),
            city(323777, "Antalya", "Antalya", 36.9081, 30.6956),
            city(325363, "Adana", "Adana", 36.9862, 35.3253),
            city(306571, "Konya", "Konya", 37.8713, 32.4846),
            city(314830, "Gaziantep", "Gaziantep", 37.0594, 37.3825),
            city(298333, "Şanlıurfa", "Şanlıurfa", 37.1671, 38.7939),
            city(745028, "İzmit", "Kocaeli", 40.7650, 29.9293),
            city(304531, "Mersin", "Mersin", 36.8120, 34.6389),
            city(316541, "Diyarbakır", "Diyarbakır", 37.9136, 40.2172),
            city(308464, "Kayseri", "Kayseri", 38.7322, 35.4853),
            city(315202, "Eskişehir", "Eskişehir", 39.7767, 30.5206),
            city(740264, "Samsun", "Samsun", 41.2798, 36.3361),
            city(738648, "Trabzon", "Trabzon", 41.0050, 39.7269),
            city(315368, "Erzurum", "Erzurum", 39.9086, 41.2769),
            city(298117, "Van", "Van", 38.4946, 43.3832),
            city(304922, "Malatya", "Malatya", 38.3502, 38.3167),
            city(317109, "Denizli", "Denizli", 37.7742, 29.0875)
        )

        fun city(
            id: Long,
            name: String,
            region: String,
            latitude: Double,
            longitude: Double
        ) = City(
            id = id,
            name = name,
            region = region,
            country = "Türkiye",
            coordinates = Coordinates(
                latitude = latitude,
                longitude = longitude
            )
        )
    }
}
