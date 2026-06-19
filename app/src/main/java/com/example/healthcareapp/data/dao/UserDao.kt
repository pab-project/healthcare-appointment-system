package com.example.healthcareapp.data.dao

import androidx.room.*
import com.example.healthcareapp.data.entity.UserEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {

    @Query("SELECT * FROM users")
    fun getAllUsers(): Flow<List<UserEntity>>

    @Query("SELECT * FROM users")
    suspend fun getAllUsersOnce(): List<UserEntity>

    @Query("SELECT * FROM users WHERE LOWER(email) = LOWER(:email) AND password = :password LIMIT 1")
    suspend fun authenticate(email: String, password: String): UserEntity?

    @Query("SELECT * FROM users WHERE LOWER(email) = LOWER(:email) LIMIT 1")
    suspend fun findByEmail(email: String): UserEntity?

    @Query("SELECT * FROM users WHERE role = :role")
    suspend fun findByRole(role: String): List<UserEntity>

    @Query("SELECT * FROM users WHERE role = 'DOCTOR' AND doctorId = :doctorId LIMIT 1")
    suspend fun findDoctorUser(doctorId: Int): UserEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(user: UserEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(users: List<UserEntity>)

    @Update
    suspend fun update(user: UserEntity)

    @Delete
    suspend fun delete(user: UserEntity)

    @Query("DELETE FROM users WHERE LOWER(email) = LOWER(:email)")
    suspend fun deleteByEmail(email: String)

    @Query("DELETE FROM users WHERE role = 'DOCTOR' AND doctorId = :doctorId")
    suspend fun deleteDoctorUser(doctorId: Int)

    @Query("SELECT COUNT(*) FROM users")
    suspend fun count(): Int

    @Query("SELECT COUNT(*) FROM users WHERE role = 'PATIENT'")
    suspend fun countPatients(): Int
}
