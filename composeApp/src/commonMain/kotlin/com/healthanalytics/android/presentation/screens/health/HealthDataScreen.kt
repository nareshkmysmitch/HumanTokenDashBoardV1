package com.healthanalytics.android.presentation.screens.health

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.healthanalytics.android.data.models.home.BloodData
import com.healthanalytics.android.presentation.preferences.PreferencesViewModel
import com.healthanalytics.android.presentation.theme.AppColors
import com.healthanalytics.android.presentation.theme.Dimensions.size12dp
import com.healthanalytics.android.presentation.theme.Dimensions.size16dp
import com.healthanalytics.android.presentation.theme.FontFamily
import com.healthanalytics.android.presentation.theme.FontSize
import com.healthanalytics.android.utils.AppConstants
import com.healthanalytics.android.utils.capitalizeFirst
import org.koin.compose.koinInject

@Composable
fun HealthDataScreen(
    viewModel: HealthDataViewModel = koinInject(),
    prefs: PreferencesViewModel = koinInject(),
    onNavigateToDetail: (BloodData?) -> Unit = {},
) {
    val preferencesState by prefs.uiState.collectAsState()
    val uiState by viewModel.uiState.collectAsState()
    val selectedMetrics by viewModel.selectedMetrics.collectAsStateWithLifecycle()


    LaunchedEffect(preferencesState.data) {
        preferencesState.data?.let { token ->
            viewModel.loadHealthMetrics(token)
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().background(AppColors.Black).padding(top = size16dp)
    ) {
        if (uiState.isLoading || preferencesState.data == null) {
            Box(
                modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(size16dp),
                contentPadding = PaddingValues(horizontal = size12dp)
            ) {
                items(AppConstants.healthMetrics) { filter ->
                    val isSelected = selectedMetrics == filter
                    FilterChip(
                        selected = isSelected,
                        onClick = { viewModel.setSelectedMetric(filter) },
                        colors = androidx.compose.material3.FilterChipDefaults.filterChipColors(
                            containerColor = AppColors.Teal,
                            labelColor = AppColors.textPrimary,
                            selectedContainerColor = AppColors.darkPink,
                            selectedLabelColor = AppColors.White,
                            disabledLabelColor = AppColors.chipUnSelected
                        ),
                        border = androidx.compose.material3.FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = isSelected,
                            borderColor = Color.Transparent,
                        ),
                        label = {
                            Text(
                                text = filter.capitalizeFirst(),
                                fontSize = FontSize.textSize16sp,
                                fontFamily = FontFamily.medium(),
                                color = AppColors.textPrimary,
                                textAlign = TextAlign.Center
                            )
                        })
                }
            }
            when (selectedMetrics) {
                "blood" -> {

                    BioMarkerList(viewModel, onNavigateToDetail = onNavigateToDetail)
                }

                "symptoms" -> {

                    SymptomsList(viewModel, onSymptomsClick = {

                    })
                }
            }
        }
    }
}




