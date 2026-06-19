package com.example.healthcareapp.data.dao

import androidx.room.*
import com.example.healthcareapp.data.entity.DoctorEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DoctorDao {

    @Query("SELECT * FROM doctors ORDER BY id ASC")
    fun getAllDoctors(): Flow<List<DoctorEntity>>

    @Query("SELECT * FROM doctors ORDER BY id ASC")
    suspend fun getAllDoctorsOnce(): List<DoctorEntity>

    @Query("SELECT * FROM doctors WHERE id = :id LIMIT 1")
    suspend fun findById(id: Int): DoctorEntity?

    @Query("SELECT * FROM doctors WHERE LOWER(name) LIKE '%' || LOWER(:query) || '%' OR LOWER(specialization) LIKE '%' || LOWER(:query) || '%'")
    suspend fun search(query: String): List<DoctorEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(doctor: DoctorEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(doctors: List<DoctorEntity>)

    @Update
    suspend fun update(doctor: DoctorEntity)

    @Delete
    suspend fun delete(doctor: DoctorEntity)

    @Query("DELETE FROM doctors WHERE id = :id")
    suspend fun deleteById(id: Int)

    @Query("SELECT COUNT(*) FROM doctors")
    suspend fun count(): Int
}
