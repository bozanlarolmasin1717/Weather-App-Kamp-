package com.kampplus.hava.feature.favorites.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kampplus.hava.core.common.result.AppResult
import com.kampplus.hava.core.ui.state.UiState
import com.kampplus.hava.feature.favorites.domain.model.FavoriteCity
import com.kampplus.hava.feature.favorites.domain.usecase.ObserveFavoriteCitiesUseCase
import com.kampplus.hava.feature.favorites.domain.usecase.RestoreFavoriteCityUseCase
import com.kampplus.hava.feature.favorites.domain.usecase.ToggleFavoriteCityUseCase
import com.kampplus.hava.feature.weather.domain.model.City
import com.kampplus.hava.feature.weather.domain.model.CityWeather
import com.kampplus.hava.feature.weather.domain.model.Coordinates
import com.kampplus.hava.feature.weather.domain.usecase.GetCurrentWeatherUseCase
import com.kampplus.hava.feature.weather.presentation.model.WeatherUiMapper
import com.kampplus.hava.feature.weather.presentation.visual.WeatherVisualState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
@OptIn(ExperimentalCoroutinesApi::class)
class FavoritesViewModel @Inject constructor(
    observeFavoriteCities: ObserveFavoriteCitiesUseCase,
    private val getCurrentWeather: GetCurrentWeatherUseCase,
    private val toggleFavoriteCity: ToggleFavoriteCityUseCase,
    private val restoreFavoriteCity: RestoreFavoriteCityUseCase,
    private val weatherUiMapper: WeatherUiMapper
) : ViewModel() {

    private var favoritesById: Map<Long, FavoriteCity> = emptyMap()

    private val _events = Channel<FavoritesEvent>(Channel.BUFFERED)
    val events: Flow<FavoritesEvent> = _events.receiveAsFlow()

    val uiState: StateFlow<UiState<List<FavoriteCityUiModel>>> = observeFavoriteCities()
        .flatMapLatest { favorites ->
            favoritesById = favorites.associateBy { it.id }

            if (favorites.isEmpty()) {
                flowOf(UiState.Empty)
            } else {
                getCurrentWeather(favorites.map { it.toCity() })
                    .map { result ->
                        val weatherById = when (result) {
                            is AppResult.Success -> result.data.associateBy { it.city.id }
                            is AppResult.Failure -> emptyMap()
                        }
                        UiState.Success(
                            favorites.map { favorite ->
                                favorite.toUiModel(weatherById[favorite.id])
                            }
                        )
                    }
            }
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
            _events.send(
                FavoritesEvent.ShowUndo(
                    removedCity = favorite
                )
            )
        }
    }

    fun onUndoRemove(favorite: FavoriteCity) {
        viewModelScope.launch {
            restoreFavoriteCity(favorite)
        }
    }

    private fun FavoriteCity.toUiModel(weather: CityWeather?): FavoriteCityUiModel {
        val weatherUi = weather?.let { weatherUiMapper.toListItem(it) }
        return FavoriteCityUiModel(
            id = id,
            title = name,
            subtitle = listOfNotNull(region, country)
                .distinct()
                .joinToString(", "),
            temperatureText = weatherUi?.temperatureText,
            conditionEmoji = weatherUi?.conditionEmoji,
            conditionLabel = weatherUi?.conditionLabel,
            visualState = weatherUi?.visualState
                ?: WeatherVisualState.ClearNight
        )
    }

    private fun FavoriteCity.toCity() = City(
        id = id,
        name = name,
        region = region,
        country = country,
        coordinates = Coordinates(latitude, longitude)
    )

    private companion object {
        const val STOP_TIMEOUT_MS = 5_000L
    }
}
