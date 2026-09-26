package com.kampplus.hava.feature.favorites.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kampplus.hava.feature.favorites.domain.model.FavoriteCity

@Composable
fun FavoritesRoute(
    onCityClick:
        (FavoriteCity) -> Unit,
    modifier: Modifier = Modifier,
    viewModel:
    FavoritesViewModel =
        hiltViewModel()
) {
    val uiState by
    viewModel
        .uiState
        .collectAsStateWithLifecycle()

    FavoritesScreen(
        uiState = uiState,
        onCityClick = { id ->

            viewModel
                .findFavorite(id)
                ?.let(onCityClick)
        },
        onRemoveFavorite =
            viewModel::onRemoveFavorite,
        modifier = modifier
    )
}
