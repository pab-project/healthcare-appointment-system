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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ProfileScreen(
    nim: String,
    nama: String,
    jurusan: String,
    angkatan: String,
    deskripsi: String,
    onHistoryClick: () -> Unit,
    onShareClick: () -> Unit,
    onSendEmailClick: () -> Unit,
    onGithubClick: () -> Unit,
    onBackClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .background(Color(0xFFF5F7FA))
    ) {
        // Header Profile (Green)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF2E7D32))
                .padding(top = 48.dp, bottom = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(
                modifier = Modifier.size(90.dp),
                shape = CircleShape,
                shadowElevation = 6.dp,
                color = Color.White
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text("👤", fontSize = 44.sp)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = nama,
                color = Color.White,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = jurusan,
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.height(12.dp))
            Surface(
                color = Color.White.copy(alpha = 0.2f),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "Mahasiswa Aktif",
                    color = Color.White,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                )
            }
        }

        // Academic Info Card
        ProfileCardSection(title = "🎓 Detail Akademik", accentColor = Color(0xFF2E7D32)) {
            AcademicInfoItem(label = "NIM", value = nim)
            AcademicInfoItem(label = "Angkatan", value = angkatan)
        }

        // About Me Card
        ProfileCardSection(title = "📝 Tentang Saya", accentColor = Color(0xFF2E7D32)) {
            Text(
                text = deskripsi,
                fontSize = 14.sp,
                color = Color(0xFF1A1A2E),
                lineHeight = 20.sp
            )
        }

        // Actions Card
        ProfileCardSection(title = "⚡ Aksi", accentColor = Color(0xFF2E7D32)) {
            ProfileActionButton("📜 Lihat Riwayat Pemeriksaan", Color(0xFF1565C0), onHistoryClick)
            
            ProfileActionButton("📤 Share Profil Ini", Color(0xFF2E7D32), onShareClick)
            Text("📌 Implicit Intent — ACTION_SEND share profil", fontSize = 11.sp, color = Color(0xFF757575), modifier = Modifier.padding(bottom = 16.dp))

            ProfileActionButton("✉️ Kirim Email", Color(0xFF2196F3), onSendEmailClick)
            Text("📌 Implicit Intent — ACTION_SENDTO buka email client", fontSize = 11.sp, color = Color(0xFF757575), modifier = Modifier.padding(bottom = 16.dp))

            ProfileActionButton("Github", Color(0xFF212121), onGithubClick)
            
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = onBackClick,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF757575)),
                shape = RoundedCornerShape(14.dp),
                contentPadding = PaddingValues(14.dp)
            ) {
                Text("← Kembali ke Halaman Utama", color = Color.White, fontSize = 15.sp)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun ProfileCardSection(title: String, accentColor: Color, content: @Composable ColumnScope.() -> Unit) {
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
                color = accentColor,
                fontSize = 16.sp
            )
            Divider(color = Color(0xFFE0E0E0), thickness = 1.dp, modifier = Modifier.padding(vertical = 12.dp))
            content()
        }
    }
}

@Composable
fun AcademicInfoItem(label: String, value: String) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 14.dp),
        color = Color(0xFFEFEFEF),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(text = label, color = Color(0xFF757575), fontSize = 11.sp)
            Text(text = value, color = Color(0xFF1A1A2E), fontSize = 15.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun ProfileActionButton(text: String, color: Color, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 10.dp),
        colors = ButtonDefaults.buttonColors(containerColor = color),
        shape = RoundedCornerShape(8.dp),
        contentPadding = PaddingValues(14.dp)
    ) {
        Text(text = text, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
    }
}
