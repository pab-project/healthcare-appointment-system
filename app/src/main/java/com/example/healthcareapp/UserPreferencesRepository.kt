package com.example.healthcareapp

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * =============================================================
 * UserPreferencesRepository.kt
 * =============================================================
 * Repository untuk mengelola session login menggunakan
 * Jetpack DataStore Preferences.
 * =============================================================
 */

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

class UserPreferencesRepository(private val context: Context) {

    private object PreferencesKeys {
        val IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")
        val USER_EMAIL = stringPreferencesKey("user_email")
        val USER_ROLE = stringPreferencesKey("user_role")
        val USER_NAME = stringPreferencesKey("user_name")
        val AUTH_TOKEN = stringPreferencesKey("auth_token")
    }

    val isLoggedIn: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.IS_LOGGED_IN] ?: false
    }

    val currentUserEmail: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.USER_EMAIL] ?: ""
    }

    val currentUserRole: Flow<UserRole?> = context.dataStore.data.map { preferences ->
        val roleStr = preferences[PreferencesKeys.USER_ROLE]
        if (roleStr != null) {
            try { UserRole.valueOf(roleStr.uppercase()) } catch (e: Exception) { null }
        } else null
    }

    val currentUserName: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.USER_NAME] ?: ""
    }

    val authToken: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.AUTH_TOKEN] ?: ""
    }

    suspend fun saveLoginSession(email: String, role: UserRole, name: String, token: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.IS_LOGGED_IN] = true
            preferences[PreferencesKeys.USER_EMAIL] = email
            preferences[PreferencesKeys.USER_ROLE] = role.name
            preferences[PreferencesKeys.USER_NAME] = name
            preferences[PreferencesKeys.AUTH_TOKEN] = token
        }
    }

    suspend fun clearLoginSession() {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.IS_LOGGED_IN] = false
            preferences.remove(PreferencesKeys.USER_EMAIL)
            preferences.remove(PreferencesKeys.USER_ROLE)
            preferences.remove(PreferencesKeys.USER_NAME)
            preferences.remove(PreferencesKeys.AUTH_TOKEN)
        }
    }
}
