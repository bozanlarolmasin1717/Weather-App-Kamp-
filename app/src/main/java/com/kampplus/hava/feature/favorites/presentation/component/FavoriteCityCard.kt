package com.kampplus.hava.feature.favorites.presentation.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kampplus.hava.core.ui.component.FavoriteToggleButton
import com.kampplus.hava.core.ui.component.GlassSurface
import com.kampplus.hava.core.ui.theme.HavaColors
import com.kampplus.hava.feature.favorites.presentation.FavoriteCityUiModel
import com.kampplus.hava.feature.weather.presentation.visual.WeatherVisualState

@Composable
fun FavoriteCityCard(item: FavoriteCityUiModel, onClick: () -> Unit, onRemoveClick: () -> Unit, modifier: Modifier = Modifier) {
    GlassSurface(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(28.dp),
        contentPadding = PaddingValues(0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.horizontalGradient(
                        listOf(
                            favoriteAccent(item.visualState).copy(alpha = 0.24f),
                            Color.Transparent
                        )
                    )
                )
                .padding(start = 20.dp, top = 18.dp, bottom = 18.dp, end = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.headlineSmall,
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
                item.conditionLabel?.let {
                    Text(
                        text = it.asString(),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                item.temperatureText?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.displaySmall
                    )
                }
                item.conditionEmoji?.let {
                    Text(
                        text = it,
                        fontSize = 26.sp
                    )
                }
            }

            FavoriteToggleButton(
                isFavorite = true,
                onClick = onRemoveClick
            )
        }
    }
}

private fun favoriteAccent(state: WeatherVisualState): Color = when (state) {
    WeatherVisualState.ClearSunny -> HavaColors.Sun
    WeatherVisualState.SunsetEvening -> Color(0xFFFF8F70)
    WeatherVisualState.ClearNight -> Color(0xFF6F8EC7)
    WeatherVisualState.Cloudy -> HavaColors.Cloud
    WeatherVisualState.Rainy -> Color(0xFF4B83AE)
    WeatherVisualState.Snowy -> HavaColors.Ice
    WeatherVisualState.Foggy -> Color(0xFFB2BEC9)
    WeatherVisualState.Thunderstorm -> Color(0xFF7A72BD)
}
