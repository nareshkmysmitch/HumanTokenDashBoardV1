package com.healthanalytics.android.presentation.screens.diagnostic

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import com.healthanalytics.android.presentation.theme.AppColors
import com.healthanalytics.android.presentation.theme.Dimensions.size12dp
import com.healthanalytics.android.presentation.theme.Dimensions.size16dp
import com.healthanalytics.android.presentation.theme.FontFamily
import com.healthanalytics.android.presentation.theme.FontSize
import com.healthanalytics.android.utils.capitalizeFirst

@Composable
fun DiagnosticScreen(viewModel: DiagnosticViewModel) {
    val accessToken by viewModel.accessToken.collectAsState()
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(accessToken) {
        accessToken?.let { token ->
            viewModel.loadDiagnostic(token)
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().background(AppColors.Black)
    ) {
        if (uiState.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            DiagnosticList(viewModel)
        }
    }
}

@Composable
fun DiagnosticList(viewModel: DiagnosticViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    val selectedFiler = uiState.selectedCategory

    Spacer(modifier = Modifier.height(size16dp))

    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(size16dp),
        contentPadding = PaddingValues(horizontal = size12dp)
    ) {
        items(viewModel.getDiagnosticFiler()) { filter ->
            val isSelected = selectedFiler == filter
            FilterChip(
                selected = isSelected,
                onClick = { viewModel.updateFilter(filter) },
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
                    filter?.capitalizeFirst()?.let {
                        Text(
                            text = it,
                            fontSize = FontSize.textSize16sp,
                            fontFamily = FontFamily.medium(),
                            color = AppColors.textPrimary,
                            textAlign = TextAlign.Center
                        )
                    }
                })
        }
    }
}
