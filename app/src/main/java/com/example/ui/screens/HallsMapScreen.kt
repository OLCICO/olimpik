package com.example.ui.screens

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
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Stadium
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
import com.example.ui.components.FrostedGlassCard
import com.example.ui.components.ZomoMintPillButton
import com.example.ui.components.ZomoSecondaryPill
import com.example.ui.theme.LilacCanvas
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

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun HallsMapScreen(
    halls: List<HallInfo>,
    isCoachMode: Boolean,
    onAddHallClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(LilacCanvas)
            .testTag("halls_map_screen"),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // TOP BANNER (Image 2 style)
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
                    .padding(horizontal = 20.dp, vertical = 18.dp)
                    .shadow(elevation = 8.dp, shape = RoundedCornerShape(26.dp))
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Kulüp Spor Kompleksleri",
                            color = Color.White.copy(alpha = 0.85f),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "${halls.size} Aktif Tesis",
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }

                    if (isCoachMode) {
                        ZomoMintPillButton(
                            text = "+ Salon Ekle",
                            onClick = onAddHallClick,
                            icon = Icons.Default.Add,
                            imageRes = R.drawable.btn_tactics
                        )
                    }
                }
            }
        }

        // Section Title
        item {
            Text(
                text = "Tüm Arenalar & Lokasyonlar",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = PorcelainTextDark,
                    fontSize = 17.sp
                )
            )
        }

        // List of halls as Frosted Glass Cards (Image 1)
        items(halls, key = { it.id }) { hall ->
            HallItemCard(hall = hall, context = context)
        }

        item {
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun HallItemCard(
    hall: HallInfo,
    context: Context,
    modifier: Modifier = Modifier
) {
    FrostedGlassCard(
        modifier = modifier.fillMaxWidth(),
        cornerRadius = 24.dp
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 3D Squircle Icon Badge
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(
                            Brush.verticalGradient(
                                listOf(ZomoMintLight, ZomoCyan)
                            )
                        )
                        .border(1.dp, Color.White.copy(alpha = 0.8f), RoundedCornerShape(14.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Stadium,
                        contentDescription = hall.name,
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = hall.name,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = PorcelainTextDark,
                            fontSize = 15.sp
                        )
                    )
                    Text(
                        text = hall.district,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = PorcelainTextMuted,
                            fontSize = 12.sp
                        )
                    )
                }

                // Mint/Cyan Pill Navigation Button (Image 2 style)
                Box(
                    modifier = Modifier
                        .shadow(elevation = 4.dp, shape = RoundedCornerShape(20.dp))
                        .clip(RoundedCornerShape(20.dp))
                        .background(
                            Brush.horizontalGradient(
                                listOf(ZomoMintLight, ZomoCyan)
                            )
                        )
                        .clickable {
                            val geoUri = Uri.parse("geo:${hall.latitude},${hall.longitude}?q=${Uri.encode(hall.name)}")
                            val intent = Intent(Intent.ACTION_VIEW, geoUri).apply {
                                setPackage("com.google.android.apps.maps")
                            }
                            if (intent.resolveActivity(context.packageManager) != null) {
                                context.startActivity(intent)
                            } else {
                                context.startActivity(Intent(Intent.ACTION_VIEW, geoUri))
                            }
                        }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Navigation,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Yol Tarifi",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                    }
                }
            }

            Text(
                text = hall.address,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = PorcelainTextMuted,
                    fontSize = 12.sp
                )
            )

            // Features FlowRow with soft lavender chips
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                hall.features.forEach { feat ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(VibrantPurpleSoft)
                            .padding(horizontal = 9.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = feat,
                            color = VibrantPurpleDark,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}
