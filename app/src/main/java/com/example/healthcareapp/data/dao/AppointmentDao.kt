package com.example.healthcareapp.data.dao

import androidx.room.*
import com.example.healthcareapp.data.entity.AppointmentEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AppointmentDao {

    @Query("SELECT * FROM appointments ORDER BY id DESC")
    fun getAllAppointments(): Flow<List<AppointmentEntity>>

    @Query("SELECT * FROM appointments ORDER BY id DESC")
    suspend fun getAllAppointmentsOnce(): List<AppointmentEntity>

    @Query("SELECT * FROM appointments WHERE id = :id LIMIT 1")
    suspend fun findById(id: Int): AppointmentEntity?

    @Query("SELECT * FROM appointments WHERE LOWER(patientEmail) = LOWER(:email) ORDER BY id DESC")
    suspend fun findByPatientEmail(email: String): List<AppointmentEntity>

    @Query("SELECT * FROM appointments WHERE LOWER(doctor) = LOWER(:doctorName) ORDER BY id DESC")
    suspend fun findByDoctorName(doctorName: String): List<AppointmentEntity>

    @Query("SELECT * FROM appointments WHERE LOWER(status) = LOWER(:status) ORDER BY id DESC")
    suspend fun findByStatus(status: String): List<AppointmentEntity>

    @Query("UPDATE appointments SET status = :status WHERE id = :id")
    suspend fun updateStatus(id: Int, status: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(appointment: AppointmentEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(appointments: List<AppointmentEntity>)

    @Update
    suspend fun update(appointment: AppointmentEntity)

    @Delete
    suspend fun delete(appointment: AppointmentEntity)

    @Query("DELETE FROM appointments WHERE LOWER(patientEmail) = LOWER(:email)")
    suspend fun deleteByPatientEmail(email: String)

    @Query("SELECT COUNT(*) FROM appointments")
    suspend fun count(): Int
}
