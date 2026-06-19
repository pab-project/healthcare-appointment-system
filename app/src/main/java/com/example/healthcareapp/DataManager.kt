package com.example.healthcareapp

import android.content.Context
import androidx.compose.runtime.mutableStateListOf
import com.example.healthcareapp.data.AppDatabase
import com.example.healthcareapp.data.entity.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * =============================================================
 * DataManager.kt
 * =============================================================
 * Object singleton yang mengelola semua data dalam aplikasi.
 *
 * Menggunakan Room Database untuk penyimpanan data yang robust dan persisten.
 * =============================================================
 */
object DataManager {

    private var repository: UserPreferencesRepository? = null
    private var database: AppDatabase? = null
    private var appContext: Context? = null

    // Coroutine scope for running background DB tasks from synchronous UI actions
    private val scope = CoroutineScope(Dispatchers.Main)

    fun init(context: Context) {
        appContext = context.applicationContext
        repository = UserPreferencesRepository(context)
        database = AppDatabase.getInstance(context)
    }

    fun getRepository(): UserPreferencesRepository? = repository
    fun getDatabase(): AppDatabase? = database

    // ==================== DYNAMIC DATA LISTS ====================
    val users = mutableStateListOf<User>()
    val doctors = mutableStateListOf<Doctor>()
    val patients = mutableStateListOf<Patient>()
    val appointments = mutableStateListOf<Appointment>()
    val historyItems = mutableStateListOf<HistoryItem>()
    val notifications = mutableStateListOf<NotificationEntity>()

    // ==================== LOAD & PERSIST DATA ====================
    suspend fun loadAppointments() {
        val db = database ?: return

        withContext(Dispatchers.IO) {
            // Load Users
            val userEntities = db.userDao().getAllUsersOnce()
            withContext(Dispatchers.Main) {
                users.clear()
                users.addAll(userEntities.map { it.toDomain() })
            }

            // Load Doctors
            val doctorEntities = db.doctorDao().getAllDoctorsOnce()
            withContext(Dispatchers.Main) {
                doctors.clear()
                doctors.addAll(doctorEntities.map { it.toDomain() })
            }

            // Load Patients
            val patientEntities = db.patientDao().getAllPatientsOnce()
            withContext(Dispatchers.Main) {
                patients.clear()
                patients.addAll(patientEntities.map { it.toDomain() })
            }

            // Load Appointments
            val apptEntities = db.appointmentDao().getAllAppointmentsOnce()
            withContext(Dispatchers.Main) {
                appointments.clear()
                appointments.addAll(apptEntities.map { it.toDomain() })

                // Map "Completed" appointments to history items
                historyItems.clear()
                val completed = apptEntities.filter { it.status.equals("Completed", ignoreCase = true) }
                historyItems.addAll(completed.map {
                    HistoryItem(
                        id = it.id,
                        doctorName = it.doctor,
                        service = it.poli,
                        date = it.date,
                        status = "Selesai"
                    )
                })
            }
        }
    }

    // Helper method to reload lists from Database on the main thread after an update
    private fun reloadData() {
        scope.launch {
            loadAppointments()
        }
    }

    // ==================== AUTHENTICATION ====================
    fun authenticate(email: String, password: String): User? {
        val found = users.find {
            it.email.equals(email, ignoreCase = true) && it.password == password
        }
        return found
    }

    // ==================== CRUD DOCTOR ====================
    fun addDoctor(name: String, specialization: String, description: String, schedule: List<String>) {
        val db = database ?: return
        scope.launch(Dispatchers.IO) {
            // Insert Doctor
            val newDoctorEntity = DoctorEntity(
                name = name,
                specialization = specialization,
                description = description,
                schedule = schedule.joinToString("|")
            )
            val newId = db.doctorDao().insert(newDoctorEntity).toInt()

            // Generate user email and user entity for doctor login
            val email = name.lowercase().replace(" ", "").replace(".", "") + "@healthcare.com"
            val userEntity = UserEntity(
                email = email,
                password = "dokter123",
                name = name,
                role = "DOCTOR",
                doctorId = newId
            )
            db.userDao().insert(userEntity)

            // Send notification about new doctor
            addNotificationInternal(
                title = "Dokter Baru Bergabung! 🩺",
                message = "$name ($specialization) telah terdaftar di HealthCare.",
                type = "INFO",
                targetEmail = ""
            )

            // Reload memory cache
            reloadData()
        }
    }

    fun updateDoctor(id: Int, name: String, specialization: String, description: String, schedule: List<String>) {
        val db = database ?: return
        scope.launch(Dispatchers.IO) {
            val doctorEntity = DoctorEntity(
                id = id,
                name = name,
                specialization = specialization,
                description = description,
                schedule = schedule.joinToString("|")
            )
            db.doctorDao().update(doctorEntity)

            // Update user name for doctor
            val user = db.userDao().findDoctorUser(id)
            if (user != null) {
                db.userDao().update(user.copy(name = name))
            }

            reloadData()
        }
    }

    fun deleteDoctor(id: Int) {
        val db = database ?: return
        scope.launch(Dispatchers.IO) {
            db.doctorDao().deleteById(id)
            db.userDao().deleteDoctorUser(id)
            reloadData()
        }
    }

    // ==================== CRUD PATIENT ====================
    fun addPatient(
        name: String,
        email: String,
        phone: String,
        gender: String,
        birthDate: String,
        address: String,
        password: String = "pasien123",
        onComplete: (() -> Unit)? = null
    ) {
        val db = database ?: return
        scope.launch(Dispatchers.IO) {
            val patientEntity = PatientEntity(
                name = name,
                email = email,
                phone = phone,
                gender = gender,
                birthDate = birthDate,
                address = address
            )
            db.patientDao().insert(patientEntity)

            // Create patient account user
            val userEntity = UserEntity(
                email = email,
                password = password,
                name = name,
                role = "PATIENT"
            )
            db.userDao().insert(userEntity)

            // Welcome Notification
            addNotificationInternal(
                title = "Selamat Datang! 🎉",
                message = "Akun Anda dengan email $email berhasil dibuat. Silakan jadwalkan appointment pertama Anda.",
                type = "INFO",
                targetEmail = email
            )

            loadAppointments()

            withContext(Dispatchers.Main) {
                onComplete?.invoke()
            }
        }
    }

    fun updatePatient(id: Int, name: String, email: String, phone: String, gender: String, birthDate: String, address: String) {
        val db = database ?: return
        scope.launch(Dispatchers.IO) {
            val oldPatient = db.patientDao().findById(id)
            val updatedPatient = PatientEntity(id, name, email, phone, gender, birthDate, address)
            db.patientDao().update(updatedPatient)

            if (oldPatient != null) {
                // Update user email/name
                val user = db.userDao().findByEmail(oldPatient.email)
                if (user != null) {
                    db.userDao().update(user.copy(name = name, email = email))
                }

                // Update patient info in existing appointments
                val appts = db.appointmentDao().findByPatientEmail(oldPatient.email)
                appts.forEach { appt ->
                    db.appointmentDao().update(appt.copy(patientName = name, patientEmail = email))
                }
            }

            reloadData()
        }
    }

    fun deletePatient(id: Int) {
        val db = database ?: return
        scope.launch(Dispatchers.IO) {
            val patient = db.patientDao().findById(id)
            if (patient != null) {
                db.patientDao().delete(patient)
                db.userDao().deleteByEmail(patient.email)
                db.appointmentDao().deleteByPatientEmail(patient.email)
            }
            reloadData()
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
        val db = database ?: return
        scope.launch(Dispatchers.IO) {
            var cleanDoctorName = doctorName
            if (doctorName.contains("(")) {
                cleanDoctorName = doctorName.substringBefore("(").trim()
            }

            // Find specialization/poli from db
            val docs = db.doctorDao().getAllDoctorsOnce()
            val doctor = docs.find { it.name.equals(cleanDoctorName, ignoreCase = true) }
            val poli = doctor?.specialization ?: "Umum"

            val apptEntity = AppointmentEntity(
                doctor = cleanDoctorName,
                poli = poli,
                date = date,
                time = time,
                status = "Pending", // Default Pending to be actioned by admin or doctor
                patientName = patientName,
                patientEmail = patientEmail
            )
            db.appointmentDao().insert(apptEntity)

            // Notifications
            // 1. Patient gets a confirmation that it is requested
            addNotificationInternal(
                title = "Janji Temu Diajukan 📅",
                message = "Janji temu Anda dengan $cleanDoctorName pada tanggal $date jam $time sedang menunggu persetujuan.",
                type = "APPOINTMENT_CREATED",
                targetEmail = patientEmail
            )

            // 2. Admin/Doctor notification (global)
            addNotificationInternal(
                title = "Pengajuan Janji Temu Baru 🔔",
                message = "Pasien $patientName mengajukan janji temu dengan $cleanDoctorName.",
                type = "INFO",
                targetEmail = "" // visible to all / admin
            )

            // Trigger system alert
            NotificationHelper.sendNotification(
                context = appContext!!,
                title = "Janji Temu Berhasil Diajukan \uD83C\uDFE5",
                message = "Jadwal konsultasi Anda dengan $cleanDoctorName sedang diproses."
            )

            reloadData()
        }
    }

    fun updateAppointmentStatus(id: Int, status: String) {
        val db = database ?: return
        scope.launch(Dispatchers.IO) {
            val appt = db.appointmentDao().findById(id)
            if (appt != null) {
                db.appointmentDao().updateStatus(id, status)

                // Notify patient
                val cleanStatus = if (status.equals("Upcoming", ignoreCase = true)) "DISETUJUI" else status.uppercase()
                val isApproved = status.equals("Upcoming", ignoreCase = true)
                val type = if (isApproved) "APPOINTMENT_APPROVED" else "APPOINTMENT_REJECTED"

                addNotificationInternal(
                    title = "Status Janji Temu: $cleanStatus 🩺",
                    message = "Janji temu Anda dengan ${appt.doctor} pada $appt.date $appt.time berstatus: $cleanStatus.",
                    type = type,
                    targetEmail = appt.patientEmail
                )

                // Trigger system alert notification
                NotificationHelper.sendNotification(
                    context = appContext!!,
                    title = "Update Janji Temu \uD83C\uDFE5",
                    message = "Janji temu Anda dengan ${appt.doctor} statusnya diubah menjadi $cleanStatus."
                )

                reloadData()
            }
        }
    }

    // ==================== NOTIFICATIONS ====================
    suspend fun loadNotifications(email: String) {
        val db = database ?: return
        withContext(Dispatchers.IO) {
            val list = db.notificationDao().getNotificationsForUserOnce(email)
            withContext(Dispatchers.Main) {
                notifications.clear()
                notifications.addAll(list)
            }
        }
    }

    fun markNotificationAsRead(id: Int) {
        val db = database ?: return
        scope.launch(Dispatchers.IO) {
            db.notificationDao().markAsRead(id)
            // Refresh
            val email = repository?.currentUserEmail?.first()
            if (!email.isNullOrBlank()) {
                loadNotifications(email)
            }
        }
    }

    fun markAllNotificationsAsRead(email: String) {
        val db = database ?: return
        scope.launch(Dispatchers.IO) {
            db.notificationDao().markAllAsRead(email)
            loadNotifications(email)
        }
    }

    fun deleteNotification(id: Int) {
        val db = database ?: return
        scope.launch(Dispatchers.IO) {
            db.notificationDao().deleteById(id)
            val email = repository?.currentUserEmail?.first()
            if (!email.isNullOrBlank()) {
                loadNotifications(email)
            }
        }
    }

    private suspend fun addNotificationInternal(title: String, message: String, type: String, targetEmail: String) {
        val db = database ?: return
        db.notificationDao().insert(
            NotificationEntity(
                title = title,
                message = message,
                type = type,
                targetEmail = targetEmail
            )
        )
    }

    fun rescheduleAppointment(id: Int, newDate: String, newTime: String) {
        val db = database ?: return
        scope.launch(Dispatchers.IO) {
            val appt = db.appointmentDao().findById(id)
            if (appt != null) {
                val updatedAppt = appt.copy(date = newDate, time = newTime)
                db.appointmentDao().update(updatedAppt)

                addNotificationInternal(
                    title = "Janji Temu Dijadwalkan Ulang 📅",
                    message = "Janji temu Anda dengan ${appt.doctor} berhasil diubah menjadi tanggal $newDate jam $newTime.",
                    type = "INFO",
                    targetEmail = appt.patientEmail
                )

                NotificationHelper.sendNotification(
                    context = appContext!!,
                    title = "Jadwal Ulang Berhasil 🏥",
                    message = "Jadwal konsultasi Anda dengan ${appt.doctor} telah diubah."
                )

                reloadData()
            }
        }
    }

    // ==================== FILTERING APPOINTMENTS ====================
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
