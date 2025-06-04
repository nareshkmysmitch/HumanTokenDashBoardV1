package com.healthanalytics.android.presentation.health

import androidx.lifecycle.ViewModel
import com.healthanalytics.android.data.api.ApiService
import com.healthanalytics.android.data.api.BloodData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class HealthDataViewModel(
    private val apiService: ApiService,
) : ViewModel() {
    private val _uiState = MutableStateFlow(HealthDataUiState())
    val uiState: StateFlow<HealthDataUiState> = _uiState.asStateFlow()

    suspend fun loadHealthMetrics(accessToken: String) {
        try {
            _uiState.update { it.copy(isLoading = true) }
            val metrics = apiService.getHealthMetrics(accessToken)
            _uiState.update {
                it.copy(
                    metrics = metrics ?: emptyList(),
                    isLoading = false,
                    lastUpdated = ""
                )
            }
        } catch (e: Exception) {
            _uiState.update {
                it.copy(
                    error = e.message ?: "Failed to load health metrics",
                    isLoading = false
                )
            }
        }
    }

    fun updateFilter(filter: String?) {
        _uiState.update { it.copy(selectedFilter = filter) }
    }

    fun getFilteredMetrics(): List<BloodData?> {
        val currentFilter = _uiState.value.selectedFilter
        return if (currentFilter == null) {
            _uiState.value.metrics
        } else {
            _uiState.value.metrics.filter { it?.displayRating == currentFilter }
        }
    }
}

data class HealthDataUiState(
    val metrics: List<BloodData?> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val selectedFilter: String? = null,
    val lastUpdated: String = "",
) 