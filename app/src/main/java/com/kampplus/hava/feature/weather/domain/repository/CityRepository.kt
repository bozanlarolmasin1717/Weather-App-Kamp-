package com.kampplus.hava.feature.weather.domain.repository

import com.kampplus.hava.core.common.result.AppResult
import com.kampplus.hava.feature.weather.domain.model.City

interface CityRepository {
    /** Ada göre şehir arar. Sonuç yoksa boş liste (hata değil). */
    suspend fun search(query: String): AppResult<List<City>>
}
