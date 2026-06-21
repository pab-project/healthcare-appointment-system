package com.example.healthcareapp

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.mutableStateListOf
import com.example.healthcareapp.data.AppDatabase
import com.example.healthcareapp.data.entity.NotificationEntity
import com.example.healthcareapp.network.*
import com.google.gson.JsonParser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Response

/**
 * =============================================================
 * DataManager.kt
 * =============================================================
 * Object singleton yang mengelola semua data dalam aplikasi.
 *
 * Menggunakan Laravel API backend di medsync.imarskun.my.id
 * =============================================================
 */
object DataManager {

    private var repository: UserPreferencesRepository? = null
    private var database: AppDatabase? = null
    private var appContext: Context? = null

    private val scope = CoroutineScope(Dispatchers.Main)

    private fun showToast(message: String) {
        appContext?.let { context ->
            scope.launch(Dispatchers.Main) {
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun getErrorMessage(response: Response<*>): String {
        return try {
            val errorBody = response.errorBody()?.string()
            if (!errorBody.isNullOrEmpty()) {
                val json = JsonParser.parseString(errorBody).asJsonObject
                json.get("message")?.asString ?: "Terjadi kesalahan server (${response.code()})"
            } else {
                "Terjadi kesalahan server (${response.code()})"
            }
        } catch (e: Exception) {
            "Terjadi kesalahan koneksi (${response.code()})"
        }
    }

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

    // Helpers to format birthdate
    fun convertBirthDateToApi(date: String): String {
        return try {
            val clean = date.replace("-", "/")
            val parts = clean.split("/")
            if (parts.size == 3) {
                val day = parts[0].padStart(2, '0')
                val month = parts[1].padStart(2, '0')
                val year = parts[2]
                "$year-$month-$day"
            } else {
                date
            }
        } catch (e: Exception) {
            date
        }
    }

    fun convertBirthDateFromApi(date: String?): String {
        if (date.isNullOrBlank()) return ""
        return try {
            val parts = date.split("-")
            if (parts.size == 3) {
                "${parts[2]}/${parts[1]}/${parts[0]}"
            } else {
                date
            }
        } catch (e: Exception) {
            date ?: ""
        }
    }

    // ==================== LOAD & PERSIST DATA ====================
    suspend fun loadAppointments() {
        val repo = repository ?: return
        val role = repo.currentUserRole.first()

        withContext(Dispatchers.IO) {
            try {
                // Load Doctors from API
                val docResponse = RetrofitClient.apiService.getDoctors()
                if (docResponse.isSuccessful) {
                    val apiDocs = docResponse.body()?.data ?: emptyList()
                    withContext(Dispatchers.Main) {
                        doctors.clear()
                        doctors.addAll(apiDocs.map {
                            Doctor(
                                id = it.id,
                                name = it.name,
                                specialization = it.specialization,
                                description = it.bio ?: "",
                                schedule = listOf("Senin 08:00 - 12:00", "Selasa 10:00 - 14:00", "Rabu 08:00 - 12:00", "Kamis 12:00 - 16:00", "Jumat 08:00 - 11:00")
                            )
                        })
                    }
                }

                // If Role is set, load specific role appointments
                if (role != null) {
                    val apptResponse = when (role) {
                        UserRole.PATIENT -> RetrofitClient.apiService.getPatientAppointments()
                        UserRole.DOCTOR -> RetrofitClient.apiService.getDoctorSchedule()
                        UserRole.ADMIN -> RetrofitClient.apiService.getAdminAppointments()
                    }

                    if (apptResponse.isSuccessful) {
                        val apiAppts = apptResponse.body()?.data ?: emptyList()
                        withContext(Dispatchers.Main) {
                            appointments.clear()
                            appointments.addAll(apiAppts.map { appt ->
                                val docName = appt.doctor?.name ?: "Unknown"
                                val specialization = appt.doctor?.specialization ?: "Umum"

                                val rawDate = appt.timeSlot?.date ?: ""
                                val formattedDate = try {
                                    val parts = rawDate.split("-")
                                    if (parts.size == 3) "${parts[2]}/${parts[1]}/${parts[0]}" else rawDate
                                } catch (e: Exception) {
                                    rawDate
                                }

                                val start = appt.timeSlot?.startTime?.substring(0, 5) ?: ""
                                val end = appt.timeSlot?.endTime?.substring(0, 5) ?: ""
                                val formattedTime = if (start.isNotEmpty() && end.isNotEmpty()) "$start - $end" else ""

                                val mappedStatus = when (appt.status.lowercase()) {
                                    "pending" -> "Pending"
                                    "confirmed", "scheduled" -> "Upcoming"
                                    "completed" -> "Completed"
                                    "cancelled" -> "Cancelled"
                                    else -> appt.status
                                }

                                Appointment(
                                    id = appt.id,
                                    doctor = docName,
                                    poli = specialization,
                                    date = formattedDate,
                                    time = formattedTime,
                                    status = mappedStatus,
                                    patientName = appt.patient?.name ?: "",
                                    patientEmail = appt.patient?.email ?: ""
                                )
                            })

                            // Populate History Items
                            historyItems.clear()
                            if (role == UserRole.PATIENT) {
                                try {
                                    val recordResponse = RetrofitClient.apiService.getPatientMedicalRecords()
                                    if (recordResponse.isSuccessful) {
                                        val apiRecords = recordResponse.body()?.data ?: emptyList()
                                        historyItems.addAll(apiRecords.map { record ->
                                            val docName = record.doctor?.name ?: "Unknown"
                                            val spec = record.doctor?.specialization ?: "Umum"
                                            val formattedDate = try {
                                                val rawDate = record.createdAt?.substringBefore(" ") ?: ""
                                                val parts = rawDate.split("-")
                                                if (parts.size == 3) "${parts[2]}/${parts[1]}/${parts[0]}" else rawDate
                                            } catch (e: Exception) {
                                                record.createdAt ?: ""
                                            }
                                            HistoryItem(
                                                id = record.id,
                                                doctorName = docName,
                                                service = spec,
                                                date = formattedDate,
                                                status = "Selesai",
                                                diagnosis = record.diagnosis,
                                                treatment = record.treatment,
                                                medications = record.medications,
                                                notes = record.notes
                                            )
                                        })
                                    } else {
                                        val completed = appointments.filter { it.status.equals("Completed", ignoreCase = true) }
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
                                } catch (e: Exception) {
                                    val completed = appointments.filter { it.status.equals("Completed", ignoreCase = true) }
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
                            } else {
                                val completed = appointments.filter { it.status.equals("Completed", ignoreCase = true) }
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

                    // Load role-specific profiles or data lists
                    when (role) {
                        UserRole.PATIENT -> {
                            val profileRes = RetrofitClient.apiService.getPatientProfile()
                            if (profileRes.isSuccessful) {
                                val p = profileRes.body()?.data
                                if (p != null) {
                                    withContext(Dispatchers.Main) {
                                        patients.clear()
                                        patients.add(
                                            Patient(
                                                id = p.id,
                                                name = p.name,
                                                email = p.email,
                                                phone = p.phone ?: "",
                                                gender = p.gender ?: "",
                                                birthDate = convertBirthDateFromApi(p.dateOfBirth),
                                                address = p.address ?: ""
                                            )
                                        )
                                    }
                                }
                            }
                        }
                        UserRole.DOCTOR -> {
                            val profileRes = RetrofitClient.apiService.getDoctorProfile()
                            if (profileRes.isSuccessful) {
                                val d = profileRes.body()?.data
                                if (d != null) {
                                    withContext(Dispatchers.Main) {
                                        val existing = doctors.find { it.id == d.id }
                                        if (existing != null) {
                                            doctors.remove(existing)
                                        }
                                        doctors.add(
                                            Doctor(
                                                id = d.id,
                                                name = d.name,
                                                specialization = d.specialization,
                                                description = d.bio ?: "",
                                                schedule = listOf("Senin 08:00 - 12:00", "Selasa 10:00 - 14:00", "Rabu 08:00 - 12:00", "Kamis 12:00 - 16:00", "Jumat 08:00 - 11:00")
                                            )
                                        )
                                    }
                                }
                            }
                        }
                        UserRole.ADMIN -> {
                            val patientsRes = RetrofitClient.apiService.getAdminPatients()
                            if (patientsRes.isSuccessful) {
                                val apiPatients = patientsRes.body()?.data ?: emptyList()
                                withContext(Dispatchers.Main) {
                                    users.clear()
                                    users.addAll(apiPatients.map {
                                        User(
                                            email = it.email,
                                            name = it.name,
                                            role = UserRole.PATIENT,
                                            password = "",
                                            id = it.userId
                                        )
                                    })
                                    patients.clear()
                                    patients.addAll(apiPatients.map {
                                        Patient(
                                            id = it.id,
                                            name = it.name,
                                            email = it.email,
                                            phone = it.phone ?: "",
                                            gender = it.gender ?: "",
                                            birthDate = convertBirthDateFromApi(it.dateOfBirth),
                                            address = it.address ?: ""
                                        )
                                    })
                                }
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun reloadData() {
        scope.launch {
            loadAppointments()
        }
    }

    // ==================== AUTHENTICATION ====================
    suspend fun authenticate(email: String, password: String): User? {
        return withContext(Dispatchers.IO) {
            try {
                val response = RetrofitClient.apiService.login(LoginRequest(email, password))
                if (response.isSuccessful) {
                    val loginRes = response.body()
                    val userRes = loginRes?.user
                    val token = loginRes?.token
                    val roleStr = loginRes?.role ?: userRes?.role
                    if (userRes != null && token != null && roleStr != null) {
                        val role = try {
                            UserRole.valueOf(roleStr.uppercase())
                        } catch (e: Exception) {
                            UserRole.PATIENT
                        }
                        val domainUser = User(
                            email = userRes.email,
                            name = userRes.name,
                            role = role,
                            password = "",
                            id = userRes.id,
                            token = token
                        )
                        repository?.saveLoginSession(userRes.email, role, userRes.name, token)
                        loadAppointments()
                        return@withContext domainUser
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return@withContext null
        }
    }

    // ==================== CRUD DOCTOR ====================
    suspend fun addDoctor(name: String, specialization: String, description: String, schedule: List<String>): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val email = name.lowercase().replace(" ", "").replace(".", "") + "@healthcare.com"
                val response = RetrofitClient.apiService.createDoctor(
                    CreateDoctorRequest(
                        name = name,
                        email = email,
                        password = "dokter123",
                        specialization = specialization,
                        phone = "0812345678",
                        bio = description
                    )
                )
                if (response.isSuccessful) {
                    loadAppointments()
                    return@withContext true
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return@withContext false
        }
    }

    suspend fun updateDoctor(id: Int, name: String, specialization: String, description: String, schedule: List<String>): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val role = repository?.currentUserRole?.first()
                val response = if (role == UserRole.DOCTOR) {
                    RetrofitClient.apiService.updateDoctorProfile(
                        UpdateDoctorRequest(
                            name = name,
                            specialization = specialization,
                            phone = null,
                            bio = description
                        )
                    )
                } else {
                    RetrofitClient.apiService.updateDoctorByAdmin(
                        id,
                        CreateDoctorRequest(
                            name = name,
                            email = "", // not updated by this endpoint
                            specialization = specialization,
                            bio = description
                        )
                    )
                }
                if (response.isSuccessful) {
                    if (role == UserRole.DOCTOR) {
                        val email = repository?.currentUserEmail?.first() ?: ""
                        val token = repository?.authToken?.first() ?: ""
                        repository?.saveLoginSession(email, role, name, token)
                    }
                    loadAppointments()
                    return@withContext true
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return@withContext false
        }
    }

    suspend fun deleteDoctor(id: Int): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val response = RetrofitClient.apiService.deleteDoctorByAdmin(id)
                if (response.isSuccessful) {
                    loadAppointments()
                    return@withContext true
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return@withContext false
        }
    }

    // ==================== CRUD PATIENT ====================
    suspend fun addPatient(
        name: String,
        email: String,
        phone: String,
        gender: String,
        birthDate: String,
        address: String,
        password: String = "pasien123"
    ): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val response = RetrofitClient.apiService.register(
                    RegisterRequest(
                        name = name,
                        email = email,
                        password = password,
                        passwordConfirmation = password
                    )
                )
                if (response.isSuccessful) {
                    val registerRes = response.body()
                    val userRes = registerRes?.user
                    val token = registerRes?.token
                    if (userRes != null && token != null) {
                        repository?.saveLoginSession(userRes.email, UserRole.PATIENT, userRes.name, token)

                        // Update profile details
                        RetrofitClient.apiService.updatePatientProfile(
                            UpdatePatientRequest(
                                name = name,
                                dateOfBirth = convertBirthDateToApi(birthDate),
                                gender = gender,
                                address = address,
                                phone = phone
                            )
                        )
                        loadAppointments()
                        return@withContext true
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return@withContext false
        }
    }

    suspend fun updatePatient(id: Int, name: String, email: String, phone: String, gender: String, birthDate: String, address: String): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val formattedBirthDate = convertBirthDateToApi(birthDate)
                val response = RetrofitClient.apiService.updatePatientProfile(
                    UpdatePatientRequest(
                        name = name,
                        dateOfBirth = formattedBirthDate,
                        gender = gender,
                        address = address,
                        phone = phone
                    )
                )
                if (response.isSuccessful) {
                    loadAppointments()
                    return@withContext true
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return@withContext false
        }
    }

    suspend fun deletePatient(id: Int): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val response = RetrofitClient.apiService.deletePatientByAdmin(id)
                if (response.isSuccessful) {
                    loadAppointments()
                    return@withContext true
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return@withContext false
        }
    }

    suspend fun getDoctorSlots(doctorId: Int): List<TimeSlotResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val response = RetrofitClient.apiService.getDoctorSlots(doctorId)
                if (response.isSuccessful) {
                    response.body()?.data ?: emptyList()
                } else {
                    emptyList()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                emptyList()
            }
        }
    }

    // ==================== APPOINTMENT OPERATIONS ====================
    suspend fun addAppointment(
        patientName: String,
        patientEmail: String,
        doctorName: String,
        date: String,
        time: String,
        symptoms: String
    ): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                var cleanDoctorName = doctorName
                if (doctorName.contains("(")) {
                    cleanDoctorName = doctorName.substringBefore("(").trim()
                }

                val doctor = doctors.find { it.name.equals(cleanDoctorName, ignoreCase = true) }
                if (doctor == null) {
                    showToast("Dokter '$cleanDoctorName' tidak ditemukan.")
                    return@withContext false
                }

                // Fetch available slots
                val slotsRes = RetrofitClient.apiService.getDoctorSlots(doctor.id)
                if (!slotsRes.isSuccessful) {
                    showToast("Gagal memuat jadwal dokter.")
                    return@withContext false
                }

                val slots = slotsRes.body()?.data ?: emptyList()
                if (slots.isEmpty()) {
                    showToast("Tidak ada jadwal tersedia untuk dokter ini.")
                    return@withContext false
                }

                // Format UI date (dd/MM/yyyy) to API date (yyyy-MM-dd)
                val targetDate = convertBirthDateToApi(date)

                // Match by date and time
                val selectedStart = time.substringBefore("-").trim()
                var targetSlot = slots.find {
                    it.date == targetDate && it.startTime.startsWith(selectedStart)
                }
                if (targetSlot == null) {
                    targetSlot = slots.find { it.date == targetDate }
                }
                if (targetSlot == null) {
                    targetSlot = slots.firstOrNull()
                }

                if (targetSlot == null) {
                    showToast("Slot waktu tidak ditemukan.")
                    return@withContext false
                }

                val response = RetrofitClient.apiService.createAppointment(
                    StoreAppointmentRequest(
                        timeSlotId = targetSlot.id,
                        notes = symptoms
                    )
                )

                if (response.isSuccessful) {
                    showToast("Janji temu berhasil diajukan!")
                    appContext?.let {
                        NotificationHelper.sendNotification(
                            context = it,
                            title = "Janji Temu Berhasil Diajukan \uD83C\uDFE5",
                            message = "Jadwal konsultasi Anda dengan $cleanDoctorName sedang diproses."
                        )
                    }
                    loadAppointments()
                    return@withContext true
                } else {
                    showToast(getErrorMessage(response))
                }
            } catch (e: Exception) {
                e.printStackTrace()
                showToast("Gagal membuat janji temu: ${e.message}")
            }
            return@withContext false
        }
    }

    suspend fun updateAppointmentStatus(
        id: Int,
        status: String,
        diagnosis: String? = null,
        treatment: String? = null,
        medications: String? = null,
        notes: String? = null
    ): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val role = repository?.currentUserRole?.first()
                val response: Response<*>? = when (status) {
                    "Upcoming" -> {
                        if (role == UserRole.ADMIN) {
                            RetrofitClient.apiService.approveAppointment(id)
                        } else {
                            showToast("Hanya admin yang dapat menyetujui janji temu.")
                            null
                        }
                    }
                    "Cancelled" -> {
                        if (role == UserRole.PATIENT) {
                            RetrofitClient.apiService.cancelAppointment(id)
                        } else if (role == UserRole.ADMIN) {
                            RetrofitClient.apiService.rejectAppointment(id, RejectAppointmentRequest("Dibatalkan"))
                        } else {
                            showToast("Anda tidak memiliki izin untuk membatalkan janji temu.")
                            null
                        }
                    }
                    "Completed" -> {
                        if (role == UserRole.DOCTOR) {
                            RetrofitClient.apiService.markAppointmentDone(
                                id,
                                CompleteAppointmentRequest(
                                    diagnosis = diagnosis,
                                    treatment = treatment,
                                    medications = medications,
                                    notes = notes
                                )
                            )
                        } else {
                            showToast("Hanya dokter yang dapat menyelesaikan janji temu.")
                            null
                        }
                    }
                    else -> {
                        showToast("Status tidak dikenali: $status")
                        null
                    }
                }

                if (response != null) {
                    if (response.isSuccessful) {
                        val statusLabel = when (status) {
                            "Upcoming" -> "Disetujui"
                            "Cancelled" -> "Dibatalkan"
                            "Completed" -> "Selesai"
                            else -> status
                        }
                        showToast("Janji temu berhasil $statusLabel!")
                        appContext?.let {
                            NotificationHelper.sendNotification(
                                context = it,
                                title = "Update Janji Temu \uD83E\uDE7A",
                                message = "Status janji temu telah diupdate menjadi $statusLabel."
                            )
                        }
                        loadAppointments()
                        return@withContext true
                    } else {
                        showToast(getErrorMessage(response))
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                showToast("Gagal mengupdate status: ${e.message}")
            }
            return@withContext false
        }
    }

    suspend fun rescheduleAppointment(id: Int, newDate: String, newTime: String): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val oldAppt = appointments.find { it.id == id }
                if (oldAppt == null) {
                    showToast("Janji temu tidak ditemukan.")
                    return@withContext false
                }
                val cancelRes = RetrofitClient.apiService.cancelAppointment(id)
                if (cancelRes.isSuccessful) {
                    val result = addAppointment(
                        patientName = oldAppt.patientName,
                        patientEmail = oldAppt.patientEmail,
                        doctorName = oldAppt.doctor,
                        date = newDate,
                        time = newTime,
                        symptoms = "Reschedule dari janji temu sebelumnya"
                    )
                    if (result) {
                        showToast("Janji temu berhasil dijadwalkan ulang!")
                    }
                    return@withContext result
                } else {
                    showToast(getErrorMessage(cancelRes))
                }
            } catch (e: Exception) {
                e.printStackTrace()
                showToast("Gagal menjadwalkan ulang: ${e.message}")
            }
            return@withContext false
        }
    }

    // ==================== NOTIFICATIONS ====================
    suspend fun loadNotifications(email: String) {
        // Keep notifications local fallback or clear
        withContext(Dispatchers.IO) {
            val db = database ?: return@withContext
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

    // ==================== FILTERING APPOINTMENTS ====================
    fun getAppointmentsForUser(email: String, role: UserRole): List<Appointment> {
        return when (role) {
            UserRole.ADMIN -> appointments.toList()
            UserRole.DOCTOR -> {
                appointments.toList() // Filter is already applied server side by getDoctorSchedule()
            }
            UserRole.PATIENT -> {
                appointments.toList() // Filter is already applied server side by getPatientAppointments()
            }
        }
    }
}
