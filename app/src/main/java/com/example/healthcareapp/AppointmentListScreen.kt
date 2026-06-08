package com.example.healthcareapp

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.HorizontalDivider

@Composable
fun AppointmentListScreen(
    appointments: List<Appointment>,
    onBackClick: () -> Unit,
    onAddAppointmentClick: () -> Unit,
    onDetailClick: (Appointment) -> Unit,
    onRescheduleClick: (Appointment) -> Unit,
    onCancelClick: (Appointment) -> Unit
) {

    var selectedTab by remember { mutableStateOf("Upcoming") }

    val filteredAppointments = appointments.filter { appt ->
        when (selectedTab) {
            "Upcoming" -> appt.status.equals("Upcoming", true)

            else ->
                appt.status.equals("Completed", true)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppBackground)
    ) {

        Column(
            modifier = Modifier.fillMaxSize()
        ) {

            // HEADER
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                Color(0xFF1565C0),
                                Color(0xFF0D47A1)
                            )
                        )
                    )
                    .padding(top = 48.dp, bottom = 30.dp)
            ) {

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    IconButton(onClick = onBackClick) {
                        Text(
                            "←",
                            color = White,
                            fontSize = 24.sp
                        )
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    Column {

                        Text(
                            "Appointment",
                            color = White,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            "${appointments.size} Jadwal Terdaftar",
                            color = White.copy(alpha = 0.8f),
                            fontSize = 13.sp
                        )
                    }
                }
            }

            // TAB
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .background(
                        White,
                        RoundedCornerShape(16.dp)
                    )
                    .padding(4.dp)
            ) {

                TabButton(
                    text = "Upcoming",
                    isSelected = selectedTab == "Upcoming",
                    onClick = { selectedTab = "Upcoming" },
                    modifier = Modifier.weight(1f)
                )

                TabButton(
                    text = "Completed",
                    isSelected = selectedTab == "Completed",
                    onClick = { selectedTab = "Completed" },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (filteredAppointments.isEmpty()) {

                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.Center
                ) {

                    Text(
                        "Belum ada data appointment",
                        color = TextSecondary
                    )
                }

            } else {

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {

                    items(filteredAppointments) { appointment ->

                        AppointmentCard(
                            appointment = appointment,
                            onDetailClick = { onDetailClick(appointment) },
                            onRescheduleClick = { onRescheduleClick(appointment) },
                            onCancelClick = { onCancelClick(appointment) }
                        )
                    }

                    item {
                        Spacer(modifier = Modifier.height(90.dp))
                    }
                }
            }
        }

        FloatingActionButton(
            onClick = onAddAppointmentClick,
            containerColor = PrimaryBlue,
            contentColor = White,
            shape = RoundedCornerShape(18.dp),
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(20.dp)
        ) {

            Text(
                "+",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun TabButton(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {

    Surface(
        modifier = modifier
            .clickable { onClick() },
        color = if (isSelected)
            PrimaryBlue
        else
            Color.Transparent,
        shape = RoundedCornerShape(12.dp)
    ) {

        Box(
            modifier = Modifier.padding(vertical = 12.dp),
            contentAlignment = Alignment.Center
        ) {

            Text(
                text = text,
                color = if (isSelected)
                    White
                else
                    TextSecondary,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        }
    }
}

@Composable
fun AppointmentCard(
    appointment: Appointment,
    onDetailClick: () -> Unit,
    onRescheduleClick: () -> Unit,
    onCancelClick: () -> Unit
) {

    val isUpcoming = appointment.status.equals("Upcoming", true)

    val statusColor =
        if (isUpcoming) AccentGreen
        else TextSecondary

    val statusBackground =
        if (isUpcoming)
            Color(0xFFE8F5E9)
        else
            Color(0xFFF1F3F4)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        )
    ) {

        Column(
            modifier = Modifier.padding(18.dp)
        ) {

            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {

                Column(
                    modifier = Modifier.weight(1f)
                ) {

                    Text(
                        text = appointment.doctor,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = appointment.poli,
                        fontSize = 13.sp,
                        color = TextSecondary
                    )
                }

                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = statusBackground
                ) {

                    Text(
                        text = appointment.status,
                        modifier = Modifier.padding(
                            horizontal = 10.dp,
                            vertical = 5.dp
                        ),
                        color = statusColor,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            HorizontalDivider(
                color = Color(0xFFE0E0E0)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Date
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("📅")
                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = appointment.date,
                    color = TextPrimary,
                    fontSize = 14.sp
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Time
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("🕒")
                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = appointment.time,
                    color = TextPrimary,
                    fontSize = 14.sp
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Upcoming Appointment
            if (isUpcoming) {

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {

                    OutlinedButton(
                        onClick = onDetailClick,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Detail")
                    }

                    OutlinedButton(
                        onClick = onRescheduleClick,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Reschedule")
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = onCancelClick,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AccentRed
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        "Batalkan Appointment",
                        color = White
                    )
                }

            } else {

                Button(
                    onClick = onDetailClick,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PrimaryBlue
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        "Lihat Detail",
                        color = White
                    )
                }
            }
        }
    }
}

@Preview(showSystemUi = true, showBackground = true)
@Composable
fun AppointmentListPreview() {

    val appointments = listOf(
        Appointment(
            id = 1,
            doctor = "Dr. Andi Wijaya",
            poli = "Penyakit Dalam",
            date = "12 Juni 2026",
            time = "09:00",
            status = "Upcoming",
            patientName = "Ardina",
            patientEmail = "ardina@gmail.com"
        ),
        Appointment(
            id = 2,
            doctor = "Dr. Siti Rahma",
            poli = "Jantung",
            date = "5 Juni 2026",
            time = "14:00",
            status = "Completed",
            patientName = "Ardina",
            patientEmail = "ardina@gmail.com"
        )
    )

    HealthcareTheme {
        AppointmentListScreen(
            appointments = appointments,
            onBackClick = {},
            onAddAppointmentClick = {},
            onDetailClick = {},
            onRescheduleClick = {},
            onCancelClick = {}
        )
    }
}