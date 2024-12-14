package com.example.capstoneproject4.ui.main.history

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.capstoneproject4.data.local.entity.AnalysisResultEntity
import com.example.capstoneproject4.data.repository.AnalysisResultRepository
import com.example.capstoneproject4.utils.NetworkUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val repository: AnalysisResultRepository,
    application: Application
) : AndroidViewModel(application) {

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    private val _history = MutableLiveData<List<AnalysisResultEntity>>()
    val history: LiveData<List<AnalysisResultEntity>> = _history

    fun loadHistory(token: String) {
        _isLoading.postValue(true)
        viewModelScope.launch {
            try {
                if (NetworkUtils.isInternetAvailable(getApplication())) {
                    val serverHistory = repository.fetchAnalysisResultsFromServer(token)
                    _history.postValue(serverHistory)
                } else {
                    _history.postValue(repository.getAnalysisResults().asLiveData().value)
                }
            } catch (e: Exception) {
                _errorMessage.postValue("Error: ${e.message}")
            } finally {
                _isLoading.postValue(false)
            }
        }
    }

    fun addAnalysisResult(result: AnalysisResultEntity) {
        viewModelScope.launch {
            repository.addAnalysisResult(result) // Tambahkan implementasi ini
        }
    }

    fun deleteAnalysisResult(result: AnalysisResultEntity) {
        viewModelScope.launch {
            repository.deleteAnalysisResult(result)
        }
    }
}
