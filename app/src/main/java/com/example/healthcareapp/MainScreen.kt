package com.example.healthcareapp

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun MainScreen(
    onMemberClick: (nim: String, nama: String, jurusan: String, angkatan: String, deskripsi: String, github: String) -> Unit,
    onDoctorListClick: () -> Unit,
    onAppointmentClick: () -> Unit,
    onFindHospitalClick: () -> Unit,
    onEmergencyCallClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .background(Color(0xFFF5F7FA))
    ) {
        // Header
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF1565C0))
                .padding(start = 24.dp, top = 48.dp, end = 24.dp, bottom = 32.dp)
        ) {
            Text("🏥", fontSize = 40.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "HealthCare",
                color = Color.White,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Appointment App",
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 16.sp
            )
            Text(
                text = "Kelompok 3",
                color = Color.White.copy(alpha = 0.5f),
                fontSize = 12.sp
            )
        }

        // App Information
        MainCardSection(title = "👤 App Information") {
            Text(
                text = "Sistem ini merupakan platform digital yang memungkinkan pengguna untuk melakukan pemesanan janji temu layanan kesehatan secara online, dengan akses informasi jadwal dokter secara real-time serta pengelolaan appointment yang terstruktur.",
                fontSize = 13.sp,
                color = Color(0xFF757575)
            )
        }

        // Staff Profiles
        MainCardSection(title = "🚀 Staff Profile") {
            Text(
                text = "📌 Explicit Intent — Kirim data ke halaman profil",
                fontSize = 11.sp,
                color = Color(0xFF757575),
                modifier = Modifier.padding(bottom = 12.dp)
            )
            
            StaffButton("👤 Mars") {
                onMemberClick(
                    "L0124045", "Berly Marcellino Suprapto", "Informatika", "2024",
                    "Pangeran Mars yang jatuh ke bumi lalu memulai perjalanannya sebagai Raja Iblis", "https://github.com/IMars-kun"
                )
            }
            StaffButton("👤 Ardina") {
                onMemberClick(
                    "L0124041", "Ardina Vidya S", "Informatika", "2024",
                    "Fokus di backend dan database.", "https://github.com/ardinavidya"
                )
            }
            StaffButton("👤 Aisyah") {
                onMemberClick(
                    "L0124085", "Aisyah Nurul S", "Informatika", "2024",
                    "Suka UI/UX design.", "https://github.com/ArulxSho"
                )
            }
            StaffButton("👤 Calista") {
                onMemberClick(
                    "L0124092", "Calista Salsabila", "Informatika", "2024",
                    "Tertarik AI dan machine learning.", "https://github.com/calistasalsabila"
                )
            }
            StaffButton("👤 Arrel") {
                onMemberClick(
                    "L0124054", "Farrel Azhar Vahrezi", "Informatika", "2024",
                    "Fokus ke cybersecurity.", "https://github.com/faleryn"
                )
            }
        }

        // Healthcare Features
        MainCardSection(title = "🏥 Healthcare Feature") {
            FeatureButton(
                text = "🗺️ Cari Rumah Sakit Terdekat",
                color = Color(0xFF2196F3),
                onClick = onFindHospitalClick
            )
            Text(
                text = "📌 Implicit Intent 3 — ACTION_VIEW buka Google Maps",
                fontSize = 11.sp,
                color = Color(0xFF757575),
                modifier = Modifier.padding(bottom = 12.dp)
            )

            FeatureButton(
                text = "🚨 Hubungi Darurat (119)",
                color = Color(0xFFD32F2F),
                onClick = onEmergencyCallClick
            )
            Text(
                text = "📌 Implicit Intent 4 — ACTION_DIAL buka aplikasi telepon",
                fontSize = 11.sp,
                color = Color(0xFF757575),
                modifier = Modifier.padding(bottom = 12.dp)
            )

            Divider(color = Color(0xFFE0E0E0), thickness = 1.dp, modifier = Modifier.padding(vertical = 8.dp))

            FeatureButton(
                text = "📅 Buat Janji Temu (Compose)",
                color = Color(0xFF1565C0),
                onClick = onAppointmentClick
            )
            Text(
                text = "📌 Jetpack Compose Integration — Form Appointment",
                fontSize = 11.sp,
                color = Color(0xFF757575),
                modifier = Modifier.padding(bottom = 12.dp)
            )
            
            FeatureButton(
                text = "👨‍⚕️ Lihat Semua Dokter",
                color = Color(0xFF1565C0),
                onClick = onDoctorListClick
            )
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun MainCardSection(title: String, content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1565C0),
                fontSize = 16.sp
            )
            Divider(color = Color(0xFFE0E0E0), thickness = 1.dp, modifier = Modifier.padding(vertical = 12.dp))
            content()
        }
    }
}

@Composable
fun StaffButton(text: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 10.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1565C0)),
        shape = RoundedCornerShape(8.dp),
        contentPadding = PaddingValues(14.dp)
    ) {
        Text(text = text, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
    }
}

@Composable
fun FeatureButton(text: String, color: Color, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp),
        colors = ButtonDefaults.buttonColors(containerColor = color),
        shape = RoundedCornerShape(8.dp),
        contentPadding = PaddingValues(14.dp)
    ) {
        Text(text = text, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
    }
}
