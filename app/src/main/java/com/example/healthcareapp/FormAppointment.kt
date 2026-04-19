package com.example.healthcareapp

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// --- Palet Warna ---
val PrimaryBlue = Color(0xFF1565C0)
val AppBackground = Color(0xFFF5F7FA)
val White = Color(0xFFFFFFFF)
val TextPrimary = Color(0xFF1A1A2E)
val TextSecondary = Color(0xFF757575)
val TextHint = Color(0xFFBDBDBD)
val AccentGreen = Color(0xFF2E7D32)
val AccentRed = Color(0xFFC62828)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppointmentScreen() {
    var patientName by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("") }
    var symptoms by remember { mutableStateOf("") }
    var selectedDoctor by remember { mutableStateOf("") }
    var selectedTime by remember { mutableStateOf("") }

    var doctorExpanded by remember { mutableStateOf(false) }
    var timeExpanded by remember { mutableStateOf(false) }

    var showSuccessDialog by remember { mutableStateOf(false) }
    var showErrorDialog by remember { mutableStateOf(false) }

    val doctorOptions = listOf("dr. Budi (Poli Umum)", "dr. Citra (Poli Gigi)", "dr. Andi (Penyakit Dalam)", "dr. Diana (Poli Anak)")
    val timeOptions = listOf("08:00 - 09:00", "09:30 - 10:30", "11:00 - 12:00", "13:30 - 14:30", "15:00 - 16:00")

    val textFieldColors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = PrimaryBlue,
        unfocusedBorderColor = TextHint,
        focusedLabelColor = PrimaryBlue,
        unfocusedLabelColor = TextSecondary,
        focusedContainerColor = White,
        unfocusedContainerColor = White
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppBackground)
            .padding(24.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text("Buat Janji Temu", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
        Text("Silakan isi data lengkap di bawah ini.", fontSize = 14.sp, color = TextSecondary, modifier = Modifier.padding(bottom = 24.dp))

        OutlinedTextField(
            value = patientName,
            onValueChange = { patientName = it },
            label = { Text("Nama Lengkap") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
            colors = textFieldColors,
            shape = RoundedCornerShape(12.dp)
        )

        ExposedDropdownMenuBox(
            expanded = doctorExpanded,
            onExpandedChange = { doctorExpanded = it },
            modifier = Modifier.padding(bottom = 16.dp)
        ) {
            OutlinedTextField(
                value = selectedDoctor,
                onValueChange = {},
                readOnly = true,
                label = { Text("Pilih Dokter") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = doctorExpanded) },
                // FIX: menuAnchor kosong biar kompatibel versi lama
                modifier = Modifier.menuAnchor().fillMaxWidth(),
                colors = textFieldColors,
                shape = RoundedCornerShape(12.dp)
            )
            ExposedDropdownMenu(
                expanded = doctorExpanded,
                onDismissRequest = { doctorExpanded = false }
                // containerColor dihapus karena nggak ada di versi lama
            ) {
                doctorOptions.forEach { doc ->
                    DropdownMenuItem(
                        text = { Text(doc) },
                        onClick = { selectedDoctor = doc; doctorExpanded = false }
                    )
                }
            }
        }

        Row(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedTextField(
                value = date,
                onValueChange = { newValue ->
                    val digits = newValue.filter { it.isDigit() }
                    if (digits.length <= 8) {
                        var d = ""; var m = ""
                        if (digits.length >= 2) d = digits.substring(0, 2)
                        if (digits.length >= 4) m = digits.substring(2, 4)
                        if ((d.isEmpty() || (d.toIntOrNull() ?: 0) <= 31) && (m.isEmpty() || (m.toIntOrNull() ?: 0) <= 12)) {
                            var formatted = ""
                            for (i in digits.indices) {
                                formatted += digits[i]
                                if ((i == 1 || i == 3) && i != digits.lastIndex) formatted += "/"
                            }
                            date = formatted
                        }
                    }
                },
                label = { Text("Tanggal") },
                modifier = Modifier.weight(1f),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                colors = textFieldColors,
                shape = RoundedCornerShape(12.dp)
            )

            ExposedDropdownMenuBox(
                expanded = timeExpanded,
                onExpandedChange = { timeExpanded = it },
                modifier = Modifier.weight(1f)
            ) {
                OutlinedTextField(
                    value = selectedTime,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Jam") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = timeExpanded) },
                    // FIX: menuAnchor kosong
                    modifier = Modifier.menuAnchor().fillMaxWidth(),
                    colors = textFieldColors,
                    shape = RoundedCornerShape(12.dp)
                )
                ExposedDropdownMenu(
                    expanded = timeExpanded,
                    onDismissRequest = { timeExpanded = false }
                ) {
                    timeOptions.forEach { t ->
                        DropdownMenuItem(
                            text = { Text(t) },
                            onClick = { selectedTime = t; timeExpanded = false }
                        )
                    }
                }
            }
        }

        OutlinedTextField(
            value = symptoms,
            onValueChange = { symptoms = it },
            label = { Text("Keluhan / Gejala") },
            modifier = Modifier.fillMaxWidth().height(150.dp).padding(bottom = 24.dp),
            colors = textFieldColors,
            shape = RoundedCornerShape(12.dp)
        )

        Button(
            onClick = {
                if (patientName.isBlank() || date.length < 10 || selectedDoctor.isBlank() || selectedTime.isBlank() || symptoms.isBlank()) {
                    showErrorDialog = true
                } else {
                    showSuccessDialog = true
                }
            },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text("Konfirmasi Jadwal", fontWeight = FontWeight.Bold, color = White)
        }
    }

    if (showErrorDialog) {
        AlertDialog(
            onDismissRequest = { showErrorDialog = false },
            title = { Text("Data Belum Lengkap", color = AccentRed, fontWeight = FontWeight.Bold) },
            text = { Text("Harap isi semua informasi dengan benar.") },
            confirmButton = {
                TextButton(onClick = { showErrorDialog = false }) {
                    Text("Paham", color = PrimaryBlue, fontWeight = FontWeight.Bold)
                }
            }
        )
    }

    if (showSuccessDialog) {
        AlertDialog(
            onDismissRequest = { showSuccessDialog = false },
            title = { Text("Berhasil!", color = AccentGreen, fontWeight = FontWeight.Bold) },
            text = { Text("Jadwal periksa untuk $patientName telah berhasil dikonfirmasi.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showSuccessDialog = false
                        patientName = ""; date = ""; symptoms = ""; selectedDoctor = ""; selectedTime = ""
                    }
                ) {
                    Text("OK", color = PrimaryBlue, fontWeight = FontWeight.Bold)
                }
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun AppointmentScreenPreview() {
    AppointmentScreen()
}