package com.healthanalytics.android.presentation.screens.recommendations

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
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
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.healthanalytics.android.data.models.Recommendation
import com.healthanalytics.android.data.models.RecommendationCategory
import com.healthanalytics.android.presentation.preferences.PreferencesViewModel
import com.healthanalytics.android.presentation.theme.AppColors
import com.healthanalytics.android.presentation.theme.Dimensions
import com.healthanalytics.android.presentation.theme.FontFamily
import com.healthanalytics.android.presentation.theme.FontSize
import com.healthanalytics.android.presentation.theme.backgroundDark
import com.healthanalytics.android.presentation.theme.backgroundLight
import com.healthanalytics.android.presentation.theme.onSurfaceVariantLight
import org.koin.compose.koinInject

@Composable
fun RecommendationsScreen(
    viewModel: RecommendationsViewModel,
    preferencesViewModel: PreferencesViewModel = koinInject(),
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
        modifier = Modifier.fillMaxSize().background(Color(0xFF111318))
    ) {
        // Header
//        Text(
//            text = "Recommendations",
//            style = MaterialTheme.typography.headlineMedium,
//            modifier = Modifier.padding(16.dp)
//        )

        // Recommendations List
        if (uiState.isLoading || preferencesState.data == null) {
            Box(
                modifier = Modifier.fillMaxSize().background(AppColors.AppBackgroundColor),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            // Subtitle with selected category and count
//            Text(
//                text = "${uiState.selectedCategory?.capitalizeFirst()} Recommendations (${
//                    uiState.selectedCategory?.let {
//                        viewModel.getCategoryCount(
//                            it
//                        )
//                    }
//                })",
//                style = MaterialTheme.typography.titleMedium,
//                color = MaterialTheme.colorScheme.onSurfaceVariant,
//                modifier = Modifier.padding(horizontal = 16.dp))

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
    androidx.compose.material3.FilterChip(
        selected = selected,
        onClick = onClick,
        label = {
            Row(
                modifier = Modifier.padding(
                    vertical = Dimensions.size8dp,
                    horizontal = Dimensions.size4dp
                ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = categoryEnum.icon,
                    fontSize = FontSize.textSize14sp,
                    fontFamily = FontFamily.medium(),
                    color = AppColors.tertiary
                )
                Spacer(modifier = Modifier.width(Dimensions.size4dp))
                Text(
                    text = "$category ($count)",
                    fontSize = FontSize.textSize14sp,
                    fontFamily = FontFamily.medium(),
                    color = AppColors.textPrimary
                )
            }
        },
        colors = androidx.compose.material3.FilterChipDefaults.filterChipColors(
            containerColor = if (selected) AppColors.Pink.copy(alpha = 0.5f) else AppColors.Pink.copy(alpha = 0.1f),
            labelColor = AppColors.textPrimary,
            selectedContainerColor = AppColors.Pink.copy(alpha = 0.5f),
            selectedLabelColor = AppColors.white
        ),
        border = androidx.compose.material3.FilterChipDefaults.filterChipBorder(
            enabled = true,
            selected=selected,
            borderColor = if (selected) androidx.compose.ui.graphics.Color.Transparent else  AppColors.Pink.copy(alpha = 0.2f)
        )
    )
}

@Composable
fun RecommendationCard(
    recommendation: Recommendation,
    viewModel: RecommendationsViewModel,
    accessToken: String?,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = Dimensions.size16dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1e2025)
        ),
        shape = RoundedCornerShape(Dimensions.cornerRadiusLarge)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimensions.size20dp)
        ) {
            // Title Row
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(AppColors.backgroundDark, shape = CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = RecommendationCategory.fromString(recommendation.category).icon,
                        fontSize = FontSize.textSize20sp,
                        fontFamily = FontFamily.medium(),
                        color = AppColors.secondary
                    )
                }
                Spacer(modifier = Modifier.width(Dimensions.size12dp))
                Text(
                    text = recommendation.name,
                    fontSize = FontSize.textSize18sp,
                    fontFamily = FontFamily.medium(),
                    color = AppColors.textPrimary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.height(Dimensions.size8dp))

            Text(
                text = "Potential Impact",
                fontSize = FontSize.textSize14sp,
                fontFamily = FontFamily.medium(),
                color = AppColors.textSecondary
            )

            Spacer(modifier = Modifier.height(Dimensions.size8dp))

            // Metrics Grid (2 columns)
            recommendation.metric_recommendations?.let { metrics ->
                if (metrics.isNotEmpty()) {
                    androidx.compose.foundation.layout.FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        maxItemsInEachRow = 2,
                        horizontalArrangement = Arrangement.spacedBy(Dimensions.size8dp),
                        verticalArrangement = Arrangement.spacedBy(Dimensions.size8dp)
                    ) {
                        metrics.forEach { metricRecommendation ->
                            Box(
                                modifier = Modifier
                                    .background(
                                        color = AppColors.backgroundDark,
                                        shape = RoundedCornerShape(10.dp)
                                    )
                                    .padding(vertical = Dimensions.size6dp, horizontal = Dimensions.size8dp),
                                contentAlignment = Alignment.CenterStart
                            ) {
                                Text(
                                    text = metricRecommendation.metric.metric.uppercase(),
                                    fontSize = FontSize.textSize12sp,
                                    fontFamily = FontFamily.regular(),
                                    color = AppColors.textPrimary
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(Dimensions.size16dp))
                }
            }

            val action = recommendation.actions?.firstOrNull()
            val userAction = action?.user_recommendation_actions?.firstOrNull()
            val isEnabled = userAction == null || userAction.is_completed == false

            // Add to Plan Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                val buttonText = if (isEnabled) "+ Add to Plan" else "Added to plan"
                val buttonColor = if (isEnabled) AppColors.Pink else AppColors.Pink.copy(alpha = 0.2f)
                androidx.compose.material3.Button(
                    onClick = { if (isEnabled) accessToken?.let { viewModel.addToPlan(it, recommendation) } },
                    enabled = true,
                    shape = RoundedCornerShape(24.dp),
                    colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                        containerColor = buttonColor,
                        contentColor = AppColors.white
                    ),
                    contentPadding = PaddingValues(horizontal = 24.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = buttonText,
                        fontSize = FontSize.textSize14sp,
                        fontFamily = FontFamily.medium(),
                        color = AppColors.white
                    )
                }
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