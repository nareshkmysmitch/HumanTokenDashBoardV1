package com.healthanalytics.android.presentation.screens.health

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import co.touchlab.kermit.Logger
import com.healthanalytics.android.BackHandler
import com.healthanalytics.android.data.api.BloodData
import com.healthanalytics.android.data.api.Cause
import com.healthanalytics.android.data.api.MetricData
import com.healthanalytics.android.data.api.ReportedSymptom
import com.healthanalytics.android.data.api.WellnessCategory
import com.healthanalytics.android.presentation.components.AppCard
import com.healthanalytics.android.presentation.preferences.PreferencesViewModel
import com.healthanalytics.android.presentation.theme.AppColors
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.koin.compose.koinInject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BioMarkerFullReportScreen(
    biomarker: BloodData,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    prefs: PreferencesViewModel = koinInject(),
    viewModel: BioMarkerReportViewModel = koinInject()
) {

    val preferencesState by prefs.uiState.collectAsState()
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(preferencesState.data) {
        preferencesState.data?.let { token ->
            prefs.saveAccessToken(token)
            viewModel.fetchBiomarkerReport("blood", biomarker.metricId ?: "", token)
        }
    }

    BackHandler(enabled = true, onBack = onNavigateBack)
    Logger.e { "BiomarkerDetailScreen $biomarker" }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                Text(
                    text = biomarker.displayName ?: "", color = AppColors.White
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
                containerColor = AppColors.PurpleTitle,
                navigationIconContentColor = AppColors.White,
                titleContentColor = AppColors.White
            )
            )
        }) { paddingValues ->
        when (val state = uiState) {
            is BioMarkerReportUiState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            is BioMarkerReportUiState.Success -> {
                LazyColumn(
                    modifier = modifier.fillMaxSize().padding(paddingValues),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        HeaderCard(biomarker, state.data?.releasedAt)
                    }

                    item {
                        TabSection(
                            state.data?.metricData?.firstOrNull()?.causes ?: emptyList(),
                            state.data?.metricData
                        )
                    }

                    item {
                        CorrelationsSection(
                            wellnessCategories = state.data?.wellnessCategories,
                            reportedSymptoms = state.data?.reportedSymptoms
                        )
                    }
                }
            }

            is BioMarkerReportUiState.Error -> {
                Box(
                    modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center
                ) {
                    Text(state.message)
                }
            }
        }
    }
}

@Composable
private fun HeaderCard(biomarker: BloodData, releasedAt: String?) {
    AppCard(
        modifier = Modifier.fillMaxWidth().padding(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = biomarker.displayName ?: "",
                    style = MaterialTheme.typography.headlineMedium,
                    color = AppColors.DarkPurple
                )
                StatusChip(status = biomarker.displayRating ?: "")
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "${biomarker.value} ${biomarker.unit}",
                style = MaterialTheme.typography.headlineLarge,
                color = AppColors.DarkPurple
            )

            Text(
                text = "Last Updated: ${formatDate(releasedAt ?: "")}",
                style = MaterialTheme.typography.bodyMedium,
                color = AppColors.DarkPurple.copy(alpha = 0.7f)
            )

            if (!biomarker.shortDescription.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = biomarker.shortDescription,
                    style = MaterialTheme.typography.bodyMedium,
                    color = AppColors.DarkPurple
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TabSection(causes: List<Cause>, metricData: List<MetricData>?) {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Why It Matters?", "Causes")

    val whyItMattersData = metricData?.firstOrNull { it.category == "why_it_matters" }

    Column(modifier = Modifier.fillMaxWidth()) {
        TabRow(selectedTabIndex = selectedTab) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = { Text(title) })
            }
        }

        Box(
            modifier = Modifier.fillMaxWidth().padding(16.dp)
        ) {
            when (selectedTab) {
                0 -> WhyItMattersContent(whyItMattersData)
                1 -> CausesContent(causes)
            }
        }
    }
}

@Composable
private fun WhyItMattersContent(metricData: MetricData?) {
    Column(
        modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        if (metricData?.content != null) {
            Text(
                text = metricData.content,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
        } else {
            Text(
                text = "Elevated ALT is a key indicator of liver inflammation or damage.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        metricData?.keyPoints?.let { keyPoints ->
            if (keyPoints.isNotBlank()) {
                Text(
                    text = keyPoints,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun CausesContent(causes: List<Cause>) {
    Column(
        modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Factors that may increase levels
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "Factors That May Increase Levels",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.error
            )
            Spacer(modifier = Modifier.height(8.dp))
            causes.filter { it.type == "increase" }.forEach { cause ->
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowUpward,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = cause.name ?: "", style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }

        // Factors that may decrease levels
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "Factors That May Decrease Levels",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))
            causes.filter { it.type == "decrease" }.forEach { cause ->
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowDownward,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = cause.name ?: "", style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }

        Text(
            text = "Note: These are general factors that may influence your Eosinophils %. Individual responses can vary based on your unique genetic makeup and overall health.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun CorrelationsSection(
    wellnessCategories: List<WellnessCategory>?, reportedSymptoms: List<ReportedSymptom>?
) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
    ) {
        Text(
            text = "Daily Wellness Factors", style = MaterialTheme.typography.titleLarge
        )
        Spacer(modifier = Modifier.height(8.dp))
        WellnessFactors(wellnessCategories)
        Spacer(modifier = Modifier.height(24.dp))
        Text(text = "Reported Symptoms", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(8.dp))
        ReportedSymptoms(reportedSymptoms)
    }
}

@Composable
private fun WellnessFactors(categories: List<WellnessCategory>?) {
    if (categories.isNullOrEmpty()) {
        Text("No wellness factors available")
        return
    }

    Column(
        modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        categories.forEach { category ->
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = category.name ?: "", style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = category.description ?: "",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    ImpactChip(impact = category.impact ?: "")
                }
            }
        }
    }
}

@Composable
private fun ReportedSymptoms(symptoms: List<ReportedSymptom>?) {
    if (symptoms.isNullOrEmpty()) {
        Text("No symptoms reported")
        return
    }

    Column(
        modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        symptoms.forEach { symptom ->
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = symptom.name ?: "",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = "${symptom.count ?: 0} times",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}

@Composable
private fun ImpactChip(impact: String) {
    val (backgroundColor, textColor) = when (impact.lowercase()) {
        "low" -> MaterialTheme.colorScheme.tertiary to MaterialTheme.colorScheme.onTertiary
        "medium" -> MaterialTheme.colorScheme.secondary to MaterialTheme.colorScheme.onSecondary
        "high" -> MaterialTheme.colorScheme.primary to MaterialTheme.colorScheme.onPrimary
        else -> MaterialTheme.colorScheme.surfaceVariant to MaterialTheme.colorScheme.onSurfaceVariant
    }

    Surface(
        color = backgroundColor, shape = MaterialTheme.shapes.small
    ) {
        Text(
            text = "$impact Impact",
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelMedium,
            color = textColor
        )
    }
}

private fun formatDate(isoString: String): String {
    return try {
        val instant = Instant.parse(isoString)
        val localDateTime = instant.toLocalDateTime(TimeZone.currentSystemDefault())
        "${localDateTime.dayOfMonth} ${localDateTime.month.name.take(3)} ${localDateTime.year}"
    } catch (e: Exception) {
        isoString
    }
}