package com.healthanalytics.android.presentation.screens.symptoms

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.healthanalytics.android.data.api.ApiService
import com.healthanalytics.android.data.models.Symptom
import com.healthanalytics.android.data.repositories.PreferencesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class SymptomsState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val symptoms: Map<String, List<Symptom>> = emptyMap(),
    val selectedSymptoms: Set<String> = emptySet()
)

class SymptomsViewModel(
    private val api: ApiService,
    private val preferencesRepository: PreferencesRepository
) : ViewModel() {
    private val _state = MutableStateFlow(SymptomsState())
    val state: StateFlow<SymptomsState> = _state.asStateFlow()

    private val _accessToken = MutableStateFlow<String?>(null)

    init {
        viewModelScope.launch {
            preferencesRepository.accessToken.collect { token ->
                _accessToken.value = token
                if (token != null) {
                    loadSymptoms()
                }
            }
        }
    }

    suspend fun loadSymptoms() {
        _state.update { it.copy(isLoading = true) }
        if (_accessToken.value == null) {
            _state.update {
                it.copy(
                    isLoading = false,
                    error = "Access token not available"
                )
            }
            return
        }
        
        try {
            val symptoms = api.getSymptoms(_accessToken.value ?: "")
            val groupedSymptoms = symptoms?.groupBy { it.category ?: "Other" } ?: emptyMap()
            _state.update {
                it.copy(
                    isLoading = false,
                    symptoms = groupedSymptoms,
                    error = null
                )
            }
        } catch (e: Exception) {
            _state.update {
                it.copy(
                    isLoading = false,
                    error = e.message ?: "Unknown error occurred"
                )
            }
        }
    }

    fun toggleSymptom(symptomId: String) {
        _state.update { currentState ->
            val updatedSelection = currentState.selectedSymptoms.toMutableSet()
            if (symptomId in updatedSelection) {
                updatedSelection.remove(symptomId)
            } else {
                updatedSelection.add(symptomId)
            }
            currentState.copy(selectedSymptoms = updatedSelection)
        }
    }

    fun getSelectedSymptomsCount(): Int = _state.value.selectedSymptoms.size
} 