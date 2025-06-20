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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import co.touchlab.kermit.Logger
import com.healthanalytics.android.BackHandler
import com.healthanalytics.android.data.api.BloodData
import com.healthanalytics.android.data.api.Correlation
import com.healthanalytics.android.presentation.components.FilledAppButton
import com.healthanalytics.android.presentation.components.HorizontalBar
import com.healthanalytics.android.presentation.theme.AppColors
import com.healthanalytics.android.presentation.theme.Dimensions
import com.healthanalytics.android.presentation.theme.Dimensions.size12dp
import com.healthanalytics.android.presentation.theme.FontFamily
import com.healthanalytics.android.presentation.theme.FontSize

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BiomarkerDetailScreen(
    biomarker: BloodData?,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    onNavigateFullReport: () -> Unit,
) {
    Logger.e("biomarker --> ${biomarker?.correlation}")
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

            if (biomarker?.correlation?.isNotEmpty() == true) {
                ConnectedBiomarkersCard(biomarker.correlation)
            }

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
                    fontFamily = FontFamily.medium(),
                    fontSize = FontSize.textSize16sp,
                    color = AppColors.textPrimaryColor
                )
                Text(
                    text = "Blood Biomarker",
                    fontFamily = FontFamily.regular(),
                    fontSize = FontSize.textSize14sp,
                    color = AppColors.inputHint,
                )
            }
            StatusChip(status = biomarker?.displayRating ?: "")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Current Value",
            fontFamily = FontFamily.medium(),
            fontSize = FontSize.textSize14sp,
            color = AppColors.inputHint,
        )
        Text(
            text = "${biomarker?.value} ${biomarker?.unit}",
            fontSize = FontSize.textSize20sp,
            color = AppColors.textPrimaryColor,
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
                fontFamily = FontFamily.medium(),
                fontSize = FontSize.textSize14sp,
                color = AppColors.textPrimaryColor,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = biomarker.shortDescription,
                fontFamily = FontFamily.regular(),
                fontSize = FontSize.textSize14sp,
                color = AppColors.White,
            )
        }
    }
}

@Composable
fun ConnectedBiomarkersCard(correlation: List<Correlation?>?) {
    Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
        Text(
            text = "Connected Biomarkers",
            fontFamily = FontFamily.medium(),
            fontSize = FontSize.textSize16sp,
            color = AppColors.textPrimaryColor,
        )
        Spacer(Modifier.height(Dimensions.size12dp))
        correlation?.forEach {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium,
                colors = CardDefaults.cardColors(containerColor = AppColors.CardGrey),
            ) {
                Column(modifier = Modifier.fillMaxWidth().padding(size12dp)) {
                    Text(
                        text = it?.sourceMetricName ?: "No Data",
                        fontFamily = FontFamily.medium(),
                        fontSize = FontSize.textSize14sp,
                        color = AppColors.textPrimaryColor,
                    )

                    Text(
                        text = it?.description ?: "No Data",
                        fontFamily = FontFamily.medium(),
                        fontSize = FontSize.textSize14sp,
                        color = AppColors.inputHint,
                    )
                }
            }
        }
    }
}