package com.healthanalytics.android.presentation.preferences

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.healthanalytics.android.data.api.PreferencesUiState
import com.healthanalytics.android.data.repositories.PreferencesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch


class PreferencesViewModel(
    private val preferencesRepository: PreferencesRepository,
) : ViewModel() {


    init {
        observeAccessToken()
    }

    private val _uiState = MutableStateFlow(PreferencesUiState<String?>())
    val uiState: StateFlow<PreferencesUiState<String?>> = _uiState.asStateFlow()

    /**
     * Returns Flow<String?> of access token for UI to collect.
     * Emits loading state while fetching the token.
     * Errors are reflected in uiState.error.
     */
    private fun observeAccessToken() {
        viewModelScope.launch {
            preferencesRepository.accessToken
                .onStart {
                    // Before first emission, show loading and clear errors
                    _uiState.update { it.copy(isLoading = true, error = null) }
                }
                .onEach { token ->
                    // When token is received, update data and hide loading/error
                    _uiState.update { it.copy(isLoading = false, error = null, data = token) }
                }
                .catch { e ->
                    // If any error occurs, show error and hide loading
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = e.message ?: "Unexpected error"
                        )
                    }
                }
        }
    }

    fun saveAccessToken(token: String) {
        viewModelScope.launch {
            try {
                preferencesRepository.saveAccessToken(token)
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message ?: "Error saving token") }
            }
        }
    }

    fun clearAccessToken() {
        viewModelScope.launch {
            try {
                preferencesRepository.clearAllData()
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message ?: "Error clearing token") }
            }
        }
    }
}