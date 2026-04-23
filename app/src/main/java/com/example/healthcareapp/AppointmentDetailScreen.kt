package com.example.healthcareapp

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import com.example.healthcareapp.Appointment
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun AppointmentDetailScreen(
    appointment: Appointment,
    onBackClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppBackground)
            .padding(16.dp)
    ) {

        Text("Detail Appointment", fontSize = 20.sp)

        Spacer(modifier = Modifier.height(16.dp))

        Text("Dokter: ${appointment.doctor}")
        Text("Poli: ${appointment.poli}")
        Text("Tanggal: ${appointment.date}")
        Text("Jam: ${appointment.time}")
        Text("Status: ${appointment.status}")

        Spacer(modifier = Modifier.height(24.dp))

        Button(onClick = onBackClick) {
            Text("Kembali")
        }
    }
}