package com.example.healthcareapp

import kotlinx.serialization.Serializable

/**
 * Data class untuk menyimpan informasi janji temu (appointment).
 *
 * @param id ID unik appointment
 * @param doctor Nama dokter
 * @param poli Spesialisasi/poli dokter
 * @param date Tanggal appointment
 * @param time Waktu appointment
 * @param status Status: "Upcoming" atau "Completed"
 * @param patientName Nama pasien yang membuat appointment
 * @param patientEmail Email pasien (untuk filter per user)
 */
@Serializable
data class Appointment(
    val id: Int,
    val doctor: String,
    val poli: String,
    val date: String,
    val time: String,
    val status: String,
    val patientName: String = "",
    val patientEmail: String = ""
)