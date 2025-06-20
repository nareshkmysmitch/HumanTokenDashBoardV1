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
    private var healthDataMap = hashMapOf<String, List<String>>()

    init {
        healthDataMap = fromHealthDataMap()
        println(
            "view model initialized --> ${viewModelScope.hashCode()}, ${this.hashCode()}, ${
                this.instanceOf(
                    HealthDataViewModel::class
                )
            }"
        )
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

    fun getFilteredMetrics(): List<BloodData?> {
        val uiState = _uiState.value
        val currentFilter = uiState.selectedFilter
        val searchQuery = uiState.searchQuery
        val filterMap = healthDataMap[currentFilter]
        val isNewData = currentFilter == AppConstants.NEW_DATA

        return uiState.metrics.filter { metric ->
            if (metric == null) return@filter false

            val matchesFilter = when {
                isNewData -> metric.isLatest == true
                currentFilter == null || currentFilter == AppConstants.ALL -> true
                else -> filterMap?.contains(metric.displayRating?.lowercase()) == true
            }

            val matchesSearch = searchQuery.isEmpty() ||
                    metric.displayName?.startsWith(searchQuery, ignoreCase = true) == true

            matchesFilter && matchesSearch
        }
    }

    fun getAvailableFilters(): List<String?> {
        return if (_uiState.value.metrics.isNotEmpty()) {
            listOf(
                AppConstants.ALL,
                AppConstants.LOW,
                AppConstants.NORMAL,
                AppConstants.HIGH,
                AppConstants.NEW_DATA
            )
        } else {
            listOf()
        }
    }

    private fun fromHealthDataMap(): HashMap<String, List<String>> {
        val ratingMap = hashMapOf<String, List<String>>()
        ratingMap[AppConstants.NORMAL] = listOf("none", "optimal", "normal")
        ratingMap[AppConstants.LOW] = listOf("very low", "low", "borderline low")
        ratingMap[AppConstants.HIGH] = listOf(
            "borderline high",
            "mildly high",
            "high",
            "very high",
            "urgent attention",
            "possibly due to non-cardiac",
            "possibly due to non-cardiac inflammation",
            "inflammation",
            "impaired glucose tolerance",
            "mildly elevated",
            "needs attention",
            "monitor",
            "increased cardiac risk"
        )
        return ratingMap
    }

    fun getHealthDataCount(currentFilter: String): Int {
        val metrics = _uiState.value.metrics

        if (currentFilter == AppConstants.ALL) {
            return metrics.size
        }

        if (currentFilter == AppConstants.NEW_DATA) {
            return metrics.count { it?.isLatest == true }
        }

        val filterList = healthDataMap[currentFilter] ?: return 0

        return metrics.count { metric ->
            val rating = metric?.displayRating?.lowercase()
            rating != null && filterList.contains(rating)
        }
    }

} 