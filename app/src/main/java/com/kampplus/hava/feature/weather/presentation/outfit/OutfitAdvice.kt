package com.kampplus.hava.feature.weather.presentation.outfit

import com.kampplus.hava.feature.weather.domain.policy.WeatherCondition

data class OutfitAdvice(
    val title: String,
    val outfit: String,
    val accessories: String,
    val tip: String,
    val moodEmoji: String
)

fun getOutfitAdvice(condition: WeatherCondition, temperatureC: Double): OutfitAdvice {
    val isCold = temperatureC < 10.0
    val isFreezing = temperatureC < 0.0
    val isMild = temperatureC in 10.0..20.0
    val isWarm = temperatureC in 20.0..27.0
    val isHot = temperatureC > 27.0

    return when {
        condition == WeatherCondition.Rain ||
            condition == WeatherCondition.RainShowers ||
            condition == WeatherCondition.Drizzle -> OutfitAdvice(
            title = "Yağmurlu Gün Stili 🌧️",
            outfit = "Su geçirmez yağmurluk veya trençkot, paçaları dar pantolon ve kaymaz tabanlı su almayan bot.",
            accessories = "Rüzgara dayanıklı sağlam şemsiye ☔, su geçirmez sırt çantası.",
            tip = "Yerlerdeki su birikintilerine dikkat et, ayaklarını kuru tut!",
            moodEmoji = "🌧️"
        )
        condition == WeatherCondition.Thunderstorm -> OutfitAdvice(
            title = "Fırtına Alarmı! ⚡",
            outfit = "Rüzgar ve su geçirmeyen dayanıklı mont, boğazlı kazak ve sağlam ayakkabı.",
            accessories = "Kapüşonlu giysi (şemsiye sert rüzgarda ters dönebilir!), sıcak tutan bere.",
            tip = "Mümkünse fırtına dinene kadar açık alanlardan uzak dur.",
            moodEmoji = "⚡"
        )
        condition == WeatherCondition.Snow ||
            condition == WeatherCondition.SnowShowers ||
            isFreezing -> OutfitAdvice(
            title = "Kış Masalı Kombini ❄️",
            outfit = "Kalın kaz tüyü kaban, yün kazak, termal içlik ve kar botu.",
            accessories = "Yün atkı 🧣, kar beresi, su geçirmez kalın eldiven 🧤.",
            tip = "Kat kat giyinmek soğuktan korunmanın en etkili yoludur!",
            moodEmoji = "☃️"
        )
        isHot -> OutfitAdvice(
            title = "Yaz ve Güneş Keyfi ☀️",
            outfit = "Hafif keten gömlek, şort veya ince pamuklu tişört, nefes alan sandalet/spor ayakkabı.",
            accessories = "UV korumalı güneş gözlüğü 🕶️, güneş şapkası 🧢, su matarası 💧.",
            tip = "Güneş kremini sürmeyi ve bol bol su içmeyi ihmal etme!",
            moodEmoji = "😎"
        )
        isWarm -> OutfitAdvice(
            title = "Bahar & Ilık Hava 🌤️",
            outfit = "Rahat pamuklu tişört, kot pantolon veya chino pantolon, hafif spor ayakkabı.",
            accessories = "Güneş gözlüğü 🕶️, akşam serinliği için omuza hafif bir hırka.",
            tip = "Açık havada yürüyüş yapmak ve vakit geçirmek için harika bir gün!",
            moodEmoji = "😊"
        )
        isMild -> OutfitAdvice(
            title = "Mevsim Geçişi Kombini 🍂",
            outfit = "Kot ceket veya ince mevsimlik mont, sweatshirt ve spor ayakkabı.",
            accessories = "İnce bir fular veya hafif şapka.",
            tip = "Rüzgar estiğinde montunun önünü kapatmayı unutma.",
            moodEmoji = "🧥"
        )
        isCold -> OutfitAdvice(
            title = "Serin & Soğuk Hava 🧥",
            outfit = "Kaban veya şişme mont, balıkçı yaka kazak ve sıcak tutan pantolon.",
            accessories = "Bere, hafif eldiven ve sıcak tutan çoraplar.",
            tip = "Yanına sıcak bir kahve veya çay alıp güne başlamak çok iyi gelir!",
            moodEmoji = "🧣"
        )
        else -> OutfitAdvice(
            title = "Günün Tavsiyesi ⛅",
            outfit = "Rahat bir günlük kombin, katmanlı giyim (tişört + fermuarlı hırka).",
            accessories = "Rahat yürüyüş ayakkabısı ve sırt çantası.",
            tip = "Günün keyfini çıkar, hava şartlarına göre hazırlıklı ol!",
            moodEmoji = "🧍"
        )
    }
}
