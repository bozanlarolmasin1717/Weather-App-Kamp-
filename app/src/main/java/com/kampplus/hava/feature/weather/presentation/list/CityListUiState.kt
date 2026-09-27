package com.kampplus.hava.feature.weather.presentation.list

import com.kampplus.hava.core.ui.state.UiState
import com.kampplus.hava.feature.weather.presentation.model.CityWeatherUiModel

data class CityListUiState(
    val query: String = "",
    val content: UiState<
        List<CityWeatherUiModel>
        > =
        UiState.Loading,
    val isRefreshing: Boolean =
        false
) {

    val isSearching:
        Boolean
        get() =
            query.trim().length >=
                CityListViewModel
                    .MIN_QUERY_LENGTH
}
