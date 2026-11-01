package com.example.ui

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.EventNote
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.TrainingSession
import com.example.ui.components.ClubTopBar
import com.example.ui.components.VibrantBottomBar
import com.example.ui.dialogs.AddBranchDialog
import com.example.ui.dialogs.AddEditSessionDialog
import com.example.ui.dialogs.AddHallDialog
import com.example.ui.dialogs.CancelSessionDialog
import com.example.ui.dialogs.FilterWizardDialog
import com.example.ui.dialogs.RescheduleSessionDialog
import com.example.ui.screens.AnalyticsScreen
import com.example.ui.screens.HallsMapScreen
import com.example.ui.screens.ParentScheduleScreen
import com.example.ui.screens.WeeklyStarsScreen
import com.example.ui.theme.NavyCard
import com.example.ui.theme.NavyDark
import com.example.ui.theme.OlimpikYellow
import com.example.viewmodel.ClubViewModel
import com.example.viewmodel.UserRole
import kotlinx.coroutines.launch

enum class ScreenTab {
    SCHEDULE,
    HALLS,
    STARS,
    ANALYTICS
}

@Composable
fun MainScreen(viewModel: ClubViewModel) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    val currentRole by viewModel.activeRole.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()
    val isAuthLoading by viewModel.isAuthLoading.collectAsState()
    val themeBranch by viewModel.themeBranch.collectAsState()
    val isDarkMode by viewModel.isDarkMode.collectAsState()
    val uiMessage by viewModel.uiMessage.collectAsState()

    val filteredSessions by viewModel.filteredSessions.collectAsState()
    val halls by viewModel.halls.collectAsState()
    val branches by viewModel.branches.collectAsState()
    val stars by viewModel.weeklyStars.collectAsState()
    val announcements by viewModel.announcements.collectAsState()
    val filterPreferences by viewModel.filterPreferences.collectAsState()
    val selectedDateFilter by viewModel.selectedDateFilter.collectAsState()

    var currentTab by remember { mutableStateOf(ScreenTab.SCHEDULE) }

    // Dialog States
    var showAddSessionDialog by remember { mutableStateOf(false) }
    var sessionToCancel by remember { mutableStateOf<TrainingSession?>(null) }
    var sessionToReschedule by remember { mutableStateOf<TrainingSession?>(null) }
    var showAddBranchDialog by remember { mutableStateOf(false) }
    var showAddHallDialog by remember { mutableStateOf(false) }
    var showLoginPromptDialog by remember { mutableStateOf(false) }
    var showWizardDialog by remember { mutableStateOf(false) }

    // Handle back button to return to SCHEDULE tab
    BackHandler(enabled = currentTab != ScreenTab.SCHEDULE) {
        currentTab = ScreenTab.SCHEDULE
    }

    // Show Snackbars for UI feedback
    LaunchedEffect(uiMessage) {
        uiMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearMessage()
        }
    }

    val isCoachMode = currentRole == UserRole.COACH

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            ClubTopBar(
                currentRole = currentRole,
                themeBranch = themeBranch,
                isDarkMode = isDarkMode,
                onRoleToggle = {
                    if (currentRole == UserRole.PARENT) {
                        if (currentUser == null) {
                            showLoginPromptDialog = true
                        } else {
                            viewModel.setRole(UserRole.COACH)
                        }
                    } else {
                        viewModel.setRole(UserRole.PARENT)
                    }
                },
                onThemeToggle = {
                    val nextTheme = when (themeBranch) {
                        "ALL" -> "BASKETBALL"
                        "BASKETBALL" -> "VOLLEYBALL"
                        else -> "ALL"
                    }
                    viewModel.setThemeBranch(nextTheme)
                },
                onDarkModeToggle = { viewModel.toggleDarkMode() },
                userEmail = currentUser?.email,
                onAuthClick = {
                    if (currentUser == null) {
                        viewModel.signInWithGoogle(context)
                    } else {
                        viewModel.signOut(context)
                    }
                }
            )
        },
        bottomBar = {
            VibrantBottomBar(
                selectedIndex = when (currentTab) {
                    ScreenTab.SCHEDULE -> 0
                    ScreenTab.HALLS -> 1
                    ScreenTab.STARS -> 2
                    ScreenTab.ANALYTICS -> 3
                },
                onTabSelected = { index ->
                    currentTab = when (index) {
                        0 -> ScreenTab.SCHEDULE
                        1 -> ScreenTab.HALLS
                        2 -> ScreenTab.STARS
                        else -> ScreenTab.ANALYTICS
                    }
                },
                onWizardClick = { showWizardDialog = true }
            )
        },
        floatingActionButton = {
            if (isCoachMode && currentTab == ScreenTab.SCHEDULE) {
                Column(horizontalAlignment = Alignment.End) {
                    // Quick button for dynamic branch addition (Image 2 Purple pill)
                    Box(
                        modifier = Modifier
                            .padding(bottom = 8.dp)
                            .shadow(elevation = 6.dp, shape = RoundedCornerShape(24.dp))
                            .clip(RoundedCornerShape(24.dp))
                            .background(
                                androidx.compose.ui.graphics.Brush.horizontalGradient(
                                    listOf(com.example.ui.theme.ZomoPurplePrimary, com.example.ui.theme.ZomoPurpleDark)
                                )
                            )
                            .border(1.dp, Color.White.copy(alpha = 0.6f), RoundedCornerShape(24.dp))
                            .clickable { showAddBranchDialog = true }
                            .padding(horizontal = 14.dp, vertical = 9.dp)
                            .testTag("quick_add_branch_button")
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Add, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(5.dp))
                            Text("BRANŞ EKLE", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.ExtraBold)
                        }
                    }

                    // Main FAB for adding training session (Image 2 Mint/Cyan "Start Now" / "Create" 3D button)
                    Box(
                        modifier = Modifier
                            .shadow(
                                elevation = 8.dp,
                                shape = RoundedCornerShape(30.dp),
                                ambientColor = Color(0x4406B6D4),
                                spotColor = Color(0x4410B981)
                            )
                            .clip(RoundedCornerShape(30.dp))
                            .background(
                                androidx.compose.ui.graphics.Brush.horizontalGradient(
                                    listOf(com.example.ui.theme.ZomoMintLight, com.example.ui.theme.ZomoCyan)
                                )
                            )
                            .border(1.2.dp, Color.White.copy(alpha = 0.7f), RoundedCornerShape(30.dp))
                            .clickable { showAddSessionDialog = true }
                            .padding(horizontal = 18.dp, vertical = 12.dp)
                            .testTag("add_session_fab")
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Add, contentDescription = "İdman Ekle", tint = Color.White, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("YENİ İDMAN", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.ExtraBold)
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentTab) {
                ScreenTab.SCHEDULE -> {
                    ParentScheduleScreen(
                        sessions = filteredSessions,
                        halls = halls,
                        branches = branches,
                        announcements = announcements,
                        filters = filterPreferences,
                        selectedDateFilter = selectedDateFilter,
                        onDateFilterChange = { viewModel.setDateFilter(it) },
                        onFiltersChange = { viewModel.updateFilters(it) },
                        onResetFilters = { viewModel.resetFilters() },
                        isCoachMode = isCoachMode,
                        onCancelSession = { sessionToCancel = it },
                        onRescheduleSession = { sessionToReschedule = it },
                        onActivateSession = { session ->
                            viewModel.updateSessionStatus(session.id, "Aktif", "İdman tekrar aktif edildi.")
                        }
                    )
                }

                ScreenTab.HALLS -> {
                    HallsMapScreen(
                        halls = halls,
                        isCoachMode = isCoachMode,
                        onAddHallClick = { showAddHallDialog = true }
                    )
                }

                ScreenTab.STARS -> {
                    WeeklyStarsScreen(
                        stars = stars,
                        isCoachMode = isCoachMode,
                        onVote = { playerId -> viewModel.voteForStarPlayer(playerId) },
                        onNominate = { name, cat, branch, ach ->
                            viewModel.nominateStarPlayer(name, cat, branch, ach) {}
                        }
                    )
                }

                ScreenTab.ANALYTICS -> {
                    AnalyticsScreen(
                        sessions = filteredSessions,
                        halls = halls,
                        weeklyStars = stars
                    )
                }
            }
        }
    }

    // Dialog: Add Session
    if (showAddSessionDialog) {
        AddEditSessionDialog(
            halls = halls,
            branches = branches,
            onDismiss = { showAddSessionDialog = false },
            onSave = { newSession ->
                viewModel.createSession(newSession) { success ->
                    if (success) showAddSessionDialog = false
                }
            }
        )
    }

    // Dialog: Cancel Session
    sessionToCancel?.let { session ->
        CancelSessionDialog(
            session = session,
            onDismiss = { sessionToCancel = null },
            onConfirm = { reason ->
                viewModel.updateSessionStatus(session.id, "İptal", reason)
                sessionToCancel = null
            }
        )
    }

    // Dialog: Reschedule Session
    sessionToReschedule?.let { session ->
        RescheduleSessionDialog(
            session = session,
            onDismiss = { sessionToReschedule = null },
            onConfirm = { newDate, newStart, newEnd, note ->
                viewModel.updateSessionStatus(
                    sessionId = session.id,
                    newStatus = "Değişti",
                    note = note,
                    newDate = newDate,
                    newStart = newStart,
                    newEnd = newEnd
                ) { success ->
                    if (success) sessionToReschedule = null
                }
            }
        )
    }

    // Dialog: Add Custom Branch
    if (showAddBranchDialog) {
        AddBranchDialog(
            onDismiss = { showAddBranchDialog = false },
            onConfirm = { name, code, desc ->
                viewModel.addCustomBranch(name, code, desc)
                showAddBranchDialog = false
            }
        )
    }

    // Dialog: Add Custom Hall
    if (showAddHallDialog) {
        AddHallDialog(
            onDismiss = { showAddHallDialog = false },
            onConfirm = { name, district, address, lat, lng, phone ->
                viewModel.addCustomHall(name, district, address, lat, lng, phone)
                showAddHallDialog = false
            }
        )
    }

    // Dialog: Coach Login Prompt with Google Sign-In button
    if (showLoginPromptDialog) {
        AlertDialog(
            onDismissRequest = { showLoginPromptDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Lock, contentDescription = null, tint = OlimpikYellow)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Antrenör Girişi Gerekli", fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column {
                    Text(
                        text = "İdman planlamak, saatleri değiştirmek, iptal duyurusu yayınlamak ve yıldız oyuncu adayları eklemek için antrenör olarak Google ile giriş yapmanız gerekmektedir.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Veliler herhangi bir giriş yapmadan tüm canlı programı ve salonları izleyebilir.",
                        style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.signInWithGoogle(context)
                        showLoginPromptDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = OlimpikYellow),
                    modifier = Modifier.testTag("google_signin_button")
                ) {
                    if (isAuthLoading) {
                        CircularProgressIndicator(modifier = Modifier.size(18.dp), color = Color(0xFF07142A), strokeWidth = 2.dp)
                    } else {
                        Text("Google ile Giriş Yap", color = Color(0xFF07142A), fontWeight = FontWeight.Bold)
                    }
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showLoginPromptDialog = false }) {
                    Text("Veli Olarak Devam Et")
                }
            }
        )
    }

    // Gamified 3D Character Filter Wizard Dialog
    if (showWizardDialog) {
        FilterWizardDialog(
            currentFilters = filterPreferences,
            halls = halls,
            branches = branches,
            onDismiss = { showWizardDialog = false },
            onApplyFilters = { newPrefs ->
                viewModel.updateFilters(newPrefs)
                showWizardDialog = false
            }
        )
    }
}
