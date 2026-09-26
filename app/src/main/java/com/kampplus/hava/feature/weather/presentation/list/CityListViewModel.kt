package com.kampplus.hava.feature.weather.presentation.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kampplus.hava.R
import com.kampplus.hava.core.common.result.AppResult
import com.kampplus.hava.core.ui.state.UiState
import com.kampplus.hava.core.ui.text.UiText
import com.kampplus.hava.feature.favorites.domain.usecase.ObserveFavoriteCityIdsUseCase
import com.kampplus.hava.feature.favorites.domain.usecase.ToggleFavoriteCityUseCase
import com.kampplus.hava.feature.weather.domain.model.City
import com.kampplus.hava.feature.weather.domain.usecase.GetCityWeathersUseCase
import com.kampplus.hava.feature.weather.presentation.model.CityWeatherUiModel
import com.kampplus.hava.feature.weather.presentation.model.WeatherUiMapper
import com.kampplus.hava.feature.weather.presentation.model.toFavorite
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class CityListViewModel @Inject constructor(
    getCityWeathers: GetCityWeathersUseCase,
    observeFavoriteCityIds: ObserveFavoriteCityIdsUseCase,
    private val toggleFavoriteCity: ToggleFavoriteCityUseCase,
    private val uiMapper: WeatherUiMapper
) : ViewModel() {

    private var loadedCities: Map<Long, City> = emptyMap()

    val uiState: StateFlow<UiState<List<CityWeatherUiModel>>> = combine(
        getCityWeathers().onEach { result ->
            if (result is AppResult.Success) loadedCities = result.data.associate { it.city.id to it.city }
        },
        observeFavoriteCityIds()
    ) { result, favoriteIds ->
        when (result) {
            is AppResult.Success ->
                if (result.data.isEmpty()) {
                    UiState.Empty
                } else {
                    UiState.Success(result.data.map { uiMapper.toListItem(it, isFavorite = it.city.id in favoriteIds) })
                }

            is AppResult.Failure -> UiState.Error(UiText.Resource(R.string.error_generic))
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MS),
        initialValue = UiState.Loading
    )

    fun findCity(cityId: Long): City? = loadedCities[cityId]

    fun onToggleFavorite(cityId: Long) {
        val city = loadedCities[cityId] ?: return
        viewModelScope.launch { toggleFavoriteCity(city.toFavorite()) }
    }

    private companion object {
        const val STOP_TIMEOUT_MS = 5_000L
    }
}
