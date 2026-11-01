package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Announcement
import com.example.ui.theme.PorcelainTextDark
import com.example.ui.theme.PorcelainTextMuted
import com.example.ui.theme.VibrantCoral
import com.example.ui.theme.VibrantCoralOrange
import com.example.ui.theme.VibrantCoralSoft
import com.example.ui.theme.VibrantPurplePrimary
import com.example.ui.theme.VibrantPurpleSoft

@Composable
fun AnnouncementBanner(
    announcements: List<Announcement>,
    modifier: Modifier = Modifier
) {
    if (announcements.isEmpty()) return

    val latest = announcements.first()
    val isAlert = latest.type == "CANCELLED" || latest.type == "RESCHEDULED"
    val accentColor = if (isAlert) VibrantCoral else VibrantPurplePrimary
    val bgColor = if (isAlert) VibrantCoralSoft else VibrantPurpleSoft

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("announcement_banner"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(bgColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isAlert) Icons.Default.Warning else Icons.Default.Campaign,
                    contentDescription = "Duyuru",
                    tint = accentColor,
                    modifier = Modifier.size(22.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = latest.title,
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = PorcelainTextDark,
                        fontSize = 14.sp
                    )
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = latest.message,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = PorcelainTextMuted,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    ),
                    maxLines = 2
                )
            }
        }
    }
}

/**
 * Seçili grubun iptal / saat değişikliği uyarısı.
 * "Kulüp Programı" çerçevesiyle aynı renkli çerçeve stilindedir.
 */
@Composable
fun GroupAlertCard(
    announcement: Announcement,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(22.dp)
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .background(Brush.horizontalGradient(listOf(VibrantCoral, VibrantCoralOrange)))
            .border(
                1.5.dp,
                Brush.linearGradient(listOf(Color.White.copy(alpha = 0.9f), Color.White.copy(alpha = 0.3f))),
                shape
            )
            .padding(16.dp)
            .testTag("group_alert_card")
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.White.copy(alpha = 0.22f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = "Uyarı",
                    tint = Color.White,
                    modifier = Modifier.size(22.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = announcement.title,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = announcement.message,
                    color = Color.White.copy(alpha = 0.92f),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    maxLines = 3
                )
            }
        }
    }
}