package com.example.capstoneproject4.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.capstoneproject4.data.local.dao.AnalysisResultDao
import com.example.capstoneproject4.data.local.entity.AnalysisResultEntity

@Database(
    entities = [AnalysisResultEntity::class],
    version = 3,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun analysisResultDao(): AnalysisResultDao
}