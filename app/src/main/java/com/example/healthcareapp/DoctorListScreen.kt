package com.example.healthcareapp

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
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
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Daftar Dokter", color = Color.White, fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF1565C0)),
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Text("←", color = Color.White, fontSize = 24.sp)
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFFF5F7FA)),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(doctors) { doctor ->
                DoctorCard(doctor = doctor, onClick = { onDoctorClick(doctor) })
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
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = doctor.name,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1565C0)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = doctor.specialization,
                fontSize = 14.sp,
                color = Color(0xFF757575)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Lihat Detail →",
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF1976D2)
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun DoctorListScreenPreview() {

    val dummyDoctors = listOf(
        Doctor(
            1,
            "Dr. A",
            "Dokter Umum",
            "Dokter yang akan membantu Anda menangani keluhan kesehatan umum.",
            listOf("Senin 08:00-12:00", "Selasa 10:00-14:00", "Rabu 08:00-12:00", "Kamis 12:00-16:00", "Jumat 08:00-11:00")
        ),
        Doctor(
            2,
            "Dr. B",
            "Dokter Gigi",
            "Dokter yang menangani kesehatan gigi.",
            listOf("Senin 09:00-13:00", "Selasa 09:00-13:00", "Rabu 13:00-17:00", "Kamis 09:00-13:00", "Jumat 09:00-12:00")
        ),
        Doctor(
            3,
            "Dr. C",
            "Dokter Anak",
            "Dokter yang menangani kesehatan anak.",
            listOf("Senin 08:00-11:00", "Selasa 11:00-15:00", "Rabu 08:00-11:00", "Kamis 11:00-15:00", "Jumat 08:00-11:00")
        ),
        Doctor(
            4,
            "Dr. D",
            "Dokter Kulit",
            "Dokter yang menangani masalah kulit.",
            listOf("Senin 10:00-14:00", "Selasa 10:00-14:00", "Rabu 14:00-18:00", "Kamis 10:00-14:00", "Jumat 10:00-13:00")
        ),
        Doctor(
            5,
            "Dr. E",
            "Dokter Mata",
            "Dokter yang menangani kesehatan mata.",
            listOf("Senin 08:00-12:00", "Selasa 08:00-12:00", "Rabu 12:00-16:00", "Kamis 08:00-12:00", "Jumat 08:00-11:00")
        )
    )

    MaterialTheme {
        DoctorListScreen(
            doctors = dummyDoctors,
            onDoctorClick = {},
            onBackClick = {}
        )
    }
}
