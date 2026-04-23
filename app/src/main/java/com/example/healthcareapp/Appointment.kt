package com.example.healthcareapp

import kotlinx.serialization.Serializable

@Serializable
data class Appointment(

    val id: Int,
    val doctor: String,
    val poli: String,
    val date: String,
    val time: String,
    val status: String
)