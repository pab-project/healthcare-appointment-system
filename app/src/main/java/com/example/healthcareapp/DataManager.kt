package com.example.healthcareapp

import androidx.compose.runtime.mutableStateListOf

object DataManager {
    val doctors = listOf(
        Doctor(
            1,
            "Dr. Andi Wijaya",
            "Dokter Umum",
            "Dokter yang berpengalaman dalam menangani berbagai keluhan kesehatan umum dengan pendekatan profesional dan ramah pasien.",
            listOf("Senin 08:00 - 12:00", "Selasa 10:00 - 14:00", "Rabu 08:00 - 12:00", "Kamis 12:00 - 16:00", "Jumat 08:00 - 11:00")
        ),
        Doctor(
            2,
            "Dr. Siti Rahma",
            "Dokter Gigi",
            "Spesialis kesehatan gigi dan mulut dengan pengalaman lebih dari 10 tahun.",
            listOf("Senin 09:00 - 13:00", "Rabu 13:00 - 17:00", "Jumat 09:00 - 12:00")
        ),
        Doctor(
            3,
            "Dr. Budi Santoso",
            "Dokter Anak",
            "Ahli kesehatan anak yang ramah dan telaten dalam melayani pasien cilik.",
            listOf("Selasa 08:00 - 12:00", "Kamis 08:00 - 12:00", "Sabtu 08:00 - 11:00")
        ),
        Doctor(
            4,
            "Dr. Diana Putri",
            "Dokter Kulit",
            "Spesialis dermatologi yang ahli dalam perawatan kesehatan kulit dan kecantikan.",
            listOf("Senin 14:00 - 18:00", "Rabu 14:00 - 18:00", "Kamis 14:00 - 18:00")
        ),
        Doctor(
            5,
            "Dr. Eka Pratama",
            "Dokter Mata",
            "Membantu Anda menjaga kesehatan penglihatan dengan teknologi terkini.",
            listOf("Selasa 13:00 - 16:00", "Jumat 13:00 - 16:00")
        )
    )

    val appointments = mutableStateListOf(
        Appointment(1, "Dr. Andi Wijaya", "Dokter Umum", "20 Apr 2026", "08:00 - 09:00", "Upcoming"),
        Appointment(2, "Dr. Siti Rahma", "Dokter Gigi", "22 Apr 2026", "09:30 - 10:30", "Upcoming"),
        Appointment(3, "Dr. Budi Santoso", "Dokter Anak", "10 Apr 2026", "08:00 - 12:00", "Completed")
    )

    val historyItems = listOf(
        HistoryItem(1, "Dr. Andi Wijaya", "Pemeriksaan Umum", "20 April 2026", "Selesai"),
        HistoryItem(2, "Dr. Siti Rahma", "Konsultasi Gigi", "18 April 2026", "Selesai"),
        HistoryItem(3, "Dr. Budi Santoso", "Cek Kesehatan", "15 April 2026", "Selesai")
    )

    fun addAppointment(patientName: String, doctorName: String, date: String, time: String, symptoms: String) {
        // Find poli from doctor name
        val doctor = doctors.find { it.name == doctorName }
        val poli = doctor?.specialization ?: "Umum"
        
        val newId = (appointments.maxByOrNull { it.id }?.id ?: 0) + 1
        appointments.add(
            Appointment(
                id = newId,
                doctor = doctorName, // Clean up name if it contains poli
                poli = poli,
                date = date,
                time = time,
                status = "Upcoming"
            )
        )
    }
}
