package com.kampplus.hava

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

/**
 * 1. BİLEŞEN: ContentCard (Tekrar kullanılabilir içerik bileşeni)
 * Kitapçıktaki "Bileşen Sözleşmesi"ne birebir uyar:
 * title ve description parametrelerini alır, kart içinde alt alta sunar.
 */
@Composable
fun ContentCard(
    title: String,
    description: String,
    modifier: Modifier = Modifier
) {
    // Card: Ekranda gölgeli/çerçeveli bir kutu (React Native'deki yuvarlatılmış View gibi)
    Card(
        modifier = modifier.fillMaxWidth(), // Kart yatayda tüm genişliği kaplasın
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp) // Hafif gölge derinliği
    ) {
        // Column: Elemanları dikeyde (alt alta) dizer (flexDirection: 'column')
        Column(
            modifier = Modifier
                .padding(16.dp) // Kartın iç boşluğu (padding)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp) // Başlık ile açıklama arasına 8dp boşluk
        ) {
            // Başlık metni
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge // Temadan gelen kalın/büyük başlık stili
            )
            // Açıklama metni
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium // Temadan gelen normal gövde metni stili
            )
        }
    }
}

/**
 * 2. BİLEŞEN: MainScreen (Ana Ekran seviyesi bileşen)
 * Bütün ekranı kaplar ve içinde ContentCard bileşenini örnek verilerle çağırır.
 */
@Composable
fun MainScreen(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize() // Bütün ekranı kapla (width & height: 100%)
            .padding(16.dp), // Ekran kenarlarından içeri 16dp pay bırak
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Üstte oluşturduğumuz bileşeni parametreleriyle çağırıyoruz
        ContentCard(
            title = "İstanbul Hava Durumu",
            description = "Hava parçalı bulutlu, sıcaklık 22°C. Rüzgar kuzeyden hafif esiyor."
        )
    }
}

/**
 * 3. ADIM: Kitapçığın istediği iki ayrı @Preview (Önizleme)
 */

// Preview 1: Kısa ve standart metin önizlemesi
@Preview(showBackground = true, name = "Kısa Metin Önizleme")
@Composable
fun ContentCardShortPreview() {
    MaterialTheme {
        ContentCard(
            title = "Ankara",
            description = "18°C Güneşli",
            modifier = Modifier.padding(16.dp)
        )
    }
}

// Preview 2: Uzun metin önizlemesi (Taşma ve satır atlama davranışını doğrulamak için)
@Preview(showBackground = true, name = "Uzun Metin Önizleme")
@Composable
fun ContentCardLongPreview() {
    MaterialTheme {
        ContentCard(
            title = "Çok Uzun İlçe ve Bölge İsmi: Kadıköy / Moda Sahil Şeridi Meteoroloji İstasyonu",
            description = "Bölge genelinde yoğun yağış ve fırtına beklenmektedir. Sıcaklıklar ani bir düşüşle 10°C seviyesine kadar gerileyecektir. Şemsiyesiz dışarı çıkılmaması tavsiye olunur.",
            modifier = Modifier.padding(16.dp)
        )
    }
}
