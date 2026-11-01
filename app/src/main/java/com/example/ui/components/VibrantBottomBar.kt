package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.automirrored.filled.ShowChart
import androidx.compose.material.icons.filled.Stadium
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.GlassBorderCyan
import com.example.ui.theme.GlassBorderGlow
import com.example.ui.theme.GlassBorderPink
import com.example.ui.theme.VibrantPurpleDark
import com.example.ui.theme.VibrantPurplePrimary
import com.example.ui.theme.VibrantPurpleSoft
import com.example.ui.theme.ZomoCyan
import com.example.ui.theme.ZomoMintLight

data class NavItem(
    val title: String,
    val icon: ImageVector,
    val isCenterPill: Boolean = false
)

/**
 * Frosted Glass Bottom Navigation Dock:
 * - Frame from Image 1: Translucent white/lavender glass with glowing iridescent top border
 * - Buttons from Image 2: Center 3D Mint/Cyan "Start Now" bulb & active Purple pills
 */
@Composable
fun VibrantBottomBar(
    selectedIndex: Int,
    onTabSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
    onWizardClick: () -> Unit = {}
) {
    val items = listOf(
        NavItem("Program", Icons.Default.CalendarMonth),
        NavItem("Salonlar", Icons.Default.Stadium),
        NavItem("Yıldızlar", Icons.Default.EmojiEvents),
        NavItem("Analiz", Icons.AutoMirrored.Filled.ShowChart)
    )

    val shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
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
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 16.dp,
                shape = shape,
                ambientColor = Color(0x337C3AED),
                spotColor = Color(0x22818CF8)
            )
            .clip(shape)
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color.White.copy(alpha = 0.96f),
                        Color(0xFFFAF7FF).copy(alpha = 0.92f)
                    )
                )
            )
            .border(width = 1.5.dp, brush = holographicBorder, shape = shape)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // First 2 tabs
            items.take(2).forEachIndexed { index, item ->
                NavTabItem(
                    title = item.title,
                    icon = item.icon,
                    isSelected = selectedIndex == index,
                    onClick = { onTabSelected(index) },
                    testTag = "nav_tab_$index"
                )
            }

            // Center Elevated 3D Wizard Bulb (Image 2 Mint/Cyan "Start Now" button style)
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .shadow(
                        elevation = 8.dp,
                        shape = CircleShape,
                        ambientColor = Color(0x4406B6D4),
                        spotColor = Color(0x4410B981)
                    )
                    .clip(CircleShape)
                    .background(
                        Brush.verticalGradient(
                            listOf(ZomoMintLight, ZomoCyan)
                        )
                    )
                    .border(1.2.dp, Color.White.copy(alpha = 0.8f), CircleShape)
                    .clickable { onWizardClick() }
                    .testTag("nav_wizard_bulb"),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = "Sihirbaz",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }

            // Next 2 tabs
            items.drop(2).forEachIndexed { subIndex, item ->
                val index = subIndex + 2
                NavTabItem(
                    title = item.title,
                    icon = item.icon,
                    isSelected = selectedIndex == index,
                    onClick = { onTabSelected(index) },
                    testTag = "nav_tab_$index"
                )
            }
        }
    }
}

@Composable
private fun NavTabItem(
    title: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    testTag: String
) {
    val interactionSource = remember { MutableInteractionSource() }

    val iconColor by animateColorAsState(
        targetValue = if (isSelected) VibrantPurplePrimary else Color(0xFF94A3B8),
        label = "iconColor"
    )

    // Cam efektli çerçeve: yarı saydam buzlu dolgu + parlak kenar + seçiliyken sıcak ışıma
    val glassShape = RoundedCornerShape(18.dp)
    val glassFill = Brush.verticalGradient(
        if (isSelected) listOf(Color.White.copy(alpha = 0.85f), Color(0xFFF1ECFF).copy(alpha = 0.55f))
        else listOf(Color.White.copy(alpha = 0.60f), Color(0xFFEDE9F7).copy(alpha = 0.30f))
    )
    val glassGlow = Brush.radialGradient(
        listOf(
            (if (isSelected) Color(0xFFFFE9A8) else Color(0xFFFFF4D6)).copy(alpha = if (isSelected) 0.55f else 0.22f),
            Color.Transparent
        )
    )
    val glassBorder = Brush.linearGradient(
        listOf(
            Color.White.copy(alpha = 0.95f),
            (if (isSelected) VibrantPurplePrimary else Color(0xFFCBD5E1)).copy(alpha = if (isSelected) 0.55f else 0.45f),
            Color.White.copy(alpha = 0.85f)
        )
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .clip(glassShape)
            .background(glassFill)
            .background(glassGlow)
            .border(if (isSelected) 1.6.dp else 1.2.dp, glassBorder, glassShape)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .padding(horizontal = 10.dp, vertical = 6.dp)
            .testTag(testTag)
    ) {
        Box(
            modifier = Modifier.size(36.dp),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = iconColor,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.height(2.dp))

        Text(
            text = title,
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                fontSize = 11.sp,
                color = if (isSelected) VibrantPurpleDark else Color(0xFF94A3B8)
            )
        )
    }
}
