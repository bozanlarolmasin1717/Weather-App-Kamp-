package com.kampplus.hava.feature.favorites.presentation

import com.kampplus.hava.feature.favorites.domain.model.FavoriteCity

sealed interface FavoritesEvent {

    data class ShowUndo(
        val removedCity: FavoriteCity
    ) : FavoritesEvent
}
