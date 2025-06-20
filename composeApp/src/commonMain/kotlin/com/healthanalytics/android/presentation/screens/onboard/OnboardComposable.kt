package com.healthanalytics.android.presentation.screens.onboard

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.healthanalytics.android.presentation.theme.AppColors
import com.healthanalytics.android.presentation.theme.Dimensions
import com.healthanalytics.android.presentation.theme.FontFamily
import com.healthanalytics.android.presentation.theme.FontSize


@Composable
fun CategoryTitleText(
    name: String
) {
    Text(
        text = name,
        fontSize = FontSize.textSize18sp,
        fontFamily = FontFamily.bold(),
        color = AppColors.primaryTextColor,
    )
}

@Composable
fun FieldNameText(
    name: String,
    modifier: Modifier= Modifier,
) {
    Text(
        text = name,
        fontSize = FontSize.textSize16sp,
        fontFamily = FontFamily.medium(),
        color = AppColors.primaryTextColor,
        modifier = modifier
    )

    Spacer(modifier= Modifier.height(Dimensions.size8dp))
}

@Composable
fun Modifier.setOTPFieldBorder(isSelected: Boolean): Modifier {
    return if (isSelected) this.focusedBorder() else this.unFocusedBorder()
}

@Composable
fun Modifier.focusedBorder(): Modifier {
    return this.border(
        width = Dimensions.size1dp,
        color = AppColors.primaryColor,
        shape = RoundedCornerShape(Dimensions.size12dp)
    )
}

@Composable
fun Modifier.unFocusedBorder(): Modifier {
    return this.border(
        width = 1.dp,
        color = AppColors.gray,
        shape = RoundedCornerShape(Dimensions.size12dp)
    )
}