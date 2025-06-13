package com.healthanalytics.android.presentation.screens.actionplan

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.healthanalytics.android.data.models.Recommendation
import com.healthanalytics.android.data.models.RecommendationCategory
import com.healthanalytics.android.presentation.preferences.PreferencesViewModel
import com.healthanalytics.android.presentation.screens.recommendations.MetricChip
import com.healthanalytics.android.presentation.screens.recommendations.RecommendationsTab
import com.healthanalytics.android.presentation.screens.recommendations.RecommendationsViewModel
import com.healthanalytics.android.presentation.theme.AppColors
import com.healthanalytics.android.utils.capitalizeFirst
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.koin.compose.koinInject

@Composable
fun ActionPlanScreen(
    viewModel: RecommendationsViewModel,
    preferencesViewModel: PreferencesViewModel,
) {
    val uiState by viewModel.uiActionState.collectAsState()
    val preferencesState by preferencesViewModel.uiState.collectAsState()
    val filteredRecommendations = viewModel.getFilteredActions()
    val totalItems = viewModel.getActionTotalItems()
    val categoryList = viewModel.getActionCategories()

    LaunchedEffect(preferencesState.data) {
        preferencesState.data?.let { token ->
            viewModel.loadActionRecommendations(token)
        }
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // Header Section
        /* Row(
             modifier = Modifier.fillMaxWidth().padding(16.dp),
             horizontalArrangement = Arrangement.End,
             verticalAlignment = Alignment.CenterVertically
         ) {
             Text(
                 text = "$totalItems items",
                 style = MaterialTheme.typography.titleMedium,
                 color = MaterialTheme.colorScheme.onSurfaceVariant
             )
         }*/

        println("category--> ${viewModel.getActionCategories()}")




        Spacer(modifier = Modifier.height(16.dp))

        // Content Section
        if (uiState.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (totalItems == 0) {
            EmptyActionPlan(viewModel)
        } else if (filteredRecommendations.isEmpty()) {
            EmptyCategoryView(viewModel)
        } else {
            // Category Row
            LazyRow(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(categoryList) { category ->
                    CategoryChip(
                        category = category,
                        selected = category == uiState.selectedCategory,
                        onClick = { viewModel.updateActionCategory(category) })
                }
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(filteredRecommendations) { recommendation ->
                    ActionPlanCard(
                        recommendation = recommendation, viewModel, preferencesState.data
                    )
                }
            }
        }
    }
}

@Composable
fun CategoryChip(
    category: String,
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
                Text(category.capitalizeFirst())
            }
        })
}

@Composable
fun EmptyActionPlan(viewModel: RecommendationsViewModel) {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Surface(
            modifier = Modifier.size(80.dp),
            shape = CircleShape,
            color = MaterialTheme.colorScheme.surfaceVariant
        ) {
            Icon(
                imageVector = Icons.Default.Assignment,
                contentDescription = null,
                modifier = Modifier.padding(16.dp).fillMaxSize(),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Your Action Plan is Empty",
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Add recommendations to your action plan to start tracking your health goals",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = { viewModel.setSelectedTab(RecommendationsTab.RECOMMENDATIONS) }) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Browse Recommendations")
        }
    }
}

@Composable
fun EmptyCategoryView(viewModel: RecommendationsViewModel) {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Surface(
            modifier = Modifier.size(80.dp),
            shape = CircleShape,
            color = MaterialTheme.colorScheme.surfaceVariant
        ) {
            Icon(
                imageVector = Icons.Default.FilterList,
                contentDescription = null,
                modifier = Modifier.padding(16.dp).fillMaxSize(),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "No Items in This Category",
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "You don't have any action plan items in the selected category",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = { viewModel.updateActionCategory("All") }) {
            Icon(
                imageVector = Icons.Default.Dashboard,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Show All Items")
        }
    }
}

@Composable
fun ActionPlanCard(
    recommendation: Recommendation,
    viewModel: RecommendationsViewModel,
    accessToken: String?,
) {
    val createAt =
        recommendation.actions?.firstOrNull()?.user_recommendation_actions?.firstOrNull()?.created_at
    val formattedDate = formatDate(createAt)

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = AppColors.White
        ),
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(16.dp)
        ) {
            // Header with icon and title
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
                        text = recommendation.name, style = MaterialTheme.typography.titleMedium
                    )
                }


            }

            Spacer(modifier = Modifier.height(16.dp))

            // Potential Impact Section
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
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        metrics.chunked(2).forEach { rowMetrics ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                rowMetrics.forEach { metricRecommendation ->
                                    MetricChip(
                                        metric = metricRecommendation.metric.metric,
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                                if (rowMetrics.size == 1) {
                                    Spacer(modifier = Modifier.weight(1f))
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Added Date
                Text(
                    text = "Added $formattedDate",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                OutlinedButton(onClick = {
                    accessToken?.let {
                        if (recommendation.category == "supplements") {
                            viewModel.removeSupplements(it, recommendation)
                        } else {
                            viewModel.removeRecommendation(it, recommendation)
                        }
                    }
                }) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Remove",
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Remove")
                }
            }
        }
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
        "$day/$month/$year"
    } ?: ""
}