package com.example.capstoneproject4.data.model

data class ScanResultRequest(
    val image_uri: String?,
    val skin_type: SkinType,
    val skin_issues: List<SkinIssue>
)
