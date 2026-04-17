package com.example.healthcareapp

data class Doctor(
    val name: String,
    val specialization: String,
    val description: String,
    val schedule: List<String>
)