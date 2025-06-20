package com.healthanalytics.android.presentation.screens.diagnostic

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.healthanalytics.android.data.api.ApiService
import com.healthanalytics.android.data.models.home.BloodProductList
import com.healthanalytics.android.data.models.home.DiagnosticUiState
import com.healthanalytics.android.data.models.home.LocalDiagnostic
import com.healthanalytics.android.data.repositories.PreferencesRepository
import com.healthanalytics.android.utils.AppConstants
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class DiagnosticViewModel(
    private val apiService: ApiService,
    private val preferencesRepository: PreferencesRepository,
) : ViewModel() {

    private val _accessToken = MutableStateFlow<String?>(null)
    val accessToken: StateFlow<String?> = _accessToken.asStateFlow()

    init {
        viewModelScope.launch {
            preferencesRepository.accessToken.collect { token ->
                println("Access Token Updated: $token")
                _accessToken.value = token
            }
        }
    }

    private val _uiState = MutableStateFlow(DiagnosticUiState())
    val uiState: StateFlow<DiagnosticUiState> = _uiState.asStateFlow()

    fun updateFilter(filter: String?) {
        _uiState.update { it.copy(selectedCategory = filter) }
    }

    fun loadDiagnostic(accessToken: String) {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true) }
                val diagnostics = apiService.getDiagnostic(accessToken)
                val diagnosticsList = fromDiagnosticDisplay(diagnostics?.bloodProductList)
                _uiState.update {
                    it.copy(
                        diagnostics = diagnosticsList,
                        selectedCategory = AppConstants.ALL,
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

    private fun fromDiagnosticDisplay(diagnostics: BloodProductList?): List<LocalDiagnostic> {
        val diagnosticsList = buildList {
            diagnostics?.packages.orEmpty().forEach { data ->
                add(
                    LocalDiagnostic(
                        id = data.id,
                        name = data.name,
                        type = data.tags?.firstOrNull(),
                        reportGenerationHr = data.reportGenerationHr,
                        price = data.packageProduct?.price,
                        description = data.description,
                        note = data.content?.why,
                        sampleType = data.packageProduct?.type,
                        fastingDuration = data.fastingDurationHr,
                        isFastingRequired = data.isFastingRequired,
                        bioMaker = data.tests
                    )
                )
            }

            diagnostics?.tests.orEmpty().forEach { data ->
                add(
                    LocalDiagnostic(
                        id = data.id,
                        name = data.name,
                        type = data.type,
                        reportGenerationHr = data.reportGenerationHr,
                        price = data.product?.price,
                        description = data.description,
                        note = data.content?.why,
                        sampleType = data.product?.type,
                        fastingDuration = data.fastingDurationHr,
                        isFastingRequired = data.isFastingRequired,
                        bioMaker = emptyList()
                    )
                )
            }
        }
        return diagnosticsList
        // Now diagnosticsList is available with all combined LocalDiagnostic items
        // Do something with diagnosticsList if needed
    }

    fun getDiagnosticFiler(): List<String?> {
        return if (_uiState.value.diagnostics.isNotEmpty()) {
          listOf(AppConstants.ALL) +
                    uiState.value.diagnostics.map { it.type ?: "" }.distinct()
        } else {
            listOf()
        }
    }


}