package com.kampplus.hava.feature.favorites.domain.usecase

import com.kampplus.hava.feature.favorites.domain.repository.FavoriteCityRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

/** Liste ve detay ekranlarının "favori mi?" sorusunu tek kaynaktan cevaplaması için. */
class ObserveFavoriteCityIdsUseCase @Inject constructor(
    private val repository: FavoriteCityRepository
) {
    operator fun invoke(): Flow<Set<Long>> = repository.observeFavorites()
        .map { favorites -> favorites.mapTo(mutableSetOf()) { it.id } }
        .distinctUntilChanged()
}
