package com.example.healthcareapp

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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
    onAppointmentDetailClick: (Appointment) -> Unit,
    onLogoutClick: () -> Unit
) {
    val upcomingCount = appointments.count { it.status == "Upcoming" }
    val doneCount = appointments.count { it.status != "Upcoming" }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppBackground)
    ) {
        // ── Header flat/kotak ─────────────────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Brush.verticalGradient(listOf(PrimaryBlue, Color(0xFF1976D2))))
                .padding(top = 40.dp, start = 24.dp, end = 24.dp, bottom = 24.dp)
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                            .background(White.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("👨‍⚕️", fontSize = 24.sp)
                    }
                    Spacer(Modifier.width(14.dp))
                    Column {
                        Text(
                            text = "Selamat datang,",
                            color = White.copy(alpha = 0.75f),
                            fontSize = 13.sp
                        )
                        Text(
                            text = doctorName,
                            color = White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                        if (doctor != null) {
                            Text(
                                text = doctor.specialization,
                                color = White.copy(alpha = 0.7f),
                                fontSize = 13.sp
                            )
                        }
                    }
                }

                Spacer(Modifier.height(16.dp))

                // Stats row — compact
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    StatCard(modifier = Modifier.weight(1f), label = "Total", value = "${appointments.size}")
                    StatCard(modifier = Modifier.weight(1f), label = "Upcoming", value = "$upcomingCount")
                    StatCard(modifier = Modifier.weight(1f), label = "Selesai", value = "$doneCount")
                }
            }
        }

        // ── Content ───────────────────────────────────────────────────────────
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Jadwal Praktik
            if (doctor != null) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = White),
                        elevation = CardDefaults.cardElevation(2.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Jadwal Praktik", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TextPrimary)
                            Spacer(Modifier.height(12.dp))
                            doctor.schedule.forEach { time ->
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(vertical = 3.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(6.dp)
                                            .clip(CircleShape)
                                            .background(PrimaryBlue)
                                    )
                                    Spacer(Modifier.width(10.dp))
                                    Text(time, fontSize = 14.sp, color = TextSecondary)
                                }
                            }
                        }
                    }
                }
            }

            // Appointment label
            item {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 2.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Appointment", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TextPrimary)
                    Surface(shape = CircleShape, color = PrimaryBlue) {
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
                        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Belum ada appointment terjadwal.", color = TextHint, fontSize = 14.sp)
                    }
                }
            }

            // Appointment list
            items(appointments) { appt ->
                val isUpcoming = appt.status == "Upcoming"
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
                                fontSize = 14.sp,
                                color = TextPrimary
                            )
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = if (isUpcoming) Color(0xFFE3F2FD) else AppBackground
                            ) {
                                Text(
                                    text = appt.status,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (isUpcoming) PrimaryBlue else TextSecondary,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                        }
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = "${appt.date}  •  ${appt.time}",
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                        Spacer(Modifier.height(10.dp))
                        Divider(color = AppBackground)
                        TextButton(
                            onClick = { onAppointmentDetailClick(appt) },
                            contentPadding = PaddingValues(horizontal = 0.dp, vertical = 4.dp)
                        ) {
                            Text("Lihat Detail →", color = PrimaryBlue, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }
        }

        // ── Logout pinned di bawah ────────────────────────────────────────────
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
                Text("Logout", fontWeight = FontWeight.Bold, fontSize = 15.sp)
            }
        }
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
            modifier = Modifier.padding(vertical = 8.dp, horizontal = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(value, color = White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Text(label, color = White.copy(alpha = 0.7f), fontSize = 10.sp)
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
        Appointment(1, "Dr. Andi Wijaya", "Dokter Umum", "20 Apr", "08:00", "Upcoming", "Berly Santoso", "berly@healthcare.com"),
        Appointment(2, "Dr. Andi Wijaya", "Dokter Umum", "19 Apr", "10:30", "Done", "Rina Dewi", "rina@example.com")
    )
    MaterialTheme {
        DoctorDashboardScreen(doc, "Dr. Andi Wijaya", appts, {}, {})
    }
}