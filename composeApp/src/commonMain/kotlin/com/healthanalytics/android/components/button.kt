package com.healthanalytics.android.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.healthanalytics.android.presentation.theme.AppColors
import com.healthanalytics.android.presentation.theme.Dimensions
import com.healthanalytics.android.presentation.theme.FontFamily
import com.healthanalytics.android.presentation.theme.FontSize

@Composable
fun PrimaryButton(
    buttonName: String,
    onClick: () -> Unit,
    enable: Boolean = true,
    modifier: Modifier= Modifier
) {
    Button(
        enabled = enable,
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(height = Dimensions.size56dp),
        colors = ButtonColors(
            containerColor = AppColors.primaryColor,
            contentColor = AppColors.tertiaryTextColor,
            disabledContainerColor = AppColors.primaryColor.copy(alpha = 0.2f),
            disabledContentColor = AppColors.tertiaryTextColor.copy(alpha = 0.2f)
        ),
        shape = RoundedCornerShape(Dimensions.size12dp)
    ) {
        Text(
            text = buttonName,
            fontSize = FontSize.textSize16sp,
            fontFamily = FontFamily.bold(),
            color = AppColors.tertiaryTextColor
        )
    }
}

@Composable
fun SecondaryButton(
    buttonName: String,
    onClick: () -> Unit,
    enable: Boolean = true,
    modifier: Modifier= Modifier
) {
    Button(
        enabled = enable,
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(height = Dimensions.size56dp),
        colors = ButtonColors(
            containerColor = AppColors.gray_100,
            contentColor = AppColors.primaryTextColor,
            disabledContainerColor = AppColors.gray_100.copy(alpha = 0.2f),
            disabledContentColor = AppColors.primaryTextColor.copy(alpha = 0.2f)
        ),
        shape = RoundedCornerShape(Dimensions.size12dp)
    ) {
        Text(
            text = buttonName,
            fontSize = FontSize.textSize16sp,
            fontFamily = FontFamily.bold(),
            color = AppColors.primaryTextColor
        )
    }
}

