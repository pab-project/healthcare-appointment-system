package com.example.healthcareapp

import kotlinx.serialization.Serializable


@Serializable
sealed class Routes {

    // Tambahkan ini di dalam sealed class Routes
    @Serializable
    data object Register : Routes()

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
    @Serializable
    data object EditProfile : Routes()

    @Serializable
    data object AdminDashboard : Routes()
    @Serializable
    data object DoctorDashboard : Routes()
    @Serializable
    data object Notifications : Routes()
    @Serializable
    data object DoctorProfile : Routes()
    @Serializable
    data object DoctorEditProfile : Routes()
    @Serializable
    data class DoctorTimeslots(val doctorId: Int? = null, val doctorName: String? = null) : Routes()
    @Serializable
    data object ForgotPassword : Routes()
}