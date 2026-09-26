package com.kampplus.hava.feature.weather.presentation.list

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kampplus.hava.feature.weather.domain.model.City

@Composable
fun CityListRoute(
    onCityClick: (City) -> Unit,
    modifier: Modifier = Modifier,
    viewModel:
    CityListViewModel =
        hiltViewModel()
) {
    val uiState by
    viewModel
        .uiState
        .collectAsStateWithLifecycle()

    CityListScreen(
        uiState = uiState,
        onCityClick = { cityId ->

            viewModel
                .findCity(cityId)
                ?.let(onCityClick)
        },
        modifier = modifier
    )
}
