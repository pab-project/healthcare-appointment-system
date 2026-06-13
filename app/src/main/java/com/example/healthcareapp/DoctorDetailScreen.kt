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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DoctorDetailScreen(
    doctor: Doctor,
    onBackClick: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Detail Dokter",
                        color = White,
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = PrimaryBlue
                ),
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Text(
                            "←",
                            color = White,
                            fontSize = 24.sp
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
        ) {

            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
            ) {

                // HEADER
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.verticalGradient(
                                listOf(
                                    PrimaryBlue,
                                    Color(0xFF1976D2)
                                )
                            )
                        )
                        .padding(vertical = 32.dp),
                    contentAlignment = Alignment.Center
                ) {

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(horizontal = 24.dp)
                    ) {

                        Box(
                            modifier = Modifier
                                .size(90.dp)
                                .clip(CircleShape)
                                .background(
                                    White.copy(alpha = 0.2f)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "👨‍⚕️",
                                fontSize = 42.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = doctor.name,
                            color = White,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = White.copy(alpha = 0.2f)
                        ) {
                            Text(
                                text = doctor.specialization,
                                color = White,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(
                                    horizontal = 14.dp,
                                    vertical = 6.dp
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {

                            DoctorStat(
                                value = "⭐ 4.9",
                                label = "Rating"
                            )

                            DoctorStat(
                                value = "250+",
                                label = "Pasien"
                            )

                            DoctorStat(
                                value = "5+",
                                label = "Tahun"
                            )
                        }
                    }
                }

                // FLOATING CARD
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .offset(y = (-24).dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = White
                    ),
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 6.dp
                    )
                ) {

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Status",
                                color = TextHint,
                                fontSize = 12.sp
                            )

                            Text(
                                text = "Tersedia",
                                color = PrimaryBlue,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Jadwal",
                                color = TextHint,
                                fontSize = 12.sp
                            )

                            Text(
                                text = "${doctor.schedule.size} Hari",
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        }
                    }
                }

                Column(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .offset(y = (-12).dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {

                    // DESKRIPSI
                    Card(
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = White
                        )
                    ) {

                        Column(
                            modifier = Modifier.padding(18.dp)
                        ) {

                            Text(
                                text = "Tentang Dokter",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = TextPrimary
                            )

                            Spacer(
                                modifier = Modifier.height(12.dp)
                            )

                            HorizontalDivider(
                                color = AppBackground
                            )

                            Spacer(
                                modifier = Modifier.height(12.dp)
                            )

                            Text(
                                text = doctor.description,
                                color = TextSecondary,
                                lineHeight = 22.sp
                            )
                        }
                    }

                    // RATING CARD
                    Card(
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = White
                        )
                    ) {

                        Row(
                            modifier = Modifier.padding(18.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {

                            Text(
                                text = "⭐",
                                fontSize = 32.sp
                            )

                            Spacer(
                                modifier = Modifier.width(12.dp)
                            )

                            Column {

                                Text(
                                    text = "4.9 / 5.0",
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary,
                                    fontSize = 18.sp
                                )

                                Text(
                                    text = "250 ulasan pasien",
                                    color = TextSecondary,
                                    fontSize = 13.sp
                                )
                            }
                        }
                    }

                    // JADWAL
                    Card(
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = White
                        )
                    ) {

                        Column(
                            modifier = Modifier.padding(18.dp)
                        ) {

                            Text(
                                text = "Jadwal Praktik",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = TextPrimary
                            )

                            Spacer(
                                modifier = Modifier.height(14.dp)
                            )

                            doctor.schedule.forEach { time ->

                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(bottom = 10.dp),
                                    shape = RoundedCornerShape(14.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = PrimaryBlue.copy(alpha = 0.08f)
                                    )
                                ) {

                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(14.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {

                                        Box(
                                            modifier = Modifier
                                                .size(38.dp)
                                                .clip(CircleShape)
                                                .background(
                                                    PrimaryBlue.copy(alpha = 0.15f)
                                                ),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text("🕒")
                                        }

                                        Spacer(
                                            modifier = Modifier.width(12.dp)
                                        )

                                        Text(
                                            text = time,
                                            color = TextPrimary,
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(
                        modifier = Modifier.height(8.dp)
                    )
                }
            }

            // BUTTON
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(AppBackground)
                    .padding(16.dp)
            ) {

                Button(
                    onClick = onBackClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PrimaryBlue
                    )
                ) {

                    Text(
                        text = "← Kembali",
                        color = White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                }
            }
        }
    }
}

@Composable
fun DoctorStat(
    value: String,
    label: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            color = White,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = label,
            color = White.copy(alpha = 0.8f),
            fontSize = 12.sp
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun DoctorDetailScreenPreview() {

    val dummyDoctor = Doctor(
        id = 1,
        name = "Dr. Andi Wijaya",
        specialization = "Dokter Umum",
        description = "Dokter yang berpengalaman dalam menangani berbagai keluhan kesehatan umum dengan pendekatan profesional dan ramah pasien.",
        schedule = listOf(
            "Senin 08:00 - 12:00",
            "Selasa 10:00 - 14:00",
            "Rabu 08:00 - 12:00",
            "Kamis 12:00 - 16:00",
            "Jumat 08:00 - 11:00"
        )
    )

    MaterialTheme {
        DoctorDetailScreen(
            doctor = dummyDoctor,
            onBackClick = {}
        )
    }
}