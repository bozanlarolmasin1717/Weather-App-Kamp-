package com.kampplus.hava.feature.weather.presentation.detail

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.kampplus.hava.R
import com.kampplus.hava.core.common.error.AppError
import com.kampplus.hava.core.common.result.AppResult
import com.kampplus.hava.core.navigation.ForecastDestination
import com.kampplus.hava.core.ui.state.UiState
import com.kampplus.hava.core.ui.text.UiText
import com.kampplus.hava.feature.favorites.data.local.InMemoryFavoriteCityDataSource
import com.kampplus.hava.feature.favorites.data.repository.FavoriteCityRepositoryImpl
import com.kampplus.hava.feature.favorites.domain.usecase.ObserveFavoriteCityIdsUseCase
import com.kampplus.hava.feature.favorites.domain.usecase.ToggleFavoriteCityUseCase
import com.kampplus.hava.feature.weather.domain.model.HourlyForecast
import com.kampplus.hava.feature.weather.domain.model.WeatherCode
import com.kampplus.hava.feature.weather.domain.usecase.GetForecastUseCase
import com.kampplus.hava.feature.weather.presentation.visual.WeatherVisualState
import com.kampplus.hava.testing.FakeWeatherRepository
import com.kampplus.hava.testing.MainDispatcherRule
import com.kampplus.hava.testing.forecast
import com.kampplus.hava.testing.testUiMapper
import java.time.LocalDateTime
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class ForecastDetailViewModelTest {

    @get:Rule
    val mainDispatcherRule =
        MainDispatcherRule()

    private val repository =
        FakeWeatherRepository()

    private val favoritesRepository =
        FavoriteCityRepositoryImpl(
            InMemoryFavoriteCityDataSource()
        )

    private fun createViewModel() = ForecastDetailViewModel(
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
        observeFavoriteCityIds =
        ObserveFavoriteCityIdsUseCase(
            favoritesRepository
        ),
        toggleFavoriteCity =
        ToggleFavoriteCityUseCase(
            favoritesRepository
        ),
        uiMapper =
        testUiMapper()
    )

    @Test
    fun `requests forecast for the city passed through navigation`() = runTest {
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
    fun `hourly starts from current hour and daily starts with today`() = runTest {
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
                    LocalDateTime.of(2026, 9, 24, 12, 0),
                    model.hourly.first().time
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
    fun `today label follows the city local forecast date instead of list position`() = runTest {
        val base = forecast()
        val withPreviousDay = base.copy(
            timeZoneId = "Europe/Berlin",
            daily = listOf(
                base.daily.first().copy(
                    date = base.current.observedAt.toLocalDate().minusDays(1)
                )
            ) + base.daily
        )

        repository.forecastResult = {
            AppResult.Success(withPreviousDay)
        }

        createViewModel()
            .uiState
            .test {
                awaitItem()
                val model = (awaitItem() as UiState.Success).data

                assertTrue(model.daily.first().dayLabel is UiText.Dynamic)
                assertEquals(
                    UiText.Resource(R.string.today),
                    model.daily[1].dayLabel
                )
                assertEquals(
                    base.current.observedAt.toLocalDate(),
                    model.daily[1].date
                )
                assertEquals("Europe/Berlin", model.timeZoneId)
            }
    }

    @Test
    fun `maps insight timeline atmosphere and today's solar metrics`() = runTest {
        val base = forecast()
        val date = base.current.observedAt.toLocalDate()
        repository.forecastResult = {
            AppResult.Success(
                base.copy(
                    current = base.current.copy(isDay = true),
                    hourly = listOf(
                        HourlyForecast(
                            time = date.atTime(13, 0),
                            temperatureC = 21.0,
                            weatherCode = WeatherCode(1),
                            precipitationProbability = 10,
                            isDay = true
                        ),
                        HourlyForecast(
                            time = date.atTime(14, 0),
                            temperatureC = 20.0,
                            weatherCode = WeatherCode(61),
                            precipitationProbability = 80,
                            precipitationMm = 0.8,
                            isDay = true
                        ),
                        HourlyForecast(
                            time = date.atTime(15, 0),
                            temperatureC = 19.0,
                            weatherCode = WeatherCode(61),
                            precipitationProbability = 70,
                            precipitationMm = 0.4,
                            isDay = true
                        ),
                        HourlyForecast(
                            time = date.atTime(16, 0),
                            temperatureC = 19.0,
                            weatherCode = WeatherCode(2),
                            precipitationProbability = 10,
                            isDay = true
                        )
                    ),
                    daily = listOf(
                        base.daily.first().copy(
                            sunrise = date.atTime(6, 42),
                            sunset = date.atTime(18, 48)
                        )
                    )
                )
            )
        }

        createViewModel().uiState.test {
            awaitItem()
            val model = (awaitItem() as UiState.Success).data

            assertEquals(WeatherVisualState.ClearSunny, model.visualState)
            assertEquals(UiText.Resource(R.string.insight_rain_afternoon), model.insight?.headline)
            assertEquals("14:00", model.insight?.timeText)
            assertEquals(2, model.timeline.size)
            assertEquals(UiText.Resource(R.string.timeline_rain_begins), model.timeline.first().headline)
            assertEquals("24°", model.highText)
            assertEquals("12°", model.lowText)
            assertEquals("06:42", model.sunriseText)
            assertEquals("18:48", model.sunsetText)
        }
    }

    @Test
    fun `shows error when forecast fails`() = runTest {
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
