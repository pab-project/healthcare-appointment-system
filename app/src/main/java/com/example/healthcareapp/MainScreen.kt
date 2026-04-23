package com.example.healthcareapp

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun MainScreen(
    isLoggedIn: Boolean,
    patient: Patient?,
    onLoginClick: () -> Unit,
    onProfileClick: () -> Unit,
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

        // HEADER
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF1565C0))
                .padding(24.dp)
        ) {
            Text("🏥", fontSize = 40.sp)
            Text("HealthCare", color = Color.White, fontSize = 26.sp, fontWeight = FontWeight.Bold)

            if (isLoggedIn && patient != null) {
                Text(
                    "Welcome, ${patient.name}",
                    color = Color.White.copy(alpha = 0.7f)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // AUTH SECTION
        if (!isLoggedIn) {
            MainCard("🔐 Authentication") {
                Button(
                    onClick = onLoginClick,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Login")
                }
            }
        } else {
            MainCard("👤 My Profile") {
                Button(
                    onClick = onProfileClick,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Lihat Profil")
                }
            }
        }

        // FEATURES
        MainCard("🏥 Healthcare Feature") {

            FeatureButton("🗺️ Cari Rumah Sakit", onFindHospitalClick)
            FeatureButton("🚨 Emergency Call", onEmergencyCallClick)

            Divider(modifier = Modifier.padding(vertical = 8.dp))

            FeatureButton("📅 Appointment", onAppointmentClick)
            FeatureButton("👨‍⚕️ List Dokter", onDoctorListClick)
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun MainCard(title: String, content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(title, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            content()
        }
    }
}

@Composable
fun FeatureButton(text: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp)
    ) {
        Text(text)
    }
}

@Preview(showBackground = true)
@Composable
fun MainPreview() {
    val dummy = Patient(
        1, "Berly", "berly@mail.com", "08123", "Laki-laki", "2004", "Klaten"
    )

    MainScreen(
        isLoggedIn = true,
        patient = dummy,
        onLoginClick = {},
        onProfileClick = {},
        onDoctorListClick = {},
        onAppointmentClick = {},
        onFindHospitalClick = {},
        onEmergencyCallClick = {}
    )
}