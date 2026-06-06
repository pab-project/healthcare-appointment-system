package com.example.healthcareapp

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
fun ProfileScreen(
    patient: Patient,
    userRole: UserRole = UserRole.PATIENT,
    onEditProfileClick: () -> Unit = {},
    onMedicalRecordClick: () -> Unit = {},
    onAppointmentClick: () -> Unit = {},
    onLogoutClick: () -> Unit,
    onBackClick: () -> Unit
) {
    var showLogoutConfirm by remember { mutableStateOf(false) }

    // Gradient background sesuai role
    val headerGradient = when (userRole) {
        UserRole.ADMIN -> Brush.verticalGradient(listOf(Color(0xFFE53935), Color(0xFFB71C1C)))
        UserRole.DOCTOR -> Brush.verticalGradient(listOf(Color(0xFF43A047), Color(0xFF1B5E20)))
        UserRole.PATIENT -> Brush.verticalGradient(listOf(Color(0xFF1E88E5), Color(0xFF0D47A1)))
    }

    val roleBadgeColor = when (userRole) {
        UserRole.ADMIN -> Color(0xFFFFEBEE)
        UserRole.DOCTOR -> Color(0xFFE8F5E9)
        UserRole.PATIENT -> Color(0xFFE3F2FD)
    }

    val roleTextColor = when (userRole) {
        UserRole.ADMIN -> Color(0xFFC62828)
        UserRole.DOCTOR -> Color(0xFF2E7D32)
        UserRole.PATIENT -> Color(0xFF1565C0)
    }

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
                        Text("👤", fontSize = 48.sp)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = patient.name,
                    color = White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
                
                Text(
                    text = patient.email,
                    color = White.copy(alpha = 0.8f),
                    fontSize = 14.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Badge Role
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = roleBadgeColor,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    val roleText = when (userRole) {
                        UserRole.ADMIN -> "🔑 Administrator"
                        UserRole.DOCTOR -> "🩺 Dokter"
                        UserRole.PATIENT -> "🧑 Pasien"
                    }
                    Text(
                        text = roleText,
                        color = roleTextColor,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                    )
                }
            }
        }

        // INFORMASI DETAIL
        ProfileCard(title = "🧾 Detail Informasi") {
            InfoItem("Nama Lengkap", patient.name, "👤")
            InfoItem("Alamat Email", patient.email, "✉️")
            InfoItem("No. Handphone", patient.phone, "📞")
            InfoItem("Jenis Kelamin", patient.gender, "🚻")
            InfoItem("Tanggal Lahir", patient.birthDate, "📅")
            InfoItem("Alamat Tinggal", patient.address, "🏠")
        }

        // PANEL MENU AKSI
        ProfileCard(title = "⚡ Pengaturan & Menu") {
            MenuButton("✏️  Edit Profil", onClick = onEditProfileClick)
            
            if (userRole == UserRole.PATIENT) {
                MenuButton("📜  Riwayat Rekam Medis", onClick = onMedicalRecordClick)
                MenuButton("📅  Jadwal Janji Temu", onClick = onAppointmentClick)
            }
            
            Divider(modifier = Modifier.padding(vertical = 12.dp), color = TextHint.copy(alpha = 0.5f))
            
            MenuButton("🚪  Keluar Akun", color = AccentRed, onClick = { showLogoutConfirm = true })
            
            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = onBackClick,
                colors = ButtonDefaults.buttonColors(containerColor = TextSecondary),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth().height(48.dp)
            ) {
                Text("← Kembali", color = White, fontWeight = FontWeight.Bold)
            }
        }
    }

    // DIALOG KONFIRMASI LOGOUT ELEGAN
    if (showLogoutConfirm) {
        AlertDialog(
            onDismissRequest = { showLogoutConfirm = false },
            title = { Text("Konfirmasi Keluar", fontWeight = FontWeight.Bold, color = TextPrimary) },
            text = { Text("Apakah Anda yakin ingin keluar dari akun Anda?") },
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
fun ProfileCard(title: String, content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = White),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(Modifier.padding(20.dp)) {
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(16.dp))
            content()
        }
    }
}

@Composable
fun InfoItem(label: String, value: String, emoji: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(AppBackground, shape = RoundedCornerShape(10.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text(emoji, fontSize = 20.sp)
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(label, fontSize = 12.sp, color = TextSecondary)
            Text(value, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
        }
    }
}

@Composable
fun MenuButton(
    text: String,
    color: Color = PrimaryBlue,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = AppBackground)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = text,
                color = if (color == PrimaryBlue) TextPrimary else color,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp
            )
            Text("→", color = if (color == PrimaryBlue) TextSecondary else color, fontWeight = FontWeight.Bold)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ProfileScreenPreview() {
    val dummy = Patient(1, "Berly Marcellino", "berly@mail.com", "08123", "Laki-laki", "2004-01-01", "Klaten")
    ProfileScreen(patient = dummy, userRole = UserRole.PATIENT, onLogoutClick = {}, onBackClick = {})
}