package com.example.healthcareapp

import android.content.Context
import androidx.compose.runtime.mutableStateListOf
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

/**
 * =============================================================
 * DataManager.kt
 * =============================================================
 * Object singleton yang mengelola semua data dalam aplikasi.
 *
 * Menggunakan Jetpack DataStore untuk persistensi data secara lokal
 * agar perubahan CRUD Dokter/Pasien/Appointment tidak hilang setelah restart.
 * =============================================================
 */
object DataManager {

    private var repository: UserPreferencesRepository? = null

    fun init(context: Context) {
        repository = UserPreferencesRepository(context)
    }

    fun getRepository(): UserPreferencesRepository? = repository

    // ==================== DEFAULT PRESET DATA ====================
    private val defaultUsers = listOf(
        User(
            email = "admin@healthcare.com",
            password = "admin123",
            name = "Administrator",
            role = UserRole.ADMIN
        ),
        User(
            email = "andi@healthcare.com",
            password = "dokter123",
            name = "Dr. Andi Wijaya",
            role = UserRole.DOCTOR,
            doctorId = 1
        ),
        User(
            email = "siti@healthcare.com",
            password = "dokter123",
            name = "Dr. Siti Rahma",
            role = UserRole.DOCTOR,
            doctorId = 2
        ),
        User(
            email = "berly@healthcare.com",
            password = "pasien123",
            name = "Berly Marcellino",
            role = UserRole.PATIENT
        )
    )

    private val defaultDoctors = listOf(
        Doctor(
            1,
            "Dr. Andi Wijaya",
            "Dokter Umum",
            "Dokter yang berpengalaman dalam menangani berbagai keluhan kesehatan umum dengan pendekatan profesional dan ramah pasien.",
            listOf("Senin 08:00 - 12:00", "Selasa 10:00 - 14:00", "Rabu 08:00 - 12:00", "Kamis 12:00 - 16:00", "Jumat 08:00 - 11:00")
        ),
        Doctor(
            2,
            "Dr. Siti Rahma",
            "Dokter Gigi",
            "Spesialis kesehatan gigi dan mulut dengan pengalaman lebih dari 10 tahun.",
            listOf("Senin 09:00 - 13:00", "Rabu 13:00 - 17:00", "Jumat 09:00 - 12:00")
        ),
        Doctor(
            3,
            "Dr. Budi Santoso",
            "Dokter Anak",
            "Ahli kesehatan anak yang ramah dan telaten dalam melayani pasien cilik.",
            listOf("Selasa 08:00 - 12:00", "Kamis 08:00 - 12:00", "Sabtu 08:00 - 11:00")
        ),
        Doctor(
            4,
            "Dr. Diana Putri",
            "Dokter Kulit",
            "Spesialis dermatologi yang ahli dalam perawatan kesehatan kulit dan kecantikan.",
            listOf("Senin 14:00 - 18:00", "Rabu 14:00 - 18:00", "Kamis 14:00 - 18:00")
        ),
        Doctor(
            5,
            "Dr. Eka Pratama",
            "Dokter Mata",
            "Membantu Anda menjaga kesehatan penglihatan dengan teknologi terkini.",
            listOf("Selasa 13:00 - 16:00", "Jumat 13:00 - 16:00")
        )
    )

    private val defaultPatients = listOf(
        Patient(
            id = 1,
            name = "Berly Marcellino",
            email = "berly@healthcare.com",
            phone = "08123456789",
            gender = "Laki-laki",
            birthDate = "2004-01-01",
            address = "Klaten, Indonesia"
        )
    )

    // ==================== DYNAMIC DATA LISTS ====================
    val users = mutableStateListOf<User>()
    val doctors = mutableStateListOf<Doctor>()
    val patients = mutableStateListOf<Patient>()
    val appointments = mutableStateListOf<Appointment>()

    // ==================== LOAD & PERSIST DATA ====================
    suspend fun loadAppointments() {
        val repo = repository ?: return

        // 1. Load Users
        val savedUsers = repo.users.first()
        users.clear()
        if (savedUsers.isNotEmpty()) {
            users.addAll(savedUsers)
        } else {
            users.addAll(defaultUsers)
            persistUsers()
        }

        // 2. Load Doctors
        val savedDoctors = repo.doctors.first()
        doctors.clear()
        if (savedDoctors.isNotEmpty()) {
            doctors.addAll(savedDoctors)
        } else {
            doctors.addAll(defaultDoctors)
            persistDoctors()
        }

        // 3. Load Patients
        val savedPatients = repo.patients.first()
        patients.clear()
        if (savedPatients.isNotEmpty()) {
            patients.addAll(savedPatients)
        } else {
            patients.addAll(defaultPatients)
            persistPatients()
        }

        // 4. Load Appointments
        val savedAppointments = repo.appointments.first()
        appointments.clear()
        if (savedAppointments.isNotEmpty()) {
            appointments.addAll(savedAppointments)
        } else {
            appointments.addAll(
                listOf(
                    Appointment(1, "Dr. Andi Wijaya", "Dokter Umum", "20/04/2026", "08:00 - 09:00", "Upcoming", "Berly Marcellino", "berly@healthcare.com"),
                    Appointment(2, "Dr. Siti Rahma", "Dokter Gigi", "22/04/2026", "09:30 - 10:30", "Upcoming", "Berly Marcellino", "berly@healthcare.com"),
                    Appointment(3, "Dr. Budi Santoso", "Dokter Anak", "10/04/2026", "08:00 - 12:00", "Completed", "Berly Marcellino", "berly@healthcare.com")
                )
            )
            persistAppointments()
        }
    }

    suspend fun persistUsers() {
        repository?.saveUsers(users.toList())
    }

    suspend fun persistDoctors() {
        repository?.saveDoctors(doctors.toList())
    }

    suspend fun persistPatients() {
        repository?.savePatients(patients.toList())
    }

    private suspend fun persistAppointments() {
        repository?.saveAppointments(appointments.toList())
    }

    // ==================== AUTHENTICATION ====================
    fun authenticate(email: String, password: String): User? {
        return users.find {
            it.email.equals(email, ignoreCase = true) && it.password == password
        }
    }

    // ==================== CRUD DOCTOR ====================
    fun addDoctor(name: String, specialization: String, description: String, schedule: List<String>) {
        val newId = (doctors.maxByOrNull { it.id }?.id ?: 0) + 1
        val newDoc = Doctor(newId, name, specialization, description, schedule)
        doctors.add(newDoc)

        // Generate email unik untuk login dokter
        val email = name.lowercase().replace(" ", "").replace(".", "") + "@healthcare.com"
        users.add(User(email, "dokter123", name, UserRole.DOCTOR, newId))

        CoroutineScope(Dispatchers.IO).launch {
            persistDoctors()
            persistUsers()
        }
    }

    fun updateDoctor(id: Int, name: String, specialization: String, description: String, schedule: List<String>) {
        val index = doctors.indexOfFirst { it.id == id }
        if (index != -1) {
            val updatedDoc = Doctor(id, name, specialization, description, schedule)
            doctors[index] = updatedDoc

            // Update nama user akun terkait
            val userIndex = users.indexOfFirst { it.role == UserRole.DOCTOR && it.doctorId == id }
            if (userIndex != -1) {
                val oldUser = users[userIndex]
                users[userIndex] = oldUser.copy(name = name)
            }

            CoroutineScope(Dispatchers.IO).launch {
                persistDoctors()
                persistUsers()
            }
        }
    }

    fun deleteDoctor(id: Int) {
        doctors.removeAll { it.id == id }
        users.removeAll { it.role == UserRole.DOCTOR && it.doctorId == id }

        CoroutineScope(Dispatchers.IO).launch {
            persistDoctors()
            persistUsers()
        }
    }

    // ==================== CRUD PATIENT ====================
    fun addPatient(name: String, email: String, phone: String, gender: String, birthDate: String, address: String) {
        val newId = (patients.maxByOrNull { it.id }?.id ?: 0) + 1
        val newPatient = Patient(newId, name, email, phone, gender, birthDate, address)
        patients.add(newPatient)

        // Buat akun user
        users.add(User(email, "pasien123", name, UserRole.PATIENT))

        CoroutineScope(Dispatchers.IO).launch {
            persistPatients()
            persistUsers()
        }
    }

    fun updatePatient(id: Int, name: String, email: String, phone: String, gender: String, birthDate: String, address: String) {
        val index = patients.indexOfFirst { it.id == id }
        if (index != -1) {
            val oldPatient = patients[index]
            val updatedPatient = Patient(id, name, email, phone, gender, birthDate, address)
            patients[index] = updatedPatient

            // Update user akun terkait
            val userIndex = users.indexOfFirst { it.email.equals(oldPatient.email, ignoreCase = true) }
            if (userIndex != -1) {
                val oldUser = users[userIndex]
                users[userIndex] = oldUser.copy(name = name, email = email)
            }

            // Update nama/email pasien di janji temu
            appointments.forEachIndexed { apptIndex, appt ->
                if (appt.patientEmail.equals(oldPatient.email, ignoreCase = true)) {
                    appointments[apptIndex] = appt.copy(patientName = name, patientEmail = email)
                }
            }

            CoroutineScope(Dispatchers.IO).launch {
                persistPatients()
                persistUsers()
                persistAppointments()
            }
        }
    }

    fun deletePatient(id: Int) {
        val patientToDelete = patients.find { it.id == id }
        if (patientToDelete != null) {
            patients.removeAll { it.id == id }
            users.removeAll { it.email.equals(patientToDelete.email, ignoreCase = true) }
            appointments.removeAll { it.patientEmail.equals(patientToDelete.email, ignoreCase = true) }

            CoroutineScope(Dispatchers.IO).launch {
                persistPatients()
                persistUsers()
                persistAppointments()
            }
        }
    }

    // ==================== APPOINTMENT OPERATIONS ====================
    fun addAppointment(
        patientName: String,
        patientEmail: String,
        doctorName: String,
        date: String,
        time: String,
        symptoms: String
    ) {
        // Hilangkan keterangan spesialisasi dalam tanda kurung jika ada
        var cleanDoctorName = doctorName
        if (doctorName.contains("(")) {
            cleanDoctorName = doctorName.substringBefore("(").trim()
        }

        val doctor = doctors.find { it.name == cleanDoctorName }
        val poli = doctor?.specialization ?: "Umum"

        val newId = (appointments.maxByOrNull { it.id }?.id ?: 0) + 1
        appointments.add(
            Appointment(
                id = newId,
                doctor = cleanDoctorName,
                poli = poli,
                date = date,
                time = time,
                status = "Pending", // Default adalah Pending agar bisa di-Acc/Reject oleh Admin
                patientName = patientName,
                patientEmail = patientEmail
            )
        )

        CoroutineScope(Dispatchers.IO).launch {
            persistAppointments()
        }
    }

    fun updateAppointmentStatus(id: Int, status: String) {
        val index = appointments.indexOfFirst { it.id == id }
        if (index != -1) {
            appointments[index] = appointments[index].copy(status = status)
            CoroutineScope(Dispatchers.IO).launch {
                persistAppointments()
            }
        }
    }

    // ==================== HISTORY (DATA STATIS) ====================
    val historyItems = listOf(
        HistoryItem(1, "Dr. Andi Wijaya", "Pemeriksaan Umum", "20 April 2026", "Selesai"),
        HistoryItem(2, "Dr. Siti Rahma", "Konsultasi Gigi", "18 April 2026", "Selesai"),
        HistoryItem(3, "Dr. Budi Santoso", "Cek Kesehatan", "15 April 2026", "Selesai")
    )

    fun getAppointmentsForUser(email: String, role: UserRole): List<Appointment> {
        return when (role) {
            UserRole.ADMIN -> appointments.toList()
            UserRole.DOCTOR -> {
                val user = users.find { it.email == email }
                val doctor = user?.doctorId?.let { id -> doctors.find { it.id == id } }
                if (doctor != null) {
                    appointments.filter { it.doctor.equals(doctor.name, ignoreCase = true) }
                } else {
                    emptyList()
                }
            }
            UserRole.PATIENT -> {
                appointments.filter { it.patientEmail.equals(email, ignoreCase = true) }
            }
        }
    }
}
