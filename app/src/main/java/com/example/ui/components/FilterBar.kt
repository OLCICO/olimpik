package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.material.icons.filled.RestartAlt
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.BranchInfo
import com.example.model.HallInfo
import com.example.model.ParentFilterPreferences
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.CyberGold
import com.example.ui.theme.OlimpikYellow

@Composable
fun FilterBar(
    filters: ParentFilterPreferences,
    halls: List<HallInfo>,
    branches: List<BranchInfo>,
    selectedDateFilter: String,
    onDateFilterChange: (String) -> Unit,
    onFiltersChange: (ParentFilterPreferences) -> Unit,
    onResetFilters: () -> Unit
) {
    val activeFilterCount = listOf(
        filters.branch != "Tümü",
        filters.hall != "Tümü",
        filters.ageGroup != "Tümü",
        filters.gender != "Tümü",
        selectedDateFilter != "ALL"
    ).count { it }

    // 3D Cockpit Control Console with enableTilt = false so clicks are 100% responsive
    Game3DCard(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("filter_bar_card"),
        shapeRadius = 22.dp,
        accentGlow = CyberCyan,
        containerColor = Color(0xFF0C1628),
        enableTilt = false
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header with Active Status and 3D Reset button
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                Brush.verticalGradient(
                                    listOf(CyberGold, Color(0xFFD97706))
                                )
                            )
                            .border(1.5.dp, Color.White.copy(alpha = 0.8f), RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.FilterAlt,
                            contentDescription = "Filtreler",
                            tint = Color(0xFF07142A),
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "CANLI FİLTRE KOKPİTİ",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Black,
                                    fontSize = 15.sp,
                                    color = Color.White,
                                    letterSpacing = 0.5.sp
                                )
                            )
                            if (activeFilterCount > 0) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(CircleShape)
                                        .background(
                                            Brush.verticalGradient(
                                                listOf(Color(0xFFFFDF00), Color(0xFFF59E0B))
                                            )
                                        )
                                        .border(1.dp, Color.White, CircleShape)
                                        .padding(horizontal = 7.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "$activeFilterCount AKTİF",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = Color(0xFF07142A),
                                            fontWeight = FontWeight.Black,
                                            fontSize = 9.sp
                                        )
                                    )
                                }
                            }
                        }
                        Text(
                            text = "Hızlı İdman & Kategori Seçimi",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = Color(0xFF94A3B8),
                                fontSize = 10.sp
                            )
                        )
                    }
                }

                // Sleek Frosted Glass Reset Button
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.White.copy(alpha = 0.08f))
                        .border(1.dp, Color.White.copy(alpha = 0.22f), RoundedCornerShape(12.dp))
                        .clickable { onResetFilters() }
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                        .testTag("reset_filters_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.RestartAlt,
                            contentDescription = "Sıfırla",
                            tint = Color(0xFFCBD5E1),
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "SIFIRLA",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Black,
                                color = Color(0xFFE2E8F0),
                                fontSize = 11.sp,
                                letterSpacing = 0.5.sp
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Date Quick Filter Tabs - Unified Frosted Glass & Stadium Gold (No Rainbow)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                GameCandy3DChip(
                    text = "TÜM GÜNLER",
                    isSelected = selectedDateFilter == "ALL",
                    onClick = { onDateFilterChange("ALL") },
                    activeTheme = GameColorTheme.YELLOW,
                    inactiveTheme = GameColorTheme.DARK,
                    isChecked = selectedDateFilter == "ALL",
                    modifier = Modifier.weight(1f),
                    testTag = "date_filter_all"
                )
                GameCandy3DChip(
                    text = "BUGÜN",
                    isSelected = selectedDateFilter == "TODAY",
                    onClick = { onDateFilterChange("TODAY") },
                    activeTheme = GameColorTheme.YELLOW,
                    inactiveTheme = GameColorTheme.DARK,
                    isChecked = selectedDateFilter == "TODAY",
                    modifier = Modifier.weight(1f),
                    testTag = "date_filter_today"
                )
                GameCandy3DChip(
                    text = "YARIN",
                    isSelected = selectedDateFilter == "TOMORROW",
                    onClick = { onDateFilterChange("TOMORROW") },
                    activeTheme = GameColorTheme.YELLOW,
                    inactiveTheme = GameColorTheme.DARK,
                    isChecked = selectedDateFilter == "TOMORROW",
                    modifier = Modifier.weight(1f),
                    testTag = "date_filter_tomorrow"
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 4 Dropdown Categories - Clean Frosted Glassmorphism
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // 1. Branch Dropdown
                GameDropdownFilter3D(
                    title = "BRANŞ",
                    selectedValue = filters.branch,
                    items = listOf("Tümü") + branches.map { it.name },
                    onSelected = { onFiltersChange(filters.copy(branch = it)) },
                    onClear = { onFiltersChange(filters.copy(branch = "Tümü")) },
                    modifier = Modifier.weight(1f),
                    tag = "branch_dropdown"
                )

                // 2. Hall Dropdown
                GameDropdownFilter3D(
                    title = "SALON / TESİS",
                    selectedValue = filters.hall,
                    items = listOf("Tümü") + halls.map { it.district },
                    onSelected = { onFiltersChange(filters.copy(hall = it)) },
                    onClear = { onFiltersChange(filters.copy(hall = "Tümü")) },
                    modifier = Modifier.weight(1f),
                    tag = "hall_dropdown"
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // 3. Age Group Dropdown
                GameDropdownFilter3D(
                    title = "YAŞ GRUBU",
                    selectedValue = filters.ageGroup,
                    items = listOf("Tümü", "U10", "U12", "U14", "U16", "U18"),
                    onSelected = { onFiltersChange(filters.copy(ageGroup = it)) },
                    onClear = { onFiltersChange(filters.copy(ageGroup = "Tümü")) },
                    modifier = Modifier.weight(1f),
                    tag = "age_dropdown"
                )

                // 4. Gender / Category Dropdown
                GameDropdownFilter3D(
                    title = "KATEGORİ",
                    selectedValue = filters.gender,
                    items = listOf("Tümü", "Kız", "Erkek", "Karışık"),
                    onSelected = { onFiltersChange(filters.copy(gender = it)) },
                    onClear = { onFiltersChange(filters.copy(gender = "Tümü")) },
                    modifier = Modifier.weight(1f),
                    tag = "gender_dropdown"
                )
            }

            // Quick Active Filter Chips Row with single-tap remove
            AnimatedVisibility(
                visible = activeFilterCount > 0,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Column {
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Aktif:",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = Color(0xFF94A3B8),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        )
                        if (filters.branch != "Tümü") {
                            ActiveFilterBadge(
                                label = filters.branch,
                                onClear = { onFiltersChange(filters.copy(branch = "Tümü")) }
                            )
                        }
                        if (filters.hall != "Tümü") {
                            ActiveFilterBadge(
                                label = filters.hall,
                                onClear = { onFiltersChange(filters.copy(hall = "Tümü")) }
                            )
                        }
                        if (filters.ageGroup != "Tümü") {
                            ActiveFilterBadge(
                                label = filters.ageGroup,
                                onClear = { onFiltersChange(filters.copy(ageGroup = "Tümü")) }
                            )
                        }
                        if (filters.gender != "Tümü") {
                            ActiveFilterBadge(
                                label = filters.gender,
                                onClear = { onFiltersChange(filters.copy(gender = "Tümü")) }
                            )
                        }
                        if (selectedDateFilter != "ALL") {
                            val dateLabel = if (selectedDateFilter == "TODAY") "Bugün" else "Yarın"
                            ActiveFilterBadge(
                                label = dateLabel,
                                onClear = { onDateFilterChange("ALL") }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ActiveFilterBadge(
    label: String,
    onClear: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF1E293B), Color(0xFF0F172A))
                )
            )
            .border(1.dp, CyberGold.copy(alpha = 0.7f), RoundedCornerShape(8.dp))
            .clickable { onClear() }
            .padding(horizontal = 7.dp, vertical = 3.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall.copy(
                    color = CyberGold,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            )
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Temizle",
                tint = Color(0xFFEF4444),
                modifier = Modifier.size(12.dp)
            )
        }
    }
}

/**
 * Game Mode Style 3D Dropdown Button matching Talk Pad / Jelly Candy Cards (Image 1):
 * - Corner sparkles (✦) in top-left and top-right
 * - Center white circular emblem hosting a sport/category icon
 * - Top category label + selected value + right-arrow chevron (›)
 * - 3D tactile extrusion ledge and glossy curved top specular reflection
 * - Top-right golden checkmark badge (✓) when active
 */
@Composable
private fun GameDropdownFilter3D(
    title: String,
    selectedValue: String,
    items: List<String>,
    onSelected: (String) -> Unit,
    onClear: () -> Unit = {},
    modifier: Modifier = Modifier,
    tag: String
) {
    var expanded by remember { mutableStateOf(false) }
    val isFiltered = selectedValue != "Tümü"
    val interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val currentOffset by androidx.compose.animation.core.animateFloatAsState(
        targetValue = if (isPressed) 3f else 0f,
        animationSpec = androidx.compose.animation.core.spring(dampingRatio = 0.55f),
        label = "dropdownPress"
    )

    val height = 72.dp
    val depth = 4.dp
    val shapeRadius = 18.dp

    val iconText = when (title) {
        "BRANŞ" -> if (selectedValue.contains("Voley", ignoreCase = true)) "🏐" else "🏀"
        "SALON / TESİS" -> "🏟️"
        "YAŞ GRUBU" -> "⚡"
        else -> "👥"
    }

    val fillBrush = if (isFiltered) {
        Brush.verticalGradient(
            listOf(
                Color(0xFF1E3252).copy(alpha = 0.88f),
                Color(0xFF101F36).copy(alpha = 0.96f)
            )
        )
    } else {
        Brush.verticalGradient(
            listOf(
                Color(0xFF16253D).copy(alpha = 0.60f),
                Color(0xFF0C1626).copy(alpha = 0.78f)
            )
        )
    }

    val borderBrush = if (isFiltered) {
        Brush.verticalGradient(
            listOf(
                OlimpikYellow.copy(alpha = 0.90f),
                OlimpikYellow.copy(alpha = 0.35f)
            )
        )
    } else {
        Brush.verticalGradient(
            listOf(
                Color.White.copy(alpha = 0.38f),
                Color.White.copy(alpha = 0.10f)
            )
        )
    }

    Box(
        modifier = modifier.testTag(tag)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(height + depth)
                .clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    onClick = { expanded = true }
                )
        ) {
            // Bottom 3D Shadow Layer
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(height)
                    .offset(y = depth)
                    .clip(RoundedCornerShape(shapeRadius))
                    .background(Color(0xFF060D17))
            )

            // Top Interactive Surface - Cam Efekti (Glassmorphism)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(height)
                    .offset(y = currentOffset.dp)
                    .clip(RoundedCornerShape(shapeRadius))
                    .background(fillBrush)
                    .border(
                        width = 1.2.dp,
                        brush = borderBrush,
                        shape = RoundedCornerShape(shapeRadius)
                    )
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                // Top Glossy Specular Reflection
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(0.45f)
                        .align(Alignment.TopCenter)
                        .clip(
                            RoundedCornerShape(
                                topStart = shapeRadius,
                                topEnd = shapeRadius,
                                bottomStart = 6.dp,
                                bottomEnd = 6.dp
                            )
                        )
                        .background(
                            Brush.verticalGradient(
                                listOf(
                                    Color.White.copy(alpha = if (isFiltered) 0.16f else 0.08f),
                                    Color.Transparent
                                )
                            )
                        )
                )

                Row(
                    modifier = Modifier.fillMaxSize(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Center Circular Frosted Glass Disc
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(
                                if (isFiltered) {
                                    Brush.verticalGradient(
                                        listOf(OlimpikYellow.copy(alpha = 0.22f), Color.White.copy(alpha = 0.08f))
                                    )
                                } else {
                                    Brush.verticalGradient(
                                        listOf(Color.White.copy(alpha = 0.12f), Color.White.copy(alpha = 0.04f))
                                    )
                                }
                            )
                            .border(
                                width = 1.dp,
                                brush = Brush.verticalGradient(
                                    listOf(
                                        if (isFiltered) OlimpikYellow.copy(alpha = 0.75f) else Color.White.copy(alpha = 0.30f),
                                        Color.White.copy(alpha = 0.08f)
                                    )
                                ),
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = iconText,
                            fontSize = 20.sp
                        )
                    }

                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = if (isFiltered) OlimpikYellow else Color(0xFF94A3B8),
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp,
                                letterSpacing = 0.5.sp
                            )
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = selectedValue,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = Color.White,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 13.sp
                                ),
                                maxLines = 1,
                                modifier = Modifier.weight(1f, fill = false)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = "›",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Black,
                                    color = if (isFiltered) OlimpikYellow else Color.White.copy(alpha = 0.6f),
                                    fontSize = 15.sp
                                )
                            )
                        }
                    }

                    if (isFiltered) {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFEF4444).copy(alpha = 0.25f))
                                .border(0.8.dp, Color(0xFFEF4444).copy(alpha = 0.6f), CircleShape)
                                .clickable { onClear() },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Sıfırla",
                                tint = Color.White,
                                modifier = Modifier.size(13.dp)
                            )
                        }
                    }
                }

                // Top-right Golden Checkmark (✓) when filtered
                if (isFiltered) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .offset(x = 2.dp, y = (-2).dp)
                            .size(16.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.verticalGradient(
                                    listOf(Color(0xFFFFDF00), Color(0xFFF59E0B))
                                )
                            )
                            .border(1.dp, Color.White, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "✓",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Black,
                                color = Color(0xFF0F172A),
                                fontSize = 9.sp
                            )
                        )
                    }
                }
            }
        }

        // Popup Dropdown Menu
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .background(Color(0xFF0C1628))
                .border(1.5.dp, CyberGold, RoundedCornerShape(14.dp))
        ) {
            items.forEach { item ->
                val isItemSelected = item == selectedValue
                DropdownMenuItem(
                    text = {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = item,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = if (isItemSelected) FontWeight.Black else FontWeight.Medium,
                                    color = if (isItemSelected) CyberGold else Color.White,
                                    fontSize = 14.sp
                                )
                            )
                            if (isItemSelected) {
                                Spacer(modifier = Modifier.width(8.dp))
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = CyberGold,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    },
                    onClick = {
                        onSelected(item)
                        expanded = false
                    }
                )
            }
        }
    }
}
