package com.example.healthcareapp

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun AppointmentListScreen(
    appointments: List<Appointment>,
    onBackClick: () -> Unit,
    onAddAppointmentClick: () -> Unit,
    onDetailClick: (Appointment) -> Unit
) {
    // Tiga tab status: Pending, Aktif (Upcoming/Approved), Selesai (Completed/Cancelled/Rejected)
    var selectedTab by remember { mutableStateOf("Pending") }

    val data = appointments.filter { appt ->
        when (selectedTab) {
            "Pending" -> appt.status.equals("Pending", ignoreCase = true)
            "Aktif" -> appt.status.equals("Upcoming", ignoreCase = true) || appt.status.equals("Approved", ignoreCase = true)
            else -> appt.status.equals("Completed", ignoreCase = true) || 
                    appt.status.equals("Cancelled", ignoreCase = true) || 
                    appt.status.equals("Rejected", ignoreCase = true)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppBackground)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                IconButton(onClick = onBackClick) {
                    Text("←", fontSize = 24.sp, color = TextPrimary, fontWeight = FontWeight.Bold)
                }
                Text(
                    text = "Jadwal Janji Temu",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }

            // Tabs Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(White, shape = RoundedCornerShape(12.dp))
                    .padding(4.dp)
            ) {
                TabButton(
                    text = "Persetujuan",
                    isSelected = selectedTab == "Pending",
                    onClick = { selectedTab = "Pending" },
                    modifier = Modifier.weight(1f)
                )
                TabButton(
                    text = "Aktif",
                    isSelected = selectedTab == "Aktif",
                    onClick = { selectedTab = "Aktif" },
                    modifier = Modifier.weight(1f)
                )
                TabButton(
                    text = "Riwayat",
                    isSelected = selectedTab == "Riwayat",
                    onClick = { selectedTab = "Riwayat" },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Appointments List
            if (data.isEmpty()) {
                Box(
                    modifier = Modifier.weight(1f).fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Tidak ada janji temu pada kategori ini.",
                        color = TextSecondary,
                        fontSize = 14.sp
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(data) { item ->
                        AppointmentCard(
                            appointment = item,
                            onDetailClick = { onDetailClick(item) }
                        )
                    }
                }
            }
        }

        // FAB Tambah Janji Temu
        FloatingActionButton(
            onClick = onAddAppointmentClick,
            containerColor = PrimaryBlue,
            contentColor = White,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text("+", fontSize = 24.sp, fontWeight = FontWeight.Bold)
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
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(if (isSelected) PrimaryBlue else White)
            .clickable { onClick() }
            .padding(vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp,
            color = if (isSelected) White else TextSecondary
        )
    }
}

@Composable
fun AppointmentCard(
    appointment: Appointment,
    onDetailClick: () -> Unit
) {
    val statusColor = when (appointment.status.lowercase()) {
        "pending" -> Color(0xFFE65100) // Orange
        "upcoming", "approved" -> AccentGreen
        "completed" -> TextSecondary
        else -> AccentRed // cancelled / rejected
    }

    val statusBg = when (appointment.status.lowercase()) {
        "pending" -> Color(0xFFFFECE0)
        "upcoming", "approved" -> Color(0xFFE8F5E9)
        "completed" -> Color(0xFFF5F5F5)
        else -> Color(0xFFFFEBEE)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = White),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = appointment.doctor,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    fontSize = 16.sp
                )
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = statusBg
                ) {
                    Text(
                        text = appointment.status,
                        color = statusColor,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Spesialisasi: ${appointment.poli}",
                color = TextSecondary,
                fontSize = 13.sp
            )
            
            Text(
                text = "Waktu: ${appointment.date} - ${appointment.time}",
                color = TextSecondary,
                fontSize = 13.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = onDetailClick) {
                    Text("Detail Info →", color = PrimaryBlue, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
            }
        }
    }
}