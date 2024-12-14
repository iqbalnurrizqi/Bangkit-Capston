package com.example.capstoneproject4.data.repository

import com.example.capstoneproject4.data.local.dao.AnalysisResultDao
import com.example.capstoneproject4.data.local.entity.AnalysisResultEntity
import com.example.capstoneproject4.data.model.SkinIssue
import com.example.capstoneproject4.data.remote.APIService
import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.time.Instant
import java.util.Locale
import javax.inject.Inject

class AnalysisResultRepository @Inject constructor(
    private val dao: AnalysisResultDao,
    private val apiService: APIService
) {

    fun getAnalysisResults(): Flow<List<AnalysisResultEntity>> {
        return dao.getAllAnalysisResults()
    }

    suspend fun deleteAnalysisResult(result: AnalysisResultEntity) {
        dao.deleteAnalysisResult(result)
    }

    suspend fun fetchAnalysisResultsFromServer(token: String): List<AnalysisResultEntity> {
        val response = apiService.getScanHistory("Bearer $token")
        return if (response.isSuccessful && response.body()?.status == "success") {
            val serverHistory = response.body()?.history?.map { history ->
                AnalysisResultEntity(
                    id = history.id,
                    userId = history.userId,
                    imageUri = history.image_uri,
                    skinType = history.skin_type.toString(),
                    skinIssues = history.skin_issues.map { issue ->
                        SkinIssue(
                            confidence = issue.confidence,
                            issue = issue.issue
                        )
                    },
                    timestamp = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
                        .parse(history.date)?.time ?: 0L
                )
            } ?: emptyList()
            // Save to local database
            dao.insertAnalysisResult(serverHistory)
            serverHistory
        } else {
            emptyList()
        }
    }

    suspend fun addAnalysisResult(result: AnalysisResultEntity) {
        dao.insertAnalysisResult(listOf(result))
    }

    fun getHistoryResults() = dao.getAllAnalysisResults()
}