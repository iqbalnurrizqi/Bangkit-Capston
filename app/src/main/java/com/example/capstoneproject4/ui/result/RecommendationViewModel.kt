package com.example.capstoneproject4.ui.result

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.capstoneproject4.data.model.Data
import com.example.capstoneproject4.data.model.RecommendationRequest
import com.example.capstoneproject4.data.model.RecommendationResponse
import com.example.capstoneproject4.data.repository.RecommendationRepository
import kotlinx.coroutines.launch

class RecommendationViewModel(private val repository: RecommendationRepository) : ViewModel() {

    fun getRecommendations(
        token: String,
        request: RecommendationRequest,
        onSuccess: (Data) -> Unit, // Hanya menerima bagian data
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val response = repository.getRecommendations(token, request)
                onSuccess(response.data)
            } catch (e: Exception) {
                onError(e.message ?: "Unknown error occurred.")
            }
        }
    }

    class Factory(private val repository: RecommendationRepository) :
        ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(RecommendationViewModel::class.java)) {
                return RecommendationViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}