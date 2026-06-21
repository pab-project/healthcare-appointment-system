package com.example.healthcareapp.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.healthcareapp.DataManager
import com.example.healthcareapp.UserRole
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HealthViewModel @Inject constructor() : ViewModel() {
    val doctors = DataManager.doctors
    val appointments = DataManager.appointments
    val historyItems = DataManager.historyItems
    val users = DataManager.users

    suspend fun loadAppointments() {
        DataManager.loadAppointments()
    }

    fun getAppointmentsForUser(email: String, role: UserRole) = DataManager.getAppointmentsForUser(email, role)

    suspend fun getDoctorSlots(doctorId: Int) = DataManager.getDoctorSlots(doctorId)

    fun addAppointment(patientName: String, patientEmail: String, doctorName: String, date: String, time: String, symptoms: String) {
        viewModelScope.launch {
            DataManager.addAppointment(patientName, patientEmail, doctorName, date, time, symptoms)
        }
    }

    fun rescheduleAppointment(id: Int, newDate: String, newTime: String) {
        viewModelScope.launch {
            DataManager.rescheduleAppointment(id, newDate, newTime)
        }
    }
}
