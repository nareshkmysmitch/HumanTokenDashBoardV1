package com.healthanalytics.android.presentation.recommendations

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import com.healthanalytics.android.data.models.Recommendation
import com.healthanalytics.android.data.models.RecommendationCategory
import com.healthanalytics.android.presentation.preferences.PreferencesViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun RecommendationsScreen(
    viewModel: RecommendationsViewModel = koinViewModel(),
    preferencesViewModel: PreferencesViewModel = koinViewModel(),
) {

    val uiState by viewModel.uiState.collectAsState()
    val preferencesState by preferencesViewModel.uiState.collectAsState()


    LaunchedEffect(preferencesState.data) {
        preferencesState.data?.let { token ->
            viewModel.loadRecommendations(token)
        }
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // Header
        Text(
            text = "Recommendations",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(16.dp)
        )

        // Recommendations List
        if (uiState.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (uiState.error != null) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = uiState.error ?: "",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        } else {
            // Category Selector
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(horizontal = 16.dp)
            ) {
                items(viewModel.getAvailableCategories()) { category ->
                    CategoryChip(
                        category = category,
                        count = viewModel.getCategoryCount(category),
                        selected = category == uiState.selectedCategory,
                        onClick = { viewModel.updateSelectedCategory(category) }
                    )
                }
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(viewModel.getFilteredRecommendations()) { recommendation ->
                    RecommendationCard(recommendation = recommendation)
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
        selected = selected,
        onClick = onClick,
        label = {
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(categoryEnum.icon)
                Text("$category ($count)")
            }
        }
    )
}

@Composable
fun RecommendationCard(recommendation: Recommendation) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Title and Difficulty
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = recommendation.name,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                recommendation.difficulty?.let { difficulty ->
                    DifficultyChip(difficulty = difficulty)
                }
            }

            // Description if available
            recommendation.description?.let { description ->
                if (description.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Metrics Grid
            recommendation.metric_recommendations?.let { metrics ->
                if (metrics.isNotEmpty()) {
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        metrics.forEach { metricRecommendation ->
                            MetricChip(metric = metricRecommendation.metric.metric)
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }

            // Add to Plan Button
            Button(
                onClick = { /* TODO: Implement add to plan */ },
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
        color = backgroundColor,
        shape = MaterialTheme.shapes.small
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
fun MetricChip(metric: String) {
    Surface(
        color = MaterialTheme.colorScheme.secondaryContainer,
        shape = MaterialTheme.shapes.small
    ) {
        Text(
            text = metric,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSecondaryContainer
        )
    }
} 