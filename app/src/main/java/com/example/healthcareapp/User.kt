package com.example.healthcareapp

import kotlinx.serialization.Serializable

/**
 * Enum untuk mendefinisikan role user dalam sistem Healthcare.
 * - ADMIN: Mengelola dan melihat semua data
 * - DOCTOR: Melihat appointment dan jadwal praktik sendiri
 * - PATIENT: Membuat appointment dan melihat riwayat sendiri
 */
@Serializable
enum class UserRole {
    ADMIN,
    DOCTOR,
    PATIENT
}

/**
 * Data class untuk menyimpan informasi user/akun.
 * Digunakan untuk autentikasi login dan role-based access.
 *
 * @param email Email unik untuk login
 * @param password Password untuk autentikasi
 * @param name Nama tampilan user
 * @param role Role user (ADMIN, DOCTOR, atau PATIENT)
 * @param doctorId ID dokter terkait (hanya untuk role DOCTOR, null untuk lainnya)
 */
@Serializable
data class User(
    val email: String,
    val password: String,
    val name: String,
    val role: UserRole,
    val doctorId: Int? = null
)
