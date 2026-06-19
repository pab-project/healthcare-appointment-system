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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppointmentDetailScreen(
    appointment: Appointment,
    onBackClick: () -> Unit
) {
    val isUpcoming = appointment.status.equals("Upcoming", ignoreCase = true)
    val isPending = appointment.status.equals("Pending", ignoreCase = true)
    val isCompleted = appointment.status.equals("Completed", ignoreCase = true) || appointment.status.equals("Done", ignoreCase = true)

    val (statusLabel, statusColor, statusBg) = when {
        isUpcoming -> Triple("Terjadwal", Color(0xFF2E7D32), Color(0xFFE8F5E9))
        isPending -> Triple("Menunggu Persetujuan", Color(0xFFEF6C00), Color(0xFFFFF3E0))
        isCompleted -> Triple("Selesai", Color(0xFF1565C0), Color(0xFFE3F2FD))
        else -> Triple(appointment.status, TextSecondary, Color(0xFFECEFF1))
    }

    val headerGradient = Brush.verticalGradient(
        colors = when {
            isUpcoming || isCompleted -> listOf(Color(0xFF1565C0), Color(0xFF0D47A1))
            isPending -> listOf(Color(0xFFEF6C00), Color(0xFFE65100))
            else -> listOf(Color(0xFF546E7A), Color(0xFF37474F))
        }
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detail Janji Temu", color = Color.White, fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF1565C0)),
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Rounded.ArrowBack,
                            contentDescription = "Kembali",
                            tint = Color.White
                        )
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(AppBackground)
                .verticalScroll(rememberScrollState())
        ) {
            // ── TOP GRADIENT STATUS HEADER ──
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(headerGradient)
                    .padding(vertical = 28.dp, horizontal = 20.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Surface(
                        shape = CircleShape,
                        color = Color.White.copy(alpha = 0.2f),
                        modifier = Modifier.size(64.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = if (isCompleted) Icons.Rounded.DoneOutline else Icons.Rounded.EventAvailable,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Janji Temu #${appointment.id}",
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = statusBg,
                        modifier = Modifier.padding(top = 4.dp)
                    ) {
                        Text(
                            text = statusLabel,
                            color = statusColor,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ── DOCTOR INFO CARD ──
            DetailCard(title = "🩺 Informasi Dokter") {
                DetailItem(
                    icon = Icons.Rounded.Person,
                    label = "Dokter",
                    value = appointment.doctor
                )
                DetailItem(
                    icon = Icons.Rounded.LocalHospital,
                    label = "Poliklinik",
                    value = appointment.poli
                )
            }

            // ── SCHEDULE CARD ──
            DetailCard(title = "📅 Waktu & Tanggal Konsultasi") {
                DetailItem(
                    icon = Icons.Rounded.CalendarToday,
                    label = "Tanggal",
                    value = appointment.date
                )
                DetailItem(
                    icon = Icons.Rounded.AccessTime,
                    label = "Jam Praktik",
                    value = appointment.time
                )
            }

            // ── PATIENT DETAIL CARD ──
            DetailCard(title = "👤 Detail Pasien") {
                DetailItem(
                    icon = Icons.Rounded.AccountCircle,
                    label = "Nama Pasien",
                    value = appointment.patientName.ifBlank { "N/A" }
                )
                DetailItem(
                    icon = Icons.Rounded.Email,
                    label = "Email Pasien",
                    value = appointment.patientEmail.ifBlank { "N/A" }
                )
            }

            // ── BACK BUTTON ──
            PaddingValues(horizontal = 20.dp, vertical = 24.dp).let { paddingValues ->
                Button(
                    onClick = onBackClick,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1565C0)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(paddingValues)
                        .height(50.dp)
                ) {
                    Text("Kembali ke Daftar", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun DetailCard(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(12.dp))
            content()
        }
    }
}

@Composable
fun DetailItem(
    icon: ImageVector,
    label: String,
    value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            shape = RoundedCornerShape(8.dp),
            color = Color(0xFFF0F4F8),
            modifier = Modifier.size(36.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Color(0xFF1565C0),
                    modifier = Modifier.size(18.dp)
                )
            }
        }
        Spacer(modifier = Modifier.width(14.dp))
        Column {
            Text(text = label, fontSize = 11.sp, color = TextSecondary)
            Text(
                text = value,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
        }
    }
}