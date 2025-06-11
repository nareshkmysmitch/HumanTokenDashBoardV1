package com.healthanalytics.android.presentation.health

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.healthanalytics.android.data.api.ApiService
import com.healthanalytics.android.data.api.BloodData
import com.healthanalytics.android.data.api.HealthDataUiState
import io.ktor.util.reflect.instanceOf
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlin.time.ExperimentalTime
import kotlin.time.Instant


class HealthDataViewModel(
    private val apiService: ApiService,
) : ViewModel() {
    private val _uiState = MutableStateFlow(HealthDataUiState())
    val uiState: StateFlow<HealthDataUiState> = _uiState.asStateFlow()

    init {
        println("view model initialized --> ${viewModelScope.hashCode()}, ${this.hashCode()}, ${this.instanceOf(HealthDataViewModel::class)}")
    }
    @OptIn(ExperimentalTime::class)
    suspend fun loadHealthMetrics(accessToken: String) {
        try {
            _uiState.update { it.copy(isLoading = true) }
            val metrics = apiService.getHealthMetrics(accessToken)
            _uiState.update {
                it.copy(
                    metrics = metrics ?: emptyList(),
                    isLoading = false,
                    lastUpdated = metrics?.maxByOrNull { bloodData ->
                        Instant.parse(bloodData?.createdAt.toString())
                    }
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

    fun updateSearchQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun getFilteredMetrics(): List<BloodData?> {
        val currentFilter = _uiState.value.selectedFilter
        val searchQuery = _uiState.value.searchQuery

        return _uiState.value.metrics.filter { metric ->
            val matchesFilter =
                currentFilter == null || currentFilter == "All" || metric?.displayRating == currentFilter
            val matchesSearch =
                searchQuery.isEmpty() || metric?.displayName?.startsWith(searchQuery) == true
            matchesFilter && matchesSearch
        }
    }

    fun getAvailableFilters(): List<String?> {
        return listOf("All") + _uiState.value.metrics
            .map { it?.displayRating }
            .distinct()
    }
} 