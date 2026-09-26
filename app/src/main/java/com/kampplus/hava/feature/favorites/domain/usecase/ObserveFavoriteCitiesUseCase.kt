package com.kampplus.hava.feature.favorites.domain.usecase

import com.kampplus.hava.feature.favorites.domain.model.FavoriteCity
import com.kampplus.hava.feature.favorites.domain.repository.FavoriteCityRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class ObserveFavoriteCitiesUseCase @Inject constructor(
    private val repository: FavoriteCityRepository
) {
    operator fun invoke(): Flow<List<FavoriteCity>> = repository.observeFavorites()
}
