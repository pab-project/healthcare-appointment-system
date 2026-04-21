package com.example.healthcareapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent

class HistoryActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val historyList = listOf(
            HistoryItem("Dr. Sarah Johnson", "Konsultasi Umum", "12 Okt 2023", "Selesai"),
            HistoryItem("Dr. Michael Chen", "Cek Gigi", "05 Okt 2023", "Selesai"),
            HistoryItem("Klinik Sehat", "Cek Lab", "28 Sep 2023", "Selesai"),
            HistoryItem("Dr. Amanda", "Spesialis Anak", "15 Sep 2023", "Selesai")
        )

        setContent {
            HealthcareTheme {
                HistoryScreen(
                    items = historyList,
                    onBackClick = { finish() }
                )
            }
        }
    }
}
