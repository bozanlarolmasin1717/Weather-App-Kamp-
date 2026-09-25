package com.kampplus.hava.core.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.kampplus.hava.core.ui.theme.HavaTheme
import com.kampplus.hava.core.ui.theme.TemperaturePalette

@Composable
fun TemperatureBadge(
    text: String,
    containerColor: Color,
    modifier: Modifier = Modifier,
    contentColor: Color = Color.White,
    size: Dp = 56.dp
) {
    Box(
        modifier = modifier
            .size(size)
            .background(
                color = containerColor,
                shape = RoundedCornerShape(14.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = contentColor,
            style = if (size > 64.dp) {
                MaterialTheme.typography.headlineMedium
            } else {
                MaterialTheme.typography.titleMedium
            }
        )
    }
}

@Preview
@Composable
private fun TemperatureBadgePreview() {
    HavaTheme {
        TemperatureBadge(
            text = "24°",
            containerColor = TemperaturePalette.Warm
        )
    }
}
