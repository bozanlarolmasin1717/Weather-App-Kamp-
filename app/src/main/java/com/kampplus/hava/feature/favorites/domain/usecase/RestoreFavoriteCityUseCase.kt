package com.kampplus.hava.feature.favorites.domain.usecase

import com.kampplus.hava.feature.favorites.domain.model.FavoriteCity
import com.kampplus.hava.feature.favorites.domain.repository.FavoriteCityRepository
import javax.inject.Inject

class RestoreFavoriteCityUseCase @Inject constructor(
    private val repository: FavoriteCityRepository
) {
    suspend operator fun invoke(city: FavoriteCity) = repository.add(city)
}
