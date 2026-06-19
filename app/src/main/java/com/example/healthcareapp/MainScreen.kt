package com.example.healthcareapp

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.healthcareapp.data.entity.NotificationEntity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    isLoggedIn: Boolean,
    patient: Patient?,
    unreadNotificationsCount: Int = 0,
    upcomingAppointments: List<Appointment> = emptyList(),
    onLoginClick: () -> Unit,
    onProfileClick: () -> Unit,
    onDoctorListClick: () -> Unit,
    onAppointmentClick: () -> Unit,
    onFindHospitalClick: () -> Unit,
    onEmergencyCallClick: () -> Unit,
    onNotificationClick: () -> Unit = {}
) {
    var showEmergencySheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()
    val scrollState = rememberScrollState()

    // Premium Color Palette
    val primaryBlue = Color(0xFF1565C0)
    val secondaryBlue = Color(0xFF1E88E5)
    val headerGradient = Brush.verticalGradient(listOf(primaryBlue, secondaryBlue))

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppBackground)
            .verticalScroll(scrollState)
    ) {
        // ── PREMIUM GRADIENT HEADER ───────────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = headerGradient,
                    shape = RoundedCornerShape(bottomStart = 28.dp, bottomEnd = 28.dp)
                )
                .padding(top = 40.dp, start = 20.dp, end = 20.dp, bottom = 28.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Profile & greeting
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            modifier = Modifier.size(48.dp),
                            shape = CircleShape,
                            color = Color.White.copy(alpha = 0.2f),
                            onClick = onProfileClick
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text("👤", fontSize = 24.sp)
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Selamat datang,",
                                color = Color.White.copy(alpha = 0.8f),
                                fontSize = 12.sp
                            )
                            Text(
                                text = patient?.name ?: "Pasien",
                                color = Color.White,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1
                            )
                        }
                    }

                    // Notification & Profile icons
                    Row(verticalAlignment = Alignment.CenterVertically) {
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
                                    tint = Color.White
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Beautiful Search Bar Placeholder
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    color = Color.White,
                    shadowElevation = 2.dp,
                    onClick = onDoctorListClick
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Search,
                            contentDescription = null,
                            tint = TextHint
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Cari dokter spesialis atau poli...",
                            color = TextSecondary,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }

        // ── MAIN LAYANAN / SERVICE GRID (2x2) ───────────────────────────────
        Text(
            text = "Layanan Kesehatan",
            modifier = Modifier.padding(start = 20.dp, top = 24.dp, bottom = 12.dp),
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp)
        ) {
            MainServiceCard(
                title = "Janji Temu",
                subtitle = "Booking jadwal baru",
                icon = Icons.Rounded.AddCircleOutline,
                color = Color(0xFF4CAF50),
                modifier = Modifier.weight(1f),
                onClick = onAppointmentClick
            )
            MainServiceCard(
                title = "Daftar Dokter",
                subtitle = "Temukan spesialis",
                icon = Icons.Rounded.PersonSearch,
                color = Color(0xFF2196F3),
                modifier = Modifier.weight(1f),
                onClick = onDoctorListClick
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp)
        ) {
            MainServiceCard(
                title = "Profil Saya",
                subtitle = "Info & rekam medis",
                icon = Icons.Rounded.AccountBox,
                color = Color(0xFF9C27B0),
                modifier = Modifier.weight(1f),
                onClick = onProfileClick
            )
            MainServiceCard(
                title = "Darurat 119",
                subtitle = "Hubungi ambulan",
                icon = Icons.Rounded.PhoneInTalk,
                color = Color(0xFFE53935),
                modifier = Modifier.weight(1f),
                onClick = { showEmergencySheet = true }
            )
        }

        // ── UPCOMING APPOINTMENTS PREVIEW ─────────────────────────────────────
        if (upcomingAppointments.isNotEmpty()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Janji Temu Terdekat",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                TextButton(onClick = onAppointmentClick, contentPadding = PaddingValues(0.dp)) {
                    Text("Lihat Semua", color = primaryBlue, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
            }

            upcomingAppointments.take(2).forEach { appt ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 6.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = Color(0xFFE3F2FD),
                            modifier = Modifier.size(44.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Rounded.EventAvailable,
                                    contentDescription = null,
                                    tint = primaryBlue
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = appt.doctor,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = TextPrimary
                            )
                            Text(
                                text = "${appt.date}  •  ${appt.time}",
                                fontSize = 12.sp,
                                color = TextSecondary
                            )
                        }
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (appt.status.equals("Upcoming", ignoreCase = true)) Color(0xFFE8F5E9) else Color(0xFFFFF3E0),
                            modifier = Modifier.padding(start = 8.dp)
                        ) {
                            Text(
                                text = appt.status,
                                color = if (appt.status.equals("Upcoming", ignoreCase = true)) Color(0xFF2E7D32) else Color(0xFFEF6C00),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }
        }

        // ── ADDITIONAL ACTIONS ───────────────────────────────────────────────
        Text(
            text = "Informasi & Bantuan",
            modifier = Modifier.padding(start = 20.dp, top = 20.dp, bottom = 12.dp),
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 4.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
            onClick = onFindHospitalClick
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Color(0xFFFFEBEE),
                    modifier = Modifier.size(40.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Rounded.LocalHospital,
                            contentDescription = null,
                            tint = Color(0xFFD32F2F)
                        )
                    }
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        "Cari Rumah Sakit Terdekat",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = TextPrimary
                    )
                    Text(
                        "Temukan fasilitas kesehatan di sekitar Anda",
                        fontSize = 11.sp,
                        color = TextSecondary
                    )
                }
                Icon(
                    imageVector = Icons.Rounded.ChevronRight,
                    contentDescription = null,
                    tint = TextHint
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
    }

    // Emergency Bottom Sheet
    if (showEmergencySheet) {
        ModalBottomSheet(
            onDismissRequest = { showEmergencySheet = false },
            sheetState = sheetState,
            containerColor = Color.White
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp)
            ) {
                Text(
                    "🚨 Panggilan Darurat Medis",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    "Segera hubungi kontak darurat di bawah ini jika terjadi kondisi kritis.",
                    fontSize = 12.sp,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.height(16.dp))
                EmergencyContactItem("🚑 Ambulans Siaga (Kemenkes)", "119")
                EmergencyContactItem("🚓 Polisi Republik Indonesia", "110")
                EmergencyContactItem("🚒 Pemadam Kebakaran", "113")
                Spacer(modifier = Modifier.height(48.dp))
            }
        }
    }
}

@Composable
fun MainServiceCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .padding(8.dp)
            .height(110.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(14.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Surface(
                modifier = Modifier.size(36.dp),
                shape = CircleShape,
                color = color.copy(alpha = 0.12f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = color,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Text(
                text = subtitle,
                fontSize = 10.sp,
                color = TextSecondary
            )
        }
    }
}

@Composable
fun EmergencyContactItem(title: String, number: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF1F0)),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFFCCC7))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(title, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = TextPrimary)
                Spacer(modifier = Modifier.height(2.dp))
                Text(number, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = AccentRed)
            }
            Button(
                onClick = { /* Call action */ },
                colors = ButtonDefaults.buttonColors(containerColor = AccentRed),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Hubungi", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}