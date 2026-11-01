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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ui.theme.OlimpikYellow

@Composable
fun AddBranchDialog(
    onDismiss: () -> Unit,
    onConfirm: (name: String, code: String, desc: String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var code by remember { mutableStateOf("") }
    var desc by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Yeni Spor Branşı Tanımla", fontWeight = FontWeight.Bold) },
        text = {
            Column {
                Text(
                    text = "Kulüp bünyesine eklenecek yeni spor branşı (Örn: Hentbol, Yüzme, Jimnastik) tanımlayın.",
                    style = MaterialTheme.typography.bodySmall
                )
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it; if (code.isEmpty()) code = it.uppercase() },
                    label = { Text("Branş Adı") },
                    placeholder = { Text("Örn: Hentbol") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = code,
                    onValueChange = { code = it },
                    label = { Text("Kısa Kod") },
                    placeholder = { Text("HANDBALL") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = desc,
                    onValueChange = { desc = it },
                    label = { Text("Açıklama") },
                    placeholder = { Text("Altyapı ve lig takımları") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { if (name.isNotBlank()) onConfirm(name, code, desc) },
                colors = ButtonDefaults.buttonColors(containerColor = OlimpikYellow)
            ) {
                Text("Branşı Ekle", color = Color(0xFF07142A), fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) { Text("Vazgeç") }
        }
    )
}

@Composable
fun AddHallDialog(
    onDismiss: () -> Unit,
    onConfirm: (name: String, district: String, address: String, lat: Double, lng: Double, phone: String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var district by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var latStr by remember { mutableStateOf("40.8500") }
    var lngStr by remember { mutableStateOf("29.4000") }
    var phone by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Yeni Spor Salonu Tanımla", fontWeight = FontWeight.Bold) },
        text = {
            Column {
                Text(
                    text = "Kulübün antrenman yapacağı yeni spor tesisi ve harita koordinatlarını girin.",
                    style = MaterialTheme.typography.bodySmall
                )
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Salon Adı") },
                    placeholder = { Text("Örn: Darıca Spor Salonu") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = district,
                    onValueChange = { district = it },
                    label = { Text("Bölge / İlçe") },
                    placeholder = { Text("Örn: Darıca / Kocaeli") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = address,
                    onValueChange = { address = it },
                    label = { Text("Açık Adres") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = latStr,
                        onValueChange = { latStr = it },
                        label = { Text("Enlem (Lat)") },
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = lngStr,
                        onValueChange = { lngStr = it },
                        label = { Text("Boylam (Lng)") },
                        modifier = Modifier.weight(1f)
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Telefon") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank()) {
                        val lat = latStr.toDoubleOrNull() ?: 40.8500
                        val lng = lngStr.toDoubleOrNull() ?: 29.4000
                        onConfirm(name, district, address, lat, lng, phone)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = OlimpikYellow)
            ) {
                Text("Salonu Kaydet", color = Color(0xFF07142A), fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) { Text("Vazgeç") }
        }
    )
}
