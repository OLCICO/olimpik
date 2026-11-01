package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
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
import androidx.compose.material.icons.filled.ArrowOutward
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.GlassBorderCyan
import com.example.ui.theme.GlassBorderGlow
import com.example.ui.theme.GlassBorderPink
import com.example.ui.theme.PorcelainTextDark
import com.example.ui.theme.PorcelainTextMuted
import com.example.ui.theme.ZomoCyan
import com.example.ui.theme.ZomoCyanLight
import com.example.ui.theme.ZomoMint
import com.example.ui.theme.ZomoMintLight
import com.example.ui.theme.ZomoPurpleDark
import com.example.ui.theme.ZomoPurplePrimary
import com.example.ui.theme.ZomoPurpleSoft

/**
 * 1. Image Style Frosted Glass Frame / Card:
 * - Rounded 24.dp shape
 * - Glowing iridescent holographic border (White -> Lavender -> Cyan -> Pink)
 * - Translucent frosted glass body
 * - Dreamy ambient shadow
 */
@Composable
fun FrostedGlassCard(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 24.dp,
    content: @Composable BoxScope.() -> Unit
) {
    val shape = RoundedCornerShape(cornerRadius)
    val holographicBorder = Brush.linearGradient(
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
            .shadow(
                elevation = 10.dp,
                shape = shape,
                ambientColor = Color(0x33A78BFA),
                spotColor = Color(0x22818CF8)
            )
            .clip(shape)
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color.White.copy(alpha = 0.94f),
                        Color(0xFFFAF7FF).copy(alpha = 0.88f)
                    )
                )
            )
            .border(width = 1.6.dp, brush = holographicBorder, shape = shape)
    ) {
        content()
    }
}

/**
 * 2. Image Style Vibrant Mint/Cyan 3D Pill Button ("Start Now" / "Create" style):
 * - Rounded 30.dp pill shape
 * - Mint-to-Cyan energetic gradient with subtle top highlight
 * - Bold pure white typography with diagonal arrow or custom icon
 */
@Composable
fun ZomoMintPillButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector = Icons.Default.ArrowOutward,
    imageRes: Int? = null
) {
    val shape = RoundedCornerShape(30.dp)
    val mintCyanGradient = Brush.horizontalGradient(
        listOf(ZomoMintLight, ZomoCyan)
    )

    Box(
        modifier = modifier
            .shadow(
                elevation = 8.dp,
                shape = shape,
                ambientColor = Color(0x4406B6D4),
                spotColor = Color(0x4410B981)
            )
            .clip(shape)
            .background(mintCyanGradient)
            .border(1.2.dp, Color.White.copy(alpha = 0.7f), shape)
            .clickable(onClick = onClick)
            .padding(horizontal = 22.dp, vertical = 14.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (imageRes != null) {
                androidx.compose.foundation.Image(
                    painter = androidx.compose.ui.res.painterResource(id = imageRes),
                    contentDescription = null,
                    modifier = Modifier.size(26.dp),
                    contentScale = androidx.compose.ui.layout.ContentScale.Fit
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text(
                text = text,
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White,
                    fontSize = 14.sp,
                    letterSpacing = 0.3.sp
                )
            )
            Spacer(modifier = Modifier.width(8.dp))
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.25f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(14.dp)
                )
            }
        }
    }
}

/**
 * 2. Image Style Vibrant Purple Pill Button ("Storage" style):
 */
@Composable
fun ZomoPurplePillButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isSelected: Boolean = true
) {
    val shape = RoundedCornerShape(22.dp)

    Box(
        modifier = modifier
            .clip(shape)
            .then(
                if (isSelected) {
                    Modifier
                        .shadow(elevation = 6.dp, shape = shape, ambientColor = Color(0x337C3AED))
                        .background(
                            Brush.horizontalGradient(
                                listOf(ZomoPurplePrimary, ZomoPurpleDark)
                            )
                        )
                } else {
                    Modifier
                        .background(ZomoPurpleSoft)
                        .border(1.dp, Color(0xFFDDD6FE), shape)
                }
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 9.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) Color.White else ZomoPurpleDark,
                fontSize = 12.sp
            )
        )
    }
}

/**
 * 2. Image Style Secondary Soft Pill ("Cloudes" / "Cancel" style):
 */
@Composable
fun ZomoSecondaryPill(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(22.dp)

    Box(
        modifier = modifier
            .clip(shape)
            .background(ZomoPurpleSoft)
            .border(1.dp, Color(0xFFDDD6FE), shape)
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = FontWeight.Bold,
                color = ZomoPurpleDark,
                fontSize = 11.sp
            )
        )
    }
}

/**
 * 2. Image Style 3D Squircle Quick Tile ("Picture", "Video", "My File", "Apps"):
 * Supports both custom 3D athlete/equipment drawables and vector icons.
 */
@Composable
fun ZomoSquircleTile(
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    imageRes: Int? = null,
    iconGradient: Brush,
    title: String,
    countText: String,
    isSelected: Boolean = false,
    onClick: () -> Unit
) {
    val shape = RoundedCornerShape(22.dp)
    val holographicBorder = Brush.linearGradient(
        listOf(
            Color.White,
            GlassBorderGlow.copy(alpha = 0.9f),
            GlassBorderCyan.copy(alpha = 0.7f),
            GlassBorderPink.copy(alpha = 0.7f)
        )
    )

    Card(
        modifier = modifier
            .shadow(
                elevation = if (isSelected) 8.dp else 4.dp,
                shape = shape,
                ambientColor = Color(0x228B5CF6)
            )
            .clip(shape)
            .clickable(onClick = onClick),
        shape = shape,
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) Color(0xFFF5F3FF) else Color.White
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    width = if (isSelected) 2.dp else 1.2.dp,
                    brush = if (isSelected) Brush.horizontalGradient(listOf(ZomoPurplePrimary, ZomoCyan)) else holographicBorder,
                    shape = shape
                )
                .padding(12.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Squircle Badge with 3D athlete or 3D sports asset
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(iconGradient)
                        .border(1.2.dp, Color.White.copy(alpha = 0.7f), RoundedCornerShape(16.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    if (imageRes != null) {
                        androidx.compose.foundation.Image(
                            painter = androidx.compose.ui.res.painterResource(id = imageRes),
                            contentDescription = title,
                            modifier = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(14.dp)),
                            contentScale = androidx.compose.ui.layout.ContentScale.Fit
                        )
                    } else if (icon != null) {
                        Icon(
                            imageVector = icon,
                            contentDescription = title,
                            tint = Color.White,
                            modifier = Modifier.size(26.dp)
                        )
                    }
                }

                Column {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = PorcelainTextDark,
                            fontSize = 14.sp
                        )
                    )
                    Text(
                        text = countText,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = PorcelainTextMuted,
                            fontSize = 11.sp
                        )
                    )
                }
            }
        }
    }
}

/**
 * 3D Game Action Button using transparent 3D assets with responsive touch effect
 */
@Composable
fun Game3DGraphicButton(
    imageRes: Int,
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(22.dp)
    Box(
        modifier = modifier
            .shadow(
                elevation = 6.dp,
                shape = shape,
                ambientColor = Color(0x3306B6D4)
            )
            .clip(shape)
            .background(
                Brush.horizontalGradient(
                    listOf(Color.White, Color(0xFFF9F6FF))
                )
            )
            .border(
                1.5.dp,
                Brush.horizontalGradient(listOf(ZomoMintLight, ZomoCyan)),
                shape
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            androidx.compose.foundation.Image(
                painter = androidx.compose.ui.res.painterResource(id = imageRes),
                contentDescription = null,
                modifier = Modifier.size(32.dp),
                contentScale = androidx.compose.ui.layout.ContentScale.Fit
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = text,
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.ExtraBold,
                    color = PorcelainTextDark,
                    fontSize = 13.sp
                )
            )
        }
    }
}
