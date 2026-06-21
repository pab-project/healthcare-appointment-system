package com.example.healthcareapp

import kotlinx.serialization.Serializable

@Serializable
data class HistoryItem(
    val id: Int,
    val doctorName: String,
    val service: String,
    val date: String,
    val status: String,
    val diagnosis: String? = null,
    val treatment: String? = null,
    val medications: List<String>? = null,
    val notes: String? = null
)
