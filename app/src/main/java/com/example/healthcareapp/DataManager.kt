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
 * PERUBAHAN DARI VERSI SEBELUMNYA:
 * - Sebelumnya: Semua data in-memory, hilang saat restart
 * - Sekarang: Data appointment di-persist ke DataStore
 * - Ditambahkan: Daftar user preset untuk multi-role login
 * - Ditambahkan: Fungsi authenticate() untuk validasi login
 *
 * DATA STATIS (tetap hardcoded karena tidak berubah):
 * - Daftar dokter
 * - Daftar riwayat pemeriksaan
 * - Daftar user/akun preset
 *
 * DATA DINAMIS (disimpan ke DataStore):
 * - Daftar appointment (bisa bertambah dari user)
 * =============================================================
 */
object DataManager {

    // Reference ke repository DataStore (di-init dari MainActivity)
    private var repository: UserPreferencesRepository? = null

    /**
     * Inisialisasi DataManager dengan repository DataStore.
     * Dipanggil sekali dari MainActivity saat app pertama kali dibuka.
     */
    fun init(context: Context) {
        repository = UserPreferencesRepository(context)
    }

    fun getRepository(): UserPreferencesRepository? = repository

    // ==================== USER ACCOUNTS ====================
    /**
     * Daftar akun preset untuk demo multi-role login.
     * Dalam produksi, ini akan disimpan di database/server.
     */
    val users = listOf(
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

    /**
     * Autentikasi user berdasarkan email dan password.
     * Mencocokkan input dengan daftar user preset.
     *
     * @param email Email yang diinput user
     * @param password Password yang diinput user
     * @return User jika credentials cocok, null jika tidak
     */
    fun authenticate(email: String, password: String): User? {
        return users.find {
            it.email.equals(email, ignoreCase = true) && it.password == password
        }
    }

    // ==================== DOCTORS (DATA STATIS) ====================
    val doctors = listOf(
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

    // ==================== APPOINTMENTS (DATA DINAMIS) ====================
    /**
     * Daftar appointment yang tersimpan di memory.
     * Data ini juga di-sync ke DataStore agar persisten.
     */
    val appointments = mutableStateListOf<Appointment>()

    /**
     * Load appointments dari DataStore ke memory.
     * Dipanggil saat app pertama kali dibuka.
     */
    suspend fun loadAppointments() {
        val repo = repository ?: return
        val saved = repo.appointments.first()
        appointments.clear()
        if (saved.isNotEmpty()) {
            appointments.addAll(saved)
        } else {
            // Data default jika belum ada yang tersimpan
            appointments.addAll(
                listOf(
                    Appointment(1, "Dr. Andi Wijaya", "Dokter Umum", "20 Apr 2026", "08:00 - 09:00", "Upcoming", "Berly Marcellino", "berly@healthcare.com"),
                    Appointment(2, "Dr. Siti Rahma", "Dokter Gigi", "22 Apr 2026", "09:30 - 10:30", "Upcoming", "Berly Marcellino", "berly@healthcare.com"),
                    Appointment(3, "Dr. Budi Santoso", "Dokter Anak", "10 Apr 2026", "08:00 - 12:00", "Completed", "Berly Marcellino", "berly@healthcare.com")
                )
            )
            // Simpan data default ke DataStore
            persistAppointments()
        }
    }

    /**
     * Menyimpan semua appointment dari memory ke DataStore.
     * Dipanggil setiap kali ada perubahan data appointment.
     */
    private suspend fun persistAppointments() {
        val repo = repository ?: return
        repo.saveAppointments(appointments.toList())
    }

    /**
     * Menambahkan appointment baru dan menyimpan ke DataStore.
     *
     * @param patientName Nama pasien
     * @param patientEmail Email pasien
     * @param doctorName Nama dokter yang dipilih
     * @param date Tanggal appointment
     * @param time Waktu appointment
     * @param symptoms Keluhan/gejala pasien
     */
    fun addAppointment(
        patientName: String,
        patientEmail: String,
        doctorName: String,
        date: String,
        time: String,
        symptoms: String
    ) {
        // Find poli from doctor name
        val doctor = doctors.find { it.name == doctorName }
        val poli = doctor?.specialization ?: "Umum"

        val newId = (appointments.maxByOrNull { it.id }?.id ?: 0) + 1
        appointments.add(
            Appointment(
                id = newId,
                doctor = doctorName,
                poli = poli,
                date = date,
                time = time,
                status = "Upcoming",
                patientName = patientName,
                patientEmail = patientEmail
            )
        )

        // Persist ke DataStore secara asynchronous
        CoroutineScope(Dispatchers.IO).launch {
            persistAppointments()
        }
    }

    // ==================== HISTORY (DATA STATIS) ====================
    val historyItems = listOf(
        HistoryItem(1, "Dr. Andi Wijaya", "Pemeriksaan Umum", "20 April 2026", "Selesai"),
        HistoryItem(2, "Dr. Siti Rahma", "Konsultasi Gigi", "18 April 2026", "Selesai"),
        HistoryItem(3, "Dr. Budi Santoso", "Cek Kesehatan", "15 April 2026", "Selesai")
    )

    /**
     * Mendapatkan appointment berdasarkan role user.
     * - ADMIN: Semua appointment
     * - DOCTOR: Appointment yang terkait dokter tersebut
     * - PATIENT: Appointment milik pasien tersebut
     */
    fun getAppointmentsForUser(email: String, role: UserRole): List<Appointment> {
        return when (role) {
            UserRole.ADMIN -> appointments.toList()
            UserRole.DOCTOR -> {
                val user = users.find { it.email == email }
                val doctor = user?.doctorId?.let { id -> doctors.find { it.id == id } }
                if (doctor != null) {
                    appointments.filter { it.doctor == doctor.name }
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
