package com.example.healthcareapp

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.EventBusy
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun AppointmentListScreen(
    appointments: List<Appointment>,
    onBackClick: () -> Unit,
    onAddAppointmentClick: () -> Unit,
    onDetailClick: (Appointment) -> Unit,
    onRescheduleClick: (Appointment, String, String) -> Unit,
    onCancelClick: (Appointment) -> Unit
) {
    var selectedTab by remember { mutableStateOf("Upcoming") }
    var showRescheduleDialog by remember { mutableStateOf(false) }
    var appointmentToReschedule by remember { mutableStateOf<Appointment?>(null) }
    var newDate by remember { mutableStateOf("") }
    var newTime by remember { mutableStateOf("") }
    var timeExpanded by remember { mutableStateOf(false) }

    var showCancelDialog by remember { mutableStateOf(false) }
    var appointmentToCancel by remember { mutableStateOf<Appointment?>(null) }

    val filteredAppointments = appointments.filter { appt ->
        when (selectedTab) {
            "Upcoming" -> appt.status.equals("Upcoming", true)
            "Pending" -> appt.status.equals("Pending", true)
            else -> appt.status.equals("Completed", true) || appt.status.equals("Done", true)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppBackground)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // HEADER
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
                    .padding(top = 40.dp, bottom = 24.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Rounded.ArrowBack,
                            contentDescription = "Kembali",
                            tint = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Column {
                        Text(
                            "Daftar Janji Temu",
                            color = Color.White,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            "${appointments.size} Total Konsultasi",
                            color = Color.White.copy(alpha = 0.8f),
                            fontSize = 13.sp
                        )
                    }
                }
            }

            // TABS (Upcoming, Pending, Completed)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp)
                    .background(
                        Color.White,
                        RoundedCornerShape(16.dp)
                    )
                    .padding(4.dp)
            ) {
                TabButton(
                    text = "Upcoming",
                    isSelected = selectedTab == "Upcoming",
                    onClick = { selectedTab = "Upcoming" },
                    modifier = Modifier.weight(1f)
                )

                TabButton(
                    text = "Pending",
                    isSelected = selectedTab == "Pending",
                    onClick = { selectedTab = "Pending" },
                    modifier = Modifier.weight(1f)
                )

                TabButton(
                    text = "History",
                    isSelected = selectedTab == "Completed",
                    onClick = { selectedTab = "Completed" },
                    modifier = Modifier.weight(1f)
                )
            }

            if (filteredAppointments.isEmpty()) {
                Box(
                    modifier = Modifier.weight(1f).fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier.padding(24.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = Color(0xFFECEFF1),
                            modifier = Modifier.size(72.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Rounded.EventBusy,
                                    contentDescription = null,
                                    tint = TextSecondary,
                                    modifier = Modifier.size(36.dp)
                               )
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Tidak Ada Janji Temu",
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary,
                            fontSize = 16.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Janji temu dengan status $selectedTab akan muncul di sini.",
                            color = TextSecondary,
                            fontSize = 12.sp,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f)
                        .padding(horizontal = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredAppointments) { appointment ->
                        AppointmentCard(
                            appointment = appointment,
                            onDetailClick = { onDetailClick(appointment) },
                            onRescheduleClick = {
                                appointmentToReschedule = appointment
                                newDate = appointment.date
                                newTime = appointment.time
                                showRescheduleDialog = true
                            },
                            onCancelClick = {
                                appointmentToCancel = appointment
                                showCancelDialog = true
                            }
                        )
                    }

                    item {
                        Spacer(modifier = Modifier.height(90.dp))
                    }
                }
            }
        }

        // Add Appointment FAB
        FloatingActionButton(
            onClick = onAddAppointmentClick,
            containerColor = Color(0xFF1565C0),
            contentColor = Color.White,
            shape = RoundedCornerShape(18.dp),
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(20.dp)
        ) {
            Icon(
                imageVector = Icons.Rounded.Add,
                contentDescription = "Buat Janji Temu Baru",
                modifier = Modifier.size(28.dp)
            )
        }
    }

    if (showRescheduleDialog && appointmentToReschedule != null) {
        val timeOptions = listOf("08:00 - 09:00", "09:30 - 10:30", "11:00 - 12:00", "13:30 - 14:30", "15:00 - 16:00")
        AlertDialog(
            onDismissRequest = { showRescheduleDialog = false },
            title = {
                Text(
                    text = "Penjadwalan Ulang 📅",
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1565C0),
                    fontSize = 18.sp
                )
            },
            text = {
                Column(modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) {
                    Text(
                        text = "Dokter: ${appointmentToReschedule!!.doctor}",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = newDate,
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
                                    newDate = formatted
                                }
                            }
                        },
                        label = { Text("Tanggal Baru (DD/MM/YYYY)") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF1565C0),
                            focusedLabelColor = Color(0xFF1565C0),
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    @OptIn(ExperimentalMaterial3Api::class)
                    ExposedDropdownMenuBox(
                        expanded = timeExpanded,
                        onExpandedChange = { timeExpanded = it },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            value = newTime,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Jam Baru") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = timeExpanded) },
                            modifier = Modifier.menuAnchor().fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF1565C0),
                                focusedLabelColor = Color(0xFF1565C0),
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            )
                        )
                        ExposedDropdownMenu(
                            expanded = timeExpanded,
                            onDismissRequest = { timeExpanded = false }
                        ) {
                            timeOptions.forEach { t ->
                                DropdownMenuItem(
                                    text = { Text(t) },
                                    onClick = { newTime = t; timeExpanded = false }
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newDate.isNotBlank() && newTime.isNotBlank()) {
                            onRescheduleClick(appointmentToReschedule!!, newDate, newTime)
                            showRescheduleDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1565C0))
                ) {
                    Text("Simpan", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showRescheduleDialog = false }) {
                    Text("Batal", color = TextSecondary)
                }
            }
        )
    }

    if (showCancelDialog && appointmentToCancel != null) {
        AlertDialog(
            onDismissRequest = { showCancelDialog = false },
            title = {
                Text(
                    text = "Batalkan Janji Temu?",
                    fontWeight = FontWeight.Bold,
                    color = AccentRed,
                    fontSize = 18.sp
                )
            },
            text = {
                Text("Apakah Anda yakin ingin membatalkan janji temu dengan ${appointmentToCancel!!.doctor}?")
            },
            confirmButton = {
                Button(
                    onClick = {
                        onCancelClick(appointmentToCancel!!)
                        showCancelDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AccentRed)
                ) {
                    Text("Ya, Batalkan", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showCancelDialog = false }) {
                    Text("Kembali", color = TextSecondary)
                }
            }
        )
    }
}

@Composable
fun TabButton(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .clickable { onClick() },
        color = if (isSelected) Color(0xFF1565C0) else Color.Transparent,
        shape = RoundedCornerShape(12.dp)
    ) {
        Box(
            modifier = Modifier.padding(vertical = 10.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                color = if (isSelected) Color.White else TextSecondary,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp
            )
        }
    }
}

@Composable
fun AppointmentCard(
    appointment: Appointment,
    onDetailClick: () -> Unit,
    onRescheduleClick: () -> Unit,
    onCancelClick: () -> Unit
) {
    val isUpcoming = appointment.status.equals("Upcoming", true)
    val isPending = appointment.status.equals("Pending", true)

    val (statusLabel, statusColor, statusBg) = when {
        isUpcoming -> Triple("Terjadwal", Color(0xFF2E7D32), Color(0xFFE8F5E9))
        isPending -> Triple("Pending", Color(0xFFEF6C00), Color(0xFFFFF3E0))
        else -> Triple("Selesai", TextSecondary, Color(0xFFECEFF1))
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(18.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = appointment.doctor,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = appointment.poli,
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                }

                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = statusBg
                ) {
                    Text(
                        text = statusLabel,
                        modifier = Modifier.padding(
                            horizontal = 10.dp,
                            vertical = 5.dp
                        ),
                        color = statusColor,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
            HorizontalDivider(color = Color(0xFFECEFF1))
            Spacer(modifier = Modifier.height(12.dp))

            // Date
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("📅", fontSize = 14.sp)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = appointment.date,
                    color = TextPrimary,
                    fontSize = 13.sp
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Time
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("🕒", fontSize = 14.sp)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = appointment.time,
                    color = TextPrimary,
                    fontSize = 13.sp
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Action Buttons
            if (isUpcoming) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onDetailClick,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Detail", fontSize = 13.sp)
                    }

                    OutlinedButton(
                        onClick = onRescheduleClick,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Reschedule", fontSize = 13.sp)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = onCancelClick,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = AccentRed),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(
                        "Batalkan Janji Temu",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }
            } else if (isPending) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onDetailClick,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Lihat Detail Pengajuan", fontSize = 13.sp)
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = onCancelClick,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = AccentRed),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(
                        "Batalkan Pengajuan",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }
            } else {
                Button(
                    onClick = onDetailClick,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1565C0)),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(
                        "Lihat Detail Riwayat",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }
            }
        }
    }
}

@Preview(showSystemUi = true, showBackground = true)
@Composable
fun AppointmentListPreview() {
    val appointments = listOf(
        Appointment(
            id = 1,
            doctor = "Dr. Andi Wijaya",
            poli = "Penyakit Dalam",
            date = "12 Juni 2026",
            time = "09:00",
            status = "Upcoming",
            patientName = "Ardina",
            patientEmail = "ardina@gmail.com"
        )
    )

    HealthcareTheme {
        AppointmentListScreen(
            appointments = appointments,
            onBackClick = {},
            onAddAppointmentClick = {},
            onDetailClick = {},
            onRescheduleClick = { _, _, _ -> },
            onCancelClick = {}
        )
    }
}