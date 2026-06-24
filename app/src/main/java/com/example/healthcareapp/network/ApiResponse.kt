package com.example.healthcareapp.network

import com.google.gson.annotations.SerializedName

data class UserResponse(
    val id: Int,
    val name: String,
    val email: String,
    val role: String,
    @SerializedName("is_active") val isActive: Boolean,
    @SerializedName("created_at") val createdAt: String?
)

data class LoginResponse(
    val status: String,
    val user: UserResponse?,
    val role: String?,
    val token: String?,
    val message: String?
)

data class RegisterResponse(
    val status: String,
    val user: UserResponse?,
    val token: String?,
    val message: String?
)

data class GenericResponse(
    val status: String,
    val message: String?
)

data class DoctorResponse(
    val id: Int,
    @SerializedName("user_id") val userId: Int,
    val name: String,
    val email: String,
    val specialization: String,
    val phone: String?,
    val bio: String?,
    @SerializedName("is_active") val isActive: Boolean,
    val schedule: List<String>? = null
)

data class DoctorListResponse(
    val data: List<DoctorResponse>
)

data class DoctorDetailResponse(
    val status: String,
    val data: DoctorResponse
)

data class TimeSlotResponse(
    val id: Int,
    @SerializedName("doctor_id") val doctorId: Int,
    val date: String,
    @SerializedName("start_time") val startTime: String,
    @SerializedName("end_time") val endTime: String,
    @SerializedName("is_booked") val isBooked: Boolean
)

data class TimeSlotListResponse(
    val status: String,
    val data: List<TimeSlotResponse>
)

data class SingleTimeSlotResponse(
    val status: String,
    val message: String?,
    val data: TimeSlotResponse?
)

data class PatientResponse(
    val id: Int,
    @SerializedName("user_id") val userId: Int,
    val name: String,
    val email: String,
    @SerializedName("date_of_birth") val dateOfBirth: String?,
    val gender: String?,
    val address: String?,
    val phone: String?,
    @SerializedName("is_active") val isActive: Boolean
)

data class PatientProfileResponse(
    val status: String,
    val data: PatientResponse
)

data class PatientListResponse(
    val data: List<PatientResponse>
)

data class AppointmentResponse(
    val id: Int,
    val status: String,
    val notes: String?,
    val patient: PatientResponse?,
    val doctor: DoctorResponse?,
    @SerializedName("time_slot") val timeSlot: TimeSlotResponse?,
    @SerializedName("created_at") val createdAt: String?
)

data class AppointmentListResponse(
    val data: List<AppointmentResponse>
)

data class AppointmentDetailResponse(
    val status: String,
    val data: AppointmentResponse
)

data class MedicalRecordResponse(
    val id: Int,
    @SerializedName("patient_id") val patientId: Int,
    @SerializedName("doctor_id") val doctorId: Int,
    val diagnosis: String,
    val treatment: String,
    val medications: List<String>?,
    val notes: String?,
    val patient: PatientResponse?,
    val doctor: DoctorResponse?,
    @SerializedName("created_at") val createdAt: String?
)


data class MedicalRecordListResponse(
    val data: List<MedicalRecordResponse>
)
