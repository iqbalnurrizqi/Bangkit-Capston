package com.example.capstoneproject4.data.model

data class ScanRequest(
    val image_uri: String,
    val skin_issues: List<SkinIssue>,
    val skin_type: String
)