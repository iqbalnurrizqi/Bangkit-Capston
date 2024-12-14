package com.example.capstoneproject4.data.local

import android.content.Context
import android.util.Log
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.text.split

private val Context.dataStore by preferencesDataStore(name = "user_preferences")

@Singleton
class DataStoreManager @Inject constructor(
    @ApplicationContext private val context: Context
) {

    companion object {
        private val TOKEN_KEY = stringPreferencesKey("token")
        private val IS_LOGGED_IN_KEY = booleanPreferencesKey("is_logged_in")
        private val REMEMBER_ME_KEY = booleanPreferencesKey("remember_me")
        private val DARK_MODE_KEY = booleanPreferencesKey("dark_mode")
        val PUSH_NOTIFICATION_KEY = booleanPreferencesKey("push_notification")

        // Tambahkan definisi berikut
        private val NAME_KEY = stringPreferencesKey("name")
        private val SKIN_TYPE_KEY = stringPreferencesKey("skin_type")
        private val SKIN_ISSUE_KEY = stringPreferencesKey("skin_issue")
        private val TREATMENT_GOAL_KEY = stringPreferencesKey("treatment_goal")
        private val USER_PROFILE_IMAGE_PATH = stringPreferencesKey("profile_image_path")
        private val PERMISSION_REQUEST_SHOWN_KEY = booleanPreferencesKey("permission_request_shown")
    }

    val darkMode: Flow<Boolean> = context.dataStore.data.map { it[DARK_MODE_KEY] ?: false }
    val pushNotification: Flow<Boolean> = context.dataStore.data.map { it[PUSH_NOTIFICATION_KEY] ?: true }

    suspend fun updateDarkMode(enabled: Boolean) {
        Log.d("DataStoreManager", "Updating dark mode: $enabled")
        context.dataStore.edit { it[DARK_MODE_KEY] = enabled }
    }

    suspend fun updatePushNotification(enabled: Boolean) {
        context.dataStore.edit { it[PUSH_NOTIFICATION_KEY] = enabled }
    }

    suspend fun saveUserSession(token: String) {
        context.dataStore.edit { preferences ->
            preferences[TOKEN_KEY] = token
            preferences[IS_LOGGED_IN_KEY] = true
        }
        Log.d("DataStoreManager", "Token saved: $token") // Debug log
    }

    fun getUserSession(): Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[TOKEN_KEY]
    }

    suspend fun setRememberMe(isRemembered: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[REMEMBER_ME_KEY] = isRemembered
        }
    }

    fun isRememberMeEnabled(): Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[REMEMBER_ME_KEY] == true
    }

    fun isLoggedIn(): Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[IS_LOGGED_IN_KEY] == true
    }

    suspend fun clearSession() {
        context.dataStore.edit { preferences ->
            preferences.clear()
        }
    }

    suspend fun saveProfileData(name: String, skinType: String, skinIssue: String) {
        context.dataStore.edit { preferences ->
            preferences[NAME_KEY] = name
            preferences[SKIN_TYPE_KEY] = skinType
            preferences[SKIN_ISSUE_KEY] = skinIssue
        }
    }

    suspend fun getUserProfileImagePath(): Flow<String?> {
        return context.dataStore.data.map { preferences ->
            preferences[USER_PROFILE_IMAGE_PATH]
        }
    }

    suspend fun saveUserProfileImagePath(path: String) {
        context.dataStore.edit { preferences ->
            preferences[USER_PROFILE_IMAGE_PATH] = path
        }
    }

    fun getSkinType(): Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[SKIN_TYPE_KEY]
    }

    fun getTreatmentGoal(): Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[TREATMENT_GOAL_KEY]
    }

    fun getSkinIssues(): Flow<List<String>> = context.dataStore.data.map { preferences ->
        preferences[SKIN_ISSUE_KEY]?.split(",") ?: emptyList()
    }
}