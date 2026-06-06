package com.example.healthcareapp

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/**
 * =============================================================
 * UserPreferencesRepository.kt
 * =============================================================
 * Repository untuk mengelola penyimpanan data menggunakan
 * Jetpack DataStore Preferences.
 *
 * ALASAN MEMILIH DATASTORE:
 * 1. Thread-safe: Operasi dilakukan secara asynchronous via Coroutine
 * 2. Non-blocking: Tidak memblokir UI thread (berbeda dengan SharedPreferences)
 * 3. Flow-based: Data berupa Kotlin Flow yang reactive, cocok dengan Compose
 * 4. Modern API: Direkomendasikan Google sebagai pengganti SharedPreferences
 * 5. Type-safe keys: Menggunakan typed key (stringPreferencesKey, booleanPreferencesKey)
 *
 * DATA YANG DISIMPAN:
 * - Login session (isLoggedIn, email, role)
 * - Data profil pasien (JSON serialized)
 * - Daftar appointment (JSON serialized)
 * =============================================================
 */

// Extension property untuk membuat DataStore instance (singleton per Context)
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

class UserPreferencesRepository(private val context: Context) {

    // ==================== KEYS ====================
    // Key-key yang digunakan untuk menyimpan data di DataStore
    private object PreferencesKeys {
        val IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")
        val USER_EMAIL = stringPreferencesKey("user_email")
        val USER_ROLE = stringPreferencesKey("user_role")
        val USER_NAME = stringPreferencesKey("user_name")
        val PATIENT_PROFILE = stringPreferencesKey("patient_profile")
        val APPOINTMENTS_JSON = stringPreferencesKey("appointments_json")
        val DOCTORS_JSON = stringPreferencesKey("doctors_json")
        val USERS_JSON = stringPreferencesKey("users_json")
        val PATIENTS_JSON = stringPreferencesKey("patients_json")
    }

    // ==================== LOGIN SESSION ====================

    /**
     * Flow yang mengembalikan status login saat ini.
     * Menggunakan Flow agar UI otomatis ter-update saat data berubah.
     */
    val isLoggedIn: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.IS_LOGGED_IN] ?: false
    }

    /**
     * Flow yang mengembalikan email user yang sedang login.
     */
    val currentUserEmail: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.USER_EMAIL] ?: ""
    }

    /**
     * Flow yang mengembalikan role user saat ini (ADMIN, DOCTOR, PATIENT).
     */
    val currentUserRole: Flow<UserRole?> = context.dataStore.data.map { preferences ->
        val roleStr = preferences[PreferencesKeys.USER_ROLE]
        if (roleStr != null) {
            try { UserRole.valueOf(roleStr) } catch (e: Exception) { null }
        } else null
    }

    /**
     * Flow yang mengembalikan nama user yang sedang login.
     */
    val currentUserName: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.USER_NAME] ?: ""
    }

    /**
     * Menyimpan session login ke DataStore.
     * Dipanggil setelah user berhasil login.
     *
     * @param email Email user
     * @param role Role user (ADMIN/DOCTOR/PATIENT)
     * @param name Nama user
     */
    suspend fun saveLoginSession(email: String, role: UserRole, name: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.IS_LOGGED_IN] = true
            preferences[PreferencesKeys.USER_EMAIL] = email
            preferences[PreferencesKeys.USER_ROLE] = role.name
            preferences[PreferencesKeys.USER_NAME] = name
        }
    }

    /**
     * Menghapus session login dari DataStore.
     * Dipanggil saat user logout.
     */
    suspend fun clearLoginSession() {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.IS_LOGGED_IN] = false
            preferences.remove(PreferencesKeys.USER_EMAIL)
            preferences.remove(PreferencesKeys.USER_ROLE)
            preferences.remove(PreferencesKeys.USER_NAME)
        }
    }

    // ==================== PATIENT PROFILE ====================

    /**
     * Menyimpan profil pasien ke DataStore sebagai JSON string.
     * Menggunakan kotlinx.serialization untuk serialize/deserialize.
     */
    suspend fun savePatientProfile(patient: Patient) {
        val json = Json.encodeToString(patient)
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.PATIENT_PROFILE] = json
        }
    }

    /**
     * Flow yang mengembalikan profil pasien dari DataStore.
     */
    val patientProfile: Flow<Patient?> = context.dataStore.data.map { preferences ->
        val json = preferences[PreferencesKeys.PATIENT_PROFILE]
        if (json != null) {
            try { Json.decodeFromString<Patient>(json) } catch (e: Exception) { null }
        } else null
    }

    // ==================== APPOINTMENTS ====================

    /**
     * Menyimpan daftar appointment ke DataStore sebagai JSON string.
     * Seluruh list di-serialize menjadi satu string JSON.
     */
    suspend fun saveAppointments(appointments: List<Appointment>) {
        val json = Json.encodeToString(appointments)
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.APPOINTMENTS_JSON] = json
        }
    }

    /**
     * Flow yang mengembalikan daftar appointment dari DataStore.
     * Jika belum ada data, mengembalikan list kosong.
     */
    val appointments: Flow<List<Appointment>> = context.dataStore.data.map { preferences ->
        val json = preferences[PreferencesKeys.APPOINTMENTS_JSON]
        if (json != null) {
            try { Json.decodeFromString<List<Appointment>>(json) } catch (e: Exception) { emptyList() }
        } else emptyList()
    }

    // ==================== DOCTORS ====================
    suspend fun saveDoctors(doctorsList: List<Doctor>) {
        val json = Json.encodeToString(doctorsList)
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.DOCTORS_JSON] = json
        }
    }

    val doctors: Flow<List<Doctor>> = context.dataStore.data.map { preferences ->
        val json = preferences[PreferencesKeys.DOCTORS_JSON]
        if (json != null) {
            try { Json.decodeFromString<List<Doctor>>(json) } catch (e: Exception) { emptyList() }
        } else emptyList()
    }

    // ==================== USERS ====================
    suspend fun saveUsers(usersList: List<User>) {
        val json = Json.encodeToString(usersList)
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.USERS_JSON] = json
        }
    }

    val users: Flow<List<User>> = context.dataStore.data.map { preferences ->
        val json = preferences[PreferencesKeys.USERS_JSON]
        if (json != null) {
            try { Json.decodeFromString<List<User>>(json) } catch (e: Exception) { emptyList() }
        } else emptyList()
    }

    // ==================== PATIENTS ====================
    suspend fun savePatients(patientsList: List<Patient>) {
        val json = Json.encodeToString(patientsList)
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.PATIENTS_JSON] = json
        }
    }

    val patients: Flow<List<Patient>> = context.dataStore.data.map { preferences ->
        val json = preferences[PreferencesKeys.PATIENTS_JSON]
        if (json != null) {
            try { Json.decodeFromString<List<Patient>>(json) } catch (e: Exception) { emptyList() }
        } else emptyList()
    }
}
