package com.healthanalytics.android.presentation.actionplan

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.healthanalytics.android.data.api.ApiService
import com.healthanalytics.android.data.models.ActionPlanUiState
import com.healthanalytics.android.data.models.Recommendation
import com.healthanalytics.android.data.models.RemoveRecommendationRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


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
                    recommendation.category?.equals(
                        _uiState.value.selectedCategory,
                        ignoreCase = true
                    ) == true)
        }
    }

    fun getAvailableCategories(): List<String> {
        return listOf("All") + _uiState.value.recommendations
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

    fun removeRecommendation(accessToken: String, recommendation: Recommendation) {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true) }

                val action = recommendation.actions?.firstOrNull()
                val userAction = action?.user_recommendation_actions?.firstOrNull()

                if (action != null && userAction != null) {
                    val request = RemoveRecommendationRequest(
                        profile_id = "65", // TODO: Get from user profile
                        health_profile_id = "65", // TODO: Get from user profile
                        food_profile_id = "65", // TODO: Get from user profile
                        reminder_id =  "1",
                        occurrence_id = "1",
                        recommendation_id = recommendation.id,
                        action_id = action.id
                    )

                    val success = apiService.removeRecommendation(accessToken, request)
                    if (success) {
                        // Reload recommendations after successful removal
                        loadRecommendations(accessToken)
                    } else {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                error = "Failed to remove recommendation"
                            )
                        }
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Failed to remove recommendation"
                    )
                }
            }
        }
    }
} 