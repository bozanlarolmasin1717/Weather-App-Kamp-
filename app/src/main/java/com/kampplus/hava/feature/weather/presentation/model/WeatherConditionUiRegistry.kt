package com.kampplus.hava.feature.weather.presentation.model

import androidx.annotation.StringRes
import com.kampplus.hava.R
import com.kampplus.hava.core.ui.text.UiText
import com.kampplus.hava.feature.weather.domain.policy.WeatherCondition
import javax.inject.Inject

data class WeatherConditionUi(
    val emoji: String,
    @param:StringRes val labelRes: Int
) {
    val label: UiText
        get() = UiText.Resource(labelRes)

    companion object {
        val Unknown = WeatherConditionUi(
            emoji = "🌡️",
            labelRes = R.string.condition_unknown
        )
    }
}

class WeatherConditionUiRegistry @Inject constructor(
    private val entries: Map<
        WeatherCondition,
        @JvmSuppressWildcards WeatherConditionUi
        >
) {
    fun resolve(condition: WeatherCondition): WeatherConditionUi =
        entries[condition] ?: WeatherConditionUi.Unknown
}
