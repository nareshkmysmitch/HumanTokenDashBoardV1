package com.healthanalytics.android.presentation.screens.health

import androidx.compose.foundation.Canvas
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import co.touchlab.kermit.Logger
import com.healthanalytics.android.BackHandler
import com.healthanalytics.android.data.api.BloodData
import com.healthanalytics.android.presentation.components.FilledAppButton
import com.healthanalytics.android.presentation.theme.AppColors
import kotlinx.serialization.json.JsonNull.content

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BiomarkerDetailScreen(
    biomarker: BloodData,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    onNavigateFullReport: () -> Unit
) {
    BackHandler(enabled = true, onBack = onNavigateBack)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Biomarker Details", color = AppColors.White
                    )
                }, navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = AppColors.White
                        )
                    }
                },  colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = AppColors.AppBackgroundColor,
                    navigationIconContentColor = AppColors.Black,
                    titleContentColor = AppColors.Black
                )
            )
        }) { paddingValues ->
        Column(
            modifier = modifier.fillMaxSize().padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            BiomarkerHeader(biomarker)
            RangeGraph(biomarker)
            BiomarkerDescription(biomarker)

            FilledAppButton(
                onClick = onNavigateFullReport,
                modifier = Modifier.fillMaxWidth().padding(top = 24.dp, start = 20.dp, end = 20.dp),

            ) {
                Text("View Full Report")
            }
        }
    }
}

@Composable
private fun BiomarkerHeader(biomarker: BloodData) {

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
                    text = biomarker.displayName ?: "",
                    style = MaterialTheme.typography.headlineMedium
                )
                Text(
                    text = "Blood Biomarker",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            StatusChip(status = biomarker.displayRating ?: "")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Current Value", style = MaterialTheme.typography.titleMedium
        )
        Text(
            text = "${biomarker.value} ${biomarker.unit}",
            style = MaterialTheme.typography.headlineLarge
        )
    }
}

@Composable
private fun RangeGraph(biomarker: BloodData) {
    val ranges = biomarker.ranges?.sortedBy { it.ratingRank } ?: emptyList()

    Logger.e { "BiomarkerDetailScreen ranges $ranges" }

    if (ranges.isEmpty()) return

    Column(
        modifier = Modifier.fillMaxWidth().padding(16.dp)
    ) {
        Canvas(
            modifier = Modifier.fillMaxWidth().height(40.dp)
        ) {
            val width = size.width
            val height = size.height
            val segmentWidth = width / ranges.size

            // Draw range segments
            ranges.forEachIndexed { index, range ->
                val color = when (range.displayRating?.lowercase()) {
                    "low" -> Color.Red.copy(alpha = 0.7f)
                    "normal" -> Color.Green.copy(alpha = 0.7f)
                    "high" -> Color.Red.copy(alpha = 0.7f)
                    "optimal" -> Color.Green.copy(alpha = 0.7f)
                    "borderline high" -> Color.Yellow.copy(alpha = 0.7f)
                    else -> Color.Gray.copy(alpha = 0.7f)
                }

                drawLine(
                    color = color,
                    start = Offset(index * segmentWidth, height / 2),
                    end = Offset((index + 1) * segmentWidth, height / 2),
                    strokeWidth = height,
                    cap = StrokeCap.Round
                )
            }

            // Draw current value marker
            val currentValue = biomarker.value ?: 0.0
            val minValue = ranges.first().range?.split(" ")?.first()?.toDoubleOrNull() ?: 0.0
            val maxValue = ranges.last().range?.split(" ")?.last()?.toDoubleOrNull() ?: 100.0
            val position = ((currentValue - minValue) / (maxValue - minValue) * width).coerceIn(
                "0".toDouble(), width.toDouble()
            )

            drawCircle(
                color = Color.White, radius = 12f, center = Offset(position.toFloat(), height / 2)
            )
            drawCircle(
                color = Color.Green, radius = 8f, center = Offset(position.toFloat(), height / 2)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            ranges.forEach { range ->
                Text(
                    text = range.displayRating ?: "",
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun BiomarkerDescription(biomarker: BloodData) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(16.dp)
    ) {
        Text(
            text = "Description", style = MaterialTheme.typography.titleLarge
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = biomarker.displayDescription ?: "", style = MaterialTheme.typography.bodyLarge
        )

        if (!biomarker.shortDescription.isNullOrBlank()) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = biomarker.shortDescription,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Black
            )
        }
    }
} 