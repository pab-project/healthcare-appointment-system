package com.example.healthcareapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent

class AppointmentActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Di sini kita panggil fungsi Compose dari file FormAppointment.kt tadi
        setContent {
            AppointmentScreen()
        }
    }
}