package com.healthanalytics.android.presentation.preferences

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.healthanalytics.android.data.repositories.PreferencesRepository
import kotlinx.coroutines.launch


class PreferencesViewModel(
    private val preferencesRepository: PreferencesRepository,
) : ViewModel() {

    fun getAccessToken() = preferencesRepository.accessToken

    fun saveAccessToken(token: String) {
        viewModelScope.launch {
            preferencesRepository.saveAccessToken(token)
        }
    }
    /* private val _uiState = MutableStateFlow(PreferencesUiState())
     val uiState: StateFlow<PreferencesUiState> = _uiState.asStateFlow()

     init {
         viewModelScope.launch {
             preferencesRepository.accessToken
                 .catch { e ->
                     _uiState.update { it.copy(error = e.message) }
                 }
                 .collect { token ->
                     _uiState.update { it.copy(accessToken = token) }
                 }
         }
     }

     fun saveAccessToken(token: String) {
         viewModelScope.launch {
             try {
                 _uiState.update { it.copy(isLoading = true) }
                 preferencesRepository.saveAccessToken(token)
                 _uiState.update { it.copy(isLoading = false) }
             } catch (e: Exception) {
                 _uiState.update {
                     it.copy(
                         isLoading = false,
                         error = e.message
                     )
                 }
             }
         }
     }

     fun clearAccessToken() {
         viewModelScope.launch {
             try {
                 _uiState.update { it.copy(isLoading = true) }
                 preferencesRepository.clearAccessToken()
                 _uiState.update { it.copy(isLoading = false) }
             } catch (e: Exception) {
                 _uiState.update {
                     it.copy(
                         isLoading = false,
                         error = e.message
                     )
                 }
             }
         }
     }*/
}