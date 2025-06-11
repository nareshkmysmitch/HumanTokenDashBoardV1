package com.healthanalytics.android.data.repository

import com.healthanalytics.android.data.api.ApiService
import com.healthanalytics.android.data.api.BiomarkerReportData

interface BiomarkerRepository {
    suspend fun getBiomarkerReport(
        accessToken: String, type: String, metricId: String
    ): BiomarkerReportData?
}

class BiomarkerRepositoryImpl(
    private val apiService: ApiService
) : BiomarkerRepository {
    override suspend fun getBiomarkerReport(
        accessToken: String, type: String, metricId: String
    ): BiomarkerReportData? {
        return apiService.getBiomarkerReport(accessToken, type, metricId)
    }
} 