package com.example.ui.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.BranchInfo
import com.example.model.HallInfo
import com.example.model.TrainingSession
import com.example.ui.theme.OlimpikYellow
import com.example.ui.theme.StatusCancelled
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun AddEditSessionDialog(
    halls: List<HallInfo>,
    branches: List<BranchInfo>,
    onDismiss: () -> Unit,
    onSave: (TrainingSession) -> Unit
) {
    val todayStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

    var title by remember { mutableStateOf("") }
    var selectedBranch by remember { mutableStateOf(branches.firstOrNull()?.name ?: "Basketbol") }
    var selectedHall by remember { mutableStateOf(halls.firstOrNull()?.name ?: "Pendik Spor Kompleksi") }
    var selectedAgeGroup by remember { mutableStateOf("U14") }
    var selectedGender by remember { mutableStateOf("Erkek") }
    var date by remember { mutableStateOf(todayStr) }
    var startTime by remember { mutableStateOf("17:00") }
    var endTime by remember { mutableStateOf("18:30") }
    var isMatch by remember { mutableStateOf(false) }
    var notes by remember { mutableStateOf("") }

    var branchExpanded by remember { mutableStateOf(false) }
    var hallExpanded by remember { mutableStateOf(false) }
    var ageExpanded by remember { mutableStateOf(false) }
    var genderExpanded by remember { mutableStateOf(false) }

    val ageGroups = listOf("U10", "U12", "U14", "U16", "U18")
    val genders = listOf("Kız", "Erkek", "Karışık")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Yeni Antrenman / Maç Planla",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Title
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Başlık (Opsiyonel)") },
                    placeholder = { Text("Örn: U14 Hücum Taktik İdmanı") },
                    modifier = Modifier.fillMaxWidth().testTag("session_title_input")
                )

                // Branch Picker Dropdown
                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = selectedBranch,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Branş") },
                        modifier = Modifier.fillMaxWidth().clickable { branchExpanded = true }
                    )
                    DropdownMenu(
                        expanded = branchExpanded,
                        onDismissRequest = { branchExpanded = false }
                    ) {
                        branches.forEach { b ->
                            DropdownMenuItem(
                                text = { Text(b.name) },
                                onClick = {
                                    selectedBranch = b.name
                                    branchExpanded = false
                                }
                            )
                        }
                    }
                }

                // Hall Picker Dropdown (Pendik, Şekerpınar, Çayırova, Gebze, Mutlukent)
                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = selectedHall,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Salon") },
                        modifier = Modifier.fillMaxWidth().clickable { hallExpanded = true }
                    )
                    DropdownMenu(
                        expanded = hallExpanded,
                        onDismissRequest = { hallExpanded = false }
                    ) {
                        halls.forEach { h ->
                            DropdownMenuItem(
                                text = { Text("${h.name} (${h.district})") },
                                onClick = {
                                    selectedHall = h.name
                                    hallExpanded = false
                                }
                            )
                        }
                    }
                }

                // Age Group and Gender
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Box(modifier = Modifier.weight(1f)) {
                        OutlinedTextField(
                            value = selectedAgeGroup,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Yaş") },
                            modifier = Modifier.fillMaxWidth().clickable { ageExpanded = true }
                        )
                        DropdownMenu(
                            expanded = ageExpanded,
                            onDismissRequest = { ageExpanded = false }
                        ) {
                            ageGroups.forEach { a ->
                                DropdownMenuItem(
                                    text = { Text(a) },
                                    onClick = {
                                        selectedAgeGroup = a
                                        ageExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    Box(modifier = Modifier.weight(1f)) {
                        OutlinedTextField(
                            value = selectedGender,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Kategori") },
                            modifier = Modifier.fillMaxWidth().clickable { genderExpanded = true }
                        )
                        DropdownMenu(
                            expanded = genderExpanded,
                            onDismissRequest = { genderExpanded = false }
                        ) {
                            genders.forEach { g ->
                                DropdownMenuItem(
                                    text = { Text(g) },
                                    onClick = {
                                        selectedGender = g
                                        genderExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                // Date
                OutlinedTextField(
                    value = date,
                    onValueChange = { date = it },
                    label = { Text("Tarih (YYYY-AA-GG)") },
                    modifier = Modifier.fillMaxWidth()
                )

                // Start and End Times
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = startTime,
                        onValueChange = { startTime = it },
                        label = { Text("Başlangıç") },
                        placeholder = { Text("17:00") },
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = endTime,
                        onValueChange = { endTime = it },
                        label = { Text("Bitiş") },
                        placeholder = { Text("18:30") },
                        modifier = Modifier.weight(1f)
                    )
                }

                // Match Toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = isMatch,
                        onCheckedChange = { isMatch = it }
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Resmi Lig veya Hazırlık Maçı",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium)
                    )
                }

                // Notes
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Koç Notu / Açıklama") },
                    placeholder = { Text("Örn: Beyaz forma ile gelinecek.") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val session = TrainingSession(
                        title = title.ifBlank { "$selectedAgeGroup $selectedBranch ${if (isMatch) "Maçı" else "İdmanı"}" },
                        branch = selectedBranch,
                        hall = selectedHall,
                        ageGroup = selectedAgeGroup,
                        gender = selectedGender,
                        date = date,
                        startTime = startTime,
                        endTime = endTime,
                        isMatch = isMatch,
                        statusNote = notes,
                        status = "Aktif"
                    )
                    onSave(session)
                },
                colors = ButtonDefaults.buttonColors(containerColor = OlimpikYellow),
                modifier = Modifier.testTag("save_session_button")
            ) {
                Text("Kaydet & Yayınla", color = Color(0xFF07142A), fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Vazgeç")
            }
        }
    )
}
