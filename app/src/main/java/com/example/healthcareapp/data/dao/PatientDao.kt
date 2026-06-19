package com.example.healthcareapp.data.dao

import androidx.room.*
import com.example.healthcareapp.data.entity.PatientEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PatientDao {

    @Query("SELECT * FROM patients ORDER BY id ASC")
    fun getAllPatients(): Flow<List<PatientEntity>>

    @Query("SELECT * FROM patients ORDER BY id ASC")
    suspend fun getAllPatientsOnce(): List<PatientEntity>

    @Query("SELECT * FROM patients WHERE id = :id LIMIT 1")
    suspend fun findById(id: Int): PatientEntity?

    @Query("SELECT * FROM patients WHERE LOWER(email) = LOWER(:email) LIMIT 1")
    suspend fun findByEmail(email: String): PatientEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(patient: PatientEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(patients: List<PatientEntity>)

    @Update
    suspend fun update(patient: PatientEntity)

    @Delete
    suspend fun delete(patient: PatientEntity)

    @Query("DELETE FROM patients WHERE id = :id")
    suspend fun deleteById(id: Int)

    @Query("SELECT COUNT(*) FROM patients")
    suspend fun count(): Int
}
