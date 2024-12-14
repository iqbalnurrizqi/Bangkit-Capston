package com.example.capstoneproject4.ui.detail

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.capstoneproject4.data.local.DataStoreManager
import com.example.capstoneproject4.data.model.ProductDetail
import com.example.capstoneproject4.data.remote.APIService
import com.example.capstoneproject4.data.remote.RetrofitClient
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class ProductDetailViewModel @Inject constructor(
    private val dataStoreManager: DataStoreManager,
    private val apiService: APIService
) : ViewModel() {

    val productDetail = MutableLiveData<ProductDetail>()
    val errorMessage = MutableLiveData<String>()

    suspend fun fetchProductDetail(productId: String) {
        withContext(Dispatchers.IO) {
            try {
                val token = dataStoreManager.getUserSession().first() ?: ""
                if (token.isEmpty()) {
                    errorMessage.postValue("Authorization token is missing.")
                    return@withContext
                }

                val authHeader = "Bearer $token"
                val response = apiService.getProductDetail(authHeader, productId)

                if (response.isSuccessful) {
                    val productData = response.body()?.data
                    if (productData != null) {
                        productDetail.postValue(productData)
                    } else {
                        errorMessage.postValue("Product details are unavailable.")
                    }
                } else {
                    errorMessage.postValue("Failed to load product details: ${response.message()}")
                }
            } catch (e: Exception) {
                errorMessage.postValue("Failed to fetch product details.")
            }
        }
    }

}
