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
import kotlinx.coroutines.launch


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
    var dateExpanded by remember { mutableStateOf(false) }

    var showSuccessDialog by remember { mutableStateOf(false) }
    var showErrorDialog by remember { mutableStateOf(false) }

    // Dynamic slot states
    var availableSlots by remember { mutableStateOf<List<com.example.healthcareapp.network.TimeSlotResponse>>(emptyList()) }
    var isLoadingSlots by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    fun formatDate(apiDate: String): String {
        return try {
            val parts = apiDate.split("-")
            if (parts.size == 3) "${parts[2]}/${parts[1]}/${parts[0]}" else apiDate
        } catch (e: Exception) {
            apiDate
        }
    }

    val doctorOptions = DataManager.doctors.map { "${it.name} (${it.specialization})" }

    val dateOptions = availableSlots.map { formatDate(it.date) }.distinct()

    val targetApiDate = try {
        val parts = date.split("/")
        if (parts.size == 3) "${parts[2]}-${parts[1]}-${parts[0]}" else date
    } catch (e: Exception) {
        date
    }

    val timeOptions = availableSlots
        .filter { it.date == targetApiDate }
        .map { slot ->
            val start = slot.startTime.substring(0, 5)
            val end = slot.endTime.substring(0, 5)
            "$start - $end"
        }
        .distinct()


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
                                onClick = {
                                    selectedDoctor = doc
                                    doctorExpanded = false
                                    date = ""
                                    selectedTime = ""
                                    availableSlots = emptyList()
                                    val cleanDocName = if (doc.contains("(")) doc.substringBefore("(").trim() else doc
                                    val matchedDoctor = DataManager.doctors.find { it.name.equals(cleanDocName, ignoreCase = true) }
                                    if (matchedDoctor != null) {
                                        isLoadingSlots = true
                                        coroutineScope.launch {
                                            availableSlots = DataManager.getDoctorSlots(matchedDoctor.id)
                                            isLoadingSlots = false
                                        }
                                    }
                                }
                            )
                        }
                    }
                }

                Row(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    ExposedDropdownMenuBox(
                        expanded = dateExpanded,
                        onExpandedChange = {
                            if (selectedDoctor.isNotEmpty() && !isLoadingSlots && dateOptions.isNotEmpty()) {
                                dateExpanded = it
                            }
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        val dateLabel = when {
                            selectedDoctor.isEmpty() -> "Pilih Dokter"
                            isLoadingSlots -> "Memuat..."
                            dateOptions.isEmpty() -> "Tidak Ada Jadwal"
                            else -> date.ifEmpty { "📅 Tanggal" }
                        }
                        OutlinedTextField(
                            value = if (date.isEmpty() && dateLabel != "📅 Tanggal") "" else date,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text(dateLabel) },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = dateExpanded) },
                            modifier = Modifier.menuAnchor().fillMaxWidth(),
                            colors = textFieldColors,
                            shape = RoundedCornerShape(12.dp)
                        )
                        ExposedDropdownMenu(
                            expanded = dateExpanded,
                            onDismissRequest = { dateExpanded = false }
                        ) {
                            dateOptions.forEach { d ->
                                DropdownMenuItem(
                                    text = { Text(d) },
                                    onClick = {
                                        date = d
                                        selectedTime = ""
                                        dateExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    ExposedDropdownMenuBox(
                        expanded = timeExpanded,
                        onExpandedChange = {
                            if (date.isNotEmpty() && timeOptions.isNotEmpty()) {
                                timeExpanded = it
                            }
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        val timeLabel = when {
                            date.isEmpty() -> "Pilih Tanggal"
                            timeOptions.isEmpty() -> "Tidak Ada Jam"
                            else -> selectedTime.ifEmpty { "🕒 Jam" }
                        }
                        OutlinedTextField(
                            value = if (selectedTime.isEmpty() && timeLabel != "🕒 Jam") "" else selectedTime,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text(timeLabel) },
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