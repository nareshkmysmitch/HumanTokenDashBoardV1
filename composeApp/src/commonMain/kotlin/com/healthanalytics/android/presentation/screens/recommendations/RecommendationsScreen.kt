package com.healthanalytics.android.presentation.screens.recommendations

import androidx.compose.foundation.Image
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.healthanalytics.android.data.models.Recommendation
import com.healthanalytics.android.data.models.RecommendationCategory
import com.healthanalytics.android.presentation.preferences.PreferencesViewModel
import com.healthanalytics.android.presentation.screens.actionplan.MetricsGrid
import com.healthanalytics.android.presentation.theme.AppColors
import com.healthanalytics.android.presentation.theme.AppStrings
import com.healthanalytics.android.presentation.theme.Dimensions
import com.healthanalytics.android.presentation.theme.FontFamily
import com.healthanalytics.android.presentation.theme.FontSize
import com.healthanalytics.android.utils.AppConstants
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
        modifier = Modifier.fillMaxSize().background(AppColors.Black)
    ) {
        // Recommendations List
        if (uiState.isLoading || preferencesState.data == null) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            // Category Selector
            LazyRow(
                modifier = Modifier.fillMaxWidth().padding(top = Dimensions.size8dp),
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
        selected = selected, onClick = onClick, label = {
            Row(
                modifier = Modifier.padding(
                    vertical = Dimensions.size8dp, horizontal = Dimensions.size4dp
                ), verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    imageVector = categoryEnum.icon,
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(AppColors.white)
                )
                /* Text(
                     text = categoryEnum.icon,
                     fontSize = FontSize.textSize14sp,
                     fontFamily = FontFamily.medium(),
                     color = AppColors.tertiary
                 )*/
                Spacer(modifier = Modifier.width(Dimensions.size4dp))
                Text(
                    text = "$category ($count)",
                    fontSize = FontSize.textSize14sp,
                    fontFamily = FontFamily.medium(),
                    color = AppColors.textPrimary
                )
            }
        }, colors = androidx.compose.material3.FilterChipDefaults.filterChipColors(
            containerColor = if (selected) AppColors.Pink.copy(alpha = 0.5f) else AppColors.Pink.copy(
                alpha = 0.1f
            ),
            labelColor = AppColors.textPrimary,
            selectedContainerColor = AppColors.Pink.copy(alpha = 0.5f),
            selectedLabelColor = AppColors.white
        ), border = androidx.compose.material3.FilterChipDefaults.filterChipBorder(
            enabled = true,
            selected = selected,
            borderColor = if (selected) androidx.compose.ui.graphics.Color.Transparent else AppColors.Pink.copy(
                alpha = 0.2f
            )
        )
    )
}

@Composable
fun RecommendationCard(
    recommendation: Recommendation,
    viewModel: RecommendationsViewModel,
    accessToken: String?,
) {
    val metricRecommendation = recommendation.metric_recommendations
    val action = recommendation.actions?.firstOrNull()
    val userAction = action?.user_recommendation_actions?.firstOrNull()
    val isEnabled = userAction == null || userAction.is_completed == false

    val isSupplements = (recommendation.category?.equals(
        AppConstants.SUPPLEMENTS,
        ignoreCase = true
    ) == true)

    Card(
        modifier = Modifier.fillMaxWidth().padding(bottom = Dimensions.size8dp),
        colors = CardDefaults.cardColors(
            containerColor = AppColors.CardGrey
        ),
        shape = RoundedCornerShape(Dimensions.cornerRadiusLarge)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(Dimensions.size12dp)
        ) {
            // Title Row
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

                MetricsGrid(metricRecommendation)
            }

            Spacer(modifier = Modifier.height(Dimensions.size16dp))

            // Add to Plan Button
            Row(
                modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End
            ) {

                val buttonText = if (isEnabled) AppStrings.ADD_TO_PLAN else AppStrings.ADDED_TO_PLAN
                val buttonColor =
                    if (isEnabled) AppColors.Pink else AppColors.Pink.copy(alpha = 0.2f)

                Button(
                    onClick = {
                        if (isEnabled) accessToken?.let {
                            viewModel.addToPlan(
                                it, recommendation
                            )
                        }
                    },
                    enabled = true,
                    shape = RoundedCornerShape(24.dp),
                    colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                        containerColor = buttonColor, contentColor = AppColors.white
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
fun PotentialImpact() {
    Text(
        text = AppStrings.POTENTIAL_IMPACT,
        fontSize = FontSize.textSize14sp,
        fontFamily = FontFamily.medium(),
        color = AppColors.white
    )
}

@Composable
fun RecommendationTitle(recommendation: Recommendation) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.size(Dimensions.size36dp)
                .background(AppColors.backgroundDark, shape = CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Image(
                imageVector = RecommendationCategory.fromString(recommendation.category).icon,
                contentDescription = null,
                colorFilter = ColorFilter.tint(AppColors.white)
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
}


@Composable
fun MetricChip(
    metric: String,
    modifier: Modifier = Modifier,
) {
    Surface(
        color = AppColors.SubGreyColor, shape = MaterialTheme.shapes.small, modifier = modifier
    ) {
        Text(
            text = metric,
            modifier = Modifier.padding(
                horizontal = Dimensions.size8dp,
                vertical = Dimensions.size4dp
            ),
            style = MaterialTheme.typography.bodySmall,
            color = AppColors.white
        )
    }
} 