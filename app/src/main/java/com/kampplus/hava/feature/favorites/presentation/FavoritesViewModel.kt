package com.kampplus.hava.feature.favorites.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kampplus.hava.core.ui.state.UiState
import com.kampplus.hava.feature.favorites.domain.model.FavoriteCity
import com.kampplus.hava.feature.favorites.domain.usecase.ObserveFavoriteCitiesUseCase
import com.kampplus.hava.feature.favorites.domain.usecase.ToggleFavoriteCityUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    observeFavoriteCities: ObserveFavoriteCitiesUseCase,
    private val toggleFavoriteCity: ToggleFavoriteCityUseCase
) : ViewModel() {

    private var favoritesById: Map<Long, FavoriteCity> = emptyMap()
    private var lastRemoved: FavoriteCity? = null

    private val _events = Channel<FavoritesEvent>(Channel.BUFFERED)

    /** Tek seferlik UI olayları (snackbar). State'e konmaz; ekran dönünce tekrar gösterilmemeli. */
    val events: Flow<FavoritesEvent> = _events.receiveAsFlow()

    val uiState: StateFlow<UiState<List<FavoriteCityUiModel>>> = observeFavoriteCities()
        .onEach { favorites -> favoritesById = favorites.associateBy { it.id } }
        .map { favorites ->
            if (favorites.isEmpty()) UiState.Empty else UiState.Success(favorites.map { it.toUiModel() })
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MS),
            initialValue = UiState.Loading
        )

    fun findFavorite(id: Long): FavoriteCity? = favoritesById[id]

    fun onRemoveFavorite(id: Long) {
        val favorite = favoritesById[id] ?: return
        viewModelScope.launch {
            toggleFavoriteCity(favorite)
            lastRemoved = favorite
            _events.send(FavoritesEvent.ShowUndo(cityName = favorite.name))
        }
    }

    fun onUndoRemove() {
        val favorite = lastRemoved ?: return
        lastRemoved = null
        viewModelScope.launch { toggleFavoriteCity(favorite) }
    }

    private fun FavoriteCity.toUiModel() = FavoriteCityUiModel(
        id = id,
        title = name,
        subtitle = listOfNotNull(region, country).distinct().joinToString(", ")
    )

    private companion object {
        const val STOP_TIMEOUT_MS = 5_000L
    }
}
