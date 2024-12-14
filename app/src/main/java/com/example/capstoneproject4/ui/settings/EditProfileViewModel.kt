package com.example.capstoneproject4.ui.settings

import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.capstoneproject4.data.local.DataStoreManager
import com.example.capstoneproject4.data.model.DataX
import com.example.capstoneproject4.data.model.ProfileResponse
import com.example.capstoneproject4.data.model.UpdateProfileRequest
import com.example.capstoneproject4.data.model.UpdateProfileResponse
import com.example.capstoneproject4.data.repository.UserRepository
import com.example.capstoneproject4.utils.FileUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okio.BufferedSink
import retrofit2.Response
import java.io.File
import javax.inject.Inject

@HiltViewModel
class EditProfileViewModel @Inject constructor(
    private val dataStoreManager: DataStoreManager,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _userProfile = MutableLiveData<DataX>()
    val userProfile: LiveData<DataX> get() = _userProfile

    private val _loadingState = MutableLiveData<Boolean>()
    val loadingState: LiveData<Boolean> get() = _loadingState

    private val _errorState = MutableLiveData<String?>()
    val errorState: LiveData<String?> get() = _errorState

    fun loadUserProfile() {
        viewModelScope.launch {
            try {
                _loadingState.value = true
                val token = dataStoreManager.getUserSession().first() ?: throw Exception("Token not found")
                val response = userRepository.getUserProfile(token)
                _userProfile.value = response.data.data
            } catch (e: Exception) {
                _errorState.value = e.message
            } finally {
                _loadingState.value = false
            }
        }
    }

    suspend fun updateUserProfile(token: String, updateRequest: UpdateProfileRequest): Response<UpdateProfileResponse> {
        return userRepository.updateUserProfile(token, updateRequest)
    }

    fun uploadPhoto(context: Context, photoUri: Uri, onResult: (String?) -> Unit) {
        viewModelScope.launch {
            try {
                // Convert URI to File
                val inputStream = context.contentResolver.openInputStream(photoUri)
                    ?: throw Exception("Unable to open InputStream from URI")
                val tempFile = File.createTempFile("upload", ".jpg", context.cacheDir)
                tempFile.outputStream().use { outputStream ->
                    inputStream.copyTo(outputStream)
                }

                val requestFile = tempFile.asRequestBody("image/jpeg".toMediaTypeOrNull())
                val body = MultipartBody.Part.createFormData("photo", tempFile.name, requestFile)

                val token = dataStoreManager.getUserSession().first()
                val response = userRepository.uploadProfilePhoto(token.toString(), body)
                if (response.isSuccessful) {
                    val photoUrl = response.body()?.photo_url
                    dataStoreManager.saveUserProfileImagePath(photoUrl ?: "")
                    onResult(photoUrl)
                } else {
                    Log.e("EditProfileViewModel", "Upload photo failed: ${response.errorBody()?.string()}")
                    onResult(null)
                }
            } catch (e: Exception) {
                Log.e("EditProfileViewModel", "Error uploading photo: ${e.message}")
                onResult(null)
            }
        }
    }

}