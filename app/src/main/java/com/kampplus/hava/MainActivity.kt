package com.kampplus.hava

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                MainScreen(modifier = Modifier.padding(innerPadding))
            }
        }
    }
}

// 1. Ana Ekran Composable'ı
@Composable
fun MainScreen(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        ContentCard(
            title = "İstanbul Hava Durumu",
            description = "Bugün parçalı bulutlu, sıcaklık 15°C civarında seyrediyor.",
            modifier = Modifier.fillMaxWidth()
        )
    }
}

// 2. Yönergedeki Sözleşmeye Uygun Kart Bileşeni
@Composable
fun ContentCard(
    title: String,
    description: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

// 3. Önizleme 1: Standart / Kısa Metin
@Preview(showBackground = true, name = "Standart Görünüm")
@Composable
fun ContentCardPreview() {
    ContentCard(
        title = "İstanbul",
        description = "Güneşli, 22°C"
    )
}

// 4. Önizleme 2: Uzun Metin Taşma Testi
@Preview(showBackground = true, name = "Uzun Metin Taşma Testi")
@Composable
fun ContentCardLongPreview() {
    ContentCard(
        title = "Marmara Bölgesi Genel Hava Tahmin Raporu",
        description = "Bölge genelinde rüzgarın kuzey ve kuzeydoğu yönlerden kuvvetli, yer yer fırtına şeklinde esmesi beklenmektedir. Sıcaklıkların mevsim normallerinin altında kalacağı tahmin ediliyor."
    )
}
// Dar ekran ve büyük yazı ölçeği testi
@Preview(showBackground = true, name = "Dar Ekran & Büyük Yazı", widthDp = 260, fontScale = 1.3f)
@Composable
fun ContentCardAccessibilityPreview() {
    ContentCard(
        title = "İstanbul Hava Durumu",
        description = "Bugün parçalı bulutlu, sıcaklık 15°C civarında seyrediyor."
    )
}
