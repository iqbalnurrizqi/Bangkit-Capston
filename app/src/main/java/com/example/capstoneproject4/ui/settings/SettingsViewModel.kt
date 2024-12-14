package com.example.capstoneproject4.ui.settings

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.capstoneproject4.data.model.ProfileResponse
import com.example.capstoneproject4.data.repository.SettingsRepository
import com.example.capstoneproject4.data.worker.NotificationWorker
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import androidx.work.Data
import androidx.work.workDataOf

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val repository: SettingsRepository
) : ViewModel() {

    val darkMode: LiveData<Boolean> = repository.darkMode.asLiveData()
    val pushNotification: LiveData<Boolean> = repository.pushNotification.asLiveData()

    private val _profileData = MutableLiveData<ProfileResponse?>()
    val profileData: LiveData<ProfileResponse?> = _profileData

    fun setDarkMode(enabled: Boolean) {
        viewModelScope.launch { repository.setDarkMode(enabled) }
    }

    fun setPushNotification(enabled: Boolean) {
        viewModelScope.launch { repository.setPushNotification(enabled) }
    }

    fun loadUserProfile() {
        viewModelScope.launch {
            try {
                val response = repository.fetchUserProfile()
                if (response.isSuccessful) {
                    Log.d("SettingsViewModel", "Profile response raw: ${response.body()}")
                    _profileData.postValue(response.body())
                } else {
                    Log.e("SettingsViewModel", "Failed to fetch profile: ${response.errorBody()?.string()}")
                    _profileData.postValue(null)
                }
            } catch (e: Exception) {
                Log.e("SettingsViewModel", "Error fetching profile", e)
                _profileData.postValue(null)
            }
        }
    }
}
