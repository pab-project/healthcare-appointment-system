package com.example.healthcareapp

object DoctorDummy {
    val doctors = listOf(
        Doctor(
            "Dr. A",
            "Dokter Umum",
            "Dokter yang akan membantu Anda menangani keluhan kesehatan umum.",
            listOf("Senin 08:00-12:00", "Selasa 10:00-14:00", "Rabu 08:00-12:00", "Kamis 12:00-16:00", "Jumat 08:00-11:00")
        ),
        Doctor(
            "Dr. B",
            "Dokter Gigi",
            "Dokter yang menangani kesehatan gigi.",
            listOf("Senin 09:00-13:00", "Selasa 09:00-13:00", "Rabu 13:00-17:00", "Kamis 09:00-13:00", "Jumat 09:00-12:00")
        ),
        Doctor(
            "Dr. C",
            "Dokter Anak",
            "Dokter yang menangani kesehatan anak.",
            listOf("Senin 08:00-11:00", "Selasa 11:00-15:00", "Rabu 08:00-11:00", "Kamis 11:00-15:00", "Jumat 08:00-11:00")
        ),
        Doctor(
            "Dr. D",
            "Dokter Kulit",
            "Dokter yang menangani masalah kulit.",
            listOf("Senin 10:00-14:00", "Selasa 10:00-14:00", "Rabu 14:00-18:00", "Kamis 10:00-14:00", "Jumat 10:00-13:00")
        ),
        Doctor(
            "Dr. E",
            "Dokter Mata",
            "Dokter yang menangani kesehatan mata.",
            listOf("Senin 08:00-12:00", "Selasa 08:00-12:00", "Rabu 12:00-16:00", "Kamis 08:00-12:00", "Jumat 08:00-11:00")
        )
    )
}