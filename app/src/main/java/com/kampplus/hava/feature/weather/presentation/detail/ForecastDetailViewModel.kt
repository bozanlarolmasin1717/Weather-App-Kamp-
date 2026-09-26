package com.kampplus.hava.feature.weather.presentation.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kampplus.hava.R
import com.kampplus.hava.core.common.result.AppResult
import com.kampplus.hava.core.navigation.ForecastDestination
import com.kampplus.hava.core.ui.state.UiState
import com.kampplus.hava.core.ui.text.UiText
import com.kampplus.hava.feature.weather.domain.model.City
import com.kampplus.hava.feature.weather.domain.model.Coordinates
import com.kampplus.hava.feature.weather.domain.model.Forecast
import com.kampplus.hava.feature.weather.domain.model.WeatherCode
import com.kampplus.hava.feature.weather.domain.policy.WeatherConditionClassifier
import com.kampplus.hava.feature.weather.domain.usecase.GetForecastUseCase
import com.kampplus.hava.feature.weather.presentation.model.DailyUiModel
import com.kampplus.hava.feature.weather.presentation.model.ForecastUiModel
import com.kampplus.hava.feature.weather.presentation.model.HourlyUiModel
import com.kampplus.hava.feature.weather.presentation.model.WeatherConditionUi
import com.kampplus.hava.feature.weather.presentation.model.WeatherConditionUiRegistry
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Locale
import javax.inject.Inject
import kotlin.math.roundToInt
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class ForecastDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getForecast: GetForecastUseCase,
    private val conditionClassifier: WeatherConditionClassifier,
    private val conditionUiRegistry: WeatherConditionUiRegistry
) : ViewModel() {

    private val city = City(
        id = checkNotNull(
            savedStateHandle[ForecastDestination.ARG_CITY_ID]
        ),
        name = checkNotNull(
            savedStateHandle[ForecastDestination.ARG_NAME]
        ),
        region = savedStateHandle[
            ForecastDestination.ARG_REGION
        ],
        country = savedStateHandle[
            ForecastDestination.ARG_COUNTRY
        ],
        coordinates = Coordinates(
            latitude = checkNotNull(
                savedStateHandle[
                    ForecastDestination.ARG_LATITUDE
                ]
            ),
            longitude = checkNotNull(
                savedStateHandle[
                    ForecastDestination.ARG_LONGITUDE
                ]
            )
        )
    )

    private val _uiState =
        MutableStateFlow<UiState<ForecastUiModel>>(
            UiState.Loading
        )

    val uiState: StateFlow<UiState<ForecastUiModel>> =
        _uiState.asStateFlow()

    init {
        load()
    }

    private fun load() {
        viewModelScope.launch {

            _uiState.value = UiState.Loading

            _uiState.value =
                when (
                    val result =
                        getForecast(city)
                ) {
                    is AppResult.Success ->
                        UiState.Success(
                            result.data.toUiModel()
                        )

                    is AppResult.Failure ->
                        UiState.Error(
                            UiText.Resource(
                                R.string.error_generic
                            )
                        )
                }
        }
    }

    private fun Forecast.toUiModel():
        ForecastUiModel {

        val conditionUi =
            conditionUi(
                current.weatherCode
            )

        val currentHour =
            current.observedAt
                .truncatedTo(
                    ChronoUnit.HOURS
                )

        return ForecastUiModel(
            cityId = city.id,
            cityName = city.name,
            subtitle =
                listOfNotNull(
                    city.region,
                    city.country
                )
                    .distinct()
                    .joinToString(", "),
            temperatureText =
                degrees(
                    current.temperatureC
                ),
            temperatureC =
                current.temperatureC,
            conditionEmoji =
                conditionUi.emoji,
            conditionLabel =
                conditionUi.label,
            feelsLikeText =
                current
                    .apparentTemperatureC
                    ?.let(::degrees),
            humidityText =
                current
                    .humidityPercent
                    ?.let {
                        "%$it"
                    },
            windText =
                current
                    .windSpeedKmh
                    ?.let {
                        "${it.roundToInt()} km/sa"
                    },
            hourly =
                hourly
                    .filter {
                        !it.time.isBefore(
                            currentHour
                        )
                    }
                    .take(HOURLY_COUNT)
                    .map { hour ->

                        HourlyUiModel(
                            timeText =
                                hour.time.format(
                                    HOUR_FORMAT
                                ),
                            emoji =
                                conditionUi(
                                    hour.weatherCode
                                ).emoji,
                            temperatureText =
                                degrees(
                                    hour.temperatureC
                                ),
                            precipitationText =
                                hour
                                    .precipitationProbability
                                    ?.takeIf {
                                        it > 0
                                    }
                                    ?.let {
                                        "%$it"
                                    }
                        )
                    },
            daily =
                daily.mapIndexed {
                        index,
                        day ->

                    DailyUiModel(
                        dayLabel =
                            if (index == 0) {
                                UiText.Resource(
                                    R.string.today
                                )
                            } else {
                                UiText.Dynamic(
                                    day.date
                                        .format(
                                            DAY_FORMAT
                                        )
                                        .replaceFirstChar(
                                            Char::titlecase
                                        )
                                )
                            },
                        emoji =
                            conditionUi(
                                day.weatherCode
                            ).emoji,
                        minText =
                            degrees(
                                day.minTemperatureC
                            ),
                        maxText =
                            degrees(
                                day.maxTemperatureC
                            ),
                        precipitationText =
                            day
                                .precipitationProbability
                                ?.takeIf {
                                    it > 0
                                }
                                ?.let {
                                    "%$it"
                                }
                    )
                }
        )
    }

    private fun conditionUi(
        code: WeatherCode
    ): WeatherConditionUi =
        conditionUiRegistry.resolve(
            conditionClassifier.classify(
                code
            )
        )

    private fun degrees(
        celsius: Double
    ) = "${celsius.roundToInt()}°"

    private companion object {

        const val HOURLY_COUNT = 24

        val HOUR_FORMAT:
            DateTimeFormatter =
            DateTimeFormatter.ofPattern(
                "HH:mm"
            )

        val DAY_FORMAT:
            DateTimeFormatter =
            DateTimeFormatter.ofPattern(
                "EEEE",
                Locale.forLanguageTag("tr")
            )
    }
}
