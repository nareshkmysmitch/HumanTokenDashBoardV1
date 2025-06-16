package com.healthanalytics.android.presentation.screens.health

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.touchlab.kermit.Logger
import com.healthanalytics.android.BackHandler
import com.healthanalytics.android.data.api.BloodData
import com.healthanalytics.android.data.api.Cause
import com.healthanalytics.android.data.api.Correlation
import com.healthanalytics.android.data.api.MetricData
import com.healthanalytics.android.data.api.ReportedSymptom
import com.healthanalytics.android.data.api.WellnessCategory
import com.healthanalytics.android.presentation.components.AppCard
import com.healthanalytics.android.presentation.preferences.PreferencesViewModel
import com.healthanalytics.android.presentation.theme.AppColors
import com.healthanalytics.android.presentation.theme.FontFamily
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.io.discardingSink
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.koinInject


@Preview()
@Composable
fun PreviewBioMarkerFullReportScreen() {


    val dummyBloodData = BloodData(
        createdAt = "2024-12-02T13:05:20.615Z",
        displayDescription = "This biomarker measures the level of hemoglobin in your blood.",
        displayName = "Hemoglobin",
        displayRating = "Normal",
        id = "bd_001",
        identifier = "HEMOGLOBIN",
        metricId = "BD10020",
        range = "13.0 - 17.0",
        releasedAt = "2024-12-01T08:00:00.000Z",
        unit = "g/dL",
        updatedAt = "2024-12-02T13:10:00.000Z",
        userId = "user_123",
        value = 13.5,

        correlation = listOf(
            Correlation(

                description = "Low hemoglobin levels may be linked to iron deficiency."
            )
        ),
        shortDescription = "Indicator of red blood cell health",
        symptomsReported = 2
    )

    val prefs: PreferencesViewModel = koinInject()
    val viewModel: BioMarkerReportViewModel = koinInject()

    BioMarkerFullReportScreen(
        biomarker = dummyBloodData, onNavigateBack = {}, modifier = Modifier, prefs, viewModel
    )
}


//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun BioMarkerFullReportScreen(
//    biomarker: BloodData,
//    onNavigateBack: () -> Unit,
//    modifier: Modifier = Modifier,
//    prefs: PreferencesViewModel = koinInject(),
//    viewModel: BioMarkerReportViewModel = koinInject(),
//) {
//
//    val preferencesState by prefs.uiState.collectAsState()
//    val uiState by viewModel.uiState.collectAsState()
//
//    LaunchedEffect(preferencesState.data) {
//        preferencesState.data?.let { token ->
//            prefs.saveAccessToken(token)
//            viewModel.fetchBiomarkerReport("blood", biomarker.metricId ?: "", token)
//        }
//    }
//
//    BackHandler(enabled = true, onBack = onNavigateBack)
//    Logger.e { "BiomarkerDetailScreen $biomarker" }
//
//    Scaffold(
//        topBar = {
//            TopAppBar(
//                title = {
//                    Text(
//                        text = biomarker.displayName ?: "", color = AppColors.White
//                    )
//                }, navigationIcon = {
//                    IconButton(onClick = onNavigateBack) {
//                        Icon(
//                            Icons.Default.ArrowBack,
//                            contentDescription = "Back",
//                            tint = AppColors.White
//                        )
//                    }
//                }, colors = TopAppBarDefaults.topAppBarColors(
//                    containerColor = AppColors.AppBackgroundColor,
//                    navigationIconContentColor = AppColors.White,
//                    titleContentColor = AppColors.White
//                )
//            )
//        }) { paddingValues ->
//        when (val state = uiState) {
//            is BioMarkerReportUiState.Loading -> {
//                Box(
//                    modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center
//                ) {
//                    CircularProgressIndicator()
//                }
//            }
//
//            is BioMarkerReportUiState.Success -> {
//                LazyColumn(
//                    modifier = modifier.fillMaxSize().padding(paddingValues),
//                    verticalArrangement = Arrangement.spacedBy(16.dp)
//                ) {
//                    item {
//                        HeaderCard(biomarker, state.data?.releasedAt)
//                    }
//
//                    item {
//                        TabSection(
//                            state.data?.metricData?.firstOrNull()?.causes ?: emptyList(),
//                            state.data?.metricData
//                        )
//                    }
//
//                    item {
//                        CorrelationsSection(
//                            wellnessCategories = state.data?.wellnessCategories,
//                            reportedSymptoms = state.data?.reportedSymptoms
//                        )
//                    }
//                }
//            }
//
//            is BioMarkerReportUiState.Error -> {
//                Box(
//                    modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center
//                ) {
//                    Text(state.message)
//                }
//            }
//        }
//    }
//}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BioMarkerFullReportScreen(
    biomarker: BloodData,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    prefs: PreferencesViewModel = koinInject(),
    viewModel: BioMarkerReportViewModel = koinInject(),
) {
    val preferencesState by prefs.uiState.collectAsState()
    val uiState by viewModel.uiState.collectAsState()

    var selectedTab by rememberSaveable { mutableStateOf(0) } // 👈 Use rememberSaveable for resilience

    LaunchedEffect(preferencesState.data) {
        preferencesState.data?.let { token ->
            prefs.saveAccessToken(token)
            viewModel.fetchBiomarkerReport("blood", biomarker.metricId ?: "", token)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = biomarker.displayName ?: "", color = AppColors.White)
                }, navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = AppColors.White
                        )
                    }
                }, colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = AppColors.AppBackgroundColor
                )
            )
        }) { paddingValues ->
        when (val state = uiState) {
            is BioMarkerReportUiState.Loading -> { /* show loader */
            }

            is BioMarkerReportUiState.Success -> {
                LazyColumn(
                    modifier = modifier.fillMaxSize().padding(paddingValues),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item { HeaderCard(biomarker, state.data?.releasedAt) }

                    item {
                        TabSection(
                            selectedTab = selectedTab,
                            onTabSelected = { selectedTab = it },
                            causes = state.data?.metricData?.firstOrNull()?.causes ?: emptyList(),
                            metricData = state.data?.metricData
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

            is BioMarkerReportUiState.Error -> { /* show error */
            }
        }
    }
}


@Composable
private fun TabSection(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit,
    causes: List<Cause>,
    metricData: List<MetricData>?
) {
    val tabs = listOf("Why It Matters?", "Causes")
    val whyItMattersData = metricData?.firstOrNull { it.category == "why_it_matters" }

    Column(modifier = Modifier.fillMaxWidth()) {
        TabRow(selectedTabIndex = selectedTab) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { onTabSelected(index) },
                    text = { Text(title) })
            }
        }

        Box(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            when (selectedTab) {
                0 -> WhyItMattersContent(whyItMattersData)
                1 -> CausesContent(causes)
            }
        }
    }
}

@Composable
private fun HeaderCard(biomarker: BloodData, releasedAt: String?) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = AppColors.CardGrey
        ),
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
                    color = AppColors.White,
                    fontFamily = FontFamily.bold()
                )
                StatusChip(status = biomarker.displayRating ?: "")
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "${biomarker.value} ${biomarker.unit}",
                style = MaterialTheme.typography.headlineLarge,
                color = AppColors.White,
                fontFamily = FontFamily.pilBold()
            )

            Text(
                text = "Last Updated: ${formatDate(releasedAt ?: "")}",
                style = MaterialTheme.typography.bodyMedium,
                color = AppColors.White.copy(alpha = 0.7f),
                fontFamily = FontFamily.regular()
            )

            if (!biomarker.shortDescription.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = biomarker.shortDescription,
                    style = MaterialTheme.typography.bodyMedium,
                    color = AppColors.White,
                    fontFamily = FontFamily.regular()
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
                fontSize = 14.sp,
                color = AppColors.White,
                fontFamily = FontFamily.regular()
            )
        } else {
            Text(
                text = "Elevated ALT is a key indicator of liver inflammation or damage.",
                fontSize = 14.sp,
                fontFamily = FontFamily.regular(),
                color = AppColors.White,
            )
        }

        metricData?.keyPoints?.forEach { points ->
            if (points?.isNotBlank() == true) {
                Text(
                    text = points,
                    fontSize = 12.sp,
                    fontFamily = FontFamily.regular(),
                    color = AppColors.White,
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
                color = MaterialTheme.colorScheme.error,
                fontFamily = FontFamily.regular()
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
                        text = cause.name ?: "",
                        style = MaterialTheme.typography.bodyMedium,
                        fontFamily = FontFamily.regular()
                    )
                }
            }
        }

        // Factors that may decrease levels
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "Factors That May Decrease Levels",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
                fontFamily = FontFamily.regular()
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
                        text = cause.name ?: "",
                        style = MaterialTheme.typography.bodyMedium,
                        fontFamily = FontFamily.regular()
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
    wellnessCategories: List<WellnessCategory>?, reportedSymptoms: List<ReportedSymptom>?,
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
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = AppColors.CardGrey
                ),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = category.name ?: "",
                            style = MaterialTheme.typography.titleMedium,
                            color = AppColors.White,
                            fontFamily = FontFamily.medium()
                        )
                        Text(
                            text = category.description ?: "",
                            style = MaterialTheme.typography.bodyMedium,
                            color = AppColors.White,
                            fontFamily = FontFamily.regular()
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
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = AppColors.CardGrey
                ),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = symptom.name ?: "",
                        fontSize = 14.sp,
                        modifier = Modifier.weight(1f),
                        fontFamily = FontFamily.bold(),
                        color = AppColors.White
                    )
                    Text(
                        text = "${symptom.count ?: 0} times",
                        color = AppColors.White,
                        fontSize = 12.sp,
                        fontFamily = FontFamily.regular()
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
            color = textColor,
            fontFamily = FontFamily.regular()
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