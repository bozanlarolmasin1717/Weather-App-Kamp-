package com.kampplus.hava.feature.weather.presentation.visual

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import com.kampplus.hava.core.ui.theme.HavaColors

@Composable
fun AtmosphericWeatherBackground(state: WeatherVisualState, modifier: Modifier = Modifier, content: @Composable BoxScope.() -> Unit) {
    val palette = atmosphericPalette(state)

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(palette.background)
            )
    ) {
        Canvas(Modifier.fillMaxSize()) {
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(palette.glow, Color.Transparent),
                    center = Offset(size.width * 0.78f, size.height * 0.12f),
                    radius = size.width * 0.72f
                ),
                radius = size.width * 0.72f,
                center = Offset(size.width * 0.78f, size.height * 0.12f)
            )

            if (state in HAZY_STATES) {
                drawCircle(
                    color = palette.haze,
                    radius = size.width * 0.62f,
                    center = Offset(size.width * 0.12f, size.height * 0.28f)
                )
                drawCircle(
                    color = palette.haze.copy(alpha = palette.haze.alpha * 0.7f),
                    radius = size.width * 0.48f,
                    center = Offset(size.width * 0.92f, size.height * 0.46f)
                )
            }

            if (state == WeatherVisualState.Rainy || state == WeatherVisualState.Thunderstorm) {
                repeat(12) { index ->
                    val x = size.width * (0.05f + index * 0.085f)
                    val y = size.height * (0.14f + (index % 4) * 0.06f)
                    drawLine(
                        color = HavaColors.Ice.copy(alpha = 0.08f),
                        start = Offset(x, y),
                        end = Offset(x - 16f, y + 42f),
                        strokeWidth = 2f,
                        cap = StrokeCap.Round
                    )
                }
            }
        }

        content()
    }
}

@Immutable
private data class AtmosphericPalette(
    val background: List<Color>,
    val glow: Color,
    val haze: Color
)

private fun atmosphericPalette(state: WeatherVisualState): AtmosphericPalette = when (state) {
    WeatherVisualState.ClearSunny -> AtmosphericPalette(
        background = listOf(Color(0xFF123C70), Color(0xFF0B2547), HavaColors.Ink),
        glow = Color(0xA6FFCA72),
        haze = Color(0x162FA8D8)
    )
    WeatherVisualState.SunsetEvening -> AtmosphericPalette(
        background = listOf(Color(0xFF402D59), Color(0xFF183252), HavaColors.Ink),
        glow = Color(0x99FF946D),
        haze = Color(0x1CBE89B8)
    )
    WeatherVisualState.ClearNight -> AtmosphericPalette(
        background = listOf(Color(0xFF132342), Color(0xFF091528), Color(0xFF050B14)),
        glow = Color(0x5278A8E8),
        haze = Color(0x142E5B8D)
    )
    WeatherVisualState.Cloudy -> AtmosphericPalette(
        background = listOf(Color(0xFF344B62), Color(0xFF192A3C), HavaColors.Ink),
        glow = Color(0x4DB8D5E8),
        haze = Color(0x224C657D)
    )
    WeatherVisualState.Rainy -> AtmosphericPalette(
        background = listOf(Color(0xFF263D54), Color(0xFF122538), Color(0xFF07111F)),
        glow = Color(0x3D80B3D6),
        haze = Color(0x2B3F5C75)
    )
    WeatherVisualState.Snowy -> AtmosphericPalette(
        background = listOf(Color(0xFF58758D), Color(0xFF2D465D), Color(0xFF101E2C)),
        glow = Color(0x80D8F4FF),
        haze = Color(0x2EE7F7FF)
    )
    WeatherVisualState.Foggy -> AtmosphericPalette(
        background = listOf(Color(0xFF5C6974), Color(0xFF35434F), Color(0xFF17232E)),
        glow = Color(0x5ADBE3E8),
        haze = Color(0x35D4DEE5)
    )
    WeatherVisualState.Thunderstorm -> AtmosphericPalette(
        background = listOf(Color(0xFF242A42), Color(0xFF151B2D), Color(0xFF070A13)),
        glow = Color(0x526E73C9),
        haze = Color(0x2E343A5C)
    )
}

private val HAZY_STATES = setOf(
    WeatherVisualState.Cloudy,
    WeatherVisualState.Rainy,
    WeatherVisualState.Snowy,
    WeatherVisualState.Foggy,
    WeatherVisualState.Thunderstorm
)
