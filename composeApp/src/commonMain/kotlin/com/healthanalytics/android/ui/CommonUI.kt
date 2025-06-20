package com.healthanalytics.android.ui

import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.healthanalytics.android.data.models.RecommendationCategoryes
import com.healthanalytics.android.data.models.RecommendationIcon
import com.healthanalytics.android.presentation.theme.AppColors
import org.jetbrains.compose.resources.painterResource

@OptIn(org.jetbrains.compose.resources.ExperimentalResourceApi::class)
@Composable
fun RecommendationIcon(category: RecommendationCategoryes, modifier: Modifier) {
    when (val icon = category.icon) {
        is RecommendationIcon.Vector -> Icon(
            imageVector = icon.imageVector,
            contentDescription = null,
            tint = AppColors.White,
            modifier = modifier
        )

        is RecommendationIcon.Painter -> Icon(
            painter = painterResource(icon.resource),
            contentDescription = null,
            tint = AppColors.White,
            modifier = modifier
        )
    }
}