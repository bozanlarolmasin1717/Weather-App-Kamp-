package com.kampplus.hava.feature.weather.domain.model

/**
 * WMO hava durumu kodu (0 açık, 61 yağmur, 95 fırtına…).
 * Ham kod domain'de korunur; nasıl yorumlanacağına `WeatherConditionClassifier` karar verir.
 */
@JvmInline
value class WeatherCode(
    val value: Int
)
