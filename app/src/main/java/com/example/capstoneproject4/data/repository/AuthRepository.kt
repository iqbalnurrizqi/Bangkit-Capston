package com.example.capstoneproject4.data.repository

import android.util.Log
import com.example.capstoneproject4.data.model.ForgotResponse
import com.example.capstoneproject4.data.model.LoginRequest
import com.example.capstoneproject4.data.model.LoginResponse
import com.example.capstoneproject4.data.model.RegisterRequest
import com.example.capstoneproject4.data.model.RegisterResponse
import com.example.capstoneproject4.data.remote.AuthService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AuthRepository(private val authService: AuthService) {

    fun login(email: String, password: String, callback: (Result<LoginResponse>) -> Unit) {
        val loginRequest = LoginRequest(email, password)
        authService.login(loginRequest).enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                Log.d("AuthRepository", "Response: ${response.body()}") // Debug respons
                if (response.isSuccessful && response.body() != null) {
                    val loginResponse = response.body()!!
                    callback(Result.success(loginResponse))
                } else {
                    Log.e("AuthRepository", "Error: ${response.errorBody()?.string()}")
                    callback(Result.failure(Exception("Login failed: ${response.errorBody()?.string()}")))
                }
            }
            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                callback(Result.failure(t))
            }
        })
    }

    fun register(email: String, password: String, name: String, callback: (Result<RegisterResponse>) -> Unit) {
        val registerRequest = RegisterRequest(email, password, name)
        authService.register(registerRequest).enqueue(object : Callback<RegisterResponse> {
            override fun onResponse(call: Call<RegisterResponse>, response: Response<RegisterResponse>) {
                if (response.isSuccessful) {
                    callback(Result.success(response.body()!!))
                } else {
                    callback(Result.failure(Exception("Register failed: ${response.errorBody()?.string()}")))
                }
            }

            override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                callback(Result.failure(t))
            }
        })
    }

    fun forgotPassword(email: String, callback: (Result<ForgotResponse>) -> Unit) {
        val emailRequest = mapOf("email" to email)
        authService.forgotPassword(emailRequest).enqueue(object : Callback<ForgotResponse> {
            override fun onResponse(call: Call<ForgotResponse>, response: Response<ForgotResponse>) {
                if (response.isSuccessful) {
                    callback(Result.success(response.body()!!))
                } else {
                    callback(Result.failure(Exception("Forgot password failed: ${response.errorBody()?.string()}")))
                }
            }

            override fun onFailure(call: Call<ForgotResponse>, t: Throwable) {
                callback(Result.failure(t))
            }
        })
    }
}