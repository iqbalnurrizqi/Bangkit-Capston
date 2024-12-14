package com.example.capstoneproject4.data.repository

import android.util.Log
import com.example.capstoneproject4.data.model.AddRoutineResponse
import com.example.capstoneproject4.data.model.HomeResponse
import com.example.capstoneproject4.data.model.Routine
import com.example.capstoneproject4.data.model.RoutineRequest
import com.example.capstoneproject4.data.remote.APIService
import retrofit2.Response

class HomeRepository(private val apiService: APIService) {

    suspend fun getHomeData(token: String): Response<HomeResponse> {
        Log.d("AuthorizationHeader", "Bearer $token")
        return apiService.getHomeData("Bearer $token")
    }

    suspend fun addProductToRoutine(token: String, request: RoutineRequest): Response<AddRoutineResponse> {
        return apiService.addRoutine("Bearer $token", request)
    }

}