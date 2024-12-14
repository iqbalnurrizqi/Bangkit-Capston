package com.example.capstoneproject4.data.remote

import com.example.capstoneproject4.data.model.AddRoutineResponse
import com.example.capstoneproject4.data.model.HomeResponse
import com.example.capstoneproject4.data.model.ProductDetailResponse
import com.example.capstoneproject4.data.model.ProfileResponse
import com.example.capstoneproject4.data.model.RecommendationRequest
import com.example.capstoneproject4.data.model.RecommendationResponse
import com.example.capstoneproject4.data.model.Routine
import com.example.capstoneproject4.data.model.RoutineRequest
import com.example.capstoneproject4.data.model.RoutineResponse
import com.example.capstoneproject4.data.model.ScanHistoryResponse
import com.example.capstoneproject4.data.model.ScanRequest
import com.example.capstoneproject4.data.model.ScanResponse
import com.example.capstoneproject4.data.model.ScanResultRequest
import com.example.capstoneproject4.data.model.UpdateProfileRequest
import com.example.capstoneproject4.data.model.UpdateProfileResponse
import com.example.capstoneproject4.data.model.UploadPhotoResponse
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path

interface APIService {

    @GET("home")
    suspend fun getHomeData(
        @Header("Authorization") token: String
    ): Response<HomeResponse>

    @POST("recommendations")
    suspend fun getRecommendations(
        @Header("Authorization") token: String,
        @Body requestBody: RecommendationRequest
    ): RecommendationResponse

    @POST("scan/scan-result")
    suspend fun uploadAnalysisResult(
        @Header("Authorization") token: String,
        @Body requestBody: ScanRequest
    ): ScanResponse

    @GET("user/profile")
    suspend fun getUserProfile(
        @Header("Authorization") token: String
    ): Response<ProfileResponse>

    @GET("scan/history")
    suspend fun getScanHistory(
        @Header("Authorization") token: String
    ): Response<ScanHistoryResponse>

    @GET("products/{product_id}")
    suspend fun getProductDetail(
        @Header("Authorization") token: String,
        @Path("product_id") productId: String
    ): Response<ProductDetailResponse>

    @PUT("profile")
    suspend fun updateUserProfile(
        @Header("Authorization") token: String,
        @Body updateProfileRequest: UpdateProfileRequest
    ): Response<UpdateProfileResponse>

    @Multipart
    @POST("user/profile/photo")
    suspend fun uploadProfilePhoto(
        @Header("Authorization") token: String,
        @Part photo: MultipartBody.Part
    ): Response<UploadPhotoResponse>

    @GET("routines")
    suspend fun getRoutines(
        @Header("Authorization") token: String
    ): Response<RoutineResponse>

    @POST("routines")
    suspend fun addRoutine(
        @Header("Authorization") token: String,
        @Body routineRequest: RoutineRequest
    ): Response<AddRoutineResponse>

}