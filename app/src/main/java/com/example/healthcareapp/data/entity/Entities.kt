package com.example.healthcareapp.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.healthcareapp.*

// ==================== USER ENTITY ====================
@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val email: String,
    val password: String,
    val name: String,
    val role: String, // "ADMIN", "DOCTOR", "PATIENT"
    val doctorId: Int? = null
) {
    fun toDomain(): User = User(
        email = email,
        password = password,
        name = name,
        role = UserRole.valueOf(role),
        doctorId = doctorId
    )

    companion object {
        fun fromDomain(user: User): UserEntity = UserEntity(
            email = user.email,
            password = user.password,
            name = user.name,
            role = user.role.name,
            doctorId = user.doctorId
        )
    }
}

// ==================== DOCTOR ENTITY ====================
@Entity(tableName = "doctors")
data class DoctorEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val specialization: String,
    val description: String,
    val schedule: String // Stored as comma-separated, e.g. "Senin 08:00 - 12:00|Rabu 13:00 - 17:00"
) {
    fun toDomain(): Doctor = Doctor(
        id = id,
        name = name,
        specialization = specialization,
        description = description,
        schedule = if (schedule.isBlank()) emptyList() else schedule.split("|")
    )

    companion object {
        fun fromDomain(doctor: Doctor): DoctorEntity = DoctorEntity(
            id = doctor.id,
            name = doctor.name,
            specialization = doctor.specialization,
            description = doctor.description,
            schedule = doctor.schedule.joinToString("|")
        )
    }
}

// ==================== PATIENT ENTITY ====================
@Entity(tableName = "patients")
data class PatientEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val email: String,
    val phone: String,
    val gender: String,
    val birthDate: String,
    val address: String
) {
    fun toDomain(): Patient = Patient(
        id = id,
        name = name,
        email = email,
        phone = phone,
        gender = gender,
        birthDate = birthDate,
        address = address
    )

    companion object {
        fun fromDomain(patient: Patient): PatientEntity = PatientEntity(
            id = patient.id,
            name = patient.name,
            email = patient.email,
            phone = patient.phone,
            gender = patient.gender,
            birthDate = patient.birthDate,
            address = patient.address
        )
    }
}

// ==================== APPOINTMENT ENTITY ====================
@Entity(tableName = "appointments")
data class AppointmentEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val doctor: String,
    val poli: String,
    val date: String,
    val time: String,
    val status: String,
    val patientName: String = "",
    val patientEmail: String = ""
) {
    fun toDomain(): Appointment = Appointment(
        id = id,
        doctor = doctor,
        poli = poli,
        date = date,
        time = time,
        status = status,
        patientName = patientName,
        patientEmail = patientEmail
    )

    companion object {
        fun fromDomain(appt: Appointment): AppointmentEntity = AppointmentEntity(
            id = appt.id,
            doctor = appt.doctor,
            poli = appt.poli,
            date = appt.date,
            time = appt.time,
            status = appt.status,
            patientName = appt.patientName,
            patientEmail = appt.patientEmail
        )
    }
}

// ==================== NOTIFICATION ENTITY ====================
@Entity(tableName = "notifications")
data class NotificationEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val message: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isRead: Boolean = false,
    val type: String = "INFO", // INFO, APPOINTMENT_CREATED, APPOINTMENT_APPROVED, APPOINTMENT_REJECTED
    val targetEmail: String = "" // notification recipient
)
