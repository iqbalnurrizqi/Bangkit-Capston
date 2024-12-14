package com.example.capstoneproject4.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.capstoneproject4.data.local.entity.AnalysisResultEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AnalysisResultDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAnalysisResult(result: List<AnalysisResultEntity>)

    @Query("SELECT * FROM analysis_results ORDER BY timestamp DESC")
    fun getAllAnalysisResults(): Flow<List<AnalysisResultEntity>>

    @Delete
    suspend fun deleteAnalysisResult(result: AnalysisResultEntity)
}