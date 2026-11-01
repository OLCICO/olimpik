package com.example.ui.screens

import com.example.R
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowOutward
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.SportsBasketball
import androidx.compose.material.icons.filled.SportsVolleyball
import androidx.compose.material.icons.filled.Stadium
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.HallInfo
import com.example.model.TrainingSession
import com.example.model.WeeklyPlayer
import com.example.ui.components.FrostedGlassCard
import com.example.ui.components.ZomoMintPillButton
import com.example.ui.theme.GlassBorderCyan
import com.example.ui.theme.GlassBorderPink
import com.example.ui.theme.LilacCanvas
import com.example.ui.theme.PorcelainTextDark
import com.example.ui.theme.PorcelainTextMuted
import com.example.ui.theme.VibrantCoral
import com.example.ui.theme.VibrantCoralOrange
import com.example.ui.theme.VibrantCoralSoft
import com.example.ui.theme.VibrantPurpleBorder
import com.example.ui.theme.VibrantPurpleDark
import com.example.ui.theme.VibrantPurpleLight
import com.example.ui.theme.VibrantPurplePrimary
import com.example.ui.theme.VibrantPurpleSoft
import com.example.ui.theme.ZomoCyan
import com.example.ui.theme.ZomoMintLight

/**
 * AnalyticsScreen with:
 * - Frames from Image 1: Frosted Glass Cards with iridescent glowing borders
 * - Buttons from Image 2: Vibrant Mint/Cyan 3D buttons ("Start Now" / "Create")
 */
@Composable
fun AnalyticsScreen(
    sessions: List<TrainingSession>,
    halls: List<HallInfo>,
    weeklyStars: List<WeeklyPlayer>,
    modifier: Modifier = Modifier
) {
    val totalSessions = sessions.size
    val basketballCount = sessions.count { it.branch.contains("Basket", ignoreCase = true) }
    val volleyballCount = sessions.count { it.branch.contains("Voley", ignoreCase = true) }
    val matchCount = sessions.count { it.isMatch }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(LilacCanvas)
            .testTag("analytics_screen"),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 20.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // TOP TOTAL BANNER (Image 2 style with cyan slider pill)
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(26.dp))
                    .background(
                        Brush.horizontalGradient(
                            listOf(VibrantPurplePrimary, VibrantPurpleDark)
                        )
                    )
                    .padding(horizontal = 20.dp, vertical = 16.dp)
                    .shadow(elevation = 8.dp, shape = RoundedCornerShape(26.dp))
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Kulüp Doluluk Oranı",
                                color = Color.White.copy(alpha = 0.85f),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = "%94 Kapasite",
                                color = Color.White,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }

                        // Mint counter pill
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(
                                    Brush.horizontalGradient(listOf(ZomoMintLight, ZomoCyan))
                                )
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "${totalSessions * 55} Sporcu / Hafta",
                                color = Color.White,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 12.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Mint/Cyan Progress Slider Bar (from Image 2)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(10.dp)
                            .clip(RoundedCornerShape(5.dp))
                            .background(Color.White.copy(alpha = 0.25f))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(0.94f)
                                .height(10.dp)
                                .clip(RoundedCornerShape(5.dp))
                                .background(
                                    Brush.horizontalGradient(
                                        listOf(ZomoMintLight, ZomoCyan)
                                    )
                                )
                        )
                    }
                }
            }
        }

        // WAVE CHART CARD: Frosted Glass Frame with iridescent border (Image 1)
        item {
            FrostedGlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(18.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Haftalık Katılım Yoğunluğu",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = PorcelainTextDark,
                                    fontSize = 15.sp
                                )
                            )
                            Text(
                                text = "Şube ve salon doluluk eğrisi",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = PorcelainTextMuted,
                                    fontSize = 11.sp
                                )
                            )
                        }

                        // Peak Pill Button in Mint/Cyan (Image 2 style)
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(
                                    Brush.horizontalGradient(
                                        listOf(ZomoMintLight, ZomoCyan)
                                    )
                                )
                                .padding(horizontal = 10.dp, vertical = 5.dp)
                        ) {
                            Text(
                                text = "Zirve: Hafta Sonu",
                                color = Color.White,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 11.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Wave Line Chart
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(130.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Y-Axis Labels
                        Column(
                            modifier = Modifier.height(120.dp),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("1600", color = Color(0xFF94A3B8), fontSize = 10.sp)
                            Text("1400", color = Color(0xFF94A3B8), fontSize = 10.sp)
                            Text("1200", color = Color(0xFF94A3B8), fontSize = 10.sp)
                            Text("1000", color = Color(0xFF94A3B8), fontSize = 10.sp)
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Canvas(
                            modifier = Modifier
                                .weight(1f)
                                .height(120.dp)
                        ) {
                            val w = size.width
                            val h = size.height

                            val path = Path().apply {
                                moveTo(0f, h * 0.75f)
                                cubicTo(
                                    w * 0.25f, h * 0.85f,
                                    w * 0.35f, h * 0.40f,
                                    w * 0.50f, h * 0.65f
                                )
                                cubicTo(
                                    w * 0.62f, h * 0.82f,
                                    w * 0.70f, h * 0.15f,
                                    w * 0.75f, h * 0.10f
                                )
                                cubicTo(
                                    w * 0.80f, h * 0.05f,
                                    w * 0.88f, h * 0.70f,
                                    w, h * 0.35f
                                )
                            }

                            // Vibrant Purple stroke
                            drawPath(
                                path = path,
                                color = VibrantPurplePrimary,
                                style = Stroke(width = 3.5f)
                            )

                            // Peak point
                            val peakX = w * 0.75f
                            val peakY = h * 0.10f

                            drawLine(
                                color = VibrantPurpleBorder,
                                start = Offset(peakX, peakY),
                                end = Offset(peakX, h),
                                strokeWidth = 1.5f,
                                pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 6f), 0f)
                            )

                            // Peak dot in cyan/mint
                            drawCircle(
                                color = ZomoCyan,
                                radius = 6f,
                                center = Offset(peakX, peakY)
                            )
                        }
                    }
                }
            }
        }

        // Section Title: Branş Dağılımı
        item {
            Text(
                text = "Branş & Seans Dağılımı",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = PorcelainTextDark,
                    fontSize = 17.sp
                )
            )
        }

        // 2-Column Metric Cards Row 1 (Frosted Glass frames)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                DashboardMetricCard(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Default.SportsBasketball,
                    iconBg = Brush.horizontalGradient(listOf(Color(0xFFFB923C), Color(0xFFEA580C))),
                    title = "Basketbol",
                    subtitle = "Tüm Yaş Grupları",
                    amount = "$basketballCount Seans"
                )

                DashboardMetricCard(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Default.SportsVolleyball,
                    iconBg = Brush.horizontalGradient(listOf(Color(0xFFA855F7), Color(0xFF7C3AED))),
                    title = "Voleybol",
                    subtitle = "Kız & Erkek Ligleri",
                    amount = "$volleyballCount Seans"
                )
            }
        }

        // 2-Column Metric Cards Row 2 (Frosted Glass frames)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                DashboardMetricCard(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Default.Stadium,
                    iconBg = Brush.horizontalGradient(listOf(ZomoMintLight, ZomoCyan)),
                    title = "Spor Arenaları",
                    subtitle = "Pendik • Gebze",
                    amount = "${halls.size} Tesis"
                )

                DashboardMetricCard(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Default.Timer,
                    iconBg = Brush.horizontalGradient(listOf(Color(0xFFF43F5E), Color(0xFFE11D48))),
                    title = "Haftalık Maçlar",
                    subtitle = "Resmi Fikstür",
                    amount = "$matchCount Maç"
                )
            }
        }

        // Bottom CTA Button (Mint/Cyan 3D Pill Button from Image 2)
        item {
            Spacer(modifier = Modifier.height(10.dp))
            ZomoMintPillButton(
                text = "DETAYLI ANALİZ RAPORU PAYLAŞ",
                onClick = { /* action */ },
                modifier = Modifier.fillMaxWidth(),
                imageRes = R.drawable.btn_stopwatch
            )
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
fun DashboardMetricCard(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    iconBg: Brush,
    title: String,
    subtitle: String,
    amount: String
) {
    FrostedGlassCard(
        modifier = modifier,
        cornerRadius = 22.dp
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(iconBg)
                    .border(1.dp, Color.White.copy(alpha = 0.7f), RoundedCornerShape(14.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }

            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = PorcelainTextDark,
                        fontSize = 14.sp
                    )
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = PorcelainTextMuted,
                        fontSize = 11.sp
                    )
                )
            }

            Text(
                text = amount,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.ExtraBold,
                    color = PorcelainTextDark,
                    fontSize = 16.sp
                )
            )
        }
    }
}
