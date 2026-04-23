package com.example.healthcareapp

import kotlinx.serialization.Serializable

@Serializable
data class HistoryItem(

    val id: Int,
    val doctorName: String,
    val service: String,
    val date: String,
    val status: String
)
