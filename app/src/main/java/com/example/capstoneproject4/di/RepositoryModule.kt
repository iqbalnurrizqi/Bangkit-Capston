package com.example.capstoneproject4.di

import com.example.capstoneproject4.data.local.dao.AnalysisResultDao
import com.example.capstoneproject4.data.remote.APIService
import com.example.capstoneproject4.data.remote.RetrofitClient
import com.example.capstoneproject4.data.repository.AnalysisResultRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideAnalysisResultRepository(
        analysisResultDao: AnalysisResultDao,
        apiService: APIService
    ): AnalysisResultRepository {
        return AnalysisResultRepository(analysisResultDao, apiService)
    }
}

