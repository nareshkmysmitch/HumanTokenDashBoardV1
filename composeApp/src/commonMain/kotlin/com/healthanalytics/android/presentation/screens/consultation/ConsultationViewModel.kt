package com.healthanalytics.android.presentation.screens.consultation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.healthanalytics.android.data.api.ApiService
import com.healthanalytics.android.data.api.Product
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ConsultationconsultUiState(
    val products: List<Product?>? = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
)

class ConsultationViewModel(
    private val apiService: ApiService
) : ViewModel() {

    private val _consultUiState = MutableStateFlow(ConsultationconsultUiState())
    val consultUiState: StateFlow<ConsultationconsultUiState> = _consultUiState.asStateFlow()

    fun loadConsultations(accessToken: String) {
        viewModelScope.launch {
            try {
                _consultUiState.update { it.copy(isLoading = true) }
                val consultations = apiService.getConsultationServices(accessToken)

                _consultUiState.update {
                    it.copy(
                        products = consultations ?: emptyList(), isLoading = false
                    )
                }
            } catch (e: Exception) {
                _consultUiState.update {
                    it.copy(
                        isLoading = false, error = e.message ?: "Failed to load recommendations"
                    )
                }
            }
        }
    }
} 