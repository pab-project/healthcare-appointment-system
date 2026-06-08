package com.example.healthcareapp

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.shape.RoundedCornerShape

@Composable
fun PatientDashboardScreen(
    userName: String = "Bety Marcellino"
) {
    val headerGradient = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF1565C0),
            Color(0xFF0D47A1)
        )
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppBackground)
            .verticalScroll(rememberScrollState())
    ) {

        // HEADER
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(headerGradient)
                .padding(top = 48.dp, bottom = 36.dp)
        ) {

            Column(
                modifier = Modifier.padding(24.dp)
            ) {

                Text(
                    text = "🏥",
                    fontSize = 48.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "HealthCare",
                    color = White,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "Welcome, $userName",
                    color = White.copy(alpha = 0.8f),
                    fontSize = 15.sp
                )
            }
        }

        // PROFILE
        SectionTitle("My Profile")

        FeatureCard(
            icon = "👤",
            title = "Lihat Profil"
        )

        Spacer(modifier = Modifier.height(12.dp))

        // HEALTHCARE FEATURE
        SectionTitle("Healthcare Feature")

        FeatureCard(
            icon = "🏥",
            title = "Cari Rumah Sakit"
        )

        FeatureCard(
            icon = "📞",
            title = "Emergency Call"
        )

        FeatureCard(
            icon = "📅",
            title = "Appointment"
        )

        FeatureCard(
            icon = "👨‍⚕️",
            title = "List Dokter"
        )

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun SectionTitle(title: String) {
    Text(
        text = title,
        fontSize = 20.sp,
        fontWeight = FontWeight.Bold,
        color = TextPrimary,
        modifier = Modifier.padding(
            start = 20.dp,
            top = 16.dp,
            bottom = 10.dp
        )
    )
}

@Composable
fun FeatureCard(
    icon: String,
    title: String,
    onClick: () -> Unit = {}
) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 6.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = White
        ),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text(
                text = icon,
                fontSize = 24.sp
            )

            Spacer(modifier = Modifier.width(16.dp))

            Text(
                text = title,
                modifier = Modifier.weight(1f),
                fontSize = 16.sp,
                color = TextPrimary
            )

            Text(
                text = "›",
                fontSize = 24.sp,
                color = TextSecondary
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun HomeScreenPreview() {
    HealthcareTheme {
        PatientDashboardScreen()
    }
}