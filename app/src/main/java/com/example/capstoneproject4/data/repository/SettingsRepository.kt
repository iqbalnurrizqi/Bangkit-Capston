package com.example.capstoneproject4.data.repository

import com.example.capstoneproject4.data.local.DataStoreManager
import com.example.capstoneproject4.data.model.ProfileResponse
import com.example.capstoneproject4.data.remote.APIService
import kotlinx.coroutines.flow.first
import retrofit2.Response
import javax.inject.Inject

class SettingsRepository @Inject constructor(
    private val dataStoreManager: DataStoreManager,
    private val apiService: APIService
) {

    val darkMode = dataStoreManager.darkMode
    val pushNotification = dataStoreManager.pushNotification

    suspend fun setDarkMode(enabled: Boolean) = dataStoreManager.updateDarkMode(enabled)
    suspend fun setPushNotification(enabled: Boolean) = dataStoreManager.updatePushNotification(enabled)

    // Function to fetch user profile
    suspend fun fetchUserProfile(): Response<ProfileResponse> {
        val token = "Bearer ${dataStoreManager.getUserSession().first()}"
        return apiService.getUserProfile(token)
    }
}
