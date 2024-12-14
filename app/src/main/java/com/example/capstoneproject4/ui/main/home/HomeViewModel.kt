package com.example.capstoneproject4.ui.main.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.capstoneproject4.data.model.AddRoutineResponse
import com.example.capstoneproject4.data.model.HomeResponse
import com.example.capstoneproject4.data.model.RoutineRequest
import com.example.capstoneproject4.data.repository.HomeRepository
import kotlinx.coroutines.launch

class HomeViewModel(private val repository: HomeRepository) : ViewModel() {

    private val _homeData = MutableLiveData<HomeResponse>()
    val homeData: LiveData<HomeResponse> get() = _homeData

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    fun fetchHomeData(token: String) {
        _isLoading.postValue(true)
        viewModelScope.launch {
            try {
                val response = repository.getHomeData(token)
                if (response.isSuccessful) {
                    _homeData.postValue(response.body())
                } else {
                    _errorMessage.postValue("Error: ${response.code()} - ${response.message()}")
                }
            } catch (e: Exception) {
                _errorMessage.postValue("Exception: ${e.message}")
            } finally {
                _isLoading.postValue(false)
            }
        }
    }

    private val _addRoutineResult = MutableLiveData<AddRoutineResponse>()
    val addRoutineResult: LiveData<AddRoutineResponse> get() = _addRoutineResult

    private val _addRoutineError = MutableLiveData<String>()
    val addRoutineError: LiveData<String> get() = _addRoutineError

    fun addProductToRoutine(token: String, productId: String, productName: String, time: String) {
        viewModelScope.launch {
            try {
                val request = RoutineRequest(
                    product_id = productId,
                    product_name = productName,
                    time = time
                )
                val response = repository.addProductToRoutine(token, request)
                if (response.isSuccessful) {
                    _addRoutineResult.postValue(response.body())
                } else {
                    _addRoutineError.postValue("Error: ${response.code()} - ${response.message()}")
                }
            } catch (e: Exception) {
                _addRoutineError.postValue("Exception: ${e.message}")
            }
        }
    }
}
