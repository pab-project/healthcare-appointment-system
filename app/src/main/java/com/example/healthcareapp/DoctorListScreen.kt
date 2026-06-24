package com.example.healthcareapp

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DoctorListScreen(
    doctors: List<Doctor>,
    onDoctorClick: (Doctor) -> Unit,
    onBackClick: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }

    val filteredDoctors = if (searchQuery.isBlank()) {
        doctors
    } else {
        doctors.filter {
            it.name.contains(searchQuery, ignoreCase = true) ||
                    it.specialization.contains(searchQuery, ignoreCase = true)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Daftar Dokter",
                        color = White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = PrimaryBlue),
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Text("←", color = White, fontSize = 24.sp)
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(AppBackground),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // ── Search bar ────────────────────────────────────────────────
            item {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Cari dokter atau spesialisasi...", color = TextHint) },
                    leadingIcon = { Text("🔍", modifier = Modifier.padding(start = 8.dp)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 4.dp),
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PrimaryBlue,
                        unfocusedBorderColor = TextHint,
                        focusedContainerColor = White,
                        unfocusedContainerColor = White,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    )
                )
            }

            // ── Jumlah dokter ────────────────────────────────────────────────
            item {
                Text(
                    text = "${filteredDoctors.size} dokter ditemukan",
                    fontSize = 13.sp,
                    color = TextSecondary,
                    modifier = Modifier.padding(horizontal = 2.dp, vertical = 4.dp)
                )
            }

            // ── List dokter ──────────────────────────────────────────────────
            items(filteredDoctors) { doctor ->
                DoctorCard(doctor = doctor, onClick = { onDoctorClick(doctor) })
            }

            // ── Empty state ──────────────────────────────────────────────────
            if (filteredDoctors.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("🔍", fontSize = 32.sp)
                            Spacer(Modifier.height(8.dp))
                            Text(
                                text = if (searchQuery.isNotBlank())
                                    "Tidak ditemukan dokter untuk \"$searchQuery\""
                                else
                                    "Tidak ada dokter tersedia.",
                                color = TextHint,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DoctorCard(doctor: Doctor, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFE3F2FD)),
                contentAlignment = Alignment.Center
            ) {
                Text("👨‍⚕️", fontSize = 22.sp)
            }

            Spacer(Modifier.width(14.dp))

            // Info
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = doctor.name,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    text = doctor.specialization,
                    fontSize = 13.sp,
                    color = TextSecondary
                )
                Spacer(Modifier.height(6.dp))
                // Jadwal pertama sebagai preview
                if (doctor.schedule.isNotEmpty()) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(5.dp)
                                .clip(CircleShape)
                                .background(PrimaryBlue)
                        )
                        Spacer(Modifier.width(6.dp))
                        Text(
                            text = doctor.schedule.first(),
                            fontSize = 12.sp,
                            color = TextHint
                        )
                    }
                }
            }

            Spacer(Modifier.width(8.dp))

            // Arrow chip
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = Color(0xFFE3F2FD)
            ) {
                Text(
                    text = "→",
                    color = PrimaryBlue,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun DoctorListScreenPreview() {
    val dummyDoctors = listOf(
        Doctor(1, "Dr. Andi Wijaya", "Dokter Umum", "Dokter yang akan membantu Anda menangani keluhan kesehatan umum.",
            listOf("Senin 08:00-12:00", "Selasa 10:00-14:00")),
        Doctor(2, "Dr. Budi Santoso", "Dokter Gigi", "Dokter yang menangani kesehatan gigi.",
            listOf("Senin 09:00-13:00", "Rabu 13:00-17:00")),
        Doctor(3, "Dr. Citra Dewi", "Dokter Anak", "Dokter yang menangani kesehatan anak.",
            listOf("Selasa 11:00-15:00", "Kamis 08:00-11:00")),
        Doctor(4, "Dr. Dani Kusuma", "Dokter Kulit", "Dokter yang menangani masalah kulit.",
            listOf("Rabu 10:00-14:00", "Jumat 10:00-13:00")),
        Doctor(5, "Dr. Eva Marlina", "Dokter Mata", "Dokter yang menangani kesehatan mata.",
            listOf("Senin 08:00-12:00", "Kamis 12:00-16:00"))
    )
    MaterialTheme {
        DoctorListScreen(doctors = dummyDoctors, onDoctorClick = {}, onBackClick = {})
    }
}