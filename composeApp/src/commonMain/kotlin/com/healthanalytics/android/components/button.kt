package com.healthanalytics.android.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
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
    isEnable: Boolean,
    onclick: () -> Unit,
    buttonName: String,
    modifier: Modifier= Modifier
) {
    Button(
        enabled = isEnable,
        onClick = onclick,
        modifier = modifier
            .fillMaxWidth()
            .height(height = Dimensions.size56dp),
        colors = ButtonColors(
            containerColor = Color(0xFF4A4A5C),
            contentColor = Color(0xFFFFFF),
            disabledContentColor = AppColors.textSecondary,
            disabledContainerColor = Color(0xFF003D46),
        ),
        shape = RoundedCornerShape(Dimensions.size12dp)
    ) {
        Text(
            text = buttonName,
            fontSize = FontSize.textSize16sp,
            fontFamily = FontFamily.bold(),
            color = if (isEnable) AppColors.white else AppColors.textSecondary
        )
    }
}