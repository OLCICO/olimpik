package com.example.ui.screens

import com.example.R
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.SportsBasketball
import androidx.compose.material.icons.filled.SportsVolleyball
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.WeeklyPlayer
import com.example.ui.components.FrostedGlassCard
import com.example.ui.components.ZomoMintPillButton
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

@Composable
fun WeeklyStarsScreen(
    stars: List<WeeklyPlayer>,
    isCoachMode: Boolean,
    onVote: (String) -> Unit,
    onNominate: (name: String, category: String, branch: String, achievement: String) -> Unit,
    modifier: Modifier = Modifier
) {
    var showNominateDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(LilacCanvas)
            .testTag("weekly_stars_screen"),
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
                            text = "Kulüp Gurur Tablosu",
                            color = Color.White.copy(alpha = 0.85f),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "Haftanın Yıldızları",
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                        val totalVotes = stars.sumOf { it.votesCount }
                        Text(
                            text = "$totalVotes Toplam Taraftar Oyu",
                            color = Color.White.copy(alpha = 0.8f),
                            fontSize = 12.sp
                        )
                    }

                    if (isCoachMode) {
                        ZomoMintPillButton(
                            text = "Aday Göster",
                            onClick = { showNominateDialog = true },
                            icon = Icons.Default.Add
                        )
                    }
                }
            }
        }

        // Section Title
        item {
            Text(
                text = "Haftanın Aday Sporcuları",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = PorcelainTextDark,
                    fontSize = 17.sp
                )
            )
        }

        // List of players as Frosted Glass Cards (Image 1)
        items(stars, key = { it.id.ifBlank { it.playerId } }) { player ->
            PlayerStarCard(player = player, onVote = { onVote(player.id) })
        }

        item {
            Spacer(modifier = Modifier.height(20.dp))
        }
    }

    if (showNominateDialog) {
        NominatePlayerDialog(
            onDismiss = { showNominateDialog = false },
            onConfirm = { name, cat, branch, ach ->
                onNominate(name, cat, branch, ach)
                showNominateDialog = false
            }
        )
    }
}

@Composable
fun PlayerStarCard(
    player: WeeklyPlayer,
    onVote: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isBasket = player.branch.contains("Basket", ignoreCase = true)

    FrostedGlassCard(
        modifier = modifier.fillMaxWidth(),
        cornerRadius = 24.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 3D Athlete Avatar Badge (Image 1 & 2 style)
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(
                        if (isBasket) {
                            Brush.horizontalGradient(listOf(Color(0xFFFB923C), Color(0xFFEA580C)))
                        } else {
                            Brush.horizontalGradient(listOf(Color(0xFFA855F7), Color(0xFF7C3AED)))
                        }
                    )
                    .shadow(elevation = 4.dp, shape = RoundedCornerShape(18.dp)),
                contentAlignment = Alignment.Center
            ) {
                androidx.compose.foundation.Image(
                    painter = androidx.compose.ui.res.painterResource(
                        id = if (isBasket) R.drawable.athlete_basket_boy else R.drawable.athlete_volley_girl
                    ),
                    contentDescription = player.name,
                    modifier = Modifier.size(46.dp),
                    contentScale = androidx.compose.ui.layout.ContentScale.Fit
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = player.name,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = PorcelainTextDark,
                        fontSize = 15.sp
                    )
                )
                Text(
                    text = "${player.category} • ${player.branch}",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = PorcelainTextMuted,
                        fontSize = 12.sp
                    )
                )
                if (player.achievement.isNotBlank()) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = player.achievement,
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = Color(0xFF8B5CF6),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        ),
                        maxLines = 1
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Mint/Cyan Vote Pill Button (Image 2 style with 3D medal)
            Box(
                modifier = Modifier
                    .shadow(elevation = 4.dp, shape = RoundedCornerShape(20.dp))
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        Brush.horizontalGradient(
                            listOf(ZomoMintLight, ZomoCyan)
                        )
                    )
                    .clickable { onVote() }
                    .padding(horizontal = 14.dp, vertical = 7.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    androidx.compose.foundation.Image(
                        painter = androidx.compose.ui.res.painterResource(id = R.drawable.btn_medal),
                        contentDescription = "Oy Ver",
                        modifier = Modifier.size(16.dp),
                        contentScale = androidx.compose.ui.layout.ContentScale.Fit
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${player.votesCount} Oy",
                        color = Color.White,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}

@Composable
fun NominatePlayerDialog(
    onDismiss: () -> Unit,
    onConfirm: (name: String, category: String, branch: String, achievement: String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("U14 Basketbol Erkek") }
    var branch by remember { mutableStateOf("Basketbol") }
    var achievement by remember { mutableStateOf("Maç MVP'si ve yüksek efor") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Yıldız Oyuncu Aday Göster", fontWeight = FontWeight.Bold, color = PorcelainTextDark)
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Oyuncu Adı Soyadı") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                OutlinedTextField(
                    value = category,
                    onValueChange = { category = it },
                    label = { Text("Kategori (örn: U14 Erkek)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                OutlinedTextField(
                    value = branch,
                    onValueChange = { branch = it },
                    label = { Text("Branş (Basketbol / Voleybol)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                OutlinedTextField(
                    value = achievement,
                    onValueChange = { achievement = it },
                    label = { Text("Başarı Notu") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank()) {
                        onConfirm(name, category, branch, achievement)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = VibrantPurplePrimary)
            ) {
                Text("Adayı Kaydet", color = Color.White)
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Vazgeç")
            }
        }
    )
}
