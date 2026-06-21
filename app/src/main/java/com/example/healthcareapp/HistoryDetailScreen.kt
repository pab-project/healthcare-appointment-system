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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryDetailScreen(
    historyItem: HistoryItem,
    onBackClick: () -> Unit
) {
    val headerGradient = Brush.verticalGradient(
        colors = listOf(Color(0xFF1565C0), Color(0xFF0D47A1))
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detail Rekam Medis", color = Color.White, fontWeight = FontWeight.Bold) },
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
            // ── TOP GRADIENT HEADER ──
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
                                imageVector = Icons.Rounded.Assignment,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Rekam Medis #${historyItem.id}",
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = Color(0xFFE8F5E9),
                        modifier = Modifier.padding(top = 4.dp)
                    ) {
                        Text(
                            text = "Selesai diperiksa",
                            color = Color(0xFF2E7D32),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ── DOCTOR INFO CARD ──
            HistoryDetailCard(title = "🩺 Informasi Dokter & Layanan") {
                HistoryDetailItem(
                    icon = Icons.Rounded.Person,
                    label = "Dokter Pemeriksa",
                    value = historyItem.doctorName
                )
                HistoryDetailItem(
                    icon = Icons.Rounded.LocalHospital,
                    label = "Poliklinik / Layanan",
                    value = historyItem.service
                )
                HistoryDetailItem(
                    icon = Icons.Rounded.CalendarToday,
                    label = "Tanggal Pemeriksaan",
                    value = historyItem.date
                )
            }

            // ── CLINICAL RECORD CARD ──
            HistoryDetailCard(title = "📝 Hasil Pemeriksaan Klinis") {
                HistoryDetailItem(
                    icon = Icons.Rounded.Healing,
                    label = "Diagnosis Utama",
                    value = historyItem.diagnosis?.ifBlank { "Tidak ada diagnosis" } ?: "Tidak ada diagnosis"
                )
                HistoryDetailItem(
                    icon = Icons.Rounded.MedicalServices,
                    label = "Tindakan Medis",
                    value = historyItem.treatment?.ifBlank { "Tidak ada tindakan" } ?: "Tidak ada tindakan"
                )
            }

            // ── MEDICATIONS CARD ──
            HistoryDetailCard(title = "💊 Resep Obat-obatan") {
                val meds = historyItem.medications
                if (!meds.isNullOrEmpty()) {
                    Text(
                        text = "Obat yang harus dikonsumsi:",
                        fontSize = 11.sp,
                        color = TextSecondary,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    
                    // Display medications as a styled list of items
                    meds.forEach { medName ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = Color(0xFFE3F2FD),
                                modifier = Modifier.size(24.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Rounded.OfflinePin,
                                        contentDescription = null,
                                        tint = Color(0xFF1565C0),
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = medName,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = TextPrimary
                            )
                        }
                    }
                } else {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Info,
                            contentDescription = null,
                            tint = TextSecondary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Tidak ada resep obat tertulis.",
                            fontSize = 13.sp,
                            color = TextSecondary
                        )
                    }
                }
            }

            // ── DOCTOR NOTES CARD ──
            HistoryDetailCard(title = "🗒️ Catatan Tambahan Dokter") {
                val notes = historyItem.notes?.trim()
                if (!notes.isNullOrEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFFFFDE7), shape = RoundedCornerShape(8.dp))
                            .padding(12.dp)
                    ) {
                        Text(
                            text = notes,
                            fontSize = 13.sp,
                            color = Color(0xFF5D4037),
                            lineHeight = 18.sp
                        )
                    }
                } else {
                    Text(
                        text = "Tidak ada catatan tambahan.",
                        fontSize = 13.sp,
                        color = TextSecondary
                    )
                }
            }

            // ── BACK BUTTON ──
            Button(
                onClick = onBackClick,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1565C0)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 24.dp)
                    .height(50.dp)
            ) {
                Text("Kembali ke Daftar", fontSize = 15.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun HistoryDetailCard(
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
fun HistoryDetailItem(
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
        Column(modifier = Modifier.weight(1f)) {
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
