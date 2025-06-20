package com.healthanalytics.android.presentation.preferences

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.healthanalytics.android.data.models.PreferencesUiState
import com.healthanalytics.android.data.repositories.PreferencesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


class PreferencesViewModel(
    private val preferencesRepository: PreferencesRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(PreferencesUiState<String?>())
    val uiState: StateFlow<PreferencesUiState<String?>> = _uiState.asStateFlow()

    private val _profileId = MutableStateFlow<String?>(null)
    val profileId: StateFlow<String?> = _profileId.asStateFlow()

    init {
        observerAccessToken()
    }

    private fun observerAccessToken() {
        viewModelScope.launch {
            preferencesRepository.accessToken
                .onStart {
                    _uiState.update { it.copy(isLoading = true) }
                }
                .catch { e ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = e.message ?: "Unknown error occurred"
                        )
                    }
                }
                .collect { tokenState ->
                    _uiState.update {
                        it.copy(
                            data = tokenState,
                            isLoading = false,
                            error = null
                        )
                    }
                }
        }

        viewModelScope.launch {
            preferencesRepository.profileId.collect { id ->
                _profileId.value = id
            }
        }
    }

    fun saveAccessToken(token: String) {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true) }
                preferencesRepository.saveAccessToken(token)
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Failed to save token"
                    )
                }
            }
        }
    }

    fun clearAccessToken() {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true) }
                preferencesRepository.clearAllData()
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Failed to clear token"
                    )
                }
            }
        }
    }

}