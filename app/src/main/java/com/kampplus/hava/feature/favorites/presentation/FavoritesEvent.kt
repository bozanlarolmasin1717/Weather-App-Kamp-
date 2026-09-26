package com.kampplus.hava.feature.favorites.presentation

sealed interface FavoritesEvent {
    data class ShowUndo(
        val cityName: String
    ) : FavoritesEvent
}
