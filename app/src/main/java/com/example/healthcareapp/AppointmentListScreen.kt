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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.healthcareapp.Appointment

@Composable
fun AppointmentListScreen(
    appointments: List<Appointment>,
    onBackClick: () -> Unit,
    onAddAppointmentClick: () -> Unit,
    onDetailClick: (Appointment) -> Unit
) {

    var selectedTab by remember { mutableStateOf("Upcoming") }

    val data = appointments.filter { 
        if (selectedTab == "Upcoming") it.status == "Upcoming" else it.status == "Completed" 
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
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBackClick) {
                    Text("←", fontSize = 24.sp, color = TextPrimary)
                }
                Text(
                    text = "Jadwal Appointment",
                    fontSize = 20.sp,
                    color = TextPrimary,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Tabs
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(White, shape = RoundedCornerShape(12.dp))
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

            Spacer(modifier = Modifier.height(16.dp))

            // List
            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                items(data) { item ->
                    AppointmentCard(
                        appointment = item,
                        isUpcoming = selectedTab == "Upcoming",
                        onDetailClick = { onDetailClick(item) }
                    )
                }
            }
        }

        // FAB
        FloatingActionButton(
            onClick = onAddAppointmentClick,
            containerColor = PrimaryBlue,
            contentColor = White,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Text("+", fontSize = 24.sp)
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
            .padding(vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = if (isSelected) White else TextSecondary
        )
    }
}

@Composable
fun AppointmentCard(
    appointment: Appointment,
    isUpcoming: Boolean,
    onDetailClick: () -> Unit
) {

    val statusColor = if (isUpcoming) AccentGreen else TextSecondary

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        colors = CardDefaults.cardColors(containerColor = White),
        shape = RoundedCornerShape(12.dp)
    ) {

        Column(modifier = Modifier.padding(12.dp)) {

            Text(
                text = "${appointment.doctor} (${appointment.poli})",
                color = TextPrimary,
                fontSize = 16.sp
            )

            Text(
                text = "${appointment.date} - ${appointment.time}",
                color = TextSecondary,
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = appointment.status,
                color = statusColor,
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row {

                TextButton(onClick = onDetailClick) {
                    Text("Detail", color = PrimaryBlue)
                }

                if (isUpcoming) {
                    TextButton(onClick = { }) {
                        Text("Reschedule", color = PrimaryBlue)
                    }

                    TextButton(onClick = { }) {
                        Text("Cancel", color = AccentRed)
                    }
                }
            }
        }
    }
}