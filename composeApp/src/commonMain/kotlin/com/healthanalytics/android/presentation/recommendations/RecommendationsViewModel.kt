package com.healthanalytics.android.presentation.recommendations

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.healthanalytics.android.data.api.ApiService
import com.healthanalytics.android.data.models.ActionPlanUiState
import com.healthanalytics.android.data.models.Recommendation
import com.healthanalytics.android.data.models.RecommendationsUiState
import com.healthanalytics.android.data.models.RemoveRecommendationRequest
import com.healthanalytics.android.data.models.RemoveSupplementsRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RecommendationsViewModel(private val apiService: ApiService) : ViewModel() {
    private val _uiState = MutableStateFlow(RecommendationsUiState())
    val uiState: StateFlow<RecommendationsUiState> = _uiState.asStateFlow()

    private val _selectedTab = MutableStateFlow(RecommendationsTab.RECOMMENDATIONS)
    val selectedTab: StateFlow<RecommendationsTab> = _selectedTab

    fun setSelectedTab(tab: RecommendationsTab) {
        _selectedTab.value = tab
    }

    fun updateSelectedCategory(category: String?) {
        _uiState.update { it.copy(selectedCategory = category) }
    }

    fun getFilteredRecommendations(): List<Recommendation> {
        return _uiState.value.recommendations.filter { recommendation ->
            recommendation.category?.equals(
                _uiState.value.selectedCategory, ignoreCase = true
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
                        isLoading = false, error = e.message ?: "Failed to load recommendations"
                    )
                }
            }
        }
    }


    private val _uiActionState = MutableStateFlow(ActionPlanUiState())
    val uiActionState: StateFlow<ActionPlanUiState> = _uiActionState.asStateFlow()

    fun updateActionCategory(category: String) {
        _uiActionState.update { it.copy(selectedCategory = category) }
    }

    fun getFilteredActions(): List<Recommendation> {
        return _uiActionState.value.recommendations.filter { recommendation ->
            recommendation.actions?.any { action ->
                action.user_recommendation_actions.isNotEmpty()
            } == true && recommendation.actions.firstOrNull()?.user_recommendation_actions?.firstOrNull()?.is_completed == true
                    && (_uiActionState.value.selectedCategory == "All" ||
                    recommendation.category?.equals(
                        _uiActionState.value.selectedCategory,
                        ignoreCase = true
                    ) == true)
        }
    }

    fun getActionAvailableCategories(): List<String> {
        return listOf("All") + _uiActionState.value.recommendations
            .map { it.category ?: "" }
            .distinct()
    }

    fun getActionTotalItems(): Int {
        return _uiActionState.value.recommendations.count { recommendation ->
            recommendation.actions?.any { action ->
                action.user_recommendation_actions.isNotEmpty()
            } == true && recommendation.actions.firstOrNull()?.user_recommendation_actions?.firstOrNull()?.is_completed == true
        }
    }

    fun loadActionRecommendations(accessToken: String) {
        viewModelScope.launch {
            try {
                _uiActionState.update { it.copy(isLoading = true) }
                val recommendations = apiService.getRecommendations(accessToken)
                _uiActionState.update {
                    it.copy(
                        recommendations = recommendations ?: emptyList(),
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _uiActionState.update {
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
                _uiActionState.update { it.copy(isLoading = true) }

                val action = recommendation.actions?.firstOrNull()
                val userAction = action?.user_recommendation_actions?.firstOrNull()

                if (action != null && userAction != null) {
                    val request = RemoveRecommendationRequest(
                        profile_id = "65", // TODO: Get from user profile
                        health_profile_id = "65", // TODO: Get from user profile
                        food_profile_id = "65", // TODO: Get from user profile
                        reminder_id = userAction.event_id ?: "",
                        occurrence_id = "1",
                        recommendation_id = recommendation.id,
                        action_id = action.id,
                        event_selection = "all",
                        module = "recommendation"
                    )

                    val success = apiService.removeRecommendation(accessToken, request)
                    if (success == true) {
                        // Reload recommendations after successful removal
                        loadActionRecommendations(accessToken)
                    } else {
                        _uiActionState.update {
                            it.copy(
                                isLoading = false,
                                error = "Failed to remove recommendation"
                            )
                        }
                    }
                }
            } catch (e: Exception) {
                _uiActionState.update {
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Failed to remove recommendation"
                    )
                }
            }
        }
    }

    fun removeSupplements(accessToken: String, recommendation: Recommendation) {
        viewModelScope.launch {
            try {
                _uiActionState.update { it.copy(isLoading = true) }

                val action = recommendation.actions?.firstOrNull()
                val userAction = action?.user_recommendation_actions?.firstOrNull()

                if (action != null && userAction != null) {
                    val request = RemoveSupplementsRequest(
                        profile_id = "65", // TODO: Get from user profile
                        reminder_id = null,
                        occurrence_id = "1",
                        recommendation_id = recommendation.id,
                        action_id = action.id,
                        is_mock = false,
                        medicine_id = userAction.medicine_id,
                        event_selection = "all",
                        module = "recommendation"
                    )

                    val success = apiService.removeSupplements(accessToken, request)
                    if (success == true) {
                        // Reload recommendations after successful removal
                        loadActionRecommendations(accessToken)
                    } else {
                        _uiActionState.update {
                            it.copy(
                                isLoading = false,
                                error = "Failed to remove recommendation"
                            )
                        }
                    }
                }
            } catch (e: Exception) {
                _uiActionState.update {
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Failed to remove recommendation"
                    )
                }
            }
        }
    }

}