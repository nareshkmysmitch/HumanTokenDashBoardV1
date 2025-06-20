package com.healthanalytics.android.presentation.screens.health

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.healthanalytics.android.data.api.ApiService
import com.healthanalytics.android.data.api.HealthDataUiState
import com.healthanalytics.android.data.models.LoadingState
import com.healthanalytics.android.data.models.home.BloodData
import com.healthanalytics.android.data.models.home.SymptomsData
import com.healthanalytics.android.utils.AppConstants
import io.ktor.util.reflect.instanceOf
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.ExperimentalTime
import kotlin.time.Instant


class HealthDataViewModel(
    private val apiService: ApiService,
) : ViewModel() {
    private val _uiState = MutableStateFlow(HealthDataUiState(isLoading = true))
    val uiState: StateFlow<HealthDataUiState> = _uiState.asStateFlow()
    private var biomarkerMap = hashMapOf<String, List<String>>()

    init {
        biomarkerMap = fromBiomarkerMap()
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
                val bloodData = metrics?.blood?.bloodData
                val symptomsData = metrics?.symptoms?.symptomsData
                it.copy(
                    biomarker = bloodData ?: emptyList(),
                    symptomsData = symptomsData ?: emptyList(),
                    isLoading = false,
                    selectedFilter = AppConstants.ALL,
                    lastUpdated = bloodData?.maxByOrNull { data ->
                        Instant.parse(data?.createdAt.toString())
                    })
            }
        } catch (e: Exception) {
            _uiState.update {
                it.copy(
                    error = e.message ?: "Failed to load health metrics", isLoading = false
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

    fun getBiomarkerFilterList(): List<BloodData?> {
        val uiState = _uiState.value
        val currentFilter = uiState.selectedFilter
        val searchQuery = uiState.searchQuery.trim()
        val filterMap = biomarkerMap[currentFilter]
        val isNewData = currentFilter == AppConstants.NEW_DATA

        return uiState.biomarker.filter { metric ->
            if (metric == null) return@filter false

            val matchesFilter = when {
                isNewData -> metric.isLatest == true
                currentFilter == null || currentFilter == AppConstants.ALL -> true
                else -> filterMap?.contains(metric.displayRating?.lowercase()) == true
            }

            val matchesSearch = searchQuery.isBlank() || metric.displayName?.startsWith(
                searchQuery,
                ignoreCase = true
            ) == true || metric.reportedSymptoms?.any {
                it.name?.contains(
                    searchQuery, ignoreCase = true
                ) == true
            } == true || metric.causes?.any {
                it.name?.contains(
                    searchQuery, ignoreCase = true
                ) == true
            } == true

            matchesFilter && matchesSearch
        }
    }

    fun getBiomarkerFilter(): List<String?> {
        return if (_uiState.value.biomarker.isNotEmpty()) {
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

    private fun fromBiomarkerMap(): HashMap<String, List<String>> {
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
        val metrics = _uiState.value.biomarker

        if (currentFilter == AppConstants.ALL) {
            return metrics.size
        }

        if (currentFilter == AppConstants.NEW_DATA) {
            return metrics.count { it?.isLatest == true }
        }

        val filterList = biomarkerMap[currentFilter] ?: return 0

        return metrics.count { metric ->
            val rating = metric?.displayRating?.lowercase()
            rating != null && filterList.contains(rating)
        }
    }

    fun getSymptomsFilterList(): List<SymptomsData?> {
        val uiState = _uiState.value
        val searchQuery = uiState.searchQuery.trim()
        return uiState.symptomsData.filter { symptoms ->
            searchQuery.isBlank() || symptoms?.name?.startsWith(
                searchQuery,
                ignoreCase = true
            ) == true
        }
    }

    private val _selectedMetrics = MutableStateFlow<String?>(AppConstants.healthMetrics.first())
    val selectedMetrics: StateFlow<String?> = _selectedMetrics.asStateFlow()

    fun setSelectedMetric(metric: String) {
        viewModelScope.launch {
            _selectedMetrics.emit(metric)
        }

    }

    private val _resetAllSymptoms = MutableStateFlow(LoadingState())
    val resetAllSymptoms: StateFlow<LoadingState> =
        _resetAllSymptoms.asStateFlow()

    fun resetAllSymptoms(accessToken: String) {
        viewModelScope.launch {
            try {
                _resetAllSymptoms.update { it.copy(isLoading = true) }

                val isSuccess = apiService.resetAllSymptoms(accessToken)

                if (isSuccess) {
                    loadHealthMetrics(accessToken)
                }

                _resetAllSymptoms.update {
                    it.copy(
                        isLoading = false,
                        isSuccess = isSuccess
                    )
                }
            } catch (e: Exception) {
                _resetAllSymptoms.update {
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Reset all symptoms failed"
                    )
                }
            }
        }
    }

}