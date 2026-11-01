package com.example.ui.screens

import com.example.R
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.SportsBasketball
import androidx.compose.material.icons.filled.SportsVolleyball
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import com.example.model.Announcement
import com.example.model.BranchInfo
import com.example.model.HallInfo
import com.example.model.ParentFilterPreferences
import com.example.model.TrainingSession
import com.example.ui.components.AnnouncementBanner
import com.example.ui.components.FrostedGlassCard
import com.example.ui.components.ScheduleCard
import com.example.ui.components.ZomoMintPillButton
import com.example.ui.components.ZomoPurplePillButton
import com.example.ui.components.ZomoSecondaryPill
import com.example.ui.components.ZomoSquircleTile
import com.example.ui.dialogs.FilterWizardDialog
import com.example.ui.theme.GlassBorderCyan
import com.example.ui.theme.GlassBorderGlow
import com.example.ui.theme.GlassBorderPink
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
import com.example.ui.theme.ZomoCyanLight
import com.example.ui.theme.ZomoMintLight
import com.example.ui.theme.ZomoPurpleBorder
import com.example.ui.theme.ZomoPurpleDark
import com.example.ui.theme.ZomoPurplePrimary
import com.example.ui.theme.ZomoPurpleSoft
import com.example.ui.theme.ZomoTileCyan
import com.example.ui.theme.ZomoTileMint
import com.example.ui.theme.ZomoTileOrange
import com.example.ui.theme.ZomoTilePink

/**
 * ParentScheduleScreen synthesized from user images:
 * - Frames (Çerçeveler) from Image 1: Translucent Frosted Glass with iridescent glowing borders
 * - Buttons (Butonlar) from Image 2: Vibrant Mint/Cyan 3D Pill ("Start Now" / "Create") & Purple Pills ("Storage")
 */
@Composable
fun ParentScheduleScreen(
    sessions: List<TrainingSession>,
    halls: List<HallInfo>,
    branches: List<BranchInfo>,
    announcements: List<Announcement>,
    filters: ParentFilterPreferences,
    selectedDateFilter: String,
    onDateFilterChange: (String) -> Unit,
    onFiltersChange: (ParentFilterPreferences) -> Unit,
    onResetFilters: () -> Unit,
    isCoachMode: Boolean,
    onCancelSession: (TrainingSession) -> Unit = {},
    onRescheduleSession: (TrainingSession) -> Unit = {},
    onActivateSession: (TrainingSession) -> Unit = {},
    modifier: Modifier = Modifier
) {
    var showWizard by remember { mutableStateOf(false) }

    // Dropdown States
    var showHallMenu by remember { mutableStateOf(false) }
    var showAgeMenu by remember { mutableStateOf(false) }

    val ageOptions = listOf("Tümü", "U10", "U12", "U14", "U16", "U18")
    val hallNames = listOf("Tümü") + halls.map { it.name }

    val basketballCount = sessions.count { it.branch.contains("Basket", ignoreCase = true) }
    val volleyballCount = sessions.count { it.branch.contains("Voley", ignoreCase = true) }
    val matchCount = sessions.count { it.isMatch }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(LilacCanvas)
            .testTag("parent_schedule_list"),
        contentPadding = PaddingValues(bottom = 36.dp)
    ) {
        // TOP SECTION
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp)
            ) {
                // Announcements
                if (announcements.isNotEmpty()) {
                    AnnouncementBanner(announcements = announcements)
                    Spacer(modifier = Modifier.height(14.dp))
                }

                // TOP BANNER (Image 2 Cloud Storage Card style + Image 1 Iridescent Frame)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(
                            elevation = 12.dp,
                            shape = RoundedCornerShape(26.dp),
                            ambientColor = Color(0x337C3AED),
                            spotColor = Color(0x33A855F7)
                        )
                        .clip(RoundedCornerShape(26.dp))
                        .background(
                            Brush.horizontalGradient(
                                listOf(ZomoPurplePrimary, ZomoPurpleDark)
                            )
                        )
                        .border(
                            1.5.dp,
                            Brush.linearGradient(
                                listOf(Color.White.copy(alpha = 0.9f), GlassBorderCyan, GlassBorderPink)
                            ),
                            RoundedCornerShape(26.dp)
                        )
                        .padding(20.dp)
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                // Mini Glowing 3D Trophy Asset
                                Box(
                                    modifier = Modifier
                                        .size(46.dp)
                                        .clip(RoundedCornerShape(14.dp))
                                        .background(Color.White.copy(alpha = 0.22f))
                                        .border(1.dp, Color.White.copy(alpha = 0.6f), RoundedCornerShape(14.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    androidx.compose.foundation.Image(
                                        painter = androidx.compose.ui.res.painterResource(id = R.drawable.btn_trophy),
                                        contentDescription = null,
                                        modifier = Modifier.size(38.dp),
                                        contentScale = androidx.compose.ui.layout.ContentScale.Fit
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = "Kulüp Programı",
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 17.sp
                                    )
                                    Text(
                                        text = "${sessions.size} Aktif Antrenman & Maç",
                                        color = Color.White.copy(alpha = 0.85f),
                                        fontSize = 12.sp
                                    )
                                }
                            }

                            // Counter badge
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(Color.White.copy(alpha = 0.2f))
                                    .padding(horizontal = 10.dp, vertical = 5.dp)
                            ) {
                                Text(
                                    text = "%94 Dolu",
                                    color = Color.White,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 12.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Cyan/Mint Slider Pill Bar (exact reproduction of Image 2's cyan progress bar)
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(12.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color.White.copy(alpha = 0.2f))
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(0.78f)
                                    .height(12.dp)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(
                                        Brush.horizontalGradient(
                                            listOf(ZomoMintLight, ZomoCyan)
                                        )
                                    )
                            )
                            // White thumb circle
                            Box(
                                modifier = Modifier
                                    .size(16.dp)
                                    .align(Alignment.CenterStart)
                                    .padding(start = 220.dp)
                                    .clip(CircleShape)
                                    .background(Color.White)
                                    .shadow(elevation = 3.dp, shape = CircleShape)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // 4 SQUIRCLE QUICK TILES (exact reproduction of Image 2's Picture/Video/My File/Apps grid):
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Hızlı Branş Seçimi",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = PorcelainTextDark,
                            fontSize = 17.sp
                        )
                    )

                    Text(
                        text = "Tümü",
                        color = ZomoPurplePrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable { onFiltersChange(filters.copy(branch = "Tümü")) }
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Row 1 of Squircles: Basketbol & Voleybol (featuring 3D athletes)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Tile 1: Basketbol (3D Basketball Boy Athlete)
                    ZomoSquircleTile(
                        modifier = Modifier.weight(1f),
                        imageRes = R.drawable.athlete_basket_boy,
                        iconGradient = Brush.verticalGradient(listOf(Color(0xFFFB923C), Color(0xFFEA580C))),
                        title = "Basketbol",
                        countText = "$basketballCount Seans",
                        isSelected = filters.branch == "Basketbol",
                        onClick = { onFiltersChange(filters.copy(branch = "Basketbol")) }
                    )

                    // Tile 2: Voleybol (3D Volleyball Girl Athlete)
                    ZomoSquircleTile(
                        modifier = Modifier.weight(1f),
                        imageRes = R.drawable.athlete_volley_girl,
                        iconGradient = Brush.verticalGradient(listOf(Color(0xFFA855F7), Color(0xFF7C3AED))),
                        title = "Voleybol",
                        countText = "$volleyballCount Seans",
                        isSelected = filters.branch == "Voleybol",
                        onClick = { onFiltersChange(filters.copy(branch = "Voleybol")) }
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Row 2 of Squircles: Maçlar & Tümü (featuring 3D Trophy and Tactics)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Tile 3: Maçlar (3D Gold Trophy)
                    ZomoSquircleTile(
                        modifier = Modifier.weight(1f),
                        imageRes = R.drawable.btn_trophy,
                        iconGradient = Brush.verticalGradient(listOf(Color(0xFFF43F5E), Color(0xFFE11D48))),
                        title = "Maç & Fikstür",
                        countText = "$matchCount Maç",
                        isSelected = filters.branch == "Maçlar",
                        onClick = {
                            val newBranch = if (filters.branch == "Maçlar") "Tümü" else "Maçlar"
                            onFiltersChange(filters.copy(branch = newBranch))
                        }
                    )

                    // Tile 4: Tümü (3D Tactics Clipboard)
                    ZomoSquircleTile(
                        modifier = Modifier.weight(1f),
                        imageRes = R.drawable.btn_tactics,
                        iconGradient = Brush.verticalGradient(listOf(ZomoMintLight, ZomoCyan)),
                        title = "Tüm Branşlar",
                        countText = "${sessions.size} Seans",
                        isSelected = filters.branch == "Tümü",
                        onClick = { onFiltersChange(filters.copy(branch = "Tümü")) }
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // DATE FILTER PILLS (from Image 2: "Storage" & "Cloudes" pill buttons):
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    listOf("Tüm Günler", "Bugün", "Yarın", "Hafta Sonu").forEach { dateLabel ->
                        val isSelected = selectedDateFilter == dateLabel
                        if (isSelected) {
                            ZomoPurplePillButton(
                                text = dateLabel,
                                onClick = { onDateFilterChange(dateLabel) },
                                modifier = Modifier.weight(1f),
                                isSelected = true
                            )
                        } else {
                            ZomoPurplePillButton(
                                text = dateLabel,
                                onClick = { onDateFilterChange(dateLabel) },
                                modifier = Modifier.weight(1f),
                                isSelected = false
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Secondary Filter Row (Salon & Yaş Dropdowns + Reset)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Salon Dropdown
                    Box(modifier = Modifier.weight(1f)) {
                        FrostedFilterDropdown(
                            label = if (filters.hall == "Tümü") "Salon: Tümü" else filters.hall.take(10) + "..",
                            isActive = filters.hall != "Tümü",
                            onClick = { showHallMenu = true }
                        )
                        DropdownMenu(
                            expanded = showHallMenu,
                            onDismissRequest = { showHallMenu = false }
                        ) {
                            hallNames.forEach { hall ->
                                DropdownMenuItem(
                                    text = { Text(hall) },
                                    onClick = {
                                        onFiltersChange(filters.copy(hall = hall))
                                        showHallMenu = false
                                    }
                                )
                            }
                        }
                    }

                    // Yaş Dropdown
                    Box(modifier = Modifier.weight(1f)) {
                        FrostedFilterDropdown(
                            label = if (filters.ageGroup == "Tümü") "Yaş: Tümü" else filters.ageGroup,
                            isActive = filters.ageGroup != "Tümü",
                            onClick = { showAgeMenu = true }
                        )
                        DropdownMenu(
                            expanded = showAgeMenu,
                            onDismissRequest = { showAgeMenu = false }
                        ) {
                            ageOptions.forEach { age ->
                                DropdownMenuItem(
                                    text = { Text(age) },
                                    onClick = {
                                        onFiltersChange(filters.copy(ageGroup = age))
                                        showAgeMenu = false
                                    }
                                )
                            }
                        }
                    }

                    // Reset Button (Zomo Secondary Pill)
                    ZomoSecondaryPill(
                        text = "Sıfırla",
                        onClick = onResetFilters
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                // MAIN ACTION BUTTON (from Image 2: Cyan/Mint "Start Now" / "Create" 3D Pill):
                ZomoMintPillButton(
                    text = "SİHİRBAZLA SEÇİM YAP",
                    onClick = { showWizard = true },
                    modifier = Modifier.fillMaxWidth(),
                    icon = Icons.Default.AutoAwesome,
                    imageRes = R.drawable.btn_stopwatch
                )
            }
        }

        // List Header
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Antrenman Listesi",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = PorcelainTextDark,
                        fontSize = 17.sp
                    )
                )

                Text(
                    text = "${sessions.size} Seans",
                    color = ZomoPurplePrimary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Sessions List with Frosted Glass Cards
        if (sessions.isEmpty()) {
            item {
                Box(modifier = Modifier.padding(20.dp)) {
                    FrostedGlassCard(modifier = Modifier.fillMaxWidth()) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("🔍", fontSize = 36.sp)
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "Eşleşen Seans Bulunamadı",
                                fontWeight = FontWeight.Bold,
                                color = PorcelainTextDark,
                                fontSize = 16.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Filtreleri sıfırlayarak tüm programı görebilirsiniz.",
                                color = PorcelainTextMuted,
                                fontSize = 13.sp
                            )
                            Spacer(modifier = Modifier.height(14.dp))
                            ZomoMintPillButton(
                                text = "Filtreleri Sıfırla",
                                onClick = onResetFilters
                            )
                        }
                    }
                }
            }
        } else {
            itemsIndexed(sessions, key = { _, s -> s.id }) { index, session ->
                Box(modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp)) {
                    ScheduleCard(
                        session = session,
                        halls = halls,
                        isCoachMode = isCoachMode,
                        onCancelClick = { onCancelSession(session) },
                        onChangeTimeClick = { onRescheduleSession(session) },
                        onActivateClick = { onActivateSession(session) },
                        index = index
                    )
                }
            }
        }
    }

    // Filter Wizard Dialog
    if (showWizard) {
        FilterWizardDialog(
            currentFilters = filters,
            halls = halls,
            branches = branches,
            onDismiss = { showWizard = false },
            onApplyFilters = { newPrefs ->
                onFiltersChange(newPrefs)
                showWizard = false
            }
        )
    }
}

@Composable
private fun FrostedFilterDropdown(
    label: String,
    isActive: Boolean,
    onClick: () -> Unit
) {
    val shape = RoundedCornerShape(18.dp)
    val borderGradient = Brush.linearGradient(
        listOf(
            Color.White,
            GlassBorderGlow.copy(alpha = 0.8f),
            GlassBorderCyan.copy(alpha = 0.6f)
        )
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(
                if (isActive) ZomoPurpleSoft else Color.White.copy(alpha = 0.9f)
            )
            .border(
                1.4.dp,
                if (isActive) Brush.horizontalGradient(listOf(ZomoPurplePrimary, ZomoCyan)) else borderGradient,
                shape
            )
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 9.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                color = if (isActive) ZomoPurpleDark else PorcelainTextMuted,
                fontWeight = if (isActive) FontWeight.Bold else FontWeight.Medium,
                fontSize = 11.sp,
                maxLines = 1
            )
            Icon(
                imageVector = Icons.Default.ArrowDropDown,
                contentDescription = null,
                tint = if (isActive) ZomoPurpleDark else PorcelainTextMuted,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}
