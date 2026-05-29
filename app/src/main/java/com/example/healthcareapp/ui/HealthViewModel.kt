package com.example.healthcareapp.ui

import androidx.lifecycle.ViewModel
import com.example.healthcareapp.data.HealthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HealthViewModel @Inject constructor(
    private val repository: HealthRepository
) : ViewModel() {
    val doctors = repository.doctors
    val appointments = repository.appointments
    val historyItems = repository.historyItems

    fun addAppointment(patientName: String, doctorName: String, date: String, time: String, symptoms: String) {
        repository.addAppointment(patientName, doctorName, date, time, symptoms)
    }
}
