package com.kampplus.hava.feature.weather.presentation.detail.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.kampplus.hava.feature.weather.presentation.model.HourlyUiModel

@Composable
fun HourlyForecastRow(
    items: List<HourlyUiModel>,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier,
        contentPadding =
            PaddingValues(
                horizontal = 16.dp
            ),
        horizontalArrangement =
            Arrangement.spacedBy(8.dp)
    ) {
        items(
            items = items,
            key = {
                it.timeText
            }
        ) { hour ->

            Card(
                modifier =
                    Modifier.width(72.dp)
            ) {
                Column(
                    modifier =
                        Modifier
                            .padding(
                                vertical = 12.dp
                            )
                            .align(
                                Alignment.CenterHorizontally
                            ),
                    horizontalAlignment =
                        Alignment.CenterHorizontally,
                    verticalArrangement =
                        Arrangement.spacedBy(
                            4.dp
                        )
                ) {
                    Text(
                        text =
                            hour.timeText,
                        style =
                            MaterialTheme
                                .typography
                                .labelMedium
                    )

                    Text(
                        text =
                            hour.emoji,
                        style =
                            MaterialTheme
                                .typography
                                .titleLarge
                    )

                    Text(
                        text =
                            hour.temperatureText,
                        style =
                            MaterialTheme
                                .typography
                                .titleMedium
                    )

                    Text(
                        text =
                            hour.precipitationText
                                ?: " ",
                        style =
                            MaterialTheme
                                .typography
                                .labelSmall,
                        color =
                            MaterialTheme
                                .colorScheme
                                .primary
                    )
                }
            }
        }
    }
}
