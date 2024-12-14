package com.example.capstoneproject4.data.model

data class DataX(
    val name: String,
    val email: String,
    val skin_type: String, // Ubah tipe dari SkinType ke String.
    val skin_issues: List<String>,
    val treatment_goal: String,
    val photo_url: String
)