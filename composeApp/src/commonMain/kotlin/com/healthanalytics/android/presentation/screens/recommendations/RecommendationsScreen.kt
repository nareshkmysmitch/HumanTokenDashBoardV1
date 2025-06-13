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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.healthanalytics.android.BackHandler
import com.healthanalytics.android.data.models.Recommendation
import com.healthanalytics.android.data.models.RecommendationCategory
import com.healthanalytics.android.presentation.preferences.PreferencesViewModel
import com.healthanalytics.android.presentation.theme.AppColors
import org.koin.compose.koinInject

@Composable
fun RecommendationsScreen(
    viewModel: RecommendationsViewModel,
    preferencesViewModel: PreferencesViewModel,
    navigateBack: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsState()
    val preferencesState by preferencesViewModel.uiState.collectAsState()
    val filterList = viewModel.getFilteredRecommendations()

    BackHandler { navigateBack() }

    LaunchedEffect(preferencesState.data) {
        preferencesState.data?.let { token ->
            viewModel.loadRecommendations(token)
        }
    }
    Column(
        modifier = Modifier.fillMaxSize().background(AppColors.AppBackgroundColor)
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
                modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(horizontal = 12.dp)
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
                contentPadding = PaddingValues(12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
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
        selected = selected, onClick = onClick, label = {
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(categoryEnum.icon)
                Text("$category ($count)")
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
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = AppColors.White // Replace with your desired color
        ),
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(8.dp)
        ) {
            // Title and Difficulty
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = RecommendationCategory.fromString(recommendation.category).icon,
                        style = MaterialTheme.typography.titleLarge
                    )
                    Text(
                        text = recommendation.name,
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Potential Impact",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Metrics Grid
            recommendation.metric_recommendations?.let { metrics ->
                if (metrics.isNotEmpty()) {
                    Column(
                        modifier = Modifier.wrapContentWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        metrics.chunked(1).forEach { rowMetrics ->
                            Row(
                                modifier = Modifier.wrapContentWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                rowMetrics.forEach { metricRecommendation ->
                                    MetricChip(
                                        metric = metricRecommendation.metric.metric,
//                                        modifier = Modifier.weight(1f)
                                    )
                                }
                                // Add empty space if odd number of metrics
//                                if (rowMetrics.size == 1) {
//                                    Spacer(modifier = Modifier.weight(1f))
//                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }

            val action = recommendation.actions?.firstOrNull()
            val userAction = action?.user_recommendation_actions?.firstOrNull()

            val isEnabled = userAction == null || userAction.is_completed == false

            // Add to Plan Button
            Button(
                onClick = { accessToken?.let { viewModel.addToPlan(it, recommendation) } },
                enabled = isEnabled,
                modifier = Modifier.align(Alignment.End)
            ) {
                Text("+ Add to Plan")
            }
        }
    }
}

@Composable
fun DifficultyChip(difficulty: String) {
    val (backgroundColor, textColor) = when (difficulty.lowercase()) {
        "easy" -> MaterialTheme.colorScheme.primary to MaterialTheme.colorScheme.onPrimary
        "medium" -> MaterialTheme.colorScheme.secondary to MaterialTheme.colorScheme.onSecondary
        "hard" -> MaterialTheme.colorScheme.tertiary to MaterialTheme.colorScheme.onTertiary
        else -> MaterialTheme.colorScheme.surfaceVariant to MaterialTheme.colorScheme.onSurfaceVariant
    }

    Surface(
        color = backgroundColor, shape = MaterialTheme.shapes.small
    ) {
        Text(
            text = difficulty.replaceFirstChar { it.uppercase() },
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelSmall,
            color = textColor
        )
    }
}

@Composable
fun MetricChip(
    metric: String,
    modifier: Modifier = Modifier,
) {
    Surface(
        color = MaterialTheme.colorScheme.secondaryContainer,
        shape = MaterialTheme.shapes.small,
        modifier = modifier
    ) {
        Text(
            text = metric,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSecondaryContainer
        )
    }
} 