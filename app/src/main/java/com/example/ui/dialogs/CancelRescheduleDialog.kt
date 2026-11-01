package com.example.ui.dialogs

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.model.TrainingSession
import com.example.ui.theme.OlimpikYellow
import com.example.ui.theme.StatusCancelled
import com.example.ui.theme.StatusChanged

@Composable
fun CancelSessionDialog(
    session: TrainingSession,
    onDismiss: () -> Unit,
    onConfirm: (reason: String) -> Unit
) {
    var reason by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("İdmanı İptal Et", fontWeight = FontWeight.Bold, color = StatusCancelled)
        },
        text = {
            Column {
                Text(
                    text = "${session.branch} - ${session.ageGroup} (${session.hall}) idmanı iptal edilecek. Sistem tüm velilere otomatik bildirim duyurusu yayınlayacaktır.",
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(
                    value = reason,
                    onValueChange = { reason = it },
                    label = { Text("İptal Sebebi / Açıklama") },
                    placeholder = { Text("Örn: Olumsuz hava koşulları / Zemin bakım") },
                    modifier = Modifier.fillMaxWidth().testTag("cancel_reason_input")
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(reason.ifBlank { "Antrenör tarafından iptal edildi." }) },
                colors = ButtonDefaults.buttonColors(containerColor = StatusCancelled)
            ) {
                Text("İptal Et & Duyur", color = Color.White)
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) { Text("Vazgeç") }
        }
    )
}

@Composable
fun RescheduleSessionDialog(
    session: TrainingSession,
    onDismiss: () -> Unit,
    onConfirm: (newDate: String, newStart: String, newEnd: String, note: String) -> Unit
) {
    var newDate by remember { mutableStateOf(session.date) }
    var newStart by remember { mutableStateOf(session.startTime) }
    var newEnd by remember { mutableStateOf(session.endTime) }
    var note by remember { mutableStateOf("Saat güncellendi.") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("İdman Saatini Güncelle", fontWeight = FontWeight.Bold, color = StatusChanged)
        },
        text = {
            Column {
                Text(
                    text = "${session.branch} (${session.hall}) için yeni saat aralığı giriniz. Çakışma kontrolü yapılacaktır.",
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(
                    value = newDate,
                    onValueChange = { newDate = it },
                    label = { Text("Tarih (YYYY-AA-GG)") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = newStart,
                        onValueChange = { newStart = it },
                        label = { Text("Başlangıç") },
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = newEnd,
                        onValueChange = { newEnd = it },
                        label = { Text("Bitiş") },
                        modifier = Modifier.weight(1f)
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = note,
                    onValueChange = { note = it },
                    label = { Text("Değişiklik Notu") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(newDate, newStart, newEnd, note) },
                colors = ButtonDefaults.buttonColors(containerColor = OlimpikYellow)
            ) {
                Text("Güncelle & Duyur", color = Color(0xFF07142A), fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) { Text("Vazgeç") }
        }
    )
}
