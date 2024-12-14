package com.example.capstoneproject4.data.repository

import com.example.capstoneproject4.data.model.HomeResponse
import com.example.capstoneproject4.data.model.ProfileResponse
import com.example.capstoneproject4.data.model.UpdateProfileRequest
import com.example.capstoneproject4.data.model.UpdateProfileResponse
import com.example.capstoneproject4.data.model.UploadPhotoResponse
import com.example.capstoneproject4.data.remote.APIService
import okhttp3.MultipartBody
import retrofit2.Response
import javax.inject.Inject

class UserRepository @Inject constructor(
    private val apiService: APIService
) {

    suspend fun getUserProfile(token: String): ProfileResponse {
        return apiService.getUserProfile("Bearer $token").body()!!
    }

    suspend fun updateUserProfile(token: String, updateRequest: UpdateProfileRequest): Response<UpdateProfileResponse> {
        val bearerToken = if (token.startsWith("Bearer ")) token else "Bearer $token"
        return apiService.updateUserProfile(bearerToken, updateRequest)
    }

    suspend fun uploadProfilePhoto(token: String, photo: MultipartBody.Part): Response<UploadPhotoResponse> {
        return apiService.uploadProfilePhoto("Bearer $token", photo)
    }

}