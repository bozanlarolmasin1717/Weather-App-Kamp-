package com.kampplus.hava.feature.weather.presentation.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kampplus.hava.R
import com.kampplus.hava.core.ui.component.ErrorView
import com.kampplus.hava.core.ui.component.FavoriteToggleButton
import com.kampplus.hava.core.ui.component.GlassSurface
import com.kampplus.hava.core.ui.component.LoadingView
import com.kampplus.hava.core.ui.component.WeatherSectionLabel
import com.kampplus.hava.core.ui.state.UiState
import com.kampplus.hava.core.ui.text.UiText
import com.kampplus.hava.core.ui.theme.HavaColors
import com.kampplus.hava.core.ui.theme.HavaTheme
import com.kampplus.hava.feature.weather.presentation.detail.component.DailyForecastItem
import com.kampplus.hava.feature.weather.presentation.detail.component.HourlyForecastRow
import com.kampplus.hava.feature.weather.presentation.detail.component.ShareButton
import com.kampplus.hava.feature.weather.presentation.model.DailyUiModel
import com.kampplus.hava.feature.weather.presentation.model.ForecastUiModel
import com.kampplus.hava.feature.weather.presentation.model.HourlyUiModel
import com.kampplus.hava.feature.weather.presentation.model.TimelineEventUiModel
import com.kampplus.hava.feature.weather.presentation.model.WeatherInsightUiModel
import com.kampplus.hava.feature.weather.presentation.visual.AtmosphericWeatherBackground
import com.kampplus.hava.feature.weather.presentation.visual.WeatherVisualState
import java.time.LocalDate
import java.time.LocalDateTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForecastDetailScreen(
    uiState: UiState<ForecastUiModel>,
    onBack: () -> Unit,
    onShare: (ForecastUiModel) -> Unit,
    onFavoriteClick: () -> Unit,
    onRetry: () -> Unit,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier,
    isRefreshing: Boolean = false
) {
    val forecast = (uiState as? UiState.Success)?.data

    AtmosphericWeatherBackground(
        state = forecast?.visualState ?: WeatherVisualState.ClearNight,
        modifier = modifier
    ) {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = {},
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = stringResource(R.string.action_back)
                            )
                        }
                    },
                    actions = {
                        forecast?.let {
                            FavoriteToggleButton(
                                isFavorite = it.isFavorite,
                                onClick = onFavoriteClick
                            )
                            ShareButton(
                                onClick = {
                                    onShare(it)
                                }
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent,
                        scrolledContainerColor = Color.Transparent
                    )
                )
            }
        ) { innerPadding ->
            PullToRefreshBox(
                isRefreshing = isRefreshing,
                onRefresh = onRefresh,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                when (uiState) {
                    UiState.Loading -> LoadingView()
                    UiState.Empty -> ErrorView(
                        message = stringResource(R.string.error_not_found)
                    )
                    is UiState.Error -> ErrorView(
                        message = uiState.message.asString(),
                        onRetry = onRetry
                    )
                    is UiState.Success -> ForecastContent(uiState.data)
                }
            }
        }
    }
}

@Composable
private fun ForecastContent(forecast: ForecastUiModel, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(bottom = 36.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        CurrentWeatherHeader(
            forecast = forecast,
            modifier = Modifier.padding(horizontal = 20.dp)
        )

        forecast.insight?.let {
            InsightSection(
                insight = it,
                modifier = Modifier.padding(horizontal = 20.dp)
            )
        }

        if (forecast.hourly.isNotEmpty()) {
            ForecastSectionTitle(stringResource(R.string.detail_hourly))
            HourlyForecastRow(items = forecast.hourly)
        }

        if (forecast.timeline.isNotEmpty()) {
            TimelineSection(
                events = forecast.timeline,
                modifier = Modifier.padding(horizontal = 20.dp)
            )
        }

        if (forecast.daily.isNotEmpty()) {
            DailySection(
                days = forecast.daily,
                modifier = Modifier.padding(horizontal = 20.dp)
            )
        }

        MetricsSection(
            forecast = forecast,
            modifier = Modifier.padding(horizontal = 20.dp)
        )
    }
}

@Composable
private fun CurrentWeatherHeader(forecast: ForecastUiModel, modifier: Modifier = Modifier) {
    val fontScale = LocalDensity.current.fontScale
    val temperatureSize = (96f / fontScale.coerceAtLeast(1f)).sp
    val conditionDescription = forecast.conditionLabel.asString()

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = forecast.cityName,
            style = MaterialTheme.typography.headlineLarge,
            textAlign = TextAlign.Center
        )

        if (forecast.subtitle.isNotBlank()) {
            Text(
                text = forecast.subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }

        Spacer(Modifier.height(12.dp))

        Text(
            text = forecast.temperatureText,
            style = MaterialTheme.typography.displayLarge.copy(
                fontSize = temperatureSize,
                lineHeight = temperatureSize * 1.05f,
                fontWeight = FontWeight.ExtraLight
            ),
            maxLines = 1
        )

        Text(
            text = "${forecast.conditionEmoji} ${forecast.conditionLabel.asString()}",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.clearAndSetSemantics {
                contentDescription = conditionDescription
            }
        )

        forecast.feelsLikeText?.let {
            Text(
                text = stringResource(R.string.detail_feels_like_value, it),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        if (forecast.highText != null && forecast.lowText != null) {
            Text(
                text = stringResource(
                    R.string.detail_high_low,
                    forecast.highText,
                    forecast.lowText
                ),
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}

@Composable
private fun InsightSection(insight: WeatherInsightUiModel, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        WeatherSectionLabel(stringResource(R.string.detail_insight))
        GlassSurface(
            modifier = Modifier.fillMaxWidth(),
            emphasized = true
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                insight.timeText?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                Text(
                    text = insight.headline.asString(),
                    style = MaterialTheme.typography.titleLarge
                )
                insight.advice?.let {
                    Text(
                        text = it.asString(),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun TimelineSection(events: List<TimelineEventUiModel>, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        WeatherSectionLabel(stringResource(R.string.detail_timeline))
        events.forEachIndexed { index, event ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = event.timeText,
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.width(54.dp)
                )

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.width(24.dp)
                ) {
                    Box(
                        Modifier
                            .size(8.dp)
                            .background(MaterialTheme.colorScheme.primary, CircleShape)
                    )
                    if (index != events.lastIndex) {
                        Box(
                            Modifier
                                .width(1.dp)
                                .height(50.dp)
                                .background(HavaColors.GlassBorder)
                        )
                    }
                }

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 10.dp, bottom = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Text(
                        text = event.headline.asString(),
                        style = MaterialTheme.typography.titleMedium
                    )
                    event.detail?.let {
                        Text(
                            text = it.asString(),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DailySection(days: List<DailyUiModel>, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        WeatherSectionLabel(stringResource(R.string.detail_daily))
        GlassSurface(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(vertical = 6.dp)
        ) {
            Column {
                days.forEachIndexed { index, day ->
                    if (index > 0) {
                        HorizontalDivider(
                            color = HavaColors.GlassBorder.copy(alpha = 0.55f)
                        )
                    }
                    DailyForecastItem(day)
                }
            }
        }
    }
}

@Composable
private fun MetricsSection(forecast: ForecastUiModel, modifier: Modifier = Modifier) {
    val metrics = listOfNotNull(
        forecast.humidityText?.let { stringResource(R.string.detail_humidity) to it },
        forecast.windText?.let { stringResource(R.string.detail_wind) to it },
        forecast.sunriseText?.let { stringResource(R.string.detail_sunrise) to it },
        forecast.sunsetText?.let { stringResource(R.string.detail_sunset) to it }
    )
    if (metrics.isEmpty()) {
        return
    }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        metrics.chunked(2).forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                row.forEach { (label, value) ->
                    MetricPanel(
                        label = label,
                        value = value,
                        modifier = Modifier.weight(1f)
                    )
                }
                if (row.size == 1) {
                    Spacer(Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun MetricPanel(label: String, value: String, modifier: Modifier = Modifier) {
    GlassSurface(
        modifier = modifier,
        contentPadding = PaddingValues(16.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            WeatherSectionLabel(label)
            Text(
                text = value,
                style = MaterialTheme.typography.headlineSmall
            )
        }
    }
}

@Composable
private fun ForecastSectionTitle(text: String) {
    WeatherSectionLabel(
        text = text,
        modifier = Modifier.padding(horizontal = 20.dp)
    )
}

@Preview(showBackground = true)
@Composable
private fun ForecastDetailScreenPreview() {
    HavaTheme {
        ForecastDetailScreen(
            uiState = UiState.Success(
                ForecastUiModel(
                    cityId = 1,
                    cityName = "Ankara",
                    subtitle = "Ankara, Türkiye",
                    temperatureText = "21°",
                    temperatureC = 21.0,
                    conditionEmoji = "☀️",
                    conditionLabel = UiText.Dynamic("Açık"),
                    isDay = true,
                    timeZoneId = "Europe/Istanbul",
                    visualState = WeatherVisualState.ClearSunny,
                    feelsLikeText = "20°",
                    humidityText = "%45",
                    windText = "12 km/sa",
                    highText = "24°",
                    lowText = "14°",
                    sunriseText = "06:42",
                    sunsetText = "18:48",
                    insight = WeatherInsightUiModel(
                        headline = UiText.Dynamic("Ilık ve açık bir gün bekleniyor."),
                        advice = UiText.Dynamic("Dışarıdaki planlar için uygun görünüyor."),
                        timeText = "12:00"
                    ),
                    hourly = List(8) {
                        HourlyUiModel(
                            time = LocalDateTime.of(2026, 9, 24, 10 + it, 0),
                            timeText = "1$it:00",
                            emoji = "☀️",
                            temperatureText = "2$it°",
                            precipitationText = null
                        )
                    },
                    daily = List(7) {
                        DailyUiModel(
                            date = LocalDate.of(2026, 9, 24).plusDays(it.toLong()),
                            dayLabel = UiText.Dynamic("Cuma"),
                            emoji = "⛅",
                            minText = "14°",
                            maxText = "24°",
                            precipitationText = "%10"
                        )
                    }
                )
            ),
            onBack = {},
            onShare = {},
            onFavoriteClick = {},
            onRetry = {},
            onRefresh = {}
        )
    }
}
