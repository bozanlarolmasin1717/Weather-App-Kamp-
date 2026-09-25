package com.kampplus.hava.feature.weather.presentation.di

import com.kampplus.hava.R
import com.kampplus.hava.feature.weather.domain.policy.WeatherCondition
import com.kampplus.hava.feature.weather.presentation.model.WeatherConditionUi
import dagger.MapKey
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoMap

@MapKey
annotation class WeatherConditionKey(
    val value: WeatherCondition
)

@Module
@InstallIn(SingletonComponent::class)
object WeatherConditionUiModule {

    @Provides
    @IntoMap
    @WeatherConditionKey(WeatherCondition.Clear)
    fun clear() =
        WeatherConditionUi("☀️", R.string.condition_clear)

    @Provides
    @IntoMap
    @WeatherConditionKey(WeatherCondition.MainlyClear)
    fun mainlyClear() =
        WeatherConditionUi("🌤️", R.string.condition_mainly_clear)

    @Provides
    @IntoMap
    @WeatherConditionKey(WeatherCondition.PartlyCloudy)
    fun partlyCloudy() =
        WeatherConditionUi("⛅", R.string.condition_partly_cloudy)

    @Provides
    @IntoMap
    @WeatherConditionKey(WeatherCondition.Overcast)
    fun overcast() =
        WeatherConditionUi("☁️", R.string.condition_overcast)

    @Provides
    @IntoMap
    @WeatherConditionKey(WeatherCondition.Fog)
    fun fog() =
        WeatherConditionUi("🌫️", R.string.condition_fog)

    @Provides
    @IntoMap
    @WeatherConditionKey(WeatherCondition.Drizzle)
    fun drizzle() =
        WeatherConditionUi("🌦️", R.string.condition_drizzle)

    @Provides
    @IntoMap
    @WeatherConditionKey(WeatherCondition.Rain)
    fun rain() =
        WeatherConditionUi("🌧️", R.string.condition_rain)

    @Provides
    @IntoMap
    @WeatherConditionKey(WeatherCondition.Snow)
    fun snow() =
        WeatherConditionUi("❄️", R.string.condition_snow)

    @Provides
    @IntoMap
    @WeatherConditionKey(WeatherCondition.RainShowers)
    fun rainShowers() =
        WeatherConditionUi("🌦️", R.string.condition_rain_showers)

    @Provides
    @IntoMap
    @WeatherConditionKey(WeatherCondition.SnowShowers)
    fun snowShowers() =
        WeatherConditionUi("🌨️", R.string.condition_snow_showers)

    @Provides
    @IntoMap
    @WeatherConditionKey(WeatherCondition.Thunderstorm)
    fun thunderstorm() =
        WeatherConditionUi("⛈️", R.string.condition_thunderstorm)
}
