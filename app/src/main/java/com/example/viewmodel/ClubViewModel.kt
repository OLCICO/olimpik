package com.example.viewmodel

import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.R
import com.example.data.ClubRepository
import com.example.model.Announcement
import com.example.model.BranchInfo
import com.example.model.HallInfo
import com.example.model.ParentFilterPreferences
import com.example.model.TrainingSession
import com.example.model.WeeklyPlayer
import com.example.util.PreferencesManager
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential.Companion.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

enum class UserRole {
    PARENT, // Veli (no mandatory login)
    COACH   // Antrenör (requires login for adding/editing)
}

class ClubViewModel(
    private val repository: ClubRepository,
    private val preferencesManager: PreferencesManager
) : ViewModel() {

    private val auth: FirebaseAuth? = try {
        Firebase.auth
    } catch (e: Exception) {
        null
    }

    private val _currentUser = MutableStateFlow<FirebaseUser?>(auth?.currentUser)
    val currentUser: StateFlow<FirebaseUser?> = _currentUser.asStateFlow()

    private val _activeRole = MutableStateFlow(UserRole.PARENT)
    val activeRole: StateFlow<UserRole> = _activeRole.asStateFlow()

    val filterPreferences: StateFlow<ParentFilterPreferences> = preferencesManager.filterState
    val themeBranch: StateFlow<String> = preferencesManager.themeBranch
    val isDarkMode: StateFlow<Boolean> = preferencesManager.isDarkMode

    val halls: StateFlow<List<HallInfo>> = repository.hallsState
    val branches: StateFlow<List<BranchInfo>> = repository.branchesState
    val weeklyStars: StateFlow<List<WeeklyPlayer>> = repository.localStars
    val announcements: StateFlow<List<Announcement>> = repository.localAnnouncements
    val allSessions: StateFlow<List<TrainingSession>> = repository.localSessions

    private val _selectedDateFilter = MutableStateFlow("ALL") // "ALL", "TODAY", "TOMORROW"
    val selectedDateFilter: StateFlow<String> = _selectedDateFilter.asStateFlow()

    private val _uiMessage = MutableStateFlow<String?>(null)
    val uiMessage: StateFlow<String?> = _uiMessage.asStateFlow()

    private val _isAuthLoading = MutableStateFlow(false)
    val isAuthLoading: StateFlow<Boolean> = _isAuthLoading.asStateFlow()

    // Filtered Sessions Stream
    val filteredSessions: StateFlow<List<TrainingSession>> = combine(
        repository.localSessions,
        filterPreferences,
        _selectedDateFilter
    ) { sessions, filters, dateFilter ->
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val todayStr = sdf.format(Date())
        val tomorrowCal = java.util.Calendar.getInstance().apply { add(java.util.Calendar.DAY_OF_YEAR, 1) }
        val tomorrowStr = sdf.format(tomorrowCal.time)

        val dayFormat = SimpleDateFormat("EEEE", Locale("tr", "TR"))
        val todayDayName = dayFormat.format(Date())
        val tomorrowDayName = dayFormat.format(tomorrowCal.time)

        sessions.filter { session ->
            val branchMatch = filters.branch == "Tümü" || session.branch.equals(filters.branch, ignoreCase = true)
            val hallMatch = filters.hall == "Tümü" ||
                session.hall.contains(filters.hall, ignoreCase = true) ||
                filters.hall.contains(session.hall, ignoreCase = true)
            val ageMatch = filters.ageGroup == "Tümü" || session.ageGroup.equals(filters.ageGroup, ignoreCase = true) ||
                session.ageGroup.equals("Karışık", ignoreCase = true)
            val genderMatch = filters.gender == "Tümü" || session.gender.equals(filters.gender, ignoreCase = true) ||
                session.gender.equals("Karışık", ignoreCase = true)

            val dateMatch = when (dateFilter) {
                "TODAY" -> session.date == todayStr || session.dayOfWeek.equals(todayDayName, ignoreCase = true)
                "TOMORROW" -> session.date == tomorrowStr || session.dayOfWeek.equals(tomorrowDayName, ignoreCase = true)
                else -> true
            }

            branchMatch && hallMatch && ageMatch && genderMatch && dateMatch
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000L),
        initialValue = emptyList()
    )

    init {
        // Observe auth state changes
        auth?.addAuthStateListener { firebaseAuth ->
            _currentUser.value = firebaseAuth.currentUser
            if (firebaseAuth.currentUser != null) {
                // If logged in, observe Firestore collection
                viewModelScope.launch {
                    repository.observeCloudSessions().collect { /* synced in repository */ }
                }
            }
        }
    }

    fun setRole(role: UserRole) {
        _activeRole.value = role
    }

    fun updateFilters(newFilters: ParentFilterPreferences) {
        preferencesManager.saveFilters(newFilters)
    }

    fun resetFilters() {
        preferencesManager.resetFilters()
        _selectedDateFilter.value = "ALL"
        _uiMessage.value = "Filtreler başarıyla sıfırlandı."
    }

    fun setDateFilter(filter: String) {
        _selectedDateFilter.value = filter
    }

    fun setThemeBranch(branch: String) {
        preferencesManager.setThemeBranch(branch)
    }

    fun toggleDarkMode() {
        preferencesManager.setDarkMode(!isDarkMode.value)
    }

    fun clearMessage() {
        _uiMessage.value = null
    }

    // Coach actions
    fun createSession(session: TrainingSession, onComplete: (Boolean) -> Unit) {
        viewModelScope.launch {
            val result = repository.createSession(session)
            if (result.isSuccess) {
                _uiMessage.value = "Yeni idman başarıyla takvime eklendi."
                onComplete(true)
            } else {
                _uiMessage.value = result.exceptionOrNull()?.message ?: "İdman eklenemedi."
                onComplete(false)
            }
        }
    }

    fun updateSessionStatus(
        sessionId: String,
        newStatus: String,
        note: String,
        newDate: String? = null,
        newStart: String? = null,
        newEnd: String? = null,
        onComplete: (Boolean) -> Unit = {}
    ) {
        viewModelScope.launch {
            val result = repository.updateSession(sessionId, newStatus, note, newDate, newStart, newEnd)
            if (result.isSuccess) {
                val statusText = when (newStatus) {
                    "İptal" -> "İdman iptal edildi ve velilere bildirim gönderildi."
                    "Değişti" -> "İdman saati güncellendi ve bildirim gönderildi."
                    else -> "İdman durumu güncellendi."
                }
                _uiMessage.value = statusText
                onComplete(true)
            } else {
                _uiMessage.value = result.exceptionOrNull()?.message ?: "Güncelleme yapılamadı."
                onComplete(false)
            }
        }
    }

    fun voteForStarPlayer(playerId: String) {
        viewModelScope.launch {
            val result = repository.voteForPlayer(playerId)
            if (result.isSuccess) {
                _uiMessage.value = "Oyunuz kaydedildi! Desteğiniz için teşekkürler."
            } else {
                _uiMessage.value = "Oy verilemedi."
            }
        }
    }

    fun nominateStarPlayer(name: String, category: String, branch: String, achievement: String, onComplete: () -> Unit) {
        viewModelScope.launch {
            val result = repository.nominatePlayer(name, category, branch, achievement)
            if (result.isSuccess) {
                _uiMessage.value = "$name haftanın oyuncusu adaylarına eklendi."
                onComplete()
            } else {
                _uiMessage.value = "Oyuncu eklenemedi."
            }
        }
    }

    fun addCustomBranch(name: String, code: String, desc: String) {
        repository.addBranch(name, code, desc)
        _uiMessage.value = "$name branşı başarıyla eklendi."
    }

    fun addCustomHall(name: String, district: String, address: String, lat: Double, lng: Double, phone: String) {
        repository.addHall(name, district, address, lat, lng, phone)
        _uiMessage.value = "$name salonu başarıyla eklendi."
    }

    // Google Sign-In with Credential Manager (Mandatory pattern)
    fun signInWithGoogle(context: Context) {
        _isAuthLoading.value = true
        val credentialManager = CredentialManager.create(context)
        val clientId = try {
            context.getString(R.string.default_web_client_id)
        } catch (e: Exception) {
            _isAuthLoading.value = false
            _uiMessage.value = "Google Giriş Hatası: Web Client ID bulunamadı."
            return
        }

        val signInOption = GetSignInWithGoogleOption.Builder(serverClientId = clientId).build()
        val request = GetCredentialRequest.Builder().addCredentialOption(signInOption).build()

        viewModelScope.launch {
            try {
                val currentAuth = auth
                if (currentAuth == null) {
                    _uiMessage.value = "Kimlik doğrulama servisi başlatılamadı."
                    _isAuthLoading.value = false
                    return@launch
                }
                val result = credentialManager.getCredential(context as Activity, request)
                val credential = result.credential
                if (credential is CustomCredential && credential.type == TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                    val googleIdToken = GoogleIdTokenCredential.createFrom(credential.data).idToken
                    val authCredential = GoogleAuthProvider.getCredential(googleIdToken, null)
                    currentAuth.signInWithCredential(authCredential).await()
                    _currentUser.value = currentAuth.currentUser
                    _activeRole.value = UserRole.COACH
                    _uiMessage.value = "Antrenör girişi başarılı: ${currentAuth.currentUser?.displayName}"
                } else {
                    _uiMessage.value = "Beklenmeyen kimlik doğrulama formatı."
                }
            } catch (e: GetCredentialCancellationException) {
                Log.w("Auth", "Google Sign-In cancelled: ${e.message}")
            } catch (e: Exception) {
                Log.e("Auth", "Google Sign-In failed", e)
                _uiMessage.value = "Giriş başarısız: ${e.localizedMessage ?: "Hata"}"
            } finally {
                _isAuthLoading.value = false
            }
        }
    }

    fun signOut(context: Context) {
        auth?.signOut()
        _currentUser.value = null
        _activeRole.value = UserRole.PARENT
        viewModelScope.launch {
            try {
                CredentialManager.create(context).clearCredentialState(ClearCredentialStateRequest())
            } catch (e: Exception) {
                Log.e("Auth", "Clear credentials failed", e)
            }
            _uiMessage.value = "Çıkış yapıldı. Veli moduna geçildi."
        }
    }
}