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
import androidx.compose.foundation.border
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
        targetValue = 1.07f,
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
                    .clip(RoundedCornerShape(36.dp))
                    .clickable { showBubble = !showBubble }
                    .background(Color.Black.copy(alpha = 0.50f))
                    .border(1.dp, Color.White.copy(alpha = 0.25f), RoundedCornerShape(36.dp))
                    .padding(start = 12.dp, end = 6.dp, top = 6.dp, bottom = 6.dp)
            ) {
                Text(
                    text = "Stil Danışmanı 👔",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                )

                Box(
                    modifier = Modifier
                        .scale(if (showBubble) 1f else pulseScale)
                        .size(66.dp)
                        .shadow(12.dp, CircleShape)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                colors = listOf(Color(0xFF60A5FA), Color(0xFF1D4ED8))
                            )
                        )
                        .border(2.dp, Color.White.copy(alpha = 0.7f), CircleShape),
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
    val suitColor = when (condition) {
        WeatherCondition.Clear, WeatherCondition.MainlyClear -> Color(0xFF1E3A8A)
        WeatherCondition.Rain, WeatherCondition.RainShowers, WeatherCondition.Drizzle -> Color(0xFF0F766E)
        WeatherCondition.Snow, WeatherCondition.SnowShowers -> Color(0xFF475569)
        WeatherCondition.Thunderstorm -> Color(0xFF4C1D95)
        else -> Color(0xFF334155)
    }

    Canvas(modifier = Modifier.size(54.dp)) {
        val w = size.width
        val h = size.height
        val cx = w / 2f
        val cy = h / 2f

        val suitPath = Path().apply {
            moveTo(cx - w * 0.42f, h)
            lineTo(cx - w * 0.28f, cy + h * 0.16f)
            lineTo(cx + w * 0.28f, cy + h * 0.16f)
            lineTo(cx + w * 0.42f, h)
            close()
        }
        drawPath(suitPath, color = suitColor)

        val shirtPath = Path().apply {
            moveTo(cx - w * 0.16f, cy + h * 0.16f)
            lineTo(cx, cy + h * 0.38f)
            lineTo(cx + w * 0.16f, cy + h * 0.16f)
            close()
        }
        drawPath(shirtPath, color = Color.White)

        val bowtiePath = Path().apply {
            moveTo(cx, cy + h * 0.22f)
            lineTo(cx - w * 0.12f, cy + h * 0.18f)
            lineTo(cx - w * 0.12f, cy + h * 0.26f)
            close()
            moveTo(cx, cy + h * 0.22f)
            lineTo(cx + w * 0.12f, cy + h * 0.18f)
            lineTo(cx + w * 0.12f, cy + h * 0.26f)
            close()
        }
        drawPath(bowtiePath, color = Color(0xFFE11D48))
        drawCircle(
            color = Color(0xFFBE123C),
            radius = w * 0.035f,
            center = Offset(cx, cy + h * 0.22f)
        )

        drawRect(
            color = Color(0xFFFFDFBA),
            topLeft = Offset(cx - w * 0.08f, cy + h * 0.06f),
            size = Size(w * 0.16f, h * 0.12f)
        )

        val headCenter = Offset(cx, cy - h * 0.08f)
        val headRadius = w * 0.24f
        drawCircle(
            color = Color(0xFFFFDFBA),
            radius = headRadius,
            center = headCenter
        )

        drawCircle(
            color = Color(0xFFFCA5A5).copy(alpha = 0.65f),
            radius = w * 0.055f,
            center = Offset(cx - w * 0.13f, cy - h * 0.05f)
        )
        drawCircle(
            color = Color(0xFFFCA5A5).copy(alpha = 0.65f),
            radius = w * 0.055f,
            center = Offset(cx + w * 0.13f, cy - h * 0.05f)
        )

        val hairPath = Path().apply {
            moveTo(cx - headRadius * 1.05f, headCenter.y - headRadius * 0.1f)
            cubicTo(
                cx - headRadius * 0.8f,
                headCenter.y - headRadius * 1.25f,
                cx + headRadius * 0.5f,
                headCenter.y - headRadius * 1.30f,
                cx + headRadius * 1.05f,
                headCenter.y - headRadius * 0.15f
            )
            cubicTo(
                cx + headRadius * 0.7f,
                headCenter.y - headRadius * 0.75f,
                cx - headRadius * 0.3f,
                headCenter.y - headRadius * 0.85f,
                cx - headRadius * 1.05f,
                headCenter.y - headRadius * 0.1f
            )
            close()
        }
        drawPath(hairPath, color = Color(0xFF3E2723))

        val browY = headCenter.y - headRadius * 0.38f
        drawLine(
            color = Color(0xFF3E2723),
            start = Offset(cx - w * 0.12f, browY + 1f),
            end = Offset(cx - w * 0.04f, browY - 1.5f),
            strokeWidth = 2.5f
        )
        drawLine(
            color = Color(0xFF3E2723),
            start = Offset(cx + w * 0.04f, browY - 1.5f),
            end = Offset(cx + w * 0.12f, browY + 1f),
            strokeWidth = 2.5f
        )

        val eyeY = headCenter.y - headRadius * 0.15f
        val leftEyeX = cx - w * 0.08f
        val rightEyeX = cx + w * 0.08f
        val eyeRadius = w * 0.038f

        drawCircle(color = Color(0xFF1E293B), radius = eyeRadius, center = Offset(leftEyeX, eyeY))
        drawCircle(color = Color(0xFF1E293B), radius = eyeRadius, center = Offset(rightEyeX, eyeY))
        drawCircle(color = Color.White, radius = eyeRadius * 0.4f, center = Offset(leftEyeX - 1f, eyeY - 1f))
        drawCircle(color = Color.White, radius = eyeRadius * 0.4f, center = Offset(rightEyeX - 1f, eyeY - 1f))

        val mouthY = headCenter.y + headRadius * 0.28f
        val smilePath = Path().apply {
            moveTo(cx - w * 0.09f, mouthY)
            quadraticTo(
                cx,
                mouthY + h * 0.10f,
                cx + w * 0.09f,
                mouthY
            )
            close()
        }
        drawPath(smilePath, color = Color(0xFFBE123C))
        drawLine(
            color = Color.White,
            start = Offset(cx - w * 0.07f, mouthY + 1f),
            end = Offset(cx + w * 0.07f, mouthY + 1f),
            strokeWidth = 2f
        )
    }
}
