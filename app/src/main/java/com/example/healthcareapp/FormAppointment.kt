package com.example.healthcareapp

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.ContentScale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormAppointment(
    patientEmail: String = "",
    onBackClick: () -> Unit,
    onConfirmClick: (String, String, String, String, String, String) -> Unit
) {
    var patientName by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("") }
    var symptoms by remember { mutableStateOf("") }
    var selectedDoctor by remember { mutableStateOf("") }
    var selectedTime by remember { mutableStateOf("") }

    var doctorExpanded by remember { mutableStateOf(false) }
    var timeExpanded by remember { mutableStateOf(false) }

    var showSuccessDialog by remember { mutableStateOf(false) }
    var showErrorDialog by remember { mutableStateOf(false) }

    val doctorOptions = DataManager.doctors.map { "${it.name} (${it.specialization})" }
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
            .verticalScroll(rememberScrollState())
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color(0xFF1565C0),
                            Color(0xFF0D47A1)
                        )
                    )
                )
                .padding(top = 48.dp, bottom = 32.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                Column {

                    Text(
                        "Buat Janji Temu",
                        color = White,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        "Buat jadwal konsultasi dokter",
                        color = White.copy(alpha = 1f),
                        fontSize = 13.sp
                    )

                    Text(
                        "Silakan isi data lengkap di bawah ini",
                        color = White.copy(alpha = 0.8f),
                        fontSize = 13.sp
                    )
                }

                IconButton(onClick = onBackClick) {
                    Text(
                        "✕",
                        color = White,
                        fontSize = 20.sp
                    )
                }
            }
        }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp)
                .offset(y = (-16).dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = White
            ),
            elevation = CardDefaults.cardElevation(6.dp)
        ) {

            Column(
                modifier = Modifier.padding(20.dp)
            ) {

                Text(
                    "Informasi Pasien",
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    fontSize = 18.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = patientName,
                    onValueChange = { patientName = it },
                    label = { Text("👤 Nama Lengkap") },
                    placeholder = {
                        Text("Masukkan nama pasien")
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    colors = textFieldColors,
                    shape = RoundedCornerShape(14.dp)
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
                        label = { Text("👨‍⚕️ Pilih Dokter") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = doctorExpanded) },
                        modifier = Modifier.menuAnchor().fillMaxWidth(),
                        colors = textFieldColors,
                        shape = RoundedCornerShape(12.dp)
                    )
                    ExposedDropdownMenu(
                        expanded = doctorExpanded,
                        onDismissRequest = { doctorExpanded = false }
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
                        label = { Text("📅 Tanggal") },
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
                            label = { Text("🕒 Jam") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = timeExpanded) },
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
                    label = { Text("🩺 Keluhan / Gejala") },
                    modifier = Modifier.fillMaxWidth().height(150.dp).padding(bottom = 24.dp),
                    colors = textFieldColors,
                    shape = RoundedCornerShape(12.dp)
                )

                Surface(
                    color = Color(0xFFE3F2FD),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "ℹ️ Pastikan data yang Anda masukkan sudah benar sebelum melakukan konfirmasi jadwal.",
                        modifier = Modifier.padding(14.dp),
                        color = PrimaryBlue,
                        fontSize = 12.sp
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        if (patientName.isBlank() || selectedDoctor.isBlank() || date.isBlank() || selectedTime.isBlank() || symptoms.isBlank()) {
                            showErrorDialog = true
                        } else {
                            showSuccessDialog = true
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PrimaryBlue
                    )
                ) {
                    Text(
                        text = "Konfirmasi Jadwal",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = White
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedButton(
                    onClick = onBackClick,
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                ) {
                    Text("Batal", color = TextSecondary)
                }
            }
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
                        onConfirmClick(patientName, patientEmail, selectedDoctor, date, selectedTime, symptoms)
                        showSuccessDialog = false
                        patientName = ""; date = ""; symptoms = ""; selectedDoctor = ""; selectedTime = ""
                        onBackClick() // Auto back after success
                    }
                ) {
                    Text("OK", color = PrimaryBlue, fontWeight = FontWeight.Bold)
                }
            }
        )
    }
}

@Preview(
    showBackground = true,
    showSystemUi = true
)
@Composable
fun FormAppointmentPreview() {
    HealthcareTheme {
        FormAppointment(
            patientEmail = "test@test.com",
            onBackClick = {},
            onConfirmClick = { _, _, _, _, _, _ -> }
        )
    }
}