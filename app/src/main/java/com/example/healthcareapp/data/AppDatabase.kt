package com.example.healthcareapp.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.healthcareapp.data.dao.*
import com.example.healthcareapp.data.entity.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * =============================================================
 * AppDatabase.kt
 * =============================================================
 * Room Database utama untuk aplikasi Healthcare.
 *
 * Mengelola 5 tabel: users, doctors, patients, appointments, notifications
 * Pre-populate data default saat pertama kali dibuat.
 * =============================================================
 */
@Database(
    entities = [
        UserEntity::class,
        DoctorEntity::class,
        PatientEntity::class,
        AppointmentEntity::class,
        NotificationEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun doctorDao(): DoctorDao
    abstract fun patientDao(): PatientDao
    abstract fun appointmentDao(): AppointmentDao
    abstract fun notificationDao(): NotificationDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "healthcare_db"
                )
                    .addCallback(PrepopulateCallback())
                    .fallbackToDestructiveMigration(dropAllTables = true)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

    /**
     * Callback untuk pre-populate data default saat database pertama kali dibuat.
     */
    private class PrepopulateCallback : Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                CoroutineScope(Dispatchers.IO).launch {
                    prepopulateData(database)
                }
            }
        }

        private suspend fun prepopulateData(db: AppDatabase) {
            // === DEFAULT USERS ===
            db.userDao().insertAll(
                listOf(
                    UserEntity(id = 1, email = "admin@healthcare.com", password = "admin123", name = "Administrator", role = "ADMIN"),
                    UserEntity(id = 2, email = "andi@healthcare.com", password = "dokter123", name = "Dr. Andi Wijaya", role = "DOCTOR", doctorId = 1),
                    UserEntity(id = 3, email = "siti@healthcare.com", password = "dokter123", name = "Dr. Siti Rahma", role = "DOCTOR", doctorId = 2),
                    UserEntity(id = 4, email = "berly@healthcare.com", password = "pasien123", name = "Berly Marcellino", role = "PATIENT")
                )
            )

            // === DEFAULT DOCTORS ===
            db.doctorDao().insertAll(
                listOf(
                    DoctorEntity(id = 1, name = "Dr. Andi Wijaya", specialization = "Dokter Umum",
                        description = "Dokter yang berpengalaman dalam menangani berbagai keluhan kesehatan umum dengan pendekatan profesional dan ramah pasien.",
                        schedule = "Senin 08:00 - 12:00|Selasa 10:00 - 14:00|Rabu 08:00 - 12:00|Kamis 12:00 - 16:00|Jumat 08:00 - 11:00"),
                    DoctorEntity(id = 2, name = "Dr. Siti Rahma", specialization = "Dokter Gigi",
                        description = "Spesialis kesehatan gigi dan mulut dengan pengalaman lebih dari 10 tahun.",
                        schedule = "Senin 09:00 - 13:00|Rabu 13:00 - 17:00|Jumat 09:00 - 12:00"),
                    DoctorEntity(id = 3, name = "Dr. Budi Santoso", specialization = "Dokter Anak",
                        description = "Ahli kesehatan anak yang ramah dan telaten dalam melayani pasien cilik.",
                        schedule = "Selasa 08:00 - 12:00|Kamis 08:00 - 12:00|Sabtu 08:00 - 11:00"),
                    DoctorEntity(id = 4, name = "Dr. Diana Putri", specialization = "Dokter Kulit",
                        description = "Spesialis dermatologi yang ahli dalam perawatan kesehatan kulit dan kecantikan.",
                        schedule = "Senin 14:00 - 18:00|Rabu 14:00 - 18:00|Kamis 14:00 - 18:00"),
                    DoctorEntity(id = 5, name = "Dr. Eka Pratama", specialization = "Dokter Mata",
                        description = "Membantu Anda menjaga kesehatan penglihatan dengan teknologi terkini.",
                        schedule = "Selasa 13:00 - 16:00|Jumat 13:00 - 16:00")
                )
            )

            // === DEFAULT PATIENTS ===
            db.patientDao().insertAll(
                listOf(
                    PatientEntity(id = 1, name = "Berly Marcellino", email = "berly@healthcare.com",
                        phone = "08123456789", gender = "Laki-laki", birthDate = "2004-01-01", address = "Klaten, Indonesia")
                )
            )

            // === DEFAULT APPOINTMENTS ===
            db.appointmentDao().insertAll(
                listOf(
                    AppointmentEntity(id = 1, doctor = "Dr. Andi Wijaya", poli = "Dokter Umum",
                        date = "20/04/2026", time = "08:00 - 09:00", status = "Upcoming",
                        patientName = "Berly Marcellino", patientEmail = "berly@healthcare.com"),
                    AppointmentEntity(id = 2, doctor = "Dr. Siti Rahma", poli = "Dokter Gigi",
                        date = "22/04/2026", time = "09:30 - 10:30", status = "Upcoming",
                        patientName = "Berly Marcellino", patientEmail = "berly@healthcare.com"),
                    AppointmentEntity(id = 3, doctor = "Dr. Budi Santoso", poli = "Dokter Anak",
                        date = "10/04/2026", time = "08:00 - 12:00", status = "Completed",
                        patientName = "Berly Marcellino", patientEmail = "berly@healthcare.com")
                )
            )

            // === WELCOME NOTIFICATION ===
            db.notificationDao().insert(
                NotificationEntity(
                    title = "Selamat Datang! 🎉",
                    message = "Selamat datang di HealthCare App. Gunakan aplikasi ini untuk mengelola janji temu dan kesehatan Anda.",
                    type = "INFO",
                    targetEmail = ""
                )
            )
        }
    }
}
