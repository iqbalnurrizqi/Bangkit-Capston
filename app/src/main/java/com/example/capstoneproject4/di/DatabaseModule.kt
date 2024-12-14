package com.example.capstoneproject4.di

import android.content.Context
import androidx.room.Room
import com.example.capstoneproject4.data.local.AppDatabase
import com.example.capstoneproject4.data.local.DataStoreManager
import com.example.capstoneproject4.data.local.dao.AnalysisResultDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "app_database"
        ).fallbackToDestructiveMigration().build()


    @Provides
    @Singleton
    fun provideAnalysisResultDao(database: AppDatabase): AnalysisResultDao =
        database.analysisResultDao()


    @Provides
    @Singleton
    fun provideDataStoreManager(
        @ApplicationContext context: Context
    ): DataStoreManager {
        return DataStoreManager(context)
    }
}
