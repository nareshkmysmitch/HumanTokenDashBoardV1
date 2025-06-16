package com.healthanalytics.android.presentation.screens.actionplan

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
import com.healthanalytics.android.data.models.MetricRecommendation
import com.healthanalytics.android.data.models.Recommendation
import com.healthanalytics.android.data.models.RecommendationCategory
import com.healthanalytics.android.presentation.preferences.PreferencesViewModel
import com.healthanalytics.android.presentation.screens.recommendations.MetricChip
import com.healthanalytics.android.presentation.screens.recommendations.PotentialImpact
import com.healthanalytics.android.presentation.screens.recommendations.RecommendationTitle
import com.healthanalytics.android.presentation.screens.recommendations.RecommendationsTab
import com.healthanalytics.android.presentation.screens.recommendations.RecommendationsViewModel
import com.healthanalytics.android.presentation.theme.AppColors
import com.healthanalytics.android.presentation.theme.AppStrings
import com.healthanalytics.android.presentation.theme.Dimensions
import com.healthanalytics.android.presentation.theme.FontFamily
import com.healthanalytics.android.presentation.theme.FontSize
import com.healthanalytics.android.utils.AppConstants
import com.healthanalytics.android.utils.capitalizeFirst
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

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
        modifier = Modifier.fillMaxSize().background(AppColors.Black)
    ) {
        // Content Section
        if (uiState.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
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
                modifier = Modifier.fillMaxWidth().padding(top = Dimensions.size8dp),
                horizontalArrangement = Arrangement.spacedBy(Dimensions.size12dp),
                contentPadding = PaddingValues(horizontal = Dimensions.size12dp)
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
                contentPadding = PaddingValues(Dimensions.size16dp),
                verticalArrangement = Arrangement.spacedBy(Dimensions.size16dp)
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
        selected = selected,
        onClick = onClick,
        colors = androidx.compose.material3.FilterChipDefaults.filterChipColors(
            containerColor = if (selected) AppColors.Pink.copy(alpha = 0.5f) else AppColors.Pink.copy(
                alpha = 0.1f
            ),
            labelColor = AppColors.textPrimary,
            selectedContainerColor = AppColors.Pink.copy(alpha = 0.5f),
            selectedLabelColor = AppColors.white
        ),
        border = androidx.compose.material3.FilterChipDefaults.filterChipBorder(
            enabled = true,
            selected = selected,
            borderColor = if (selected) androidx.compose.ui.graphics.Color.Transparent else AppColors.Pink.copy(
                alpha = 0.2f
            )
        ),
        label = {
            Row(
                horizontalArrangement = Arrangement.spacedBy(Dimensions.size4dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(categoryEnum.icon)
                Text(
                    category.capitalizeFirst(),
                    fontSize = FontSize.textSize14sp,
                    fontFamily = FontFamily.medium(),
                    color = AppColors.textPrimary,
                    textAlign = TextAlign.Center
                )
            }
        })
}

@Composable
fun EmptyActionPlan(viewModel: RecommendationsViewModel) {
    Column(
        modifier = Modifier.fillMaxSize().padding(Dimensions.size16dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Surface(
            modifier = Modifier.size(Dimensions.size80dp),
            shape = CircleShape,
            color = MaterialTheme.colorScheme.surfaceVariant
        ) {
            Icon(
                imageVector = Icons.Default.Assignment,
                contentDescription = null,
                modifier = Modifier.padding(Dimensions.size16dp).fillMaxSize(),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(modifier = Modifier.height(Dimensions.size16dp))

        Text(
            text = AppStrings.YOUR_ACTION_PLAN_IS_EMPTY,
            fontSize = FontSize.textSize24sp,
            fontFamily = FontFamily.bold(),
            color = AppColors.white,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(Dimensions.size8dp))

        Text(
            text = AppStrings.ADD_RECOMMENDATIONS,
            fontSize = FontSize.textSize16sp,
            fontFamily = FontFamily.medium(),
            color = AppColors.TextGrey,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(Dimensions.size24dp))

        Button(
            onClick = { viewModel.setSelectedTab(RecommendationsTab.RECOMMENDATIONS) }) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = null,
                modifier = Modifier.size(Dimensions.size20dp)
            )
            Spacer(modifier = Modifier.width(Dimensions.size8dp))
            Text(
                AppStrings.BROWSE_RECOMMENDATIONS,
                fontSize = FontSize.textSize16sp,
                color = AppColors.white,
                fontFamily = FontFamily.bold(),
            )
        }
    }
}

@Composable
fun EmptyCategoryView(viewModel: RecommendationsViewModel) {
    Column(
        modifier = Modifier.fillMaxSize().padding(Dimensions.size16dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Surface(
            modifier = Modifier.size(Dimensions.size80dp),
            shape = CircleShape,
            color = MaterialTheme.colorScheme.surfaceVariant
        ) {
            Icon(
                imageVector = Icons.Default.FilterList,
                contentDescription = null,
                modifier = Modifier.padding(Dimensions.size16dp).fillMaxSize(),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(modifier = Modifier.height(Dimensions.size16dp))

        Text(
            text = AppStrings.NO_ITEMS_IN_THIS_CATEGORY,
            fontSize = FontSize.textSize24sp,
            fontFamily = FontFamily.bold(),
            color = AppColors.white,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(Dimensions.size8dp))

        Text(
            text = AppStrings.NOT_HAVE_ANY_ACTION_PLAN,
            fontSize = FontSize.textSize16sp,
            fontFamily = FontFamily.medium(),
            color = AppColors.TextGrey,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(Dimensions.size24dp))

        Button(
            onClick = { viewModel.updateActionCategory(AppConstants.ALL) }) {
            Icon(
                imageVector = Icons.Default.Dashboard,
                contentDescription = null,
                modifier = Modifier.size(Dimensions.size20dp)
            )
            Spacer(modifier = Modifier.width(Dimensions.size8dp))
            Text(
                AppStrings.SHOW_ALL_ITEMS, fontSize = FontSize.textSize16sp,
                color = AppColors.white,
                fontFamily = FontFamily.bold(),
            )
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

    val metricRecommendation = recommendation.metric_recommendations

    val isSupplements = recommendation.category.equals(
        AppConstants.SUPPLEMENTS,
        ignoreCase = true
    )

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = AppColors.CardGrey
        ),
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(Dimensions.size12dp)
        ) {
            // Header with icon and title
            RecommendationTitle(recommendation)

            Spacer(modifier = Modifier.height(Dimensions.size16dp))

            if (isSupplements) {
                Text(
                    text = recommendation.description ?: "",
                    fontSize = FontSize.textSize16sp,
                    fontFamily = FontFamily.medium(),
                    color = AppColors.textSecondary
                )
            } else {
                PotentialImpact()

                Spacer(modifier = Modifier.height(Dimensions.size8dp))

                // Metrics Grid
                MetricsGrid(metricRecommendation)
            }

            Spacer(modifier = Modifier.height(Dimensions.size16dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Added Date
                Text(
                    text = "Added $formattedDate",
                    fontSize = FontSize.textSize14sp,
                    fontFamily = FontFamily.medium(),
                    color = AppColors.TextGrey,
                )

                OutlinedButton(onClick = {
                    accessToken?.let {
                        if (recommendation.category.equals(
                                AppConstants.SUPPLEMENTS,
                                ignoreCase = true
                            )
                        ) {
                            viewModel.removeSupplements(it, recommendation)
                        } else {
                            viewModel.removeRecommendation(it, recommendation)
                        }
                    }
                }) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Remove",
                        modifier = Modifier.size(Dimensions.size20dp)
                    )
                    Spacer(modifier = Modifier.width(Dimensions.size8dp))
                    Text(
                        "Remove",
                        fontSize = FontSize.textSize16sp,
                        fontFamily = FontFamily.bold(),
                    )
                }
            }
        }
    }
}

@Composable
fun MetricsGrid(metricRecommendation: List<MetricRecommendation>?) {
    metricRecommendation.let { metrics ->
        if (metrics?.isNotEmpty() == true) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(Dimensions.size8dp)
            ) {
                metrics.chunked(2).forEach { rowMetrics ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(Dimensions.size8dp)
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
}


fun formatDate(isoString: String?): String {
    return isoString?.let {
        val instant = Instant.parse(isoString)
        val systemTz = TimeZone.currentSystemDefault()
        val localDateTime = instant.toLocalDateTime(systemTz)

        val day = localDateTime.dayOfMonth.toString().padStart(2, '0')
        val month =
            localDateTime.month.name.lowercase().replaceFirstChar { it.uppercase() }.take(3)
        val year = localDateTime.year
        "$day/$month/$year"
    } ?: ""
}