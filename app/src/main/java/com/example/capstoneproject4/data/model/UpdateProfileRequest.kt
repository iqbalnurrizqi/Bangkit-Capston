package com.example.capstoneproject4.data.model

data class UpdateProfileRequest(
    val name: String,
    val skin_type: String,
    val skin_issues: List<String>,
    val treatment_goal: String
)
