package com.healthanalytics.android.presentation.screens.health

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import com.healthanalytics.android.data.models.home.BloodData
import com.healthanalytics.android.presentation.preferences.PreferencesViewModel
import com.healthanalytics.android.presentation.theme.AppColors
import com.healthanalytics.android.presentation.theme.Dimensions
import com.healthanalytics.android.presentation.theme.Dimensions.size12dp
import com.healthanalytics.android.presentation.theme.Dimensions.size16dp
import com.healthanalytics.android.presentation.theme.Dimensions.size4dp
import com.healthanalytics.android.presentation.theme.Dimensions.size8dp
import com.healthanalytics.android.presentation.theme.FontFamily
import com.healthanalytics.android.presentation.theme.FontSize
import humantokendashboardv1.composeapp.generated.resources.Res
import humantokendashboardv1.composeapp.generated.resources.search_biomarkers
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.resources.stringResource

@Composable
fun HealthDataScreen(
    viewModel: HealthDataViewModel,
    prefs: PreferencesViewModel,
    onNavigateToDetail: (BloodData?) -> Unit,
) {
    val preferencesState by prefs.uiState.collectAsState()
    val uiState by viewModel.uiState.collectAsState()
    val keyboardController = LocalSoftwareKeyboardController.current
    val filteredMetrics = viewModel.getFilteredMetrics()
    val availableFilters = viewModel.getAvailableFilters()

    LaunchedEffect(preferencesState.data) {
        preferencesState.data?.let { token ->
            prefs.saveAccessToken(token)
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
            Spacer(modifier = Modifier.height(size16dp))

            OutlinedTextField(
                value = uiState.searchQuery,
                onValueChange = {
                    viewModel.updateSearchQuery(it)
                },
                placeholder = {
                    Text(
                        text = stringResource(Res.string.search_biomarkers),
                        fontSize = FontSize.textSize16sp,
                        fontFamily = FontFamily.regular(),
                        textAlign = TextAlign.Start,
                        color = AppColors.descriptionColor,
                    )
                },
                maxLines = 1,
                singleLine = true,
                textStyle = TextStyle(
                    color = AppColors.White,
                    fontSize = FontSize.textSize16sp,
                    fontFamily = FontFamily.medium(),
                    textAlign = TextAlign.Start
                ),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = AppColors.darkPink,
                    unfocusedBorderColor = AppColors.textFieldUnFocusedColor,
                    focusedContainerColor = AppColors.Black,
                    unfocusedContainerColor = AppColors.Black,
                    errorBorderColor = AppColors.error
                ),
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Remove",
                        tint = AppColors.descriptionColor
                    )
                },
                keyboardActions = KeyboardActions(onDone = {
                    keyboardController?.hide()
                }),
                shape = RoundedCornerShape(size12dp),
                modifier = Modifier.fillMaxWidth().padding(horizontal = size12dp)
            )

            Spacer(modifier = Modifier.height(size16dp))

            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(size16dp),
                contentPadding = PaddingValues(horizontal = size12dp)
            ) {
                items(availableFilters) { filter ->
                    val count = viewModel.getHealthDataCount(filter ?: "")
                    val selected = uiState.selectedFilter == filter
                    FilterChip(
                        selected = selected,
                        onClick = { viewModel.updateFilter(if (uiState.selectedFilter == filter) null else filter) },
                        colors = androidx.compose.material3.FilterChipDefaults.filterChipColors(
                            containerColor = if (selected) AppColors.darkPink else AppColors.darkPink.copy(
                                alpha = 0.1f
                            ),
                            labelColor = AppColors.textPrimary,
                            selectedContainerColor = AppColors.darkPink,
                            selectedLabelColor = AppColors.White
                        ),
                        border = androidx.compose.material3.FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = selected,
                            borderColor = if (selected) Color.Transparent else AppColors.Pink.copy(
                                alpha = 0.2f
                            )
                        ),
                        label = {
                            Text(
                                text = "$filter ($count)",
                                fontSize = FontSize.textSize14sp,
                                fontFamily = FontFamily.medium(),
                                color = AppColors.textPrimary,
                                textAlign = TextAlign.Center
                            )
                        })
                }
            }

            Spacer(modifier = Modifier.height(size16dp))

            Card(
                modifier = Modifier.fillMaxWidth().wrapContentHeight()
                    .padding(horizontal = size12dp),
                colors = CardDefaults.cardColors(containerColor = AppColors.CardGrey),
            ) {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(Dimensions.size10dp)
                ) {
                    val lastPosition = filteredMetrics.size.minus(1)
                    items(filteredMetrics) { metric ->
                        MetricCard(
                            metric = metric,
                            onMetricClick = { onNavigateToDetail(metric) },
                            searchQuery = uiState.searchQuery
                        )

                        if (lastPosition != filteredMetrics.indexOf(metric)) {
                            HorizontalDivider(modifier = Modifier.padding(start = size12dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MetricCard(
    metric: BloodData?, onMetricClick: (BloodData) -> Unit = {},
    searchQuery: String,
) {
    val isSearching = searchQuery.isNotBlank()
    val symptomsReported = metric?.symptomsReported
    val isLatest = metric?.isLatest == true

    Column(
        modifier = Modifier.fillMaxWidth().padding(size12dp)
            .clickable { metric?.let { onMetricClick(it) } }) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                if (isLatest) {
                    Box(
                        modifier = Modifier.size(size8dp)
                            .background(color = AppColors.error, shape = RoundedCornerShape(50))
                    )
                    Spacer(modifier = Modifier.width(size4dp))
                }
                Text(
                    text = metric?.displayName ?: "",
                    maxLines = 2,
                    fontSize = FontSize.textSize16sp,
                    fontFamily = FontFamily.medium(),
                    color = AppColors.White,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
            }

            StatusChip(status = metric?.displayRating ?: "")
        }

        Spacer(modifier = Modifier.height(size8dp))

        Row(
            modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically
        ) {
            Row(modifier = Modifier.weight(1f)) {
                Text(
                    text = "${metric?.value}",
                    fontSize = FontSize.textSize16sp,
                    fontFamily = FontFamily.medium(),
                    color = AppColors.White,
                )
                Spacer(modifier = Modifier.width(Dimensions.size4dp))
                Text(
                    text = " ${metric?.unit}",
                    fontSize = FontSize.textSize14sp,
                    fontFamily = FontFamily.regular(),
                    color = AppColors.TextGrey,
                )
            }
        }

        Spacer(modifier = Modifier.height(size8dp))

        if (symptomsReported != null && symptomsReported > 0) {
            val filterSymptoms = if (isSearching) {
                metric.reportedSymptoms?.filter {
                    it.name?.contains(searchQuery, ignoreCase = true) == true
                }.orEmpty()
            } else emptyList()

            val filterText = if (isSearching) {
                if (filterSymptoms.isEmpty()) {
                    ""
                } else if (filterSymptoms.size > 1) {
                    "${filterSymptoms.first().name} +${filterSymptoms.size.minus(1)}"
                } else {
                    filterSymptoms.firstOrNull()?.name ?: ""
                }
            } else {
                "$symptomsReported symptoms reported"
            }

            if (filterText.isNotBlank()) {
                Column(
                    modifier = Modifier
                        .wrapContentSize()
                        .background(
                            color = AppColors.tagTransparentBlue,
                            shape = RoundedCornerShape(50)
                        )
                        .padding(vertical = size4dp, horizontal = size8dp)
                ) {
                    Text(
                        text = filterText,
                        fontSize = FontSize.textSize12sp,
                        fontFamily = FontFamily.medium(),
                        color = AppColors.tagBlue,
                    )
                }

                Spacer(modifier = Modifier.height(size8dp))
            }
        }

        if (isSearching) {
            val filterCauses = metric?.causes.orEmpty().filter {
                it.name?.contains(searchQuery, ignoreCase = true) == true
            }

            if (filterCauses.isNotEmpty()) {
                val filterText = if (filterCauses.size > 1) {
                    "${filterCauses.first().name} +${filterCauses.size.minus(1)}"
                } else {
                    filterCauses.first().name ?: ""
                }

                Column(
                    modifier = Modifier
                        .wrapContentSize()
                        .background(
                            color = AppColors.tagYellow.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(50)
                        )
                        .padding(vertical = size4dp, horizontal = size8dp)
                ) {
                    Text(
                        text = filterText,
                        fontSize = FontSize.textSize12sp,
                        fontFamily = FontFamily.medium(),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        color = AppColors.tagYellow,
                    )
                }

                Spacer(modifier = Modifier.height(size8dp))
            }
        }

        Text(
            text = "Last updated: ${formatDate(metric?.updatedAt ?: "")}",
            fontSize = FontSize.textSize12sp,
            fontFamily = FontFamily.regular(),
            color = AppColors.White,
        )
    }

}

@Composable
fun StatusChip(status: String) {

    val (backgroundColor, textColor) = when (status.lowercase()) {
        "normal" -> (AppColors.NormalColor to AppColors.White)
        "low" -> (AppColors.LowColor to AppColors.Black)
        "high" -> (AppColors.HighColor to AppColors.Black)
        "optimal" -> (AppColors.OptimalColor to AppColors.White)
        "none" -> (AppColors.NoneColor to AppColors.White)
        else -> (AppColors.YellowColor to AppColors.Black)
    }

    Surface(
        color = backgroundColor, shape = MaterialTheme.shapes.small
    ) {
        Text(
            text = status,
            modifier = Modifier.wrapContentWidth()
                .padding(horizontal = size8dp, vertical = size4dp),
            fontSize = FontSize.textSize12sp,
            fontFamily = FontFamily.medium(),
            color = textColor
        )
    }
}

fun formatDate(isoString: String?): String {
    return isoString?.let {
        val instant = Instant.parse(isoString)
        val systemTz = TimeZone.currentSystemDefault()
        val localDateTime = instant.toLocalDateTime(systemTz)

        val day = localDateTime.dayOfMonth.toString().padStart(2, '0')
        val month = localDateTime.month.name.lowercase().replaceFirstChar { it.uppercase() }.take(3)
        val year = localDateTime.year

        val hour = localDateTime.hour
        val minute = localDateTime.minute.toString().padStart(2, '0')

        val hour12 = if (hour % 12 == 0) 12 else hour % 12
        val ampm = if (hour < 12) "AM" else "PM"

        "$day $month $year ${hour12.toString().padStart(2, '0')}:$minute $ampm"
    } ?: ""
}


