package com.kampplus.hava.feature.weather.presentation.model

import com.kampplus.hava.R
import com.kampplus.hava.core.ui.text.UiText
import com.kampplus.hava.feature.weather.domain.model.City
import com.kampplus.hava.feature.weather.domain.model.CityWeather
import com.kampplus.hava.feature.weather.domain.model.Forecast
import com.kampplus.hava.feature.weather.domain.model.TimelineEvent
import com.kampplus.hava.feature.weather.domain.model.TimelineEventAdvice
import com.kampplus.hava.feature.weather.domain.model.TimelineEventHeadline
import com.kampplus.hava.feature.weather.domain.model.WeatherCode
import com.kampplus.hava.feature.weather.domain.model.WeatherInsight
import com.kampplus.hava.feature.weather.domain.model.WeatherInsightAdvice
import com.kampplus.hava.feature.weather.domain.model.WeatherInsightHeadline
import com.kampplus.hava.feature.weather.domain.policy.WeatherConditionClassifier
import com.kampplus.hava.feature.weather.domain.policy.WeatherInsightGenerator
import com.kampplus.hava.feature.weather.domain.policy.WeatherTimelineGenerator
import com.kampplus.hava.feature.weather.presentation.visual.WeatherVisualStateResolver
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Locale
import javax.inject.Inject
import kotlin.math.roundToInt

class WeatherUiMapper @Inject constructor(
    private val conditionClassifier: WeatherConditionClassifier,
    private val conditionUiRegistry: WeatherConditionUiRegistry,
    private val visualStateResolver: WeatherVisualStateResolver,
    private val insightGenerator: WeatherInsightGenerator,
    private val timelineGenerator: WeatherTimelineGenerator
) {

    fun toListItem(cityWeather: CityWeather, isFavorite: Boolean = false): CityWeatherUiModel = with(cityWeather) {
        val conditionUi =
            conditionUi(
                current.weatherCode
            )

        CityWeatherUiModel(
            cityId = city.id,
            title = city.name,
            subtitle = subtitle(city),
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
            visualState = visualStateResolver.resolve(
                weatherCode = current.weatherCode,
                isDay = current.isDay,
                cityLocalTime = current.observedAt
            ),
            isFavorite =
            isFavorite
        )
    }

    fun toForecast(city: City, forecast: Forecast, isFavorite: Boolean = false): ForecastUiModel = with(forecast) {
        val conditionUi =
            conditionUi(
                current.weatherCode
            )

        val currentHour =
            current.observedAt
                .truncatedTo(
                    ChronoUnit.HOURS
                )

        val today = daily.firstOrNull {
            it.date == current.observedAt.toLocalDate()
        }

        ForecastUiModel(
            cityId = city.id,
            cityName = city.name,
            subtitle = subtitle(city),
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
            isDay = current.isDay,
            timeZoneId = timeZoneId,
            visualState = visualStateResolver.resolve(
                weatherCode = current.weatherCode,
                isDay = current.isDay,
                cityLocalTime = current.observedAt
            ),
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
            highText = today?.maxTemperatureC?.let(::degrees),
            lowText = today?.minTemperatureC?.let(::degrees),
            sunriseText = today?.sunrise?.format(HOUR_FORMAT),
            sunsetText = today?.sunset?.format(HOUR_FORMAT),
            insight = insightGenerator.generate(forecast)?.toUiModel(),
            timeline = timelineGenerator.generate(forecast).map { it.toUiModel() },
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
                        time = hour.time,
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
                        percent(
                            hour.precipitationProbability
                        )
                    )
                },
            daily =
            daily.map { day ->

                DailyUiModel(
                    date = day.date,
                    dateText = day.date.format(DATE_FORMAT),
                    dayLabel =
                    if (day.date == current.observedAt.toLocalDate()) {
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
                    percent(
                        day.precipitationProbability
                    )
                )
            },
            isFavorite =
            isFavorite
        )
    }

    private fun conditionUi(code: WeatherCode): WeatherConditionUi = conditionUiRegistry.resolve(
        conditionClassifier.classify(
            code
        )
    )

    private fun WeatherInsight.toUiModel() = WeatherInsightUiModel(
        headline = UiText.Resource(
            when (headline) {
                WeatherInsightHeadline.ThunderstormsExpected -> R.string.insight_thunderstorm
                WeatherInsightHeadline.RainExpected -> if (relevantFrom?.hour in 12..17) {
                    R.string.insight_rain_afternoon
                } else {
                    R.string.insight_rain
                }
                WeatherInsightHeadline.SnowExpected -> R.string.insight_snow
                WeatherInsightHeadline.StrongWindsExpected -> R.string.insight_strong_wind
                WeatherInsightHeadline.ColderThisEvening -> R.string.insight_cooling
                WeatherInsightHeadline.WarmAndClearDay -> R.string.insight_warm_clear
            }
        ),
        advice = advice?.let {
            UiText.Resource(
                when (it) {
                    WeatherInsightAdvice.AvoidExposedAreas -> R.string.advice_avoid_exposed
                    WeatherInsightAdvice.TakeUmbrella -> R.string.advice_take_umbrella
                    WeatherInsightAdvice.AllowExtraTravelTime -> R.string.advice_extra_travel_time
                    WeatherInsightAdvice.SecureLooseItems -> R.string.advice_secure_items
                    WeatherInsightAdvice.TakeLightJacket -> R.string.advice_light_jacket
                    WeatherInsightAdvice.GoodForOutdoorPlans -> R.string.advice_outdoor_plans
                }
            )
        },
        timeText = relevantFrom?.format(HOUR_FORMAT)
    )

    private fun TimelineEvent.toUiModel() = TimelineEventUiModel(
        time = time,
        timeText = time.format(HOUR_FORMAT),
        headline = UiText.Resource(
            when (headline) {
                TimelineEventHeadline.RainBegins -> R.string.timeline_rain_begins
                TimelineEventHeadline.RainEasing -> R.string.timeline_rain_easing
                TimelineEventHeadline.RainContinuing -> R.string.timeline_rain_continuing
                TimelineEventHeadline.GettingWarmer -> R.string.timeline_getting_warmer
                TimelineEventHeadline.GettingCooler -> R.string.timeline_getting_cooler
                TimelineEventHeadline.StrongWindsBegin -> R.string.timeline_strong_winds
                TimelineEventHeadline.CoolingAfterSunset -> R.string.timeline_evening_cooling
            }
        ),
        detail = advice?.let {
            UiText.Resource(
                when (it) {
                    TimelineEventAdvice.TakeUmbrella -> R.string.advice_take_umbrella
                    TimelineEventAdvice.ConditionsImproving -> R.string.timeline_conditions_improving
                    TimelineEventAdvice.KeepRainProtectionReady -> R.string.timeline_keep_rain_protection
                    TimelineEventAdvice.DressForWarmerConditions -> R.string.timeline_dress_warmer
                    TimelineEventAdvice.ConsiderALightLayer -> R.string.advice_light_jacket
                    TimelineEventAdvice.SecureLooseItems -> R.string.advice_secure_items
                }
            )
        }
    )

    private fun subtitle(city: City) = listOfNotNull(
        city.region,
        city.country
    )
        .distinct()
        .joinToString(", ")

    private fun degrees(celsius: Double) = "${celsius.roundToInt()}°"

    private fun percent(value: Int?) = value
        ?.takeIf {
            it > 0
        }
        ?.let {
            "%$it"
        }

    private companion object {

        const val HOURLY_COUNT =
            24

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

        val DATE_FORMAT: DateTimeFormatter = DateTimeFormatter.ofPattern(
            "d MMM",
            Locale.forLanguageTag("tr")
        )
    }
}
