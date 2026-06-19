package com.example.healthcareapp

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun DoctorProfileScreen(
    doctor: Doctor,
    doctorEmail: String,
    onEditProfileClick: () -> Unit = {},
    onLogoutClick: () -> Unit,
    onBackClick: () -> Unit
) {
    var showLogoutConfirm by remember { mutableStateOf(false) }

    // Gradient background khusus Dokter (Hijau)
    val headerGradient = Brush.verticalGradient(listOf(Color(0xFF43A047), Color(0xFF1B5E20)))
    val badgeColor = Color(0xFFE8F5E9)
    val badgeTextColor = Color(0xFF2E7D32)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .background(AppBackground)
    ) {
        // HEADER DENGAN GRADIENT & ELEMEN PREMIUM
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(headerGradient)
                .padding(top = 48.dp, bottom = 32.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Surface(
                    shape = CircleShape,
                    color = White,
                    shadowElevation = 8.dp,
                    modifier = Modifier.size(96.dp)
                ) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("👨‍⚕️", fontSize = 48.sp)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = doctor.name,
                    color = White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
                
                Text(
                    text = doctorEmail,
                    color = White.copy(alpha = 0.8f),
                    fontSize = 14.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Badge Role
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = badgeColor,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "🩺 Dokter Spesialis",
                        color = badgeTextColor,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                    )
                }
            }
        }

        // STATS SECTION
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .offset(y = (-16).dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatsCard(label = "Pasien", value = "250+", modifier = Modifier.weight(1f))
            StatsCard(label = "Jadwal", value = "${doctor.schedule.size} Hari", modifier = Modifier.weight(1f))
            StatsCard(label = "Rating", value = "4.9 ⭐", modifier = Modifier.weight(1f))
        }

        // INFORMASI DETAIL
        ProfileCard(title = "🧾 Detail Informasi & Spesialisasi") {
            InfoItem("Nama Lengkap", doctor.name, "👤")
            InfoItem("Bidang Spesialisasi", doctor.specialization, "🩺")
            InfoItem("Biografi / Bio", doctor.description.ifBlank { "Belum ada biografi diisi." }, "📝")
        }

        // JADWAL PRAKTIK
        ProfileCard(title = "📅 Jadwal Praktik") {
            if (doctor.schedule.isEmpty()) {
                Text(
                    "Belum ada jadwal praktik.",
                    color = TextSecondary,
                    fontSize = 13.sp,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            } else {
                doctor.schedule.forEach { time ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("🕒", fontSize = 16.sp)
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = time,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = TextPrimary
                        )
                    }
                }
            }
        }

        // PANEL MENU AKSI
        ProfileCard(title = "⚡ Pengaturan & Menu") {
            MenuButton("✏️  Edit Profil Dokter", onClick = onEditProfileClick)
            
            Divider(modifier = Modifier.padding(vertical = 12.dp), color = TextHint.copy(alpha = 0.5f))
            
            MenuButton("🚪  Keluar Akun", color = AccentRed, onClick = { showLogoutConfirm = true })
            
            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = onBackClick,
                colors = ButtonDefaults.buttonColors(containerColor = TextSecondary),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Text("← Kembali ke Dashboard", color = White, fontWeight = FontWeight.Bold)
            }
        }
    }

    // DIALOG KONFIRMASI LOGOUT ELEGAN
    if (showLogoutConfirm) {
        AlertDialog(
            onDismissRequest = { showLogoutConfirm = false },
            title = { Text("Konfirmasi Keluar", fontWeight = FontWeight.Bold, color = TextPrimary) },
            text = { Text("Apakah Anda yakin ingin keluar dari akun Dokter?") },
            confirmButton = {
                Button(
                    onClick = {
                        showLogoutConfirm = false
                        onLogoutClick()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AccentRed),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Keluar", color = White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutConfirm = false }) {
                    Text("Batal", color = TextSecondary, fontWeight = FontWeight.Bold)
                }
            }
        )
    }
}

@Composable
fun StatsCard(label: String, value: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(value, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32))
            Spacer(modifier = Modifier.height(2.dp))
            Text(label, fontSize = 11.sp, color = TextSecondary)
        }
    }
}
