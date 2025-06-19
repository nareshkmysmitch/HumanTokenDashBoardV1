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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.healthanalytics.android.data.models.Recommendation
import com.healthanalytics.android.data.models.RecommendationCategoryes
import com.healthanalytics.android.presentation.preferences.PreferencesViewModel
import com.healthanalytics.android.presentation.screens.actionplan.MetricsGrid
import com.healthanalytics.android.presentation.theme.AppColors
import com.healthanalytics.android.presentation.theme.AppStrings
import com.healthanalytics.android.presentation.theme.Dimensions
import com.healthanalytics.android.presentation.theme.FontFamily
import com.healthanalytics.android.presentation.theme.FontSize
import com.healthanalytics.android.ui.CardPrimaryButton
import com.healthanalytics.android.ui.RecommendationIcon
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
    val categoryEnum = RecommendationCategoryes.fromString(category)
    androidx.compose.material3.FilterChip(
        selected = selected, onClick = onClick,
        label = {
            Row(
                modifier = Modifier.padding(
                    vertical = Dimensions.size8dp, horizontal = Dimensions.size4dp
                ), verticalAlignment = Alignment.CenterVertically
            ) {
                RecommendationIcon(categoryEnum, Modifier.size(Dimensions.size14dp))
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

                CardPrimaryButton(
                    modifier = Modifier.wrapContentWidth(),
                    txt = buttonText,
                    buttonColor = buttonColor,
                    onClick = {
                        if (isEnabled) accessToken?.let {
                            viewModel.addToPlan(
                                it, recommendation
                            )
                        }
                    }
                )
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
        color = AppColors.White
    )
}

@Composable
fun RecommendationTitle(recommendation: Recommendation) {
    val icon = RecommendationCategoryes.fromString(recommendation.category)
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.size(Dimensions.size36dp)
                .background(AppColors.backgroundDark, shape = CircleShape),
            contentAlignment = Alignment.Center
        ) {
            RecommendationIcon(icon, modifier = Modifier.size(Dimensions.size16dp))
        }
        Spacer(modifier = Modifier.width(Dimensions.size8dp))
        Text(
            text = recommendation.name,
            fontSize = FontSize.textSize20sp,
            fontFamily = FontFamily.semiBold(),
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
            fontSize = FontSize.textSize12sp,
            fontFamily = FontFamily.regular(),
            color = AppColors.White
        )
    }
} 