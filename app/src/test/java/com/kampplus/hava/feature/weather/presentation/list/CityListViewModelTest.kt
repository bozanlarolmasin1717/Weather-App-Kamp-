package com.kampplus.hava.feature.weather.presentation.list

import com.kampplus.hava.core.common.error.AppError
import com.kampplus.hava.core.common.result.AppResult
import com.kampplus.hava.core.ui.state.UiState
import com.kampplus.hava.feature.weather.domain.usecase.GetCityWeathersUseCase
import com.kampplus.hava.testing.FakeWeatherRepository
import com.kampplus.hava.testing.MainDispatcherRule
import com.kampplus.hava.testing.city
import com.kampplus.hava.testing.cityWeather
import com.kampplus.hava.testing.testUiMapper

class CityListViewModelTest {

    @get:Rule
    val mainDispatcherRule =
        MainDispatcherRule()

    private val repository =
        FakeWeatherRepository()

    private fun createViewModel() =
        CityListViewModel(
            getCityWeathers =
                GetCityWeathersUseCase(
                    repository
                ),
            uiMapper =
                testUiMapper()
        )

    @Test
    fun `emits loading then formatted city weathers`() =
        runTest {

            repository.cityWeathersResult = {

                AppResult.Success(
                    listOf(
                        cityWeather(
                            city =
                                city(
                                    name =
                                        "İzmir",
                                    region =
                                        "İzmir"
                                ),
                            temperatureC =
                                26.6
                        )
                    )
                )
            }

            createViewModel()
                .uiState
                .test {

                    assertEquals(
                        UiState.Loading,
                        awaitItem()
                    )

                    val item =
                        (
                            awaitItem()
                                as UiState.Success
                            )
                            .data
                            .single()

                    assertEquals(
                        "İzmir",
                        item.title
                    )

                    assertEquals(
                        "İzmir, Türkiye",
                        item.subtitle
                    )

                    assertEquals(
                        "27°",
                        item.temperatureText
                    )
                }
        }

    @Test
    fun `emits empty when there is no city`() =
        runTest {

            repository.cityWeathersResult = {
                AppResult.Success(
                    emptyList()
                )
            }

            createViewModel()
                .uiState
                .test {

                    assertEquals(
                        UiState.Loading,
                        awaitItem()
                    )

                    assertEquals(
                        UiState.Empty,
                        awaitItem()
                    )
                }
        }

    @Test
    fun `emits error when repository fails`() =
        runTest {

            repository.cityWeathersResult = {
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
