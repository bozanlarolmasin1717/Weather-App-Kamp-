package com.kampplus.hava.feature.weather.presentation.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kampplus.hava.core.common.result.AppResult
import com.kampplus.hava.core.ui.state.UiState
import com.kampplus.hava.core.ui.text.toUiText
import com.kampplus.hava.feature.favorites.domain.usecase.ObserveFavoriteCityIdsUseCase
import com.kampplus.hava.feature.favorites.domain.usecase.ToggleFavoriteCityUseCase
import com.kampplus.hava.feature.weather.domain.model.City
import com.kampplus.hava.feature.weather.domain.model.CityWeather
import com.kampplus.hava.feature.weather.domain.usecase.GetCityWeathersUseCase
import com.kampplus.hava.feature.weather.presentation.model.CityWeatherUiModel
import com.kampplus.hava.feature.weather.presentation.model.WeatherUiMapper
import com.kampplus.hava.feature.weather.presentation.model.toFavorite
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class CityListViewModel @Inject constructor(
    getCityWeathers:
    GetCityWeathersUseCase,
    observeFavoriteCityIds:
    ObserveFavoriteCityIdsUseCase,
    private val toggleFavoriteCity:
    ToggleFavoriteCityUseCase,
    private val uiMapper:
    WeatherUiMapper
) : ViewModel() {

    private var loadedCities:
        Map<Long, City> =
        emptyMap()

    private val reloadTrigger =
        MutableStateFlow(0)

    @OptIn(
        ExperimentalCoroutinesApi::class
    )
    val uiState:
        StateFlow<
            UiState<
                List<CityWeatherUiModel>
                >
            > =
        combine(
            reloadTrigger
                .flatMapLatest {

                    getCityWeathers()
                        .onEach { result ->

                            if (
                                result
                                    is AppResult.Success
                            ) {
                                loadedCities =
                                    result.data
                                        .associate {
                                            it.city.id to
                                                it.city
                                        }
                            }
                        }
                        .map<
                            AppResult<
                                List<CityWeather>
                                >,
                            AppResult<
                                List<CityWeather>
                                >?
                            > {
                            it
                        }
                        .onStart {
                            emit(null)
                        }
                },
            observeFavoriteCityIds()
        ) {
                result,
                favoriteIds ->

            when (result) {

                null ->
                    UiState.Loading

                is AppResult.Success ->
                    if (
                        result.data
                            .isEmpty()
                    ) {
                        UiState.Empty
                    } else {
                        UiState.Success(
                            result.data.map {
                                uiMapper
                                    .toListItem(
                                        it,
                                        isFavorite =
                                            it.city.id in
                                                favoriteIds
                                    )
                            }
                        )
                    }

                is AppResult.Failure ->
                    UiState.Error(
                        result
                            .error
                            .toUiText()
                    )
            }
        }
            .stateIn(
                scope =
                    viewModelScope,
                started =
                    SharingStarted
                        .WhileSubscribed(
                            STOP_TIMEOUT_MS
                        ),
                initialValue =
                    UiState.Loading
            )

    fun findCity(
        cityId: Long
    ): City? =
        loadedCities[cityId]

    fun onRetry() {
        reloadTrigger.update {
            it + 1
        }
    }

    fun onToggleFavorite(
        cityId: Long
    ) {
        val city =
            loadedCities[cityId]
                ?: return

        viewModelScope.launch {
            toggleFavoriteCity(
                city.toFavorite()
            )
        }
    }

    private companion object {
        const val STOP_TIMEOUT_MS =
            5_000L
    }
}
