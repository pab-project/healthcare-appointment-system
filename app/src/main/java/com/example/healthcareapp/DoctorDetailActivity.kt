package com.example.healthcareapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent

class DoctorDetailActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val name = intent.getStringExtra("NAME") ?: "Unknown"
        val specialization = intent.getStringExtra("SPECIALIZATION") ?: "-"
        val description = intent.getStringExtra("DESCRIPTION") ?: "-"
        val schedule = intent.getStringArrayListExtra("SCHEDULE") ?: arrayListOf("-")

        val doctor = Doctor(name, specialization, description, schedule)

        setContent {
            HealthcareTheme {
                DoctorDetailScreen(
                    doctor = doctor,
                    onBackClick = { finish() }
                )
            }
        }
    }
}