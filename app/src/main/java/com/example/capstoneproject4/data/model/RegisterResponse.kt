package com.example.capstoneproject4.data.model

data class RegisterResponse(
    val message: String,
    val status: Boolean,
    val statusCode: Int,
    val userId: String
)