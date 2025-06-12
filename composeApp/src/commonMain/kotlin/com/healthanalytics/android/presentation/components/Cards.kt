package com.healthanalytics.android.presentation.components

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.healthanalytics.android.presentation.components.CardDefaults.defaultElevation
import com.healthanalytics.android.presentation.theme.AppColors

object CardDefaults {
    val defaultCornerRadius = 12.dp
    val defaultElevation = 2.dp
}

@Composable
fun AppCard(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(com.healthanalytics.android.presentation.components.CardDefaults.defaultCornerRadius),
        colors = CardDefaults.cardColors(
            containerColor = AppColors.GrayCardColor,
            contentColor = AppColors.DarkPurple
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = defaultElevation
        ),
        content = content
    )
} 