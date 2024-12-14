package com.example.capstoneproject4.data.model

// RoutineResponse.kt
data class RoutineResponse(
    val status: Boolean,
    val statusCode: Int,
    val routines: List<Routine>
)
