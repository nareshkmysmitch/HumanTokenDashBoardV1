package com.healthanalytics.android.presentation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.healthanalytics.android.presentation.theme.AppColors

object ButtonDefaults {
    val defaultHorizontalPadding = 16.dp
    val defaultVerticalPadding = 12.dp

    val defaultContentPadding = PaddingValues(
        horizontal = defaultHorizontalPadding, vertical = defaultVerticalPadding
    )
}

@Composable
fun FilledAppButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    contentPadding: PaddingValues = com.healthanalytics.android.presentation.components.ButtonDefaults.defaultContentPadding,
    content: @Composable RowScope.() -> Unit // <- fix here
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        colors = ButtonDefaults.buttonColors(
            containerColor = AppColors.Pink,
            contentColor = AppColors.White,
            disabledContainerColor = AppColors.Pink.copy(alpha = 0.6f),
            disabledContentColor = AppColors.White.copy(alpha = 0.6f)
        ),
        contentPadding = contentPadding,
        content = content,
    )
}

@Composable
fun OutlinedAppButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    contentPadding: PaddingValues = com.healthanalytics.android.presentation.components.ButtonDefaults.defaultContentPadding,
    content: @Composable RowScope.() -> Unit // <- fix here
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = AppColors.White,
            contentColor = AppColors.DarkPurple,
            disabledContainerColor = AppColors.White.copy(alpha = 0.6f),
            disabledContentColor = AppColors.DarkPurple.copy(alpha = 0.6f)
        ),
        border = BorderStroke(1.dp, AppColors.DarkPurple),
        contentPadding = contentPadding,
        content = content
    )
} 