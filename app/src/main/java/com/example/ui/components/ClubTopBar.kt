package com.example.ui.components

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SwitchAccount
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.GlassBorderCyan
import com.example.ui.theme.GlassBorderGlow
import com.example.ui.theme.GlassBorderPink
import com.example.ui.theme.PorcelainTextDark
import com.example.ui.theme.PorcelainTextMuted
import com.example.ui.theme.VibrantPurpleSoft
import com.example.ui.theme.ZomoCyan
import com.example.ui.theme.ZomoMintLight
import com.example.ui.theme.ZomoPurpleDark
import com.example.ui.theme.ZomoPurplePrimary
import com.example.viewmodel.UserRole

/**
 * Top Bar with:
 * - Frame from Image 1: Translucent frosted glass with holographic glowing bottom border
 * - Buttons from Image 2: Mint/Cyan & Royal Purple 3D action pills
 */
@Composable
fun ClubTopBar(
    currentRole: UserRole,
    themeBranch: String,
    isDarkMode: Boolean,
    onRoleToggle: () -> Unit,
    onThemeToggle: () -> Unit,
    onDarkModeToggle: () -> Unit,
    userEmail: String? = null,
    onAuthClick: () -> Unit
) {
    val holographicBorder = Brush.horizontalGradient(
        listOf(
            Color.White,
            GlassBorderGlow.copy(alpha = 0.9f),
            GlassBorderCyan.copy(alpha = 0.8f),
            GlassBorderPink.copy(alpha = 0.7f),
            Color.White
        )
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color.White.copy(alpha = 0.97f),
                        Color(0xFFFAF7FF).copy(alpha = 0.93f)
                    )
                )
            )
            .statusBarsPadding()
            .border(
                width = 1.dp,
                brush = holographicBorder,
                shape = RoundedCornerShape(bottomStart = 20.dp, bottomEnd = 20.dp)
            )
            .padding(horizontal = 20.dp, vertical = 12.dp)
            .testTag("club_top_bar")
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Left: User Avatar + Name
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable { onAuthClick() }
            ) {
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(VibrantPurpleSoft)
                        .border(
                            1.5.dp,
                            Brush.linearGradient(listOf(ZomoPurplePrimary, ZomoCyan)),
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(
                            id = if (themeBranch == "VOLLEYBALL") R.drawable.athlete_volley_girl else R.drawable.athlete_basket_boy
                        ),
                        contentDescription = "Profil",
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = if (userEmail != null) userEmail.substringBefore("@") else "Olimpik Sporcu",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = PorcelainTextDark,
                            fontSize = 15.sp
                        )
                    )
                    Text(
                        text = if (currentRole == UserRole.COACH) "Antrenör Paneli" else "Veli & Sporcu",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = PorcelainTextMuted,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    )
                }
            }

            // Right: Sport Switcher & Role Toggle Button (Image 2 styles)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Sport Switcher Pill
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(VibrantPurpleSoft)
                        .border(1.dp, Color(0xFFDDD6FE), CircleShape)
                        .clickable { onThemeToggle() }
                        .testTag("theme_branch_toggle"),
                    contentAlignment = Alignment.Center
                ) {
                    when (themeBranch) {
                        "BASKETBALL" -> Image(
                            painter = painterResource(id = R.drawable.btn_basket_ball),
                            contentDescription = "Basketbol",
                            modifier = Modifier.size(24.dp)
                        )
                        "VOLLEYBALL" -> Image(
                            painter = painterResource(id = R.drawable.btn_volley_ball),
                            contentDescription = "Voleybol",
                            modifier = Modifier.size(24.dp)
                        )
                        else -> Image(
                            painter = painterResource(id = R.drawable.btn_trophy),
                            contentDescription = "Kulüp",
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }

                // Role Toggle Pill Button (from Image 2: Mint/Cyan when Coach, Royal Purple when Parent)
                val roleGradient = if (currentRole == UserRole.COACH) {
                    Brush.horizontalGradient(listOf(ZomoMintLight, ZomoCyan))
                } else {
                    Brush.horizontalGradient(listOf(ZomoPurplePrimary, ZomoPurpleDark))
                }

                Box(
                    modifier = Modifier
                        .shadow(elevation = 4.dp, shape = RoundedCornerShape(20.dp))
                        .clip(RoundedCornerShape(20.dp))
                        .background(roleGradient)
                        .border(1.dp, Color.White.copy(alpha = 0.6f), RoundedCornerShape(20.dp))
                        .clickable { onRoleToggle() }
                        .padding(horizontal = 14.dp, vertical = 8.dp)
                        .testTag("role_toggle_button")
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.SwitchAccount,
                            contentDescription = "Rol",
                            tint = Color.White,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = if (currentRole == UserRole.COACH) "Antrenör" else "Veli",
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                }
            }
        }
    }
}
