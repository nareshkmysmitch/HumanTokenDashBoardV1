package com.healthanalytics.android.presentation.screens.health

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.healthanalytics.android.data.api.ApiService
import com.healthanalytics.android.data.api.BloodData
import com.healthanalytics.android.data.api.HealthDataUiState
import com.healthanalytics.android.utils.AppConstants
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
    private val _uiState = MutableStateFlow(HealthDataUiState(isLoading = true))
    val uiState: StateFlow<HealthDataUiState> = _uiState.asStateFlow()

    init {
        println("view model initialized --> ${viewModelScope.hashCode()}, ${this.hashCode()}, ${this.instanceOf(HealthDataViewModel::class)}")
    }
    @OptIn(ExperimentalTime::class)
    suspend fun loadHealthMetrics(accessToken: String) {
        try {
//            _uiState.update { it.copy(isLoading = true) }
            val metrics = apiService.getHealthMetrics(accessToken)
            _uiState.update {
                it.copy(
                    metrics = metrics ?: emptyList(),
                    isLoading = false,
                    selectedFilter = AppConstants.ALL,
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


    fun getAvailableFilters(): List<String?> {
        return if (_uiState.value.metrics.isNotEmpty()) {
            listOf(AppConstants.ALL) + _uiState.value.metrics
                .map { it?.displayRating }
                .distinct()
        } else {
            listOf()
        }
    }

    fun getFilteredMetrics(): List<BloodData?> {
        val currentFilter = _uiState.value.selectedFilter?.lowercase()
        val searchQuery = _uiState.value.searchQuery.lowercase()

        val allMetrics = _uiState.value.metrics

        return allMetrics.filter { metric ->
            val rating = metric?.displayRating?.lowercase()
            val name = metric?.displayName?.lowercase()

            val matchesFilter = when {
                currentFilter.isNullOrEmpty() || currentFilter == "all" -> true
                currentFilter == NEW_DATA_FILTER -> metric?.isLatest == true
                else -> {
                    val categoryValues = ratingMap[currentFilter] ?: emptyList()
                    rating in categoryValues
                }
            }

            val matchesSearch = searchQuery.isEmpty() || name?.startsWith(searchQuery) == true

            matchesFilter && matchesSearch
        }
    }


    val ratingMap = mapOf(
        "normal" to listOf("none", "optimal", "normal"),
        "low" to listOf("very low", "low", "borderline low"),
        "high" to listOf(
            "borderline high", "mildly high", "high", "very high", "urgent attention",
            "possibly due to non-cardiac", "possibly due to non-cardiac inflammation",
            "inflammation", "impaired glucose tolerance", "mildly elevated",
            "needs attention", "monitor", "increased cardiac risk"
        )
    )


    fun getAvailableFilterGroups(availableFilters: List<String?>): List<String> {
        val lowercaseFilters = availableFilters.map { it?.lowercase() ?: "No Data" }

        val categoryFilters = ratingMap.filter { (_, values) ->
            values.any { lowercaseFilters.contains(it) }
        }.keys.toMutableList()

        return categoryFilters
    }

} 