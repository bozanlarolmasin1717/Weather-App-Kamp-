package com.kampplus.hava.feature.weather.presentation.model

import androidx.compose.ui.graphics.Color
import com.kampplus.hava.feature.weather.domain.policy.WeatherCondition

data class WeatherGradientColors(
    val topColor: Color,
    val bottomColor: Color,
    val cardBackground: Color,
    val accentColor: Color
)

fun weatherGradientColors(condition: WeatherCondition): WeatherGradientColors = when (condition) {
    WeatherCondition.Clear, WeatherCondition.MainlyClear -> WeatherGradientColors(
        topColor = Color(0xFFF59E0B),
        bottomColor = Color(0xFF1E3A8A),
        cardBackground = Color(0x2EFFFFFF),
        accentColor = Color(0xFFFDE047)
    )
    WeatherCondition.Rain, WeatherCondition.Drizzle, WeatherCondition.RainShowers -> WeatherGradientColors(
        topColor = Color(0xFF1E293B),
        bottomColor = Color(0xFF0F172A),
        cardBackground = Color(0x3338BDF8),
        accentColor = Color(0xFF38BDF8)
    )
    WeatherCondition.Thunderstorm -> WeatherGradientColors(
        topColor = Color(0xFF3B0764),
        bottomColor = Color(0xFF0F172A),
        cardBackground = Color(0x33C084FC),
        accentColor = Color(0xFFA855F7)
    )
    WeatherCondition.Snow, WeatherCondition.SnowShowers -> WeatherGradientColors(
        topColor = Color(0xFF38BDF8),
        bottomColor = Color(0xFF0369A1),
        cardBackground = Color(0x3BFFFFFF),
        accentColor = Color(0xFFE0F2FE)
    )
    WeatherCondition.PartlyCloudy -> WeatherGradientColors(
        topColor = Color(0xFF475569),
        bottomColor = Color(0xFF1E293B),
        cardBackground = Color(0x2EFFFFFF),
        accentColor = Color(0xFF94A3B8)
    )
    WeatherCondition.Overcast, WeatherCondition.Fog -> WeatherGradientColors(
        topColor = Color(0xFF334155),
        bottomColor = Color(0xFF0F172A),
        cardBackground = Color(0x2EFFFFFF),
        accentColor = Color(0xFFCBD5E1)
    )
    WeatherCondition.Unknown -> WeatherGradientColors(
        topColor = Color(0xFF1E293B),
        bottomColor = Color(0xFF0F172A),
        cardBackground = Color(0x2EFFFFFF),
        accentColor = Color(0xFF60A5FA)
    )
}
