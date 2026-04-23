package com.example.healthcareapp

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun ProfileScreen(
    patient: Patient,
    onEditProfileClick: () -> Unit = {},
    onMedicalRecordClick: () -> Unit = {},
    onAppointmentClick: () -> Unit = {},
    onLogoutClick: () -> Unit,
    onBackClick: () -> Unit
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
                .background(Color(0xFF2E7D32))
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(shape = CircleShape, color = Color.White) {
                Box(Modifier.size(80.dp), contentAlignment = Alignment.Center) {
                    Text("👤", fontSize = 40.sp)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(patient.name, color = Color.White, fontWeight = FontWeight.Bold)
            Text(patient.email, color = Color.White.copy(0.7f))
        }

        // INFO
        ProfileCard("🧾 Informasi") {
            Info("Nama", patient.name)
            Info("Email", patient.email)
            Info("No HP", patient.phone)
            Info("Gender", patient.gender)
            Info("TTL", patient.birthDate)
            Info("Alamat", patient.address)
        }

        // MENU
        ProfileCard("⚡ Menu") {
            ActionBtn("✏️ Edit Profil", onEditProfileClick)
            ActionBtn("📜 Rekam Medis", onMedicalRecordClick)
            ActionBtn("📅 List Appointment", onAppointmentClick)
            ActionBtn("🚪 Logout", onLogoutClick)

            Spacer(modifier = Modifier.height(8.dp))

            Button(onClick = onBackClick, modifier = Modifier.fillMaxWidth()) {
                Text("← Back")
            }
        }
    }
}

@Composable
fun ProfileCard(title: String, content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(title, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            content()
        }
    }
}

@Composable
fun Info(label: String, value: String) {
    Column(modifier = Modifier.padding(bottom = 8.dp)) {
        Text(label, fontSize = 12.sp, color = Color.Gray)
        Text(value, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun ActionBtn(text: String, onClick: () -> Unit) {
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
fun ProfilePreview() {
    val dummy = Patient(
        1, "Berly", "berly@mail.com", "08123", "Laki-laki", "2004", "Klaten"
    )

    ProfileScreen(
        patient = dummy,
        onEditProfileClick = {},
        onMedicalRecordClick = {},
        onAppointmentClick = {},
        onLogoutClick = {},
        onBackClick = {}
    )
}