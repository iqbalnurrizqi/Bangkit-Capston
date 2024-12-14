package com.example.capstoneproject4.data.model

data class History(
    val id: String,
    val userId: String,
    val image_uri: String,
    val skin_type: String,
    val skin_issues: List<SkinIssue>,
    val date: String
)