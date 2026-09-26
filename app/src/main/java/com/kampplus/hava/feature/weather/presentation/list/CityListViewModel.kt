package com.kampplus.hava.feature.weather.presentation.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kampplus.hava.R
import com.kampplus.hava.core.common.result.AppResult
import com.kampplus.hava.core.ui.state.UiState
import com.kampplus.hava.core.ui.text.UiText
import com.kampplus.hava.feature.weather.domain.model.City
import com.kampplus.hava.feature.weather.domain.usecase.GetCityWeathersUseCase
import com.kampplus.hava.feature.weather.presentation.model.CityWeatherUiModel
import com.kampplus.hava.feature.weather.presentation.model.WeatherUiMapper
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn

@HiltViewModel
class CityListViewModel @Inject constructor(
    getCityWeathers:
    GetCityWeathersUseCase,
    private val uiMapper:
    WeatherUiMapper
) : ViewModel() {

    private var loadedCities:
        Map<Long, City> =
        emptyMap()

    val uiState:
        StateFlow<
            UiState<
                List<CityWeatherUiModel>
                >
            > =
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
            .map { result ->

                when (result) {

                    is AppResult.Success ->
                        if (
                            result.data
                                .isEmpty()
                        ) {
                            UiState.Empty
                        } else {
                            UiState.Success(
                                result.data.map(
                                    uiMapper::toListItem
                                )
                            )
                        }

                    is AppResult.Failure ->
                        UiState.Error(
                            UiText.Resource(
                                R.string.error_generic
                            )
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

    private companion object {
        const val STOP_TIMEOUT_MS =
            5_000L
    }
}
