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

@Composable
fun AppointmentListScreen() {

    var selectedTab by remember { mutableStateOf("Upcoming") }

    val upcomingList = listOf(
        "dr. Budi (Poli Umum) - 20 Apr 2026 - 09:00",
        "dr. Andi (Penyakit Dalam) - 22 Apr 2026 - 11:00"
    )

    val completedList = listOf(
        "dr. Citra (Poli Gigi) - 10 Apr 2026 - 10:00"
    )

    val data = if (selectedTab == "Upcoming") upcomingList else completedList

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
            Text(
                text = "Jadwal Appointment",
                fontSize = 24.sp,
                color = TextPrimary
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Tab Filter
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

            // List Appointment
            LazyColumn {
                items(data) { item ->
                    AppointmentCard(
                        data = item,
                        isUpcoming = selectedTab == "Upcoming"
                    )
                }
            }
        }

        // Floating Button
        FloatingActionButton(
            onClick = {
                // navController.navigate("FormAppointment") // navController is unresolved
            },
            containerColor = PrimaryBlue,
            contentColor = White,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Text("+")
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
fun AppointmentCard(data: String, isUpcoming: Boolean) {

    val statusColor = if (isUpcoming) AccentGreen else TextSecondary
    val statusText = if (isUpcoming) "Upcoming" else "Completed"

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        colors = CardDefaults.cardColors(containerColor = White),
        shape = RoundedCornerShape(12.dp)
    ) {

        Column(modifier = Modifier.padding(12.dp)) {

            Text(
                text = data,
                color = TextPrimary,
                fontSize = 16.sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = statusText,
                color = statusColor,
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row {
                TextButton(onClick = { }) {
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