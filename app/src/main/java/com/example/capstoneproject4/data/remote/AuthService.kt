package com.example.capstoneproject4.data.remote

import com.example.capstoneproject4.data.model.ForgotResponse
import com.example.capstoneproject4.data.model.LoginRequest
import com.example.capstoneproject4.data.model.LoginResponse
import com.example.capstoneproject4.data.model.RegisterRequest
import com.example.capstoneproject4.data.model.RegisterResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthService {
    @POST("auth/login")
    fun login(@Body loginRequest: LoginRequest): Call<LoginResponse>

    @POST("auth/register")
    fun register(@Body registerRequest: RegisterRequest): Call<RegisterResponse>

    @POST("auth/forgot-password")
    fun forgotPassword(@Body emailRequest: Map<String, String>): Call<ForgotResponse>
}