package com.healthanalytics.android.presentation.screens.actionplan

import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.healthanalytics.android.data.models.MetricRecommendation
import com.healthanalytics.android.data.models.Recommendation
import com.healthanalytics.android.data.models.RecommendationCategoryes
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
import com.healthanalytics.android.ui.CardTransparentButton
import com.healthanalytics.android.ui.RecommendationIcon
import com.healthanalytics.android.ui.TransparentButton
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
    val categoryEnum = RecommendationCategoryes.fromString(category)

    FilterChip(
        selected = selected,
        onClick = onClick,
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
            Row(
                horizontalArrangement = Arrangement.spacedBy(Dimensions.size4dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RecommendationIcon(categoryEnum, modifier = Modifier.size(Dimensions.size14dp))
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
            color = AppColors.Teal
        ) {
            Icon(
                imageVector = Icons.Default.Assignment,
                contentDescription = null,
                modifier = Modifier.padding(Dimensions.size16dp).fillMaxSize(),
                tint = AppColors.White
            )
        }

        Spacer(modifier = Modifier.height(Dimensions.size16dp))

        Text(
            text = AppStrings.YOUR_ACTION_PLAN_IS_EMPTY,
            fontSize = FontSize.textSize24sp,
            fontFamily = FontFamily.bold(),
            color = AppColors.White,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(Dimensions.size8dp))

        Text(
            text = AppStrings.ADD_RECOMMENDATIONS,
            fontSize = FontSize.textSize16sp,
            fontFamily = FontFamily.medium(),
            color = AppColors.White,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(Dimensions.size24dp))

        TransparentButton(
            modifier = Modifier.wrapContentWidth(),
            icon = Icons.Default.ArrowBack,
            txt = AppStrings.BROWSE_RECOMMENDATIONS,
            onClicked = {
                viewModel.setSelectedTab(RecommendationsTab.RECOMMENDATIONS)
            })
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
            color = AppColors.Teal
        ) {
            Icon(
                imageVector = Icons.Default.FilterAlt,
                contentDescription = null,
                modifier = Modifier.padding(Dimensions.size16dp).fillMaxSize(),
                tint = AppColors.White
            )
        }

        Spacer(modifier = Modifier.height(Dimensions.size16dp))

        Text(
            text = AppStrings.NO_ITEMS_IN_THIS_CATEGORY,
            fontSize = FontSize.textSize24sp,
            fontFamily = FontFamily.bold(),
            color = AppColors.White,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(Dimensions.size8dp))

        Text(
            text = AppStrings.NOT_HAVE_ANY_ACTION_PLAN,
            fontSize = FontSize.textSize16sp,
            fontFamily = FontFamily.medium(),
            color = AppColors.White,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(Dimensions.size24dp))

        TransparentButton(
            modifier = Modifier.wrapContentWidth(),
            icon = Icons.Default.Dashboard,
            txt = AppStrings.SHOW_ALL_ITEMS,
            onClicked = {
                viewModel.updateActionCategory(AppConstants.ALL)
            })
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
        AppConstants.SUPPLEMENTS, ignoreCase = true
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
                    fontFamily = FontFamily.regular(),
                    color = AppColors.textPrimary
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
                    fontFamily = FontFamily.regular(),
                    color = AppColors.textPrimary,
                )
                CardTransparentButton(
                    modifier = Modifier.wrapContentWidth(),
                    icon = Icons.Default.Delete,
                    txt = AppStrings.REMOVE,
                    onClicked = {
                        accessToken?.let {
                            if (recommendation.category.equals(
                                    AppConstants.SUPPLEMENTS, ignoreCase = true
                                )
                            ) {
                                viewModel.removeSupplements(it, recommendation)
                            } else {
                                viewModel.removeRecommendation(it, recommendation)
                            }
                        }
                    })
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
                verticalArrangement = Arrangement.spacedBy(Dimensions.size4dp)
            ) {

                FlowRow(
                    modifier = Modifier.padding(vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    metrics.forEach { metricRecommendation ->
                        MetricChip(
                            metric = metricRecommendation.metric.metric
                        )
                    }
                }

//                metrics.chunked(2).forEach { rowMetrics ->
//                    Row(
//                        modifier = Modifier.fillMaxWidth(),
//                        horizontalArrangement = Arrangement.spacedBy(Dimensions.size8dp)
//                    ) {
//                        rowMetrics.forEach { metricRecommendation ->
//                            MetricChip(
//                                metric = metricRecommendation.metric.metric,
//                                modifier = Modifier.weight(1f)
//                            )
//                        }
//                        if (rowMetrics.size == 1) {
//                            Spacer(modifier = Modifier.weight(1f))
//                        }
//                    }
//                }
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