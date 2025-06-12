package com.healthanalytics.android.presentation.screens.health

import com.healthanalytics.android.data.api.BiomarkerReportData
import com.healthanalytics.android.data.repository.BiomarkerRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class BioMarkerReportViewModel(private val repository: BiomarkerRepository) {
    private val _uiState = MutableStateFlow<BioMarkerReportUiState>(BioMarkerReportUiState.Loading)
    val uiState: StateFlow<BioMarkerReportUiState> = _uiState.asStateFlow()

    suspend fun fetchBiomarkerReport(type: String, metricId: String, accessToken: String) {
        try {
            _uiState.update { BioMarkerReportUiState.Loading }

            val data = repository.getBiomarkerReport(accessToken, type, metricId)
            data?.let {
                _uiState.update { BioMarkerReportUiState.Success(data) }
            } ?: run {
                _uiState.update { BioMarkerReportUiState.Error("No data available") }
            }
        } catch (e: Exception) {
            _uiState.update { BioMarkerReportUiState.Error(e.message ?: "Unknown error occurred") }
        }
    }
}

sealed class BioMarkerReportUiState {
    data object Loading : BioMarkerReportUiState()
    data class Success(val data: BiomarkerReportData?) : BioMarkerReportUiState()
    data class Error(val message: String) : BioMarkerReportUiState()
} 