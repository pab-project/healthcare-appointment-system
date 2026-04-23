package com.example.healthcareapp

import kotlinx.serialization.Serializable

@Serializable
data class Doctor(

    val id: Int,
    val name: String,
    val specialization: String,
    val description: String,
    val schedule: List<String>
)