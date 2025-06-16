package com.healthanalytics.android.presentation.screens.health

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
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
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import com.healthanalytics.android.data.api.BloodData
import com.healthanalytics.android.presentation.preferences.PreferencesViewModel
import com.healthanalytics.android.presentation.theme.AppColors
import com.healthanalytics.android.presentation.theme.Dimensions
import com.healthanalytics.android.presentation.theme.Dimensions.size12dp
import com.healthanalytics.android.presentation.theme.Dimensions.size16dp
import com.healthanalytics.android.presentation.theme.Dimensions.size4dp
import com.healthanalytics.android.presentation.theme.FontFamily
import com.healthanalytics.android.presentation.theme.FontSize
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

@Composable
fun HealthDataScreen(
    viewModel: HealthDataViewModel,
    prefs: PreferencesViewModel,
    onNavigateToDetail: (BloodData?) -> Unit,
) {
    val preferencesState by prefs.uiState.collectAsState()
    val uiState by viewModel.uiState.collectAsState()

    val filteredMetrics = viewModel.getFilteredMetrics()
    val availableFilters = viewModel.getAvailableFilters()
    val isSearchVisible by remember { mutableStateOf(false) }

    LaunchedEffect(preferencesState.data) {
        preferencesState.data?.let { token ->
            prefs.saveAccessToken(token)
            viewModel.loadHealthMetrics(token)
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().background(AppColors.Black).padding(top = size16dp)
    ) {

        AnimatedVisibility(
            visible = isSearchVisible,
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut()
        ) {
            OutlinedTextField(
                value = uiState.searchQuery,
                onValueChange = { viewModel.updateSearchQuery(it) },
                modifier = Modifier.fillMaxWidth().padding(Dimensions.size16dp),
                placeholder = { Text("Search health data") },
                singleLine = true
            )
        }

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
                items(availableFilters) { filter ->
                    val selected = uiState.selectedFilter == filter
                    FilterChip(
                        selected = selected,
                        onClick = { viewModel.updateFilter(if (uiState.selectedFilter == filter) null else filter) },
                        colors = androidx.compose.material3.FilterChipDefaults.filterChipColors(
                            containerColor = if (selected) AppColors.Pink.copy(alpha = 0.5f) else AppColors.Pink.copy(
                                alpha = 0.1f
                            ),
                            labelColor = AppColors.textPrimary,
                            selectedContainerColor = AppColors.Pink.copy(alpha = 0.5f),
                            selectedLabelColor = AppColors.White
                        ),
                        border = androidx.compose.material3.FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = selected,
                            borderColor = if (selected) androidx.compose.ui.graphics.Color.Transparent else AppColors.Pink.copy(
                                alpha = 0.2f
                            )
                        ),
                        label = {
                            Text(
                                text = filter ?: "",
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
                            metric = metric, onMetricClick = { onNavigateToDetail(metric) })

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
) {
    val symptomsReported = metric?.symptomsReported
    Column(
        modifier = Modifier.fillMaxWidth().padding(size12dp)
            .clickable { metric?.let { onMetricClick(it) } }) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = metric?.displayName ?: "",
                maxLines = 2,
                fontSize = FontSize.textSize22sp,
                fontFamily = FontFamily.bold(),
                color = AppColors.textPrimary,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )

            StatusChip(status = metric?.displayRating ?: "")
        }

        Spacer(modifier = Modifier.height(Dimensions.size8dp))

        Row(
            modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically
        ) {
            Row(modifier = Modifier.weight(1f)) {
                Text(
                    text = "${metric?.value}",
                    fontSize = FontSize.textSize18sp,
                    fontFamily = FontFamily.semiBold(),
                    color = AppColors.textPrimary,
                )
                Text(
                    text = " ${metric?.unit}",
                    fontSize = FontSize.textSize14sp,
                    fontFamily = FontFamily.medium(),
                    color = AppColors.TextGrey,
                )
            }
            Text(
                text = "Blood",
                fontSize = FontSize.textSize14sp,
                fontFamily = FontFamily.medium(),
                textAlign = TextAlign.Center,
                color = AppColors.textPrimary,
            )
        }

        Spacer(modifier = Modifier.height(Dimensions.size8dp))
        if (symptomsReported != null && symptomsReported > 0) {
            Column(
                modifier = Modifier.wrapContentSize().background(
                    color = Color(0xFF192D50), shape = RoundedCornerShape(50)
                ).padding(PaddingValues(vertical = size4dp, horizontal = Dimensions.size8dp))
            ) {
                Text(
                    text = "${metric.symptomsReported} symptoms reported",
                    fontSize = FontSize.textSize14sp,
                    fontFamily = FontFamily.medium(),
                    color = Color(0xFF60a5fa),
                )
            }

            Spacer(modifier = Modifier.height(Dimensions.size8dp))
        }

        Text(
            text = "Last updated: ${formatDate(metric?.updatedAt ?: "")}",
            fontSize = FontSize.textSize14sp,
            fontFamily = FontFamily.medium(),
            color = AppColors.TextGrey,
        )
    }

}

@Composable
fun StatusChip(status: String) {

    val backgroundColor = when (status.lowercase()) {
        "normal" -> (AppColors.NormalColor)
        "low" -> AppColors.LowColor
        "high" -> AppColors.HighColor
        "optimal" -> AppColors.OptimalColor
        "none" -> AppColors.NoneColor
        else -> AppColors.YellowColor
    }

    Surface(
        color = backgroundColor, shape = MaterialTheme.shapes.small
    ) {
        Text(
            text = status,
            modifier = Modifier.wrapContentWidth()
                .padding(horizontal = Dimensions.size8dp, vertical = size4dp),
            fontSize = FontSize.textSize12sp,
            fontFamily = FontFamily.medium(),
            color = AppColors.textPrimary
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


