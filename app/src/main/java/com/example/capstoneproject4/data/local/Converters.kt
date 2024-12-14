package com.example.capstoneproject4.data.local

import androidx.room.TypeConverter
import com.example.capstoneproject4.data.model.SkinIssue
import com.example.capstoneproject4.data.model.SkinType
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {
    private val gson = Gson()

    @TypeConverter
    fun fromSkinType(skinType: SkinType): String {
        return gson.toJson(skinType)
    }

    @TypeConverter
    fun toSkinType(data: String): SkinType {
        return gson.fromJson(data, SkinType::class.java)
    }

    @TypeConverter
    fun fromSkinIssues(skinIssues: List<SkinIssue>?): String {
        return gson.toJson(skinIssues)
    }

    @TypeConverter
    fun toSkinIssues(data: String?): List<SkinIssue>? {
        val listType = object : TypeToken<List<SkinIssue>>() {}.type
        return gson.fromJson(data, listType)
    }
}