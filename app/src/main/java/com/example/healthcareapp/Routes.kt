package com.example.healthcareapp

import kotlinx.serialization.Serializable


@Serializable
sealed class Routes {
    @Serializable
    data object Landing : Routes()
    @Serializable
    data object Login : Routes()
    @Serializable
    data object Home : Routes()
    @Serializable
    data object DoctorList : Routes()
    @Serializable
    data class DoctorDetail(val doctorId: Int) : Routes()
    @Serializable
    data object FormAppointment : Routes()
    @Serializable
    data object AppointmentList : Routes()
    @Serializable
    data class AppointmentDetail(val id: Int) : Routes()
    @Serializable
    data object HistoryList : Routes()
    @Serializable
    data class HistoryDetail(val id: Int) : Routes()
    @Serializable
    data object Profile : Routes()
}