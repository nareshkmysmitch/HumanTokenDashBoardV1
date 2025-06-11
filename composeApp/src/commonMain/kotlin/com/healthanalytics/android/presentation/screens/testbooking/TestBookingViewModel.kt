package com.healthanalytics.android.presentation.screens.testbooking

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.healthanalytics.android.data.api.ApiService
import com.healthanalytics.android.data.api.Product
import com.healthanalytics.android.data.repositories.PreferencesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class TestBookingState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val availableTests: List<Product> = emptyList(),
    val selectedTests: Set<Product> = emptySet(),
    val totalAmount: Double = 0.0
)

class TestBookingViewModel(
    private val api: ApiService,
    private val preferencesRepository: PreferencesRepository
) : ViewModel() {
    private val _state = MutableStateFlow(TestBookingState())
    val state: StateFlow<TestBookingState> = _state.asStateFlow()

    private val _accessToken = MutableStateFlow<String?>(null)
    val accessToken: StateFlow<String?> = _accessToken

    init {
        viewModelScope.launch {
            preferencesRepository.accessToken.collect { token ->
                _accessToken.value = token
                if (token != null) {
                    loadTests(token)
                }
            }
        }
    }

    suspend fun loadTests(accessToken: String) {
//        _state.update { it.copy(isLoading = true) }
        try {
            val tests = api.getTestBookings(accessToken)
            _state.update { 
                it.copy(
                    isLoading = false,
                    availableTests = tests?.filterNotNull() ?: emptyList(),
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

    fun toggleTestSelection(test: Product) {
        _state.update { currentState ->
            val updatedSelection = currentState.selectedTests.toMutableSet()
            if (test in updatedSelection) {
                updatedSelection.remove(test)
            } else {
                updatedSelection.add(test)
            }
            
            val totalAmount = updatedSelection.sumOf { product ->
                product.price?.toDoubleOrNull() ?: 0.0
            }
            
            currentState.copy(
                selectedTests = updatedSelection,
                totalAmount = totalAmount
            )
        }
    }

    fun scheduleTests() {
        // TODO: Implement test scheduling
        val selectedTests = _state.value.selectedTests
        if (selectedTests.isEmpty()) return
        
        viewModelScope.launch {
            try {
                // TODO: Call API to schedule tests
                println("Scheduling tests: ${selectedTests.map { it.name }}")
            } catch (e: Exception) {
                _state.update { 
                    it.copy(error = e.message ?: "Failed to schedule tests")
                }
            }
        }
    }
} 