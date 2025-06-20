package com.healthanalytics.android.presentation.screens.questionnaire

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.healthanalytics.android.presentation.theme.AppColors
import com.healthanalytics.android.presentation.theme.Dimensions
import com.healthanalytics.android.presentation.theme.FontFamily
import com.healthanalytics.android.presentation.theme.FontSize
import kotlinx.datetime.Month

@Composable
fun Modifier.questionCardPadding(): Modifier {
    return this.padding(
        vertical = Dimensions.size10dp,
        horizontal = Dimensions.size16dp,
    )
}

@Composable
fun Modifier.questionCardBackground(): Modifier {
    return this
        .background(
            color = AppColors.backGround,
            shape = RoundedCornerShape(Dimensions.cornerRadiusLarge)
        )
        .padding(Dimensions.size16dp)
}

@Composable
fun Modifier.setBorder(isSelected: Boolean): Modifier {
    return if (isSelected) this.selectedBorder() else this.defaultBorder()
}

@Composable
fun Modifier.selectedBorder(): Modifier {
    return this.border(
        width = Dimensions.size1dp,
        color = AppColors.primaryColor,
        shape = RoundedCornerShape(Dimensions.cornerRadiusLarge)
    )
}

@Composable
fun Modifier.defaultBorder(): Modifier {
    return this.border(
        width = 1.dp,
        color = AppColors.gray,
        shape = RoundedCornerShape(Dimensions.cornerRadiusLarge)
    )
}

@Composable
fun CategoryName(category: String?) {
    Text(
        text = (category ?: "").uppercase(),
        fontFamily = FontFamily.regular(),
        fontSize = FontSize.textSize16sp,
        color = AppColors.textSecondary,
    )
}

@Composable
fun OptionName(name: String?, textAlign: TextAlign = TextAlign.Start) {
    Text(
        text = name ?: "",
        color = AppColors.textPrimary,
        fontSize = FontSize.textSize14sp,
        fontFamily = FontFamily.medium(),
        textAlign = textAlign,
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
fun OptionDescription(description: String?, textAlign: TextAlign = TextAlign.Start) {
    if (description != null) {
        Text(
            text = description,
            color = AppColors.textSecondary,
            fontSize = FontSize.textSize12sp,
            fontFamily = FontFamily.regular(),
            textAlign = textAlign,
            lineHeight = FontSize.textSize12sp,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun RequiredErrorText() {
    Text(
        text = "This is required",
        fontFamily = FontFamily.regular(),
        fontSize = FontSize.textSize16sp,
        color = AppColors.textSecondary,
    )
}