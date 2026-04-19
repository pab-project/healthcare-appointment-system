package com.example.healthcareapp
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun LandingPage(
    onLoginClick: () -> Unit,
    onRegisterClick: () -> Unit,
    onDoctorListClick: () -> Unit,
    onAppointmentClick: () -> Unit,
    onScheduleClick: () -> Unit,
    onAdminClick: () -> Unit,
    onFindHospital: () -> Unit,
    onEmergencyCall: () -> Unit,
    isAdmin: Boolean = false
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
                .background(Color(0xFF1976D2))
                .padding(24.dp)
        ) {
            Text("🏥", fontSize = 40.sp)
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Healthcare App",
                color = Color.White,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Appointment & Medical Record System",
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 14.sp
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // INFO CARD
        CardSection(title = "📘 App Information") {
            Text(
                text = "Aplikasi ini digunakan untuk melakukan booking appointment dengan dokter, melihat jadwal, serta mengakses riwayat medis pasien.",
                fontSize = 14.sp,
                color = Color.Gray
            )
        }

        // QUICK ACTION
        CardSection(title = "🚀 Quick Actions") {

            ActionButton("🔐 Login/Register", onLoginClick)
            ActionButton("👨‍⚕️ List Doctor", onDoctorListClick)
            ActionButton("📅 Book Appointment", onAppointmentClick)
            ActionButton("🕒 My Schedule", onScheduleClick)

            if (isAdmin) {
                ActionButton("🛠 Admin Dashboard", onAdminClick)
            }
        }

        // HEALTHCARE FEATURE
        CardSection(title = "🏥 Healthcare Feature") {

            ActionButton(
                text = "🗺️ Cari Rumah Sakit",
                color = Color(0xFF2196F3),
                onClick = onFindHospital
            )

            ActionButton(
                text = "🚨 Emergency Call (119)",
                color = Color(0xFFD32F2F),
                onClick = onEmergencyCall
            )
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun ActionButton(
    text: String,
    onClick: () -> Unit,
    color: Color = Color(0xFF1976D2)
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 10.dp),
        colors = ButtonDefaults.buttonColors(containerColor = color),
        shape = RoundedCornerShape(12.dp)
    ) {
        Text(text = text, color = Color.White)
    }
}

@Composable
fun CardSection(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1976D2),
                fontSize = 16.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            content()
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LandingPagePreview() {
    LandingPage(
        onLoginClick = {},
        onRegisterClick = {},
        onDoctorListClick = {},
        onAppointmentClick = {},
        onScheduleClick = {},
        onAdminClick = {},
        onFindHospital = {},
        onEmergencyCall = {},
        isAdmin = true
    )
}