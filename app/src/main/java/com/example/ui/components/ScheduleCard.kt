package com.example.ui.components

import com.example.R
import android.content.Context
import android.content.Intent
import android.net.Uri
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.NorthEast
import androidx.compose.material.icons.filled.Update
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.HallInfo
import com.example.model.TrainingSession
import com.example.ui.theme.PorcelainTextDark
import com.example.ui.theme.PorcelainTextMuted
import com.example.ui.theme.VibrantCoral
import com.example.ui.theme.VibrantCoralOrange
import com.example.ui.theme.VibrantPurpleDark
import com.example.ui.theme.VibrantPurpleLight
import com.example.ui.theme.VibrantPurplePrimary
import com.example.ui.theme.VibrantPurpleSoft
import com.example.ui.theme.ZomoCyan
import com.example.ui.theme.ZomoMintLight

/**
 * ScheduleCard synthesized according to user instructions:
 * - Frame from Image 1: Frosted Glass card with glowing iridescent holographic border
 * - Buttons from Image 2: Vibrant Mint/Cyan 3D pill and Royal Purple pills
 */
@Composable
fun ScheduleCard(
    session: TrainingSession,
    halls: List<HallInfo>,
    isCoachMode: Boolean,
    onCancelClick: () -> Unit = {},
    onChangeTimeClick: () -> Unit = {},
    onActivateClick: () -> Unit = {},
    index: Int? = null,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val isBasketball = session.branch.contains("Basket", ignoreCase = true)

    val badgeGradient = if (isBasketball) {
        Brush.horizontalGradient(listOf(VibrantCoral, VibrantCoralOrange))
    } else {
        Brush.horizontalGradient(listOf(VibrantPurpleLight, VibrantPurpleDark))
    }

    val matchedHall = halls.firstOrNull {
        it.name.equals(session.hall, ignoreCase = true) || session.hall.contains(it.district, ignoreCase = true)
    }

    FrostedGlassCard(
        modifier = modifier
            .fillMaxWidth()
            .testTag("session_card_${session.id}"),
        cornerRadius = 24.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Left: 3D Squircle Sport Badge with 3D athlete (Image 2 style)
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(badgeGradient)
                        .border(1.2.dp, Color.White.copy(alpha = 0.8f), RoundedCornerShape(16.dp))
                        .shadow(elevation = 4.dp, shape = RoundedCornerShape(16.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    androidx.compose.foundation.Image(
                        painter = androidx.compose.ui.res.painterResource(
                            id = if (isBasketball) R.drawable.athlete_basket_boy else R.drawable.athlete_volley_girl
                        ),
                        contentDescription = session.branch,
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(14.dp)),
                        contentScale = androidx.compose.ui.layout.ContentScale.Fit
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                // Center: Title + Subtitle
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = session.title.ifBlank { "${session.branch} ${session.ageGroup}" },
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = PorcelainTextDark,
                            fontSize = 15.sp
                        ),
                        maxLines = 1
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "${session.hall} • ${session.dayOfWeek}",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = PorcelainTextMuted,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        ),
                        maxLines = 1
                    )
                    if (session.coachName.isNotBlank()) {
                        Text(
                            text = "Koç: ${session.coachName}",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = Color(0xFF8B5CF6),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.width(10.dp))

                // Right: Time & Status Pill Button (from Image 2: Mint/Cyan & Purple pills)
                Column(
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "${session.startTime} - ${session.endTime}",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = PorcelainTextDark,
                            fontSize = 13.sp
                        )
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    // Pill Status Button
                    when (session.status) {
                        "İptal" -> {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(Color(0xFFFFECEB))
                                    .border(1.dp, Color(0xFFFCA5A5), RoundedCornerShape(20.dp))
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "İptal",
                                    color = Color(0xFFDC2626),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp
                                )
                            }
                        }
                        "Değişti" -> {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(Color(0xFFE0F2FE))
                                    .border(1.dp, Color(0xFF7DD3FC), RoundedCornerShape(20.dp))
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "Değişti",
                                    color = Color(0xFF0284C7),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp
                                )
                            }
                        }
                        else -> {
                            // Active / Match Button (Image 2: Mint/Cyan "Start Now" or Sunset Coral)
                            val buttonGradient = if (session.isMatch) {
                                Brush.horizontalGradient(listOf(VibrantCoral, VibrantCoralOrange))
                            } else {
                                Brush.horizontalGradient(listOf(ZomoMintLight, ZomoCyan))
                            }

                            Box(
                                modifier = Modifier
                                    .shadow(elevation = 4.dp, shape = RoundedCornerShape(20.dp))
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(buttonGradient)
                                    .border(1.dp, Color.White.copy(alpha = 0.6f), RoundedCornerShape(20.dp))
                                    .padding(horizontal = 12.dp, vertical = 5.dp)
                            ) {
                                Text(
                                    text = if (session.isMatch) "Maç 🏆" else "Aktif ✓",
                                    color = Color.White,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                }
            }

            // Hall Navigation & Coach Actions Row
            if (matchedHall != null || isCoachMode) {
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    if (matchedHall != null) {
                        // Navigation button in soft lavender pill (Image 2 "Cloudes" style)
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clip(RoundedCornerShape(14.dp))
                                .background(VibrantPurpleSoft)
                                .border(1.dp, Color(0xFFDDD6FE), RoundedCornerShape(14.dp))
                                .clickable {
                                    val geoUri = Uri.parse("geo:${matchedHall.latitude},${matchedHall.longitude}?q=${Uri.encode(matchedHall.name)}")
                                    val intent = Intent(Intent.ACTION_VIEW, geoUri).apply {
                                        setPackage("com.google.android.apps.maps")
                                    }
                                    if (intent.resolveActivity(context.packageManager) != null) {
                                        context.startActivity(intent)
                                    } else {
                                        context.startActivity(Intent(Intent.ACTION_VIEW, geoUri))
                                    }
                                }
                                .padding(horizontal = 11.dp, vertical = 6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Navigation,
                                contentDescription = "Yol Tarifi",
                                tint = VibrantPurplePrimary,
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(5.dp))
                            Text(
                                text = "Yol Tarifi Al",
                                color = VibrantPurplePrimary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    } else {
                        Spacer(modifier = Modifier.width(1.dp))
                    }

                    if (isCoachMode) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Saat Değiştir
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(Color(0xFFF1F5F9))
                                    .clickable { onChangeTimeClick() }
                                    .padding(horizontal = 9.dp, vertical = 5.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Update, contentDescription = null, tint = Color(0xFF475569), modifier = Modifier.size(12.dp))
                                    Spacer(modifier = Modifier.width(3.dp))
                                    Text("Saat", color = Color(0xFF475569), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                            }

                            // İptal / Aktifleştir
                            if (session.status != "İptal") {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(14.dp))
                                        .background(Color(0xFFFFECEB))
                                        .clickable { onCancelClick() }
                                        .padding(horizontal = 9.dp, vertical = 5.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.Cancel, contentDescription = null, tint = Color(0xFFDC2626), modifier = Modifier.size(12.dp))
                                        Spacer(modifier = Modifier.width(3.dp))
                                        Text("İptal", color = Color(0xFFDC2626), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            } else {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(14.dp))
                                        .background(Color(0xFFD1FAE5))
                                        .clickable { onActivateClick() }
                                        .padding(horizontal = 9.dp, vertical = 5.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF059669), modifier = Modifier.size(12.dp))
                                        Spacer(modifier = Modifier.width(3.dp))
                                        Text("Aktif Et", color = Color(0xFF059669), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
