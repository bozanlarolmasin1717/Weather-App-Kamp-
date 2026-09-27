package com.kampplus.hava.feature.weather.presentation.list.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kampplus.hava.core.ui.component.FavoriteToggleButton
import com.kampplus.hava.core.ui.component.GlassSurface
import com.kampplus.hava.core.ui.text.UiText
import com.kampplus.hava.core.ui.theme.HavaColors
import com.kampplus.hava.core.ui.theme.HavaTheme
import com.kampplus.hava.feature.weather.presentation.model.CityWeatherUiModel
import com.kampplus.hava.feature.weather.presentation.visual.WeatherVisualState

@Composable
fun CityWeatherCard(item: CityWeatherUiModel, onClick: () -> Unit, onFavoriteClick: () -> Unit, modifier: Modifier = Modifier) {
    GlassSurface(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 138.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(28.dp),
        contentPadding = PaddingValues(0.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.horizontalGradient(
                        listOf(
                            cardAccent(item.visualState).copy(alpha = 0.28f),
                            Color.Transparent
                        )
                    )
                )
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 40.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = item.title,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Text(
                        text = item.subtitle,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Text(
                        text = item.conditionLabel.asString(),
                        style = MaterialTheme.typography.bodyLarge,
                        color = HavaColors.TextPrimary.copy(alpha = 0.92f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Column(
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Text(
                        text = item.temperatureText,
                        style = MaterialTheme.typography.displaySmall.copy(
                            fontSize = 42.sp,
                            fontWeight = FontWeight.Light
                        )
                    )

                    Text(
                        text = item.conditionEmoji,
                        fontSize = 28.sp
                    )
                }
            }

            FavoriteToggleButton(
                isFavorite = item.isFavorite,
                onClick = onFavoriteClick,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 52.dp)
            )
        }
    }
}

private fun cardAccent(state: WeatherVisualState): Color = when (state) {
    WeatherVisualState.ClearSunny -> HavaColors.Sun
    WeatherVisualState.SunsetEvening -> Color(0xFFFF8F70)
    WeatherVisualState.ClearNight -> Color(0xFF6F8EC7)
    WeatherVisualState.Cloudy -> HavaColors.Cloud
    WeatherVisualState.Rainy -> Color(0xFF4B83AE)
    WeatherVisualState.Snowy -> HavaColors.Ice
    WeatherVisualState.Foggy -> Color(0xFFB2BEC9)
    WeatherVisualState.Thunderstorm -> Color(0xFF7A72BD)
}

@Preview
@Composable
private fun CityWeatherCardPreview() {
    HavaTheme {
        Box(
            modifier = Modifier
                .background(HavaColors.Ink)
                .padding(16.dp)
        ) {
            CityWeatherCard(
                item = CityWeatherUiModel(
                    cityId = 1,
                    title = "Ankara",
                    subtitle = "Ankara, Türkiye",
                    temperatureText = "21°",
                    temperatureC = 21.0,
                    conditionEmoji = "☀️",
                    conditionLabel = UiText.Dynamic("Açık"),
                    visualState = WeatherVisualState.ClearSunny
                ),
                onClick = {},
                onFavoriteClick = {}
            )
        }
    }
}
