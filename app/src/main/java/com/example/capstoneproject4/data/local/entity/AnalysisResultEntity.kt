package com.example.capstoneproject4.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.capstoneproject4.data.model.SkinIssue

@Entity(tableName = "analysis_results")
data class AnalysisResultEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val imageUri: String,
    val skinType: String,
    val skinIssues: List<SkinIssue>,
    val timestamp: Long
)

