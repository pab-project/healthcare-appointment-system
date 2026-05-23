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
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F7FA))
    ) {
        // HEADER - Warna hijau untuk Dokter
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF2E7D32))
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(shape = CircleShape, color = Color.White.copy(alpha = 0.2f)) {
                Box(Modifier.size(72.dp), contentAlignment = Alignment.Center) {
                    Text("👨‍⚕️", fontSize = 36.sp)
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text("Doctor Dashboard", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
            Text("Selamat datang, $doctorName", color = Color.White.copy(alpha = 0.8f), fontSize = 14.sp)
            if (doctor != null) {
                Text(doctor.specialization, color = Color.White.copy(alpha = 0.7f), fontSize = 13.sp)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Surface(shape = RoundedCornerShape(12.dp), color = Color.White.copy(alpha = 0.2f)) {
                Text("🩺 DOKTER", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp))
            }
        }

        // JADWAL PRAKTIK
        if (doctor != null) {
            Card(modifier = Modifier.fillMaxWidth().padding(16.dp), shape = RoundedCornerShape(16.dp), elevation = CardDefaults.cardElevation(4.dp)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("🕒 Jadwal Praktik", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF2E7D32))
                    Spacer(modifier = Modifier.height(8.dp))
                    doctor.schedule.forEach { time ->
                        Text("• $time", fontSize = 14.sp, color = Color(0xFF757575), modifier = Modifier.padding(vertical = 2.dp))
                    }
                }
            }
        }

        // APPOINTMENT TERKAIT
        Card(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp), shape = RoundedCornerShape(16.dp), elevation = CardDefaults.cardElevation(4.dp)) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("📅 Appointment Saya (${appointments.size})", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF2E7D32))
                Spacer(modifier = Modifier.height(8.dp))
                if (appointments.isEmpty()) {
                    Text("Belum ada appointment", color = Color(0xFF757575), fontSize = 14.sp)
                }
            }
        }

        // LIST APPOINTMENT
        LazyColumn(
            modifier = Modifier.weight(1f).padding(horizontal = 16.dp),
            contentPadding = PaddingValues(vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(appointments) { appt ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Pasien: ${appt.patientName.ifBlank { "N/A" }}", fontWeight = FontWeight.Bold, color = Color(0xFF1A1A2E))
                            Surface(shape = RoundedCornerShape(8.dp),
                                color = if (appt.status == "Upcoming") Color(0xFFE8F5E9) else Color(0xFFEEEEEE)) {
                                Text(appt.status, fontSize = 12.sp, fontWeight = FontWeight.Bold,
                                    color = if (appt.status == "Upcoming") Color(0xFF2E7D32) else Color(0xFF757575),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                            }
                        }
                        Text("${appt.date} - ${appt.time}", color = Color(0xFF757575), fontSize = 14.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        TextButton(onClick = { onAppointmentDetailClick(appt) }) {
                            Text("Lihat Detail", color = Color(0xFF2E7D32))
                        }
                    }
                }
            }
        }

        // LOGOUT
        Card(modifier = Modifier.fillMaxWidth().padding(16.dp), shape = RoundedCornerShape(16.dp), elevation = CardDefaults.cardElevation(4.dp)) {
            Column(modifier = Modifier.padding(16.dp)) {
                Button(onClick = onLogoutClick, modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC62828)), shape = RoundedCornerShape(12.dp)) {
                    Text("🚪 Logout")
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun DoctorDashboardPreview() {
    val doc = Doctor(1, "Dr. Andi Wijaya", "Dokter Umum", "Desc", listOf("Senin 08:00 - 12:00"))
    val appts = listOf(
        Appointment(1, "Dr. Andi Wijaya", "Dokter Umum", "20 Apr", "08:00", "Upcoming", "Berly", "berly@healthcare.com")
    )
    MaterialTheme {
        DoctorDashboardScreen(doc, "Dr. Andi Wijaya", appts, {}, {})
    }
}
