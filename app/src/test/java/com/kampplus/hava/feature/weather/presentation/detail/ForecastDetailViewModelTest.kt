package com.kampplus.hava.feature.weather.presentation.detail

import androidx.lifecycle.SavedStateHandle
import com.kampplus.hava.R
import com.kampplus.hava.core.common.error.AppError
import com.kampplus.hava.core.common.result.AppResult
import com.kampplus.hava.core.navigation.ForecastDestination
import com.kampplus.hava.core.ui.state.UiState
import com.kampplus.hava.core.ui.text.UiText
import com.kampplus.hava.feature.weather.domain.usecase.GetForecastUseCase
import com.kampplus.hava.testing.FakeWeatherRepository
import com.kampplus.hava.testing.MainDispatcherRule
import com.kampplus.hava.testing.forecast
import com.kampplus.hava.testing.testUiMapper

class ForecastDetailViewModelTest {

    @get:Rule
    val mainDispatcherRule =
        MainDispatcherRule()

    private val repository =
        FakeWeatherRepository()

    private fun createViewModel() =
        ForecastDetailViewModel(
            savedStateHandle =
                SavedStateHandle(
                    mapOf(
                        ForecastDestination
                            .ARG_CITY_ID to
                                311046L,
                        ForecastDestination
                            .ARG_NAME to
                                "İzmir",
                        ForecastDestination
                            .ARG_REGION to
                                "İzmir",
                        ForecastDestination
                            .ARG_COUNTRY to
                                "Türkiye",
                        ForecastDestination
                            .ARG_LATITUDE to
                                38.4127,
                        ForecastDestination
                            .ARG_LONGITUDE to
                                27.1384
                    )
                ),
            getForecast =
                GetForecastUseCase(
                    repository
                ),
            uiMapper =
                testUiMapper()
        )

    @Test
    fun `requests forecast for the city passed through navigation`() =
        runTest {

            repository.forecastResult = {
                AppResult.Success(
                    forecast()
                )
            }

            createViewModel()
                .uiState
                .test {

                    assertEquals(
                        UiState.Loading,
                        awaitItem()
                    )

                    val model =
                        (
                            awaitItem()
                                as UiState.Success
                            )
                            .data

                    assertEquals(
                        "İzmir",
                        model.cityName
                    )

                    assertEquals(
                        "21°",
                        model.temperatureText
                    )

                    assertEquals(
                        "12 km/sa",
                        model.windText
                    )
                }

            val requested =
                repository
                    .requestedForecasts
                    .single()

            assertEquals(
                38.4127,
                requested
                    .coordinates
                    .latitude,
                0.0
            )

            assertEquals(
                311046L,
                requested.id
            )
        }

    @Test
    fun `hourly starts from current hour and daily starts with today`() =
        runTest {

            repository.forecastResult = {
                AppResult.Success(
                    forecast()
                )
            }

            createViewModel()
                .uiState
                .test {

                    awaitItem()

                    val model =
                        (
                            awaitItem()
                                as UiState.Success
                            )
                            .data

                    assertEquals(
                        24,
                        model.hourly.size
                    )

                    assertEquals(
                        "12:00",
                        model
                            .hourly
                            .first()
                            .timeText
                    )

                    assertEquals(
                        UiText.Resource(
                            R.string.today
                        ),
                        model
                            .daily
                            .first()
                            .dayLabel
                    )

                    assertEquals(
                        "%30",
                        model
                            .daily
                            .first()
                            .precipitationText
                    )
                }
        }

    @Test
    fun `shows error when forecast fails`() =
        runTest {

            repository.forecastResult = {
                AppResult.Failure(
                    AppError.Network
                )
            }

            createViewModel()
                .uiState
                .test {

                    assertEquals(
                        UiState.Loading,
                        awaitItem()
                    )

                    assertTrue(
                        awaitItem()
                            is UiState.Error
                    )
                }
        }
}
