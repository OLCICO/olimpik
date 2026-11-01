package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.SportsScore
import androidx.compose.material.icons.filled.Stadium
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.TrainingSession
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.CyberGold

@Composable
fun NextSessionBanner(
    sessions: List<TrainingSession>,
    modifier: Modifier = Modifier
) {
    val nextUpcoming = sessions.firstOrNull { it.status != "İptal" } ?: sessions.firstOrNull() ?: return

    val infiniteTransition = rememberInfiniteTransition(label = "nextPulse")
    val pulse by infiniteTransition.animateFloat(
        initialValue = 0.7f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(1400, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "pulse"
    )

    // 3D Cyber Next Match Banner
    Game3DCard(
        modifier = modifier
            .fillMaxWidth()
            .testTag("next_session_banner"),
        shapeRadius = 20.dp,
        accentGlow = CyberGold,
        containerColor = Color(0xFF0F2042)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.horizontalGradient(
                        listOf(
                            Color(0xFF1E3A8A).copy(alpha = 0.9f),
                            Color(0xFF0F172A).copy(alpha = 0.95f),
                            Color(0xFF0284C7).copy(alpha = 0.4f)
                        )
                    )
                )
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 3D Battle/Score Emblem
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(
                            Brush.verticalGradient(
                                listOf(CyberGold, Color(0xFFD97706))
                            )
                        )
                        .border(1.5.dp, Color.White.copy(alpha = 0.7f), RoundedCornerShape(14.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.SportsScore,
                        contentDescription = null,
                        tint = Color(0xFF07142A),
                        modifier = Modifier.size(30.dp)
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(CyberGold)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "SIRADAKİ GÖREV / MAÇ",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Black,
                                    color = CyberGold,
                                    fontSize = 11.sp,
                                    letterSpacing = 1.sp
                                )
                            )
                        }

                        GameHudChip(
                            text = if (nextUpcoming.isMatch) "MAÇ GÜNÜ" else "CANLI",
                            color = if (nextUpcoming.isMatch) CyberGold else CyberCyan
                        )
                    }

                    Spacer(modifier = Modifier.height(3.dp))

                    Text(
                        text = "${nextUpcoming.title} · ${nextUpcoming.hall}",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Black,
                            color = Color.White,
                            fontSize = 15.sp
                        ),
                        maxLines = 1
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Stadium, contentDescription = null, tint = Color(0xFF94A3B8), modifier = Modifier.size(13.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${nextUpcoming.date} · ${nextUpcoming.startTime} - ${nextUpcoming.endTime}",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = Color(0xFFE2E8F0),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }
            }
        }
    }
}
