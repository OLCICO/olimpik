package com.example.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.ServerTimestamp

@IgnoreExtraProperties
data class TrainingSession(
    @DocumentId val id: String = "",
    val sessionId: String = "",
    val title: String = "",
    val branch: String = "Basketbol", // Basketbol, Voleybol
    val hall: String = "Pendik Spor Kompleksi", // Pendik, Şekerpınar, Çayırova, Gebze, Mutlukent
    val ageGroup: String = "U14", // U10, U12, U14, U16, U18, Karışık
    val gender: String = "Erkek", // Kız, Erkek, Karışık
    val dayOfWeek: String = "Pazartesi",
    val date: String = "", // YYYY-MM-DD
    val startTime: String = "17:00",
    val endTime: String = "18:30",
    val coachId: String = "",
    val coachName: String = "",
    val status: String = "Aktif", // Aktif, İptal, Değişti
    val statusNote: String = "",
    val isMatch: Boolean = false,
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@IgnoreExtraProperties
data class WeeklyPlayer(
    @DocumentId val id: String = "",
    val playerId: String = "",
    val name: String = "",
    val category: String = "U14 Basketbol Erkek",
    val branch: String = "Basketbol",
    val achievement: String = "",
    val week: String = "",
    val votesCount: Long = 0L,
    val nominatedBy: String = "",
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@IgnoreExtraProperties
data class Announcement(
    @DocumentId val id: String = "",
    val announcementId: String = "",
    val title: String = "",
    val message: String = "",
    val type: String = "GENERAL", // CANCELLED, RESCHEDULED, GENERAL, MATCH
    val relatedSessionId: String = "",
    val authorId: String = "",
    val authorName: String = "",
    @ServerTimestamp val createdAt: Timestamp? = null
)

@IgnoreExtraProperties
data class CoachProfile(
    @DocumentId val id: String = "",
    val userId: String = "",
    val name: String = "",
    val email: String = "",
    val role: String = "Baş Antrenör",
    val branch: String = "Basketbol",
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

data class HallInfo(
    val id: String,
    val name: String,
    val district: String,
    val address: String,
    val latitude: Double,
    val longitude: Double,
    val phone: String,
    val features: List<String>
)

data class BranchInfo(
    val id: String,
    val name: String,
    val code: String,
    val iconName: String,
    val description: String
)

data class ParentFilterPreferences(
    val branch: String = "Tümü", // Tümü, Basketbol, Voleybol
    val hall: String = "Tümü",   // Tümü, Pendik, Şekerpınar, Çayırova, Gebze, Mutlukent
    val ageGroup: String = "Tümü", // Tümü, U10, U12, U14, U16, U18
    val gender: String = "Tümü"  // Tümü, Kız, Erkek, Karışık
)
