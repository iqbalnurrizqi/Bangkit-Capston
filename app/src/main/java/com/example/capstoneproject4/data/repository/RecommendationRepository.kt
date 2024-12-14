package com.example.capstoneproject4.data.repository

import com.example.capstoneproject4.data.model.RecommendationRequest
import com.example.capstoneproject4.data.model.RecommendationResponse
import com.example.capstoneproject4.data.remote.APIService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RecommendationRepository(private val apiService: APIService) {

    suspend fun getRecommendations(
        token: String,
        request: RecommendationRequest
    ): RecommendationResponse {
        return withContext(Dispatchers.IO) {
            apiService.getRecommendations(token, request)
        }
    }
}