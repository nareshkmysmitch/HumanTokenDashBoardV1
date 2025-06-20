package com.healthanalytics.android.presentation.screens.health

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.healthanalytics.android.BackHandler
import com.healthanalytics.android.data.api.BloodData
import com.healthanalytics.android.presentation.components.FilledAppButton
import com.healthanalytics.android.presentation.components.HorizontalBar
import com.healthanalytics.android.presentation.theme.AppColors
import com.healthanalytics.android.presentation.theme.FontFamily

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BiomarkerDetailScreen(
    biomarker: BloodData?,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    onNavigateFullReport: () -> Unit,
) {
    BackHandler(enabled = true, onBack = onNavigateBack)
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Biomarker Details", color = AppColors.White, fontFamily = FontFamily.bold()
                    )
                }, navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = AppColors.White
                        )
                    }
                }, colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = AppColors.AppBackgroundColor,
                    navigationIconContentColor = AppColors.White,
                    titleContentColor = AppColors.White
                )
            )
        }) { paddingValues ->
        Column(
            modifier = modifier.fillMaxSize().padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            BiomarkerHeader(biomarker)
            if (biomarker?.ranges?.isNotEmpty() == true && biomarker.value != null) {
                HorizontalBar(biomarker.ranges, biomarker.value)
            }
            //RangeGraph(biomarker)
            BiomarkerDescription(biomarker)

            FilledAppButton(
                onClick = onNavigateFullReport,
                modifier = Modifier.fillMaxWidth().padding(top = 24.dp, start = 20.dp, end = 20.dp),

                ) {
                Text("View Full Report", fontFamily = FontFamily.bold(), fontSize = 18.sp)
            }
        }
    }
}

@Composable
private fun BiomarkerHeader(biomarker: BloodData?) {

    Column(
        modifier = Modifier.fillMaxWidth().padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = biomarker?.displayName ?: "",
                    style = MaterialTheme.typography.headlineMedium,
                    fontFamily = FontFamily.bold()
                )
                Text(
                    text = "Blood Biomarker",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontFamily = FontFamily.medium()
                )
            }
            StatusChip(status = biomarker?.displayRating ?: "")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Current Value",
            style = MaterialTheme.typography.titleMedium,
            fontFamily = FontFamily.bold()
        )
        Text(
            text = "${biomarker?.value} ${biomarker?.unit}",
            style = MaterialTheme.typography.headlineLarge,
            fontFamily = FontFamily.pilBold()
        )
    }
}


@Composable
private fun BiomarkerDescription(biomarker: BloodData?) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(16.dp)
    ) {
        if (!biomarker?.shortDescription.isNullOrBlank()) {
            Text(
                text = "Description",
                style = MaterialTheme.typography.titleMedium,
                fontFamily = FontFamily.bold()
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = biomarker.shortDescription,
                style = MaterialTheme.typography.bodyMedium,
                color = AppColors.White,
                fontFamily = FontFamily.medium()
            )
        }
    }
} 