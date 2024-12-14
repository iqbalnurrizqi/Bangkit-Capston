package com.example.capstoneproject4.utils

import android.content.Context
import android.net.Uri
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

object FileUtils {
    fun createImageMultipart(context: Context, uri: Uri, key: String): MultipartBody.Part {
        val file = File(uri.path ?: "")
        val requestFile = file.asRequestBody(context.contentResolver.getType(uri)?.toMediaTypeOrNull())
        return MultipartBody.Part.createFormData(key, file.name, requestFile)
    }
}