package com.example.healthcareapp

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
fun DoctorDashboardScreen(
    doctor: Doctor?,
    doctorName: String,
    appointments: List<Appointment>,
    unreadNotificationsCount: Int = 0,
    onAppointmentDetailClick: (Appointment) -> Unit,
    onAcceptClick: (Int) -> Unit = {},
    onRejectClick: (Int) -> Unit = {},
    onCompleteClick: (Int, String, String, String, String) -> Unit = { _, _, _, _, _ -> },
    onProfileClick: () -> Unit = {},
    onTimeslotsClick: () -> Unit = {},
    onNotificationClick: () -> Unit = {},
    onLogoutClick: () -> Unit
) {
    val upcomingCount = appointments.count { it.status == "Upcoming" }
    val pendingCount = appointments.count { it.status.equals("Pending", ignoreCase = true) }
    val doneCount = appointments.count { it.status.equals("Completed", ignoreCase = true) || it.status.equals("Done", ignoreCase = true) }

    var activeCompletingApptId by remember { mutableStateOf<Int?>(null) }
    var diagnosis by remember { mutableStateOf("") }
    var treatment by remember { mutableStateOf("") }
    var medications by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppBackground)
    ) {
        // ── MODERN GRADIENT HEADER (DOKTER GREEN/TEAL BRUSH) ────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color(0xFF2E7D32), Color(0xFF1B5E20))
                    ),
                    shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp)
                )
                .padding(top = 40.dp, start = 20.dp, end = 20.dp, bottom = 24.dp)
        ) {
            Column {
                // Top row with profile pic, greeting, and notification/profile icons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            modifier = Modifier.size(52.dp),
                            shape = CircleShape,
                            color = White.copy(alpha = 0.2f),
                            onClick = onProfileClick
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text("👨‍⚕️", fontSize = 26.sp)
                            }
                        }
                        Spacer(Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Selamat datang,",
                                color = White.copy(alpha = 0.8f),
                                fontSize = 12.sp
                            )
                            Text(
                                text = doctorName,
                                color = White,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1
                            )
                            if (doctor != null) {
                                Text(
                                    text = doctor.specialization,
                                    color = White.copy(alpha = 0.7f),
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }

                    // Top Action Icons
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // Notifications icon
                        IconButton(onClick = onNotificationClick) {
                            BadgedBox(
                                badge = {
                                    if (unreadNotificationsCount > 0) {
                                        Badge(
                                            containerColor = AccentRed,
                                            contentColor = Color.White
                                        ) {
                                            Text("$unreadNotificationsCount")
                                        }
                                    }
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.Notifications,
                                    contentDescription = "Notifikasi",
                                    tint = White
                                )
                            }
                        }

                        // Profile icon
                        IconButton(onClick = onProfileClick) {
                            Icon(
                                imageVector = Icons.Rounded.AccountCircle,
                                contentDescription = "Profil Saya",
                                tint = White,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }
                }

                Spacer(Modifier.height(20.dp))

                // Stats row — compact
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    StatCard(modifier = Modifier.weight(1f), label = "Semua", value = "${appointments.size}")
                    StatCard(modifier = Modifier.weight(1f), label = "Upcoming", value = "$upcomingCount")
                    StatCard(modifier = Modifier.weight(1f), label = "Pending", value = "$pendingCount")
                    StatCard(modifier = Modifier.weight(1f), label = "Selesai", value = "$doneCount")
                }
            }
        }

        // ── CONTENT ───────────────────────────────────────────────────────────
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Jadwal Praktik Dokter
            if (doctor != null) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = White),
                        elevation = CardDefaults.cardElevation(2.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Jadwal Praktik", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TextPrimary)
                                TextButton(onClick = onTimeslotsClick, contentPadding = PaddingValues(0.dp)) {
                                    Text("Kelola Jadwal", fontSize = 12.sp, color = Color(0xFF2E7D32))
                                }
                            }
                            Spacer(Modifier.height(8.dp))
                            doctor.schedule.forEach { time ->
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(vertical = 3.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(6.dp)
                                            .clip(CircleShape)
                                            .background(Color(0xFF2E7D32))
                                    )
                                    Spacer(Modifier.width(10.dp))
                                    Text(time, fontSize = 13.sp, color = TextSecondary)
                                }
                            }
                        }
                    }
                }
            }

            // Appointment label
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 2.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Janji Temu Pasien", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TextPrimary)
                    Surface(shape = CircleShape, color = Color(0xFF2E7D32)) {
                        Text(
                            text = "${appointments.size}",
                            color = White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 9.dp, vertical = 3.dp)
                        )
                    }
                }
            }

            // Empty state
            if (appointments.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Belum ada janji temu terjadwal.", color = TextHint, fontSize = 13.sp)
                    }
                }
            }

            // Appointment list
            items(appointments) { appt ->
                val isUpcoming = appt.status.equals("Upcoming", ignoreCase = true)
                val isPending = appt.status.equals("Pending", ignoreCase = true)
                val isCancelled = appt.status.equals("Cancelled", ignoreCase = true) || appt.status.equals("Ditolak", ignoreCase = true)

                val badgeColor = when {
                    isUpcoming -> Color(0xFFE8F5E9) // soft green
                    isPending -> Color(0xFFFFF3E0) // soft orange
                    isCancelled -> Color(0xFFFFEBEE) // soft red
                    else -> AppBackground
                }

                val badgeTextColor = when {
                    isUpcoming -> Color(0xFF2E7D32)
                    isPending -> Color(0xFFEF6C00)
                    isCancelled -> Color(0xFFC62828)
                    else -> TextSecondary
                }

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = White),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = appt.patientName.ifBlank { "Pasien N/A" },
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = TextPrimary
                            )
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = badgeColor
                            ) {
                                Text(
                                    text = appt.status,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = badgeTextColor,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                        }
                        
                        Spacer(Modifier.height(4.dp))
                        
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Rounded.Event, contentDescription = null, tint = TextHint, modifier = Modifier.size(14.dp))
                            Spacer(Modifier.width(6.dp))
                            Text(
                                text = "${appt.date}  •  ${appt.time}",
                                fontSize = 12.sp,
                                color = TextSecondary
                            )
                        }

                        if (appt.poli.isNotBlank()) {
                            Spacer(Modifier.height(4.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Rounded.LocalHospital, contentDescription = null, tint = TextHint, modifier = Modifier.size(14.dp))
                                Spacer(Modifier.width(6.dp))
                                Text(
                                    text = appt.poli,
                                    fontSize = 12.sp,
                                    color = TextSecondary
                                )
                            }
                        }

                        Spacer(Modifier.height(12.dp))
                        Divider(color = AppBackground)
                        Spacer(Modifier.height(10.dp))

                        if (isPending) {
                            // ACCEPT & REJECT BUTTONS FOR DOCTOR
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Button(
                                    onClick = { onAcceptClick(appt.id) },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("Setujui", color = White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                }
                                Button(
                                    onClick = { onRejectClick(appt.id) },
                                    colors = ButtonDefaults.buttonColors(containerColor = AccentRed),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("Tolak", color = White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                }
                            }
                        } else {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                TextButton(
                                    onClick = { onAppointmentDetailClick(appt) },
                                    contentPadding = PaddingValues(horizontal = 0.dp, vertical = 4.dp)
                                ) {
                                    Text("Lihat Detail →", color = Color(0xFF2E7D32), fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                                }

                                if (isUpcoming) {
                                    Button(
                                        onClick = {
                                            activeCompletingApptId = appt.id
                                            diagnosis = ""
                                            treatment = ""
                                            medications = ""
                                            notes = ""
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                                        shape = RoundedCornerShape(8.dp),
                                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                                        modifier = Modifier.height(32.dp)
                                    ) {
                                        Text("Selesai", color = White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // ── LOGOUT PINNED DI BAWAH ────────────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(AppBackground)
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Button(
                onClick = onLogoutClick,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = AccentRed),
                shape = RoundedCornerShape(14.dp),
                contentPadding = PaddingValues(vertical = 14.dp)
            ) {
                Text("Keluar Akun", fontWeight = FontWeight.Bold, fontSize = 15.sp)
            }
        }
    }

    if (activeCompletingApptId != null) {
        AlertDialog(
            onDismissRequest = { activeCompletingApptId = null },
            title = {
                Text(
                    text = "Input Rekam Medis Pasien",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = TextPrimary
                )
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                        .padding(vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Masukkan rekam medis pasien sebelum menyelesaikan janji temu.",
                        fontSize = 13.sp,
                        color = TextSecondary
                    )

                    OutlinedTextField(
                        value = diagnosis,
                        onValueChange = { diagnosis = it },
                        label = { Text("Diagnosis Utama *") },
                        placeholder = { Text("Tulis diagnosis utama...") },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 3
                    )

                    OutlinedTextField(
                        value = treatment,
                        onValueChange = { treatment = it },
                        label = { Text("Tindakan / Pengobatan") },
                        placeholder = { Text("Tulis tindakan atau perawatan...") },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 3
                    )

                    OutlinedTextField(
                        value = medications,
                        onValueChange = { medications = it },
                        label = { Text("Resep Obat") },
                        placeholder = { Text("Paracetamol 500mg, Amoxicillin...") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = notes,
                        onValueChange = { notes = it },
                        label = { Text("Catatan Tambahan") },
                        placeholder = { Text("Catatan tambahan...") },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 2
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val apptId = activeCompletingApptId ?: return@Button
                        if (diagnosis.isNotBlank()) {
                            onCompleteClick(apptId, diagnosis, treatment, medications, notes)
                            activeCompletingApptId = null
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                    enabled = diagnosis.isNotBlank()
                ) {
                    Text("Simpan & Selesaikan", color = White)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { activeCompletingApptId = null }
                ) {
                    Text("Batal", color = TextSecondary)
                }
            },
            shape = RoundedCornerShape(16.dp),
            containerColor = White
        )
    }
}

@Composable
private fun StatCard(modifier: Modifier = Modifier, label: String, value: String) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(10.dp),
        color = White.copy(alpha = 0.15f)
    ) {
        Column(
            modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(value, color = White, fontSize = 15.sp, fontWeight = FontWeight.Bold)
            Text(label, color = White.copy(alpha = 0.7f), fontSize = 9.sp)
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun DoctorDashboardPreview() {
    val doc = Doctor(
        id = 1,
        name = "Dr. Andi Wijaya",
        specialization = "Dokter Umum",
        description = "Berpengalaman 10 tahun",
        schedule = listOf("Senin 08:00 - 12:00", "Rabu 13:00 - 17:00", "Jumat 08:00 - 11:00")
    )
    val appts = listOf(
        Appointment(1, "Dr. Andi Wijaya", "Dokter Umum", "20 Apr", "08:00", "Pending", "Berly Santoso", "berly@healthcare.com"),
        Appointment(2, "Dr. Andi Wijaya", "Dokter Umum", "19 Apr", "10:30", "Upcoming", "Rina Dewi", "rina@example.com")
    )
    MaterialTheme {
        DoctorDashboardScreen(
            doctor = doc,
            doctorName = "Dr. Andi Wijaya",
            appointments = appts,
            unreadNotificationsCount = 2,
            onAppointmentDetailClick = {},
            onAcceptClick = {},
            onRejectClick = {},
            onLogoutClick = {}
        )
    }
}