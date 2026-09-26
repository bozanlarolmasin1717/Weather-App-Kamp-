package com.kampplus.hava.feature.weather.presentation.sound

import android.content.Context
import android.media.MediaPlayer
import com.kampplus.hava.R
import com.kampplus.hava.feature.weather.domain.policy.WeatherCondition

class WeatherSoundManager(private val context: Context) {

    private var mediaPlayer: MediaPlayer? = null
    private var currentResId: Int? = null
    var isEnabled: Boolean = false
        private set

    fun toggleSound(currentCondition: WeatherCondition): Boolean {
        isEnabled = !isEnabled
        if (isEnabled) {
            play(currentCondition)
        } else {
            stop()
        }
        return isEnabled
    }

    fun play(condition: WeatherCondition) {
        if (!isEnabled) return

        val resId = soundForCondition(condition)
        if (currentResId == resId && mediaPlayer?.isPlaying == true) return

        stop()
        currentResId = resId

        try {
            mediaPlayer = MediaPlayer.create(context, resId)?.apply {
                isLooping = true
                setVolume(0.6f, 0.6f)
                start()
            }
        } catch (_: Exception) {
            mediaPlayer = null
        }
    }

    fun stop() {
        try {
            mediaPlayer?.let {
                if (it.isPlaying) it.stop()
                it.release()
            }
        } catch (_: Exception) {
        } finally {
            mediaPlayer = null
            currentResId = null
        }
    }

    fun release() {
        stop()
    }

    private fun soundForCondition(condition: WeatherCondition): Int = when (condition) {
        WeatherCondition.Rain, WeatherCondition.RainShowers, WeatherCondition.Drizzle -> R.raw.weather_rain
        WeatherCondition.Thunderstorm -> R.raw.weather_thunder
        WeatherCondition.Snow, WeatherCondition.SnowShowers -> R.raw.weather_snow
        WeatherCondition.Clear, WeatherCondition.MainlyClear -> R.raw.weather_sunny
        else -> R.raw.weather_wind
    }
}
