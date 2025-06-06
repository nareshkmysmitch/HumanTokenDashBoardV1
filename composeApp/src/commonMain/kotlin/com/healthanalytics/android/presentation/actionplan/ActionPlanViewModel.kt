package com.healthanalytics.android.presentation.actionplan

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.healthanalytics.android.data.api.ApiService
import com.healthanalytics.android.data.models.Recommendation
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ActionPlanUiState(
    val recommendations: List<Recommendation> = emptyList(),
    val selectedCategory: String = "All",
    val isLoading: Boolean = false,
    val error: String? = null
)

class ActionPlanViewModel(private val apiService: ApiService) : ViewModel() {
    private val _uiState = MutableStateFlow(ActionPlanUiState())
    val uiState: StateFlow<ActionPlanUiState> = _uiState.asStateFlow()

    fun updateSelectedCategory(category: String) {
        _uiState.update { it.copy(selectedCategory = category) }
    }

    fun getFilteredRecommendations(): List<Recommendation> {
        return _uiState.value.recommendations.filter { recommendation ->
            recommendation.actions?.any { action ->
                action.user_recommendation_actions.isNotEmpty()
            } == true && (_uiState.value.selectedCategory == "All" || 
                recommendation.category?.equals(_uiState.value.selectedCategory, ignoreCase = true) == true)
        }
    }

    fun getAvailableCategories(): List<String> {
        return listOf("All") + _uiState.value.recommendations
            .filter { recommendation ->
                recommendation.actions?.any { action ->
                    action.user_recommendation_actions.isNotEmpty()
                } == true
            }
            .map { it.category ?: "" }
            .distinct()
    }

    fun getTotalItems(): Int {
        return _uiState.value.recommendations.count { recommendation ->
            recommendation.actions?.any { action ->
                action.user_recommendation_actions.isNotEmpty()
            } == true
        }
    }

    fun loadRecommendations(accessToken: String) {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true) }
                val recommendations = apiService.getRecommendations(accessToken)
                _uiState.update {
                    it.copy(
                        recommendations = recommendations ?: emptyList(),
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