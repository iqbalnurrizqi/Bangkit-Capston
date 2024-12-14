package com.example.capstoneproject4.data.model

data class LoginResponse(
    val `data`: String,
    val message: String,
    val status: Boolean,
    val statusCode: Int
)