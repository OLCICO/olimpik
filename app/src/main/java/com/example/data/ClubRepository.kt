package com.example.data

import android.content.Context
import android.util.Log
import com.example.R
import com.example.model.Announcement
import com.example.model.BranchInfo
import com.example.model.CoachProfile
import com.example.model.HallInfo
import com.example.model.TrainingSession
import com.example.model.WeeklyPlayer
import com.example.util.OperationType
import com.example.util.handleFirestoreError
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class ClubRepository(
    private val context: Context,
    private val firestore: FirebaseFirestore?
) {
    // Secondary constructor resolving database ID from resources safely
    constructor(context: Context) : this(
        context,
        try {
            if (com.google.firebase.FirebaseApp.getApps(context).isEmpty()) {
                com.google.firebase.FirebaseApp.initializeApp(context)
            }
            val dbId = try {
                context.getString(R.string.firestore_database_id)
            } catch (e: Exception) {
                null
            }
            if (!dbId.isNullOrBlank()) {
                FirebaseFirestore.getInstance(dbId)
            } else {
                FirebaseFirestore.getInstance()
            }
        } catch (e: Exception) {
            Log.w("ClubRepo", "Firestore not available: ${e.message}")
            null
        }
    )

    private val auth: com.google.firebase.auth.FirebaseAuth?
        get() = try {
            if (com.google.firebase.FirebaseApp.getApps(context).isEmpty()) {
                com.google.firebase.FirebaseApp.initializeApp(context)
            }
            Firebase.auth
        } catch (e: Exception) {
            null
        }

    // 5 Official Club Facilities in Pendik & Kocaeli region
    val defaultHalls = listOf(
        HallInfo(
            id = "hall_pendik",
            name = "Pendik Spor Kompleksi",
            district = "Pendik",
            address = "Kurtköy Mah. Ankara Cad. No: 120, Pendik / İstanbul",
            latitude = 40.8986,
            longitude = 29.2882,
            phone = "0216 585 00 00",
            features = listOf("Parke Zemin", "FIBA Standart Pota", "500 Kişilik Tribün", "Geniş Otopark")
        ),
        HallInfo(
            id = "hall_sekerpinar",
            name = "Şekerpınar Kapalı Spor Salonu",
            district = "Şekerpınar",
            address = "Şekerpınar Mah. Fevzi Çakmak Cad. Çayırova / Kocaeli",
            latitude = 40.8491,
            longitude = 29.3887,
            phone = "0262 658 10 20",
            features = listOf("Voleybol Taraflex Zemin", "Basketbol Sahası", "Soyunma Odaları", "Kafeterya")
        ),
        HallInfo(
            id = "hall_cayirova",
            name = "Çayırova Kapalı Spor Salonu",
            district = "Çayırova",
            address = "Yenimahalle Menderes Cad. No: 14, Çayırova / Kocaeli",
            latitude = 40.8252,
            longitude = 29.3725,
            phone = "0262 743 40 50",
            features = listOf("Modern Parke Zemin", "Kondisyon Merkezi", "Sağlık Odası")
        ),
        HallInfo(
            id = "hall_gebze",
            name = "Gebze Arena Spor Salonu",
            district = "Gebze",
            address = "Gaziler Mah. İssingölü Cad. Gebze / Kocaeli",
            latitude = 40.8027,
            longitude = 29.4307,
            phone = "0262 642 70 80",
            features = listOf("1500 Kişilik Ana Tribün", "Elektronik Skorboard", "Canlı Yayın Altyapısı")
        ),
        HallInfo(
            id = "hall_mutlukent",
            name = "Mutlukent Spor Tesisleri",
            district = "Mutlukent",
            address = "Mutlukent Mah. 2040. Sok. No: 8, Gebze / Kocaeli",
            latitude = 40.8175,
            longitude = 29.4520,
            phone = "0262 646 90 90",
            features = listOf("Altyapı Antrenman Sahası", "Özel Şut Makineleri", "Rejenerasyon Alanı")
        )
    )

    private val _hallsState = MutableStateFlow(defaultHalls)
    val hallsState = _hallsState.asStateFlow()

    // Dynamic Branches
    val defaultBranches = listOf(
        BranchInfo(
            id = "branch_basketball",
            name = "Basketbol",
            code = "BASKETBALL",
            iconName = "sports_basketball",
            description = "Temel eğitimden A takıma lisanslı basketbol ligleri ve antrenmanları"
        ),
        BranchInfo(
            id = "branch_volleyball",
            name = "Voleybol",
            code = "VOLLEYBALL",
            iconName = "sports_volleyball",
            description = "Midi, küçük, yıldız ve genç kız/erkek voleybol takımı çalışmaları"
        )
    )

    private val _branchesState = MutableStateFlow(defaultBranches)
    val branchesState = _branchesState.asStateFlow()

    // Dynamic default training sessions aligned with current date
    private fun createInitialSessions(): List<TrainingSession> {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val dayFormat = SimpleDateFormat("EEEE", Locale("tr", "TR"))

        fun getDateOffset(days: Int): Pair<String, String> {
            val cal = java.util.Calendar.getInstance()
            cal.add(java.util.Calendar.DAY_OF_YEAR, days)
            val dStr = sdf.format(cal.time)
            val rawDay = dayFormat.format(cal.time)
            val dayStr = rawDay.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale("tr", "TR")) else it.toString() }
            return Pair(dStr, dayStr)
        }

        val (todayDate, todayDay) = getDateOffset(0)
        val (tomorrowDate, tomorrowDay) = getDateOffset(1)
        val (d2Date, d2Day) = getDateOffset(2)
        val (d3Date, d3Day) = getDateOffset(3)
        val (d4Date, d4Day) = getDateOffset(4)
        val (d5Date, d5Day) = getDateOffset(5)

        val demoSessions = listOf(
            TrainingSession(
                id = "sess_101",
                sessionId = "sess_101",
                title = "U14 Erkek Basketbol Takım İdmanı",
                branch = "Basketbol",
                hall = "Pendik Spor Kompleksi",
                ageGroup = "U14",
                gender = "Erkek",
                dayOfWeek = todayDay,
                date = todayDate,
                startTime = "17:00",
                endTime = "18:30",
                coachName = "Ahmet Yılmaz (Koç)",
                coachId = "coach_default",
                status = "Aktif",
                statusNote = "Tam kadro katılım zorunludur. Hücum setleri çalışılacak.",
                isMatch = false
            ),
            TrainingSession(
                id = "sess_102",
                sessionId = "sess_102",
                title = "U16 Kız Voleybol Servis & Manşet",
                branch = "Voleybol",
                hall = "Şekerpınar Kapalı Spor Salonu",
                ageGroup = "U16",
                gender = "Kız",
                dayOfWeek = todayDay,
                date = todayDate,
                startTime = "18:30",
                endTime = "20:00",
                coachName = "Selin Kaya (Koç)",
                coachId = "coach_default",
                status = "Aktif",
                statusNote = "Haftalık maç hazırlık taktiği.",
                isMatch = false
            ),
            TrainingSession(
                id = "sess_103",
                sessionId = "sess_103",
                title = "U10 Karışık Minikler Basketbol",
                branch = "Basketbol",
                hall = "Çayırova Kapalı Spor Salonu",
                ageGroup = "U10",
                gender = "Karışık",
                dayOfWeek = tomorrowDay,
                date = tomorrowDate,
                startTime = "16:00",
                endTime = "17:15",
                coachName = "Emre Can (Koç)",
                coachId = "coach_default",
                status = "Aktif",
                statusNote = "Top hakimiyeti ve temel pas egzersizleri.",
                isMatch = false
            ),
            TrainingSession(
                id = "sess_104",
                sessionId = "sess_104",
                title = "U18 Genç Erkek Lig Maçı",
                branch = "Basketbol",
                hall = "Gebze Arena Spor Salonu",
                ageGroup = "U18",
                gender = "Erkek",
                dayOfWeek = tomorrowDay,
                date = tomorrowDate,
                startTime = "19:00",
                endTime = "20:45",
                coachName = "Ahmet Yılmaz (Koç)",
                coachId = "coach_default",
                status = "Aktif",
                statusNote = "Olimpik Spor vs Kocaeli Basketbol Kulübü",
                isMatch = true
            ),
            TrainingSession(
                id = "sess_105",
                sessionId = "sess_105",
                title = "U12 Kız Voleybol Antrenmanı",
                branch = "Voleybol",
                hall = "Mutlukent Spor Tesisleri",
                ageGroup = "U12",
                gender = "Kız",
                dayOfWeek = d2Day,
                date = d2Date,
                startTime = "17:30",
                endTime = "19:00",
                coachName = "Merve Aydın (Koç)",
                coachId = "coach_default",
                status = "Değişti",
                statusNote = "Salon zemin bakımı nedeniyle saat 17:30'a alındı.",
                isMatch = false
            ),
            TrainingSession(
                id = "sess_106",
                sessionId = "sess_106",
                title = "U16 Erkek Basketbol Gelişim",
                branch = "Basketbol",
                hall = "Pendik Spor Kompleksi",
                ageGroup = "U16",
                gender = "Erkek",
                dayOfWeek = d3Day,
                date = d3Date,
                startTime = "18:00",
                endTime = "19:30",
                coachName = "Ahmet Yılmaz (Koç)",
                coachId = "coach_default",
                status = "İptal",
                statusNote = "Koç semineri dolayısıyla bu seans iptal edilmiştir. Telafi Cumartesi yapılacak.",
                isMatch = false
            ),
            TrainingSession(
                id = "sess_107",
                sessionId = "sess_107",
                title = "U14 Kız Basketbol Taktik Antrenmanı",
                branch = "Basketbol",
                hall = "Şekerpınar Kapalı Spor Salonu",
                ageGroup = "U14",
                gender = "Kız",
                dayOfWeek = d4Day,
                date = d4Date,
                startTime = "16:30",
                endTime = "18:00",
                coachName = "Ahmet Yılmaz (Koç)",
                coachId = "coach_default",
                status = "Aktif",
                statusNote = "Savunma ribaundu ve hızlı hücum geçişleri.",
                isMatch = false
            ),
            TrainingSession(
                id = "sess_108",
                sessionId = "sess_108",
                title = "U18 Genç Kız Voleybol Bölge Karşılaşması",
                branch = "Voleybol",
                hall = "Gebze Arena Spor Salonu",
                ageGroup = "U18",
                gender = "Kız",
                dayOfWeek = d5Day,
                date = d5Date,
                startTime = "18:00",
                endTime = "20:00",
                coachName = "Selin Kaya (Koç)",
                coachId = "coach_default",
                status = "Aktif",
                statusNote = "Şampiyonluk grup aşaması mücadelesi.",
                isMatch = true
            )
        )

        return (demoSessions + createWeekendSessions())
            .sortedWith(compareBy({ it.date }, { it.startTime }, { it.hall }))
    }

    /**
     * Test amaçlı hafta sonu programı: önümüzdeki 2 hafta sonu (Cumartesi + Pazar),
     * her salonda 10:00-20:00 arası saatlik antrenmanlar.
     *  10-14 Basketbol, 14-18 Voleybol, 18-20 Altyapı takım antrenmanları.
     * Saatleri değiştirmek için aşağıdaki "slots" listesini düzenlemek yeterli.
     */
    private fun createWeekendSessions(): List<TrainingSession> {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val bCoach = "Ahmet Yılmaz (Koç)"
        val vCoach = "Selin Kaya (Koç)"

        class Slot(val branch: String, val ages: List<String>, val start: String, val end: String, val coach: String)

        val slots = listOf(
            // 10:00 - 14:00 Basketbol
            Slot("Basketbol", listOf("U10", "U12"), "10:00", "11:00", bCoach),
            Slot("Basketbol", listOf("U14"), "11:00", "12:00", bCoach),
            Slot("Basketbol", listOf("U16"), "12:00", "13:00", bCoach),
            Slot("Basketbol", listOf("U18"), "13:00", "14:00", bCoach),
            // 14:00 - 18:00 Voleybol
            Slot("Voleybol", listOf("U10", "U12"), "14:00", "15:00", vCoach),
            Slot("Voleybol", listOf("U14"), "15:00", "16:00", vCoach),
            Slot("Voleybol", listOf("U16"), "16:00", "17:00", vCoach),
            Slot("Voleybol", listOf("U18"), "17:00", "18:00", vCoach),
            // 18:00 - 20:00 Altyapı takım antrenmanları (tüm yaş grupları)
            Slot("Basketbol", listOf("Karışık"), "18:00", "19:00", bCoach),
            Slot("Voleybol", listOf("Karışık"), "19:00", "20:00", vCoach)
        )

        val result = mutableListOf<TrainingSession>()
        for (offset in 0..13) {
            val cal = java.util.Calendar.getInstance()
            cal.add(java.util.Calendar.DAY_OF_YEAR, offset)
            val dayName = when (cal.get(java.util.Calendar.DAY_OF_WEEK)) {
                java.util.Calendar.SATURDAY -> "Cumartesi"
                java.util.Calendar.SUNDAY -> "Pazar"
                else -> continue
            }
            val date = sdf.format(cal.time)
            for (hall in defaultHalls) {
                for (slot in slots) {
                    for (age in slot.ages) {
                        val ageKey = if (age == "Karışık") "alt" else age
                        val sid = "wk_${date}_${hall.id}_${slot.branch.take(3).lowercase()}_$ageKey"
                        result.add(
                            TrainingSession(
                                id = sid,
                                sessionId = sid,
                                title = if (age == "Karışık") "${slot.branch} Altyapı Takım Antrenmanı" else "$age ${slot.branch} Antrenmanı",
                                branch = slot.branch,
                                hall = hall.name,
                                ageGroup = age,
                                gender = "Karışık",
                                dayOfWeek = dayName,
                                date = date,
                                startTime = slot.start,
                                endTime = slot.end,
                                coachName = slot.coach,
                                coachId = "coach_default",
                                status = "Aktif",
                                statusNote = "Hafta sonu antrenman programı (test).",
                                isMatch = false
                            )
                        )
                    }
                }
            }
        }
        return result
    }

    private val _localSessions = MutableStateFlow(createInitialSessions())
    val localSessions = _localSessions.asStateFlow()

    // Initial Weekly Star Players
    private val initialStars = listOf(
        WeeklyPlayer(
            id = "star_1",
            playerId = "star_1",
            name = "Eren Çelik",
            category = "U14 Basketbol Erkek",
            branch = "Basketbol",
            achievement = "Son maçta 28 sayı, 9 asist ve 4 top çalma ile galibiyet mimarı.",
            week = "2026 - 43. Hafta",
            votesCount = 42L,
            nominatedBy = "Ahmet Yılmaz"
        ),
        WeeklyPlayer(
            id = "star_2",
            playerId = "star_2",
            name = "Zeynep Doğan",
            category = "U16 Voleybol Kız",
            branch = "Voleybol",
            achievement = "Derbi maçında 12 blok, 18 hücum sayısı ile sahanın yıldızı.",
            week = "2026 - 43. Hafta",
            votesCount = 37L,
            nominatedBy = "Selin Kaya"
        ),
        WeeklyPlayer(
            id = "star_3",
            playerId = "star_3",
            name = "Kaan Arslan",
            category = "U12 Basketbol Karışık",
            branch = "Basketbol",
            achievement = "Haftanın en çok gelişim gösteren ve %100 idman katılımı sağlayan sporcusu.",
            week = "2026 - 43. Hafta",
            votesCount = 29L,
            nominatedBy = "Emre Can"
        )
    )

    private val _localStars = MutableStateFlow(initialStars)
    val localStars = _localStars.asStateFlow()

    // Initial Announcements
    private val initialAnnouncements = listOf(
        Announcement(
            id = "ann_1",
            announcementId = "ann_1",
            title = "Cuma U16 Basketbol İdmanı İptal Edildi",
            message = "Koç semineri nedeniyle Cuma idmanı ertelenmiştir. Cumartesi saat 14:00'te telafi yapılacaktır.",
            type = "CANCELLED",
            relatedSessionId = "sess_106",
            authorName = "Ahmet Yılmaz (Baş Antrenör)",
            authorId = "coach_1"
        ),
        Announcement(
            id = "ann_2",
            announcementId = "ann_2",
            title = "Mutlukent U12 Voleybol Saati Değişti",
            message = "Perşembe günkü U12 voleybol seansımız saat 17:30'a kaydırılmıştır.",
            type = "RESCHEDULED",
            relatedSessionId = "sess_105",
            authorName = "Merve Aydın (Antrenör)",
            authorId = "coach_2"
        ),
        Announcement(
            id = "ann_3",
            announcementId = "ann_3",
            title = "Hafta Sonu Gebze Arena Lig Maçı",
            message = "Çarşamba günü U18 takımımızın Gebze Arena'da kritik lig maçı var. Tüm velilerimiz davetlidir!",
            type = "MATCH",
            authorName = "Kulüp Yönetimi",
            authorId = "management"
        )
    )

    private val _localAnnouncements = MutableStateFlow(initialAnnouncements)
    val localAnnouncements = _localAnnouncements.asStateFlow()

    // Dynamic Branch Management
    fun addBranch(name: String, code: String, description: String) {
        val newBranch = BranchInfo(
            id = "branch_${UUID.randomUUID()}",
            name = name,
            code = code.uppercase(),
            iconName = "sports",
            description = description
        )
        _branchesState.value = _branchesState.value + newBranch
    }

    // Dynamic Hall Management
    fun addHall(name: String, district: String, address: String, lat: Double, lng: Double, phone: String) {
        val newHall = HallInfo(
            id = "hall_${UUID.randomUUID()}",
            name = name,
            district = district,
            address = address,
            latitude = lat,
            longitude = lng,
            phone = phone,
            features = listOf("Çok Amaçlı Salon", "Soyunma Odası")
        )
        _hallsState.value = _hallsState.value + newHall
    }

    // Clash Detection Logic: Checks if same hall has overlapping session on the same date
    fun checkSessionClash(
        targetHall: String,
        targetDate: String,
        targetStart: String,
        targetEnd: String,
        excludeSessionId: String? = null
    ): TrainingSession? {
        val allSessions = _localSessions.value
        val newStartMinutes = timeToMinutes(targetStart)
        val newEndMinutes = timeToMinutes(targetEnd)

        return allSessions.firstOrNull { session ->
            if (session.id == excludeSessionId || session.sessionId == excludeSessionId) return@firstOrNull false
            if (session.status == "İptal") return@firstOrNull false // Cancelled sessions don't block hall
            if (session.hall.equals(targetHall, ignoreCase = true) && session.date == targetDate) {
                val existingStart = timeToMinutes(session.startTime)
                val existingEnd = timeToMinutes(session.endTime)
                // Overlap condition: startA < endB && endA > startB
                newStartMinutes < existingEnd && newEndMinutes > existingStart
            } else {
                false
            }
        }
    }

    private fun timeToMinutes(time: String): Int {
        return try {
            val parts = time.trim().split(":")
            parts[0].toInt() * 60 + parts[1].toInt()
        } catch (e: Exception) {
            0
        }
    }

    // Coach Session Creation
    suspend fun createSession(session: TrainingSession): Result<String> {
        val clash = checkSessionClash(
            targetHall = session.hall,
            targetDate = session.date,
            targetStart = session.startTime,
            targetEnd = session.endTime
        )
        if (clash != null) {
            return Result.failure(
                IllegalStateException("ÇAKIŞMA UYARISI: ${session.hall} salonunda bu saatte (${clash.startTime}-${clash.endTime}) zaten \"${clash.title}\" antrenmanı var!")
            )
        }

        val generatedId = if (session.sessionId.isNotBlank()) session.sessionId else UUID.randomUUID().toString()
        val finalSession = session.copy(
            id = generatedId,
            sessionId = generatedId,
            coachId = auth?.currentUser?.uid ?: "coach_local",
            coachName = auth?.currentUser?.displayName ?: session.coachName.ifBlank { "Koç" }
        )

        // Always update local memory first for snappy UI
        _localSessions.value = listOf(finalSession) + _localSessions.value

        // If authenticated with Firestore, sync to Cloud
        val uid = auth?.currentUser?.uid
        if (uid != null && firestore != null) {
            try {
                val payload = hashMapOf(
                    "sessionId" to generatedId,
                    "title" to finalSession.title,
                    "branch" to finalSession.branch,
                    "hall" to finalSession.hall,
                    "ageGroup" to finalSession.ageGroup,
                    "gender" to finalSession.gender,
                    "dayOfWeek" to finalSession.dayOfWeek,
                    "date" to finalSession.date,
                    "startTime" to finalSession.startTime,
                    "endTime" to finalSession.endTime,
                    "coachId" to uid,
                    "coachName" to finalSession.coachName,
                    "status" to finalSession.status,
                    "statusNote" to finalSession.statusNote,
                    "isMatch" to finalSession.isMatch,
                    "createdAt" to FieldValue.serverTimestamp()
                )
                firestore.collection("sessions").document(generatedId).set(payload).await()
            } catch (e: Exception) {
                handleFirestoreError(e, OperationType.CREATE, "sessions/$generatedId")
                Log.w("ClubRepo", "Failed to sync to Firestore: ${e.message}")
            }
        }

        return Result.success(generatedId)
    }

    // Coach Session Status Update or Time Reschedule
    suspend fun updateSession(
        sessionId: String,
        newStatus: String,
        note: String,
        newDate: String? = null,
        newStart: String? = null,
        newEnd: String? = null
    ): Result<Unit> {
        val currentList = _localSessions.value
        val index = currentList.indexOfFirst { it.id == sessionId || it.sessionId == sessionId }
        if (index == -1) return Result.failure(IllegalArgumentException("İdman bulunamadı"))

        val existing = currentList[index]
        val updatedDate = newDate ?: existing.date
        val updatedStart = newStart ?: existing.startTime
        val updatedEnd = newEnd ?: existing.endTime

        if (newDate != null || newStart != null || newEnd != null) {
            val clash = checkSessionClash(
                targetHall = existing.hall,
                targetDate = updatedDate,
                targetStart = updatedStart,
                targetEnd = updatedEnd,
                excludeSessionId = sessionId
            )
            if (clash != null && newStatus != "İptal") {
                return Result.failure(
                    IllegalStateException("ÇAKIŞMA UYARISI: ${existing.hall} salonunda (${clash.startTime}-${clash.endTime}) çakışan başka bir antrenman var!")
                )
            }
        }

        val updated = existing.copy(
            status = newStatus,
            statusNote = note,
            date = updatedDate,
            startTime = updatedStart,
            endTime = updatedEnd
        )

        val updatedList = currentList.toMutableList()
        updatedList[index] = updated
        _localSessions.value = updatedList

        // Create instant notification / announcement for parents
        val alertType = when (newStatus) {
            "İptal" -> "CANCELLED"
            "Değişti" -> "RESCHEDULED"
            else -> "GENERAL"
        }
        val alertTitle = when (newStatus) {
            "İptal" -> "${updated.branch} - ${updated.ageGroup} İdmanı İptal Edildi"
            "Değişti" -> "${updated.branch} - ${updated.ageGroup} Saat Değişikliği"
            else -> "${updated.branch} İdman Güncellemesi"
        }
        val alertMsg = "${updated.hall} salonundaki ${updated.date} tarihli idman: $note"
        postAnnouncement(alertTitle, alertMsg, alertType, sessionId)

        // Sync to Firestore if authenticated
        val uid = auth?.currentUser?.uid
        if (uid != null && firestore != null) {
            try {
                firestore.collection("sessions").document(sessionId).update(
                    mapOf(
                        "status" to newStatus,
                        "statusNote" to note,
                        "date" to updatedDate,
                        "startTime" to updatedStart,
                        "endTime" to updatedEnd,
                        "updatedAt" to FieldValue.serverTimestamp()
                    )
                ).await()
            } catch (e: Exception) {
                handleFirestoreError(e, OperationType.UPDATE, "sessions/$sessionId")
            }
        }

        return Result.success(Unit)
    }

    // Nominate Star Player
    suspend fun nominatePlayer(
        name: String,
        category: String,
        branch: String,
        achievement: String
    ): Result<String> {
        val newId = UUID.randomUUID().toString()
        val currentWeek = SimpleDateFormat("'2026 - 'ww'. Hafta'", Locale("tr")).format(Date())
        val coachName = auth?.currentUser?.displayName ?: "Koç"
        val star = WeeklyPlayer(
            id = newId,
            playerId = newId,
            name = name,
            category = category,
            branch = branch,
            achievement = achievement,
            week = currentWeek,
            votesCount = 1L,
            nominatedBy = coachName
        )

        _localStars.value = listOf(star) + _localStars.value

        val uid = auth?.currentUser?.uid
        if (uid != null && firestore != null) {
            try {
                firestore.collection("weekly_players").document(newId).set(
                    mapOf(
                        "playerId" to newId,
                        "name" to name,
                        "category" to category,
                        "branch" to branch,
                        "achievement" to achievement,
                        "week" to currentWeek,
                        "votesCount" to 1L,
                        "nominatedBy" to uid,
                        "createdAt" to FieldValue.serverTimestamp()
                    )
                ).await()
            } catch (e: Exception) {
                handleFirestoreError(e, OperationType.CREATE, "weekly_players/$newId")
            }
        }
        return Result.success(newId)
    }

    // Cast vote for star player
    suspend fun voteForPlayer(playerId: String): Result<Unit> {
        val currentList = _localStars.value
        val index = currentList.indexOfFirst { it.id == playerId || it.playerId == playerId }
        if (index != -1) {
            val player = currentList[index]
            val updated = player.copy(votesCount = player.votesCount + 1)
            val newList = currentList.toMutableList()
            newList[index] = updated
            _localStars.value = newList

            val uid = auth?.currentUser?.uid
            if (uid != null && firestore != null) {
                try {
                    firestore.collection("weekly_players").document(playerId).update(
                        mapOf(
                            "votesCount" to FieldValue.increment(1),
                            "updatedAt" to FieldValue.serverTimestamp()
                        )
                    ).await()
                } catch (e: Exception) {
                    handleFirestoreError(e, OperationType.UPDATE, "weekly_players/$playerId")
                }
            }
            return Result.success(Unit)
        }
        return Result.failure(IllegalArgumentException("Oyuncu bulunamadı"))
    }

    private fun postAnnouncement(title: String, msg: String, type: String, relatedSessionId: String = "") {
        val newAnn = Announcement(
            id = UUID.randomUUID().toString(),
            announcementId = UUID.randomUUID().toString(),
            title = title,
            message = msg,
            type = type,
            relatedSessionId = relatedSessionId,
            authorName = auth?.currentUser?.displayName ?: "Antrenör",
            authorId = auth?.currentUser?.uid ?: "coach"
        )
        _localAnnouncements.value = listOf(newAnn) + _localAnnouncements.value
    }

    // Realtime Firestore Sync Listener (Active when authenticated)
    fun observeCloudSessions(): Flow<List<TrainingSession>> = flow {
        if (auth?.currentUser == null || firestore == null) {
            emit(_localSessions.value)
            return@flow
        }
        try {
            firestore.collection("sessions")
                .snapshots()
                .collect { snapshot ->
                    val cloudSessions = snapshot.toObjects(TrainingSession::class.java)
                    if (cloudSessions.isNotEmpty()) {
                        // Merge with local sessions ensuring unique IDs
                        val merged = (cloudSessions + _localSessions.value)
                            .distinctBy { it.id.ifBlank { it.sessionId } }
                        _localSessions.value = merged
                        emit(merged)
                    } else {
                        emit(_localSessions.value)
                    }
                }
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.LIST, "sessions")
            emit(_localSessions.value)
        }
    }
}