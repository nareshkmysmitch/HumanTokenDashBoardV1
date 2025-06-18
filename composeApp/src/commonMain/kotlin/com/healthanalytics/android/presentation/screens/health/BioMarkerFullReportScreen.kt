package com.healthanalytics.android.presentation.screens.health

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
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.MedicalInformation
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import com.healthanalytics.android.BackHandler
import com.healthanalytics.android.data.api.BloodData
import com.healthanalytics.android.data.api.MetricData
import com.healthanalytics.android.data.api.ReportedSymptom
import com.healthanalytics.android.data.api.WellnessCategory
import com.healthanalytics.android.presentation.preferences.PreferencesViewModel
import com.healthanalytics.android.presentation.theme.AppColors
import com.healthanalytics.android.presentation.theme.Dimensions
import com.healthanalytics.android.presentation.theme.FontFamily
import com.healthanalytics.android.presentation.theme.FontSize
import com.healthanalytics.android.utils.capitalizeFirst
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
    viewModel: BioMarkerReportViewModel = koinInject(),
) {
    val preferencesState by prefs.uiState.collectAsState()
    val uiState by viewModel.uiState.collectAsState()

    var selectedTab by rememberSaveable { mutableStateOf(0) } // 👈 Use rememberSaveable for resilience

    BackHandler(enabled = true, onBack = { onNavigateBack() })

    var biomarkerDesc by remember { mutableStateOf("") }

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
                    Text(
                        text = biomarker.displayName ?: "",
                        color = AppColors.textPrimaryColor,
                        fontSize = 24.sp,
                        fontFamily = FontFamily.semiBold()
                    )
                }, navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = AppColors.textPrimaryColor
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
                    item { HeaderCard(biomarker, state.data?.releasedAt, biomarkerDesc) }

                    item {
                        TabSection(
                            selectedTab = selectedTab,
                            onTabSelected = { selectedTab = it },
                            metricData = state.data?.metricData,
                            biomarker.displayName.toString(),
                            onTabChanged = {
                                biomarkerDesc = it
                            }
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
    metricData: List<MetricData>?, // This is your full original list
    name: String,
    onTabChanged: (String) -> Unit,
) {
    val uniqueTabDefinitions = remember(metricData) {
        metricData
            ?.mapNotNull { metric ->
                metric.contentType?.let { type ->
                    val displayTitle = type.replace('_', ' ').capitalizeFirst()
                    Triple(displayTitle, type, metric)
                }
            }?.distinctBy { it.first } ?: emptyList()
    }

    val tabTitles = remember(uniqueTabDefinitions) { uniqueTabDefinitions.map { it.first } }

    Column(modifier = Modifier.fillMaxWidth()) {
        if (tabTitles.isNotEmpty()) {
            TabRow(selectedTabIndex = selectedTab) {
                tabTitles.forEachIndexed { index, tabTitle ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { onTabSelected(index) },
                        text = {
                            Text(
                                text = tabTitle,
                                fontSize = FontSize.textSize18sp,
                                color = AppColors.textPrimaryColor,
                                fontFamily = FontFamily.semiBold()
                            )
                        }
                    )
                }
            }
        }
        val selectedTabOriginalContentType = uniqueTabDefinitions.getOrNull(selectedTab)?.second
        val metricsForSelectedTabCategory = remember(selectedTabOriginalContentType, metricData) {
            if (selectedTabOriginalContentType != null && metricData != null) {
                metricData.filter { it.contentType == selectedTabOriginalContentType }
            } else {
                emptyList()
            }
        }

        val contentDesc =
            metricsForSelectedTabCategory.find { it.category == "short_description" }?.content ?: ""
        onTabChanged(contentDesc)
        Box(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            TypeBasedCardDesc(
                metricsForSelectedTabCategory,
                name = name,
            )
        }
    }
}

@Composable
fun TypeBasedCardDesc(
    metricData: List<MetricData>,
    name: String,
) {
    val whyItMattersData = metricData.firstOrNull { it.category == "why_it_matters" }


    var increaseLevelDesc by remember { mutableStateOf<List<String>>(emptyList()) }
    var decreaseLevelDesc by remember { mutableStateOf<List<String>>(emptyList()) }
    LaunchedEffect(metricData) {
        val allIncreases = mutableListOf<String>()
        val allDecreases = mutableListOf<String>()

        metricData.forEach { data ->
            data.subgroups?.let { subgroups ->
                subgroups.increase?.let { allIncreases.addAll(it) } // filterNotNull if strings can be null
                subgroups.decrease?.let { allDecreases.addAll(it) }
            }
        }
        increaseLevelDesc = allIncreases.toList() // Assign new lists
        decreaseLevelDesc = allDecreases.toList()
    }

    Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Outlined.Info,
                contentDescription = "Info",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(24.dp).padding(end = 4.dp)
            )
            Text(
                text = "Why It Matters?",
                style = MaterialTheme.typography.titleLarge,
                fontFamily = FontFamily.semiBold(),
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        WhyItMattersContent(whyItMattersData)
        Spacer(Modifier.height(Dimensions.size24dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Outlined.MedicalInformation,
                contentDescription = "Causes",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(24.dp).padding(end = 4.dp)
            )
            Text(
                text = "Causes",
                style = MaterialTheme.typography.titleLarge,
                fontFamily = FontFamily.semiBold(),
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        CausesContent(
            name = name,
            increaseLevelDesc = increaseLevelDesc,
            decreaseLevelDesc = decreaseLevelDesc
        )
    }
}

@Composable
private fun HeaderCard(biomarker: BloodData, releasedAt: String?, biomarkerDesc: String) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = AppColors.cardBlueColor
        ),
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(16.dp)
        ) {
            Text(
                text = biomarker.displayName ?: "",
                style = MaterialTheme.typography.headlineMedium,
                color = AppColors.textPrimaryColor,
                fontSize = FontSize.textSize20sp,
                fontFamily = FontFamily.semiBold()
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "${biomarker.value} ${biomarker.unit}",
//                        style = MaterialTheme.typography.headlineLarge,
                        color = AppColors.textPrimaryColor,
                        fontFamily = FontFamily.pilBold(),
                        fontSize = FontSize.textSize18sp,
                        maxLines = 1,
                    )

                    Text(
                        text = "Last Updated: ${formatDate(releasedAt ?: "")}",
//                        style = MaterialTheme.typography.bodyMedium,
                        fontSize = FontSize.textSize14sp,
                        color = AppColors.textPrimaryColor,
                        fontFamily = FontFamily.regular(),
                        maxLines = 1,
                    )

                }
                StatusChip(status = biomarker.displayRating ?: "")
            }

            if (!biomarker.shortDescription.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = biomarkerDesc,
//                    style = MaterialTheme.typography.bodyMedium,
                    fontSize = FontSize.textSize16sp,
                    color = AppColors.textPrimaryColor,
                    fontFamily = FontFamily.regular()
                )
            }
        }
    }
}

@Composable
private fun WhyItMattersContent(metricData: MetricData?) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = AppColors.cardBlueColor
        ),
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(Dimensions.size12dp),
        ) {
            if (metricData?.title != null) {
                Text(
                    text = metricData.title,
                    fontSize = 18.sp,
                    color = AppColors.textPrimaryColor,
                    fontFamily = FontFamily.semiBold(),
                    modifier = Modifier.padding(bottom = Dimensions.size14dp)
                )
            }
            if (metricData?.content != null) {
                Text(
                    text = metricData.content,
                    fontSize = 16.sp,
                    color = AppColors.textPrimaryColor,
                    fontFamily = FontFamily.medium()
                )
            } else {
                Text(
                    text = "Elevated ALT is a key indicator of liver inflammation or damage.",
                    fontSize = 16.sp,
                    fontFamily = FontFamily.medium(),
                    color = AppColors.textPrimaryColor,
                )
            }
            Text(
                text = "Key Impact",
                modifier = Modifier.fillMaxWidth()
                    .padding(top = Dimensions.size16dp),
                fontSize = FontSize.textSize14sp,
                color = AppColors.darkPink,
                fontFamily = FontFamily.medium()
            )

            Spacer(Modifier.height(Dimensions.size8dp))
            metricData?.keyPoints?.forEachIndexed { index, points ->
                if (points?.isNotBlank() == true) {
                    Row {
                        Text(
                            text = "${index + 1}. ",
                            fontSize = FontSize.textSize14sp,
                            fontFamily = FontFamily.medium(),
                            color = AppColors.textSecondary,
                        )
                        Text(
                            text = points,
                            fontSize = FontSize.textSize14sp,
                            fontFamily = FontFamily.medium(),
                            color = AppColors.textPrimaryColor,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CausesContent(
    name: String,
    increaseLevelDesc: List<String>,
    decreaseLevelDesc: List<String>,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = AppColors.cardBlueColor
        ),
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(Dimensions.size12dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Factors that may increase levels
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Factors That May Increase Levels",
                    fontSize = FontSize.textSize14sp,
                    color = MaterialTheme.colorScheme.error,
                    fontFamily = FontFamily.semiBold()
                )
                Spacer(modifier = Modifier.height(8.dp))
                increaseLevelDesc.forEach { cause ->
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
                            text = cause,
                            fontSize = FontSize.textSize14sp,
                            fontFamily = FontFamily.regular(),
                            color = AppColors.textPrimaryColor,
                        )
                    }
                }
            }

            // Factors that may decrease levels
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Factors That May Decrease Levels",
                    color = MaterialTheme.colorScheme.primary,
                    fontFamily = FontFamily.semiBold(),
                    fontSize = FontSize.textSize14sp,
                )
                Spacer(modifier = Modifier.height(8.dp))
                decreaseLevelDesc.forEach { cause ->
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
                            text = cause,
                            fontFamily = FontFamily.regular(),
                            fontSize = FontSize.textSize14sp,
                            color = AppColors.textPrimaryColor,
                        )
                    }
                }
            }

            Text(
                text = "Note: These are general factors that may influence your ${name}. Individual responses can vary based on your unique genetic makeup and overall health.",
                color = AppColors.textPrimaryColor,
                fontSize = FontSize.textSize12sp,
                fontFamily = FontFamily.regular()
            )
        }
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
            text = "Correlations with Daily Wellness & Symptoms",
            fontSize = FontSize.textSize18sp,
            color = AppColors.textPrimaryColor,
            fontFamily = FontFamily.semiBold()
        )
        if (wellnessCategories?.isNotEmpty() == true) {
            Spacer(modifier = Modifier.height(8.dp))
            DailyWellness(wellnessCategories)
        }
        Spacer(modifier = Modifier.height(8.dp))
        ReportedSymptoms(reportedSymptoms)
        Spacer(modifier = Modifier.height(8.dp))
        Row {
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = "Info",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .size(18.dp)
                    .padding(end = 4.dp)
            )

            Text(
                text = "Correlations are based on patterns from user-reported data and may vary individually. Track your daily wellness and symptoms to discover your personal patterns.",
                fontSize = FontSize.textSize14sp,
                color = AppColors.textPrimaryColor,
                fontFamily = FontFamily.medium()
            )
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun WellnessFactors(categories: List<WellnessCategory>?) {
    if (categories.isNullOrEmpty()) {
        Text(
            text = "No wellness factors available",
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            fontSize = FontSize.textSize18sp,
            color = AppColors.textPrimaryColor,
        )
        return
    }

    Column(
        modifier = Modifier.fillMaxWidth().padding(horizontal = Dimensions.size8dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        categories.forEach { category ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = AppColors.cardBlueColor
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
                            fontSize = FontSize.textSize16sp,
                            color = AppColors.textPrimaryColor,
                            fontFamily = FontFamily.medium()
                        )
                        Text(
                            text = category.description ?: "",
                            fontSize = FontSize.textSize14sp,
                            color = AppColors.textPrimaryColor,
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
fun DailyWellness(wellnessCategories: List<WellnessCategory>?) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = AppColors.cardDarkBlueColor),
    ) {
        Column {
            Text(
                text = "Daily Wellness Factors",
                modifier = Modifier.fillMaxWidth()
                    .padding(horizontal = Dimensions.size16dp)
                    .padding(top = Dimensions.size16dp),
                fontSize = FontSize.textSize16sp,
                color = AppColors.success,
                fontFamily = FontFamily.medium()
            )
            Spacer(modifier = Modifier.height(8.dp))
            WellnessFactors(wellnessCategories)
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun ReportedSymptoms(symptoms: List<ReportedSymptom>?) {
    if (symptoms.isNullOrEmpty()) {
        Text("No symptoms reported")
        return
    }
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = AppColors.cardBlueColor),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = Dimensions.size16dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = "Reported Symptoms",
                modifier = Modifier.fillMaxWidth()
                    .padding(horizontal = Dimensions.size16dp),
                style = MaterialTheme.typography.titleMedium,
                fontSize = FontSize.textSize16sp,
                color = AppColors.error,
                fontFamily = FontFamily.medium()
            )
            Card(
                modifier = Modifier.fillMaxWidth()
                    .padding(top = Dimensions.size4dp)
                    .padding(horizontal = Dimensions.size16dp),
                colors = CardDefaults.cardColors(containerColor = AppColors.cardDarkBlueColor),
            ) {
                symptoms.forEach { symptom ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = symptom.name ?: "",
                            fontSize = FontSize.textSize16sp,
                            modifier = Modifier.weight(1f),
                            fontFamily = FontFamily.medium(),
                            color = AppColors.textPrimaryColor
                        )
                        Text(
                            text = "${symptom.count ?: 0} times",
                            color = AppColors.textPrimaryColor,
                            fontSize = FontSize.textSize14sp,
                            fontFamily = FontFamily.regular()
                        )
                    }
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