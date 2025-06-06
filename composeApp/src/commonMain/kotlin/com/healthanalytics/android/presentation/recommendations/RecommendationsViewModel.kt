package com.healthanalytics.android.presentation.recommendations

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.healthanalytics.android.data.api.ApiService
import com.healthanalytics.android.data.models.Recommendation
import com.healthanalytics.android.data.models.RecommendationsUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


class RecommendationsViewModel(private val apiService: ApiService) : ViewModel() {
    private val _uiState = MutableStateFlow(RecommendationsUiState())
    val uiState: StateFlow<RecommendationsUiState> = _uiState.asStateFlow()

    fun updateSelectedCategory(category: String?) {
        _uiState.update { it.copy(selectedCategory = category) }
    }

    fun getFilteredRecommendations(): List<Recommendation> {
        return _uiState.value.recommendations.filter { recommendation ->
            recommendation.category?.equals(
                _uiState.value.selectedCategory,
                ignoreCase = true
            ) == true
        }
    }

    fun getAvailableCategories(): List<String> {
        return _uiState.value.recommendations
            .map { it.category ?: "" }
            .distinct()
    }

    fun getCategoryCount(category: String): Int {
        return _uiState.value.recommendations.count {
            it.category.equals(category, ignoreCase = true)
        }
    }

    fun loadRecommendations(accessToken: String) {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true) }
                val recommendations = apiService.getRecommendations(accessToken)
                val categories =
                    recommendations?.map { it.category ?: "" }?.distinct() ?: emptyList()
                _uiState.update {
                    it.copy(
                        recommendations = recommendations ?: emptyList(),
                        selectedCategory = categories.firstOrNull(),
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Failed to load recommendations"
                    )
                }
            }
        }
    }
} 