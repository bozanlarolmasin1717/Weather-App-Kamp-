package com.kampplus.hava.feature.weather.presentation.detail.component

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.kampplus.hava.feature.weather.presentation.model.DailyUiModel

@Composable
fun DailyForecastItem(
    day: DailyUiModel,
    modifier: Modifier = Modifier
) {
    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 16.dp,
                    vertical = 10.dp
                ),
        verticalAlignment =
            Alignment.CenterVertically
    ) {
        Text(
            text =
                day.dayLabel.asString(),
            style =
                MaterialTheme.typography
                    .bodyLarge,
            modifier =
                Modifier.weight(1f)
        )

        Text(
            text =
                day.precipitationText
                    .orEmpty(),
            style =
                MaterialTheme.typography
                    .labelMedium,
            color =
                MaterialTheme.colorScheme
                    .primary,
            modifier =
                Modifier.width(44.dp),
            textAlign =
                TextAlign.End
        )

        Text(
            text =
                day.emoji,
            style =
                MaterialTheme.typography
                    .titleLarge,
            modifier =
                Modifier.padding(
                    horizontal = 12.dp
                )
        )

        Text(
            text =
                day.minText,
            style =
                MaterialTheme.typography
                    .bodyLarge,
            color =
                MaterialTheme.colorScheme
                    .onSurfaceVariant,
            modifier =
                Modifier.width(40.dp),
            textAlign =
                TextAlign.End
        )

        Text(
            text =
                day.maxText,
            style =
                MaterialTheme.typography
                    .titleMedium,
            modifier =
                Modifier.width(44.dp),
            textAlign =
                TextAlign.End
        )
    }
}
