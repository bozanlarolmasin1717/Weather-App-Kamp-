package com.kampplus.hava.feature.favorites.domain.usecase

import com.kampplus.hava.feature.favorites.domain.model.FavoriteCity
import com.kampplus.hava.feature.favorites.domain.repository.FavoriteCityRepository
import javax.inject.Inject

/** Favori ise çıkarır, değilse ekler. Sonuçta favori olup olmadığını döner. */
class ToggleFavoriteCityUseCase @Inject constructor(
    private val repository: FavoriteCityRepository
) {
    suspend operator fun invoke(city: FavoriteCity): Boolean = if (repository.isFavorite(city.id)) {
        repository.remove(city.id)
        false
    } else {
        repository.add(city)
        true
    }
}
