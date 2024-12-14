package com.example.capstoneproject4.data.model

// AddRoutineResponse.kt
data class AddRoutineResponse(
    val status: Boolean,
    val statusCode: Int,
    val message: String,
    val routineId: String
)
