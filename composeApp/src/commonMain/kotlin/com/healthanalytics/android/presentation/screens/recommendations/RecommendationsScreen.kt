package com.healthanalytics.android.presentation.screens.recommendations

import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import com.healthanalytics.android.data.models.Recommendation
import com.healthanalytics.android.data.models.RecommendationCategory
import com.healthanalytics.android.presentation.preferences.PreferencesViewModel
import com.healthanalytics.android.presentation.theme.AppColors
import com.healthanalytics.android.presentation.theme.Dimensions
import com.healthanalytics.android.presentation.theme.FontFamily
import com.healthanalytics.android.presentation.theme.FontSize
import com.healthanalytics.android.utils.capitalizeFirst

@Composable
fun RecommendationsScreen(
    viewModel: RecommendationsViewModel,
    preferencesViewModel: PreferencesViewModel,
) {
    val uiState by viewModel.uiState.collectAsState()
    val preferencesState by preferencesViewModel.uiState.collectAsState()
    val filterList = viewModel.getFilteredRecommendations()


    LaunchedEffect(preferencesState.data) {
        preferencesState.data?.let { token ->
            viewModel.loadRecommendations(token)
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().background(AppColors.Black)
    ) {
        // Recommendations List
        if (uiState.isLoading || preferencesState.data == null) {
            Box(
                modifier = Modifier.fillMaxSize().background(AppColors.AppBackgroundColor),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            // Category Selector
            LazyRow(
                modifier = Modifier.fillMaxWidth().padding(top = Dimensions.size12dp),
                horizontalArrangement = Arrangement.spacedBy(Dimensions.size12dp),
                contentPadding = PaddingValues(horizontal = Dimensions.size12dp)
            ) {
                items(viewModel.getRecommendationCategories()) { category ->
                    CategoryChip(
                        category = category,
                        count = viewModel.getCategoryCount(category),
                        selected = category == uiState.selectedCategory,
                        onClick = { viewModel.updateRecommendationCategory(category) })
                }
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(Dimensions.size12dp),
                verticalArrangement = Arrangement.spacedBy(Dimensions.size12dp),
            ) {
                items(filterList) { recommendation ->
                    RecommendationCard(
                        accessToken = preferencesState.data,
                        viewModel = viewModel,
                        recommendation = recommendation
                    )
                }
            }
        }
    }
}

@Composable
fun CategoryChip(
    category: String,
    count: Int,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val categoryEnum = RecommendationCategory.fromString(category)

    FilterChip(
        selected = selected, onClick = onClick, colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = AppColors.DarkPink,
        ), label = {
            Row(
                horizontalArrangement = Arrangement.spacedBy(Dimensions.size4dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(categoryEnum.icon)
                Text(
                    text = "${category.capitalizeFirst()} ($count)",
                    fontSize = FontSize.textSize14sp,
                    fontFamily = FontFamily.medium(),
                    color = AppColors.textPrimary,
                    textAlign = TextAlign.Center
                )
            }
        })
}

@Composable
fun RecommendationCard(
    recommendation: Recommendation,
    viewModel: RecommendationsViewModel,
    accessToken: String?,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = AppColors.CardGrey
        ),
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(Dimensions.size12dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(Dimensions.size8dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = RecommendationCategory.fromString(recommendation.category).icon,
                        style = MaterialTheme.typography.titleLarge
                    )
                    Text(
                        text = recommendation.name,
                        fontSize = FontSize.textSize22sp,
                        color = AppColors.white,
                        fontFamily = FontFamily.bold(),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Spacer(modifier = Modifier.height(Dimensions.size16dp))

            Text(
                text = "Potential Impact",
                fontSize = FontSize.textSize16sp,
                color = AppColors.white,
                fontFamily = FontFamily.medium(),
            )

            Spacer(modifier = Modifier.height(Dimensions.size8dp))

            // Metrics Grid
            recommendation.metric_recommendations?.let { metrics ->
                if (metrics.isNotEmpty()) {
                    Column(
                        modifier = Modifier.wrapContentWidth(),
                        verticalArrangement = Arrangement.spacedBy(Dimensions.size8dp)
                    ) {
                        metrics.chunked(1).forEach { rowMetrics ->
                            Row(
                                modifier = Modifier.wrapContentWidth(),
                                horizontalArrangement = Arrangement.spacedBy(Dimensions.size8dp)
                            ) {
                                rowMetrics.forEach { metricRecommendation ->
                                    MetricChip(
                                        metric = metricRecommendation.metric.metric,
                                    )
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(Dimensions.size16dp))
                }
            }

            val action = recommendation.actions?.firstOrNull()
            val userAction = action?.user_recommendation_actions?.firstOrNull()

            val isEnabled = userAction == null || userAction.is_completed == false

            Button(
                onClick = { accessToken?.let { viewModel.addToPlan(it, recommendation) } },
                enabled = isEnabled,
                modifier = Modifier.align(Alignment.End)
            ) {
                Text(
                    text = "+ Add to Plan",
                    fontSize = FontSize.textSize16sp,
                    color = AppColors.white,
                    fontFamily = FontFamily.bold(),
                )
            }
        }
    }
}

@Composable
fun MetricChip(
    metric: String,
    modifier: Modifier = Modifier,
) {
    Surface(
        color = AppColors.SubGreyColor,
        shape = MaterialTheme.shapes.small,
        modifier = modifier
    ) {
        Text(
            text = metric,
            modifier = Modifier.padding(
                horizontal = Dimensions.size8dp,
                vertical = Dimensions.size4dp
            ),
            fontSize = FontSize.textSize14sp,
            color = AppColors.white,
            fontFamily = FontFamily.medium(),
        )
    }
} 