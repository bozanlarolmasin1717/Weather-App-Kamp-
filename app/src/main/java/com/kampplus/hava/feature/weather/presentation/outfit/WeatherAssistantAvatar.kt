package com.kampplus.hava.feature.weather.presentation.outfit

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kampplus.hava.feature.weather.domain.policy.WeatherCondition

@Composable
fun WeatherAssistantAvatar(condition: WeatherCondition, temperatureC: Double, modifier: Modifier = Modifier) {
    var showBubble by remember { mutableStateOf(false) }
    val advice = remember(condition, temperatureC) { getOutfitAdvice(condition, temperatureC) }

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.06f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200),
            repeatMode = RepeatMode.Reverse
        ),
        label = "avatarPulse"
    )

    Box(
        modifier = modifier.padding(16.dp),
        contentAlignment = Alignment.BottomEnd
    ) {
        Column(
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            AnimatedVisibility(
                visible = showBubble,
                enter = scaleIn(initialScale = 0.8f) + fadeIn(),
                exit = scaleOut(targetScale = 0.8f) + fadeOut()
            ) {
                SpeechBubbleCard(
                    advice = advice,
                    onDismiss = { showBubble = false }
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .clip(RoundedCornerShape(32.dp))
                    .clickable { showBubble = !showBubble }
                    .background(Color.Black.copy(alpha = 0.45f))
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Text(
                    text = "Giyim Önerisi",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                )

                Box(
                    modifier = Modifier
                        .scale(if (showBubble) 1f else pulseScale)
                        .size(54.dp)
                        .shadow(8.dp, CircleShape)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(Color(0xFF60A5FA), Color(0xFF1D4ED8))
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    HumanFigure2D(condition = condition)
                }
            }
        }
    }
}

@Composable
private fun SpeechBubbleCard(advice: OutfitAdvice, onDismiss: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.End,
        modifier = Modifier.widthIn(max = 320.dp)
    ) {
        Card(
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xF01E293B)),
            elevation = CardDefaults.cardElevation(defaultElevation = 12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(text = advice.moodEmoji, fontSize = 22.sp)
                        Text(
                            text = advice.title,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        )
                    }
                    IconButton(onClick = onDismiss, modifier = Modifier.size(24.dp)) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Kapat",
                            tint = Color.White.copy(alpha = 0.7f),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color.White.copy(alpha = 0.1f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text(
                            text = "👕 Ne Giymelisin?",
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF93C5FD)
                            )
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = advice.outfit,
                            style = MaterialTheme.typography.bodySmall.copy(color = Color.White)
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color.White.copy(alpha = 0.1f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text(
                            text = "🕶️ Aksesuarlar",
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFFDE047)
                            )
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = advice.accessories,
                            style = MaterialTheme.typography.bodySmall.copy(color = Color.White)
                        )
                    }
                }

                Text(
                    text = "💡 ${advice.tip}",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = Color.White.copy(alpha = 0.85f),
                        fontSize = 11.sp
                    )
                )

                Button(
                    onClick = onDismiss,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(38.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3B82F6))
                ) {
                    Text(
                        text = "Tamam, Teşekkürler!",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )
                }
            }
        }

        Canvas(
            modifier = Modifier
                .padding(end = 28.dp)
                .size(width = 16.dp, height = 10.dp)
        ) {
            val path = Path().apply {
                moveTo(0f, 0f)
                lineTo(size.width, 0f)
                lineTo(size.width / 2, size.height)
                close()
            }
            drawPath(path, color = Color(0xF01E293B))
        }
    }
}

@Composable
private fun HumanFigure2D(condition: WeatherCondition) {
    val shirtColor = when (condition) {
        WeatherCondition.Clear, WeatherCondition.MainlyClear -> Color(0xFFF59E0B)
        WeatherCondition.Rain, WeatherCondition.RainShowers, WeatherCondition.Drizzle -> Color(0xFF0284C7)
        WeatherCondition.Snow, WeatherCondition.SnowShowers -> Color(0xFFE2E8F0)
        WeatherCondition.Thunderstorm -> Color(0xFF7C3AED)
        else -> Color(0xFF10B981)
    }

    Canvas(modifier = Modifier.size(42.dp)) {
        val centerX = size.width / 2
        val centerY = size.height / 2

        drawCircle(
            color = Color(0xFFFCD34D),
            radius = size.width * 0.22f,
            center = Offset(centerX, centerY - size.height * 0.12f)
        )

        drawArc(
            color = Color(0xFF451A03),
            startAngle = 180f,
            sweepAngle = 180f,
            useCenter = true,
            topLeft = Offset(centerX - size.width * 0.22f, centerY - size.height * 0.35f),
            size = Size(size.width * 0.44f, size.height * 0.28f)
        )

        drawCircle(
            color = Color(0xFF1F2937),
            radius = 2.2f,
            center = Offset(centerX - 4.5f, centerY - size.height * 0.12f)
        )
        drawCircle(
            color = Color(0xFF1F2937),
            radius = 2.2f,
            center = Offset(centerX + 4.5f, centerY - size.height * 0.12f)
        )

        drawArc(
            color = Color(0xFFB45309),
            startAngle = 10f,
            sweepAngle = 160f,
            useCenter = false,
            topLeft = Offset(centerX - 4f, centerY - size.height * 0.08f),
            size = Size(8f, 6f)
        )

        val torsoPath = Path().apply {
            moveTo(centerX - size.width * 0.28f, size.height)
            lineTo(centerX - size.width * 0.16f, centerY + size.height * 0.08f)
            lineTo(centerX + size.width * 0.16f, centerY + size.height * 0.08f)
            lineTo(centerX + size.width * 0.28f, size.height)
            close()
        }
        drawPath(torsoPath, color = shirtColor)
    }
}
