package com.example.data

import androidx.test.core.app.ApplicationProvider
import com.example.base.FirestoreEmulatorTestBase
import com.example.model.TrainingSession
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withTimeout
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test
import java.util.UUID

class ClubRuleTest : FirestoreEmulatorTestBase() {

  @Test
  fun unauthenticatedUser_cannotReadSessions_failsPermissionDenied() = runBlocking {
    auth.signOut()
    try {
      withTimeout(5000L) {
        firestore.collection("sessions").get().await()
      }
      fail("Expected PERMISSION_DENIED for unauthenticated caller")
    } catch (e: Exception) {
      val firestoreException = checkNotNull(e.unwrapFirestoreException()) {
        "Expected FirebaseFirestoreException but got $e"
      }
      assertEquals(FirebaseFirestoreException.Code.PERMISSION_DENIED, firestoreException.code)
    }
  }

  @Test
  fun authenticatedCoach_canCreateSession() = runBlocking {
    val uid = signInTestUser("coach_test@olimpikspor.com")
    val sessId = "sess_${UUID.randomUUID()}"

    val payload = hashMapOf(
      "sessionId" to sessId,
      "title" to "U14 Basketbol Antrenmanı",
      "branch" to "Basketbol",
      "hall" to "Pendik Spor Kompleksi",
      "ageGroup" to "U14",
      "gender" to "Erkek",
      "dayOfWeek" to "Pazartesi",
      "date" to "2026-10-26",
      "startTime" to "17:00",
      "endTime" to "18:30",
      "coachId" to uid,
      "coachName" to "Test Koç",
      "status" to "Aktif",
      "statusNote" to "Hücum setleri",
      "isMatch" to false,
      "createdAt" to FieldValue.serverTimestamp()
    )

    withTimeout(5000L) {
      firestore.collection("sessions").document(sessId).set(payload).await()
    }

    val snapshot = withTimeout(5000L) {
      firestore.collection("sessions").document(sessId).get().await()
    }
    assertTrue(snapshot.exists())
    assertEquals("U14 Basketbol Antrenmanı", snapshot.getString("title"))
  }

  @Test
  fun repository_detectsSessionClash() {
    val context = ApplicationProvider.getApplicationContext<android.content.Context>()
    val repository = ClubRepository(context, firestore)

    // Pendik hall has sess_101 at 17:00-18:30 on 2026-10-26
    val conflicting = repository.checkSessionClash(
      targetHall = "Pendik Spor Kompleksi",
      targetDate = "2026-10-26",
      targetStart = "17:30",
      targetEnd = "19:00"
    )
    assertNotNull("Expected clash detection for overlapping session in Pendik", conflicting)

    // Non-overlapping time slot
    val nonConflicting = repository.checkSessionClash(
      targetHall = "Pendik Spor Kompleksi",
      targetDate = "2026-10-26",
      targetStart = "19:00",
      targetEnd = "20:30"
    )
    assertNull("No clash expected for later non-overlapping slot", nonConflicting)
  }
}
