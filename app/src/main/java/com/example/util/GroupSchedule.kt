package com.example.util

import com.example.model.Announcement
import com.example.model.ParentFilterPreferences
import com.example.model.TrainingSession
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

private const val ANY = "Tümü"
private const val MIXED = "Karışık"

/** Veli en az bir grup (branş ya da yaş grubu) seçtiyse true. */
fun ParentFilterPreferences.hasGroup(): Boolean = branch != ANY || ageGroup != ANY

/** "Basketbol U14" gibi grup etiketi. */
fun ParentFilterPreferences.groupLabel(): String =
    listOf(branch, ageGroup).filter { it != ANY }.joinToString(" ")

private fun timeToMinutes(time: String): Int = try {
    val p = time.trim().split(":")
    p[0].toInt() * 60 + p[1].toInt()
} catch (e: Exception) {
    0
}

/** İdmanın, velinin seçtiği gruba ait olup olmadığı. "Karışık" yaş/cinsiyet herkese açıktır. */
private fun TrainingSession.matchesGroup(f: ParentFilterPreferences, exactAge: Boolean): Boolean {
    val branchOk = f.branch == ANY || branch.equals(f.branch, ignoreCase = true)
    val hallOk = f.hall == ANY || hall.contains(f.hall, ignoreCase = true) || f.hall.contains(hall, ignoreCase = true)
    val ageOk = f.ageGroup == ANY || ageGroup.equals(f.ageGroup, ignoreCase = true) ||
        (!exactAge && ageGroup.equals(MIXED, ignoreCase = true))
    val genderOk = f.gender == ANY || gender.equals(f.gender, ignoreCase = true) || gender.equals(MIXED, ignoreCase = true)
    return branchOk && hallOk && ageOk && genderOk
}

/** Seçilen grubun bir sonraki (iptal olmayan) antrenmanı. Önce tam yaş eşleşmesi aranır. */
fun nextGroupSession(all: List<TrainingSession>, f: ParentFilterPreferences): TrainingSession? {
    val today = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
    val cal = Calendar.getInstance()
    val nowMin = cal.get(Calendar.HOUR_OF_DAY) * 60 + cal.get(Calendar.MINUTE)

    fun upcoming(exactAge: Boolean) = all
        .filter {
            it.matchesGroup(f, exactAge) && it.status != "İptal" && !it.isMatch && it.date.isNotBlank() &&
                (it.date > today || (it.date == today && timeToMinutes(it.endTime) > nowMin))
        }
        .minWithOrNull(compareBy({ it.date }, { it.startTime }))

    return upcoming(true) ?: upcoming(false)
}

/**
 * İptal / saat değişikliği duyuruları.
 * Veli yalnızca kendi grubunun duyurusunu görür; antrenör hepsini görür.
 */
fun alertsForGroup(
    announcements: List<Announcement>,
    all: List<TrainingSession>,
    f: ParentFilterPreferences,
    isCoach: Boolean
): List<Announcement> {
    val alerts = announcements.filter { it.type == "CANCELLED" || it.type == "RESCHEDULED" }
    if (isCoach) return alerts.take(2)
    if (!f.hasGroup()) return emptyList()
    return alerts.filter { a ->
        if (a.relatedSessionId.isBlank()) return@filter false
        val s = all.firstOrNull { it.id == a.relatedSessionId || it.sessionId == a.relatedSessionId }
        s != null && s.status != "Aktif" && s.matchesGroup(f, false)
    }.take(2)
}

/** "13:00" -> "13.00" */
fun dotTime(time: String): String = time.replace(':', '.')

/** "2026-11-07" -> "07.11" */
fun shortDate(date: String): String =
    if (date.length >= 10) "${date.substring(8, 10)}.${date.substring(5, 7)}" else date
