package com.example.healthcareapp

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
fun AdminDashboardScreen(
    adminName: String,
    totalDoctors: Int,
    totalAppointments: Int,
    totalPatients: Int,
    onDoctorListClick: () -> Unit,
    onAppointmentListClick: () -> Unit,
    onLogoutClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .background(Color(0xFFF5F7FA))
    ) {
        // HEADER - Warna merah/dark untuk Admin
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFC62828))
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(shape = CircleShape, color = Color.White.copy(alpha = 0.2f)) {
                Box(Modifier.size(72.dp), contentAlignment = Alignment.Center) {
                    Text("👨‍💼", fontSize = 36.sp)
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text("Admin Dashboard", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
            Text("Selamat datang, $adminName", color = Color.White.copy(alpha = 0.8f), fontSize = 14.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Surface(shape = RoundedCornerShape(12.dp), color = Color.White.copy(alpha = 0.2f)) {
                Text("🔑 ADMINISTRATOR", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp))
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // STATISTIK
        Card(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp), shape = RoundedCornerShape(16.dp), elevation = CardDefaults.cardElevation(4.dp)) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("📊 Statistik Sistem", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFFC62828))
                Spacer(modifier = Modifier.height(12.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                    StatCard("👨‍⚕️", "Dokter", totalDoctors.toString(), Color(0xFF1565C0))
                    StatCard("🧑", "Pasien", totalPatients.toString(), Color(0xFF2E7D32))
                    StatCard("📅", "Appointment", totalAppointments.toString(), Color(0xFFE65100))
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // MENU ADMIN
        Card(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp), shape = RoundedCornerShape(16.dp), elevation = CardDefaults.cardElevation(4.dp)) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("⚡ Menu Admin", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFFC62828))
                Spacer(modifier = Modifier.height(12.dp))
                Button(onClick = onDoctorListClick, modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1565C0)), shape = RoundedCornerShape(12.dp)) {
                    Text("👨‍⚕️ Lihat Semua Dokter")
                }
                Button(onClick = onAppointmentListClick, modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE65100)), shape = RoundedCornerShape(12.dp)) {
                    Text("📅 Lihat Semua Appointment")
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // LOGOUT
        Card(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp), shape = RoundedCornerShape(16.dp), elevation = CardDefaults.cardElevation(4.dp)) {
            Column(modifier = Modifier.padding(16.dp)) {
                Button(onClick = onLogoutClick, modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC62828)), shape = RoundedCornerShape(12.dp)) {
                    Text("🚪 Logout")
                }
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun StatCard(emoji: String, label: String, value: String, color: Color) {
    Card(shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.1f)), modifier = Modifier.width(100.dp)) {
        Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(emoji, fontSize = 24.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(value, fontWeight = FontWeight.Bold, fontSize = 20.sp, color = color)
            Text(label, fontSize = 12.sp, color = Color(0xFF757575))
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun AdminDashboardPreview() {
    MaterialTheme {
        AdminDashboardScreen("Administrator", 5, 3, 1, {}, {}, {})
    }
}
