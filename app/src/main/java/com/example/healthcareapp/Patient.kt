package com.example.healthcareapp

import kotlinx.serialization.Serializable

@Serializable
data class Patient(

    val id: Int,
    val name: String,
    val email: String,
    val phone: String,
    val gender: String,
    val birthDate: String,
    val address: String
)
