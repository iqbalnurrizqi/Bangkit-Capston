package com.example.capstoneproject4.data.model

data class UploadPhotoResponse(
    val status: Boolean,
    val statusCode: Int,
    val message: String,
    val photo_url: String?
)
