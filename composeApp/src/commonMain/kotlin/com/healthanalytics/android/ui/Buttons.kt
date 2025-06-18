package com.healthanalytics.android.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.healthanalytics.android.presentation.theme.AppColors
import com.healthanalytics.android.presentation.theme.Dimensions
import com.healthanalytics.android.presentation.theme.FontFamily
import com.healthanalytics.android.presentation.theme.FontSize

@Composable
fun TransparentButton(
    icon: ImageVector,
    txt: String,
    onClicked: () -> Unit,
    modifier: Modifier,
    isEnabled: Boolean = true,
) {
    OutlinedButton(
        modifier = modifier.height(Dimensions.size50dp),
        onClick = onClicked,
        shape = RoundedCornerShape(Dimensions.size12dp),
        colors = ButtonDefaults.buttonColors(
            contentColor = AppColors.White,
            containerColor = AppColors.Black
        ),
        enabled = isEnabled,
        border = BorderStroke(
            width = Dimensions.size1dp,
            color = AppColors.lineBorderColor,
        )
    ) {
        Icon(
            imageVector = icon,
            contentDescription = "Remove",
            modifier = Modifier.size(Dimensions.size16dp),
            tint = if (isEnabled) AppColors.White else AppColors.buttonGrey
        )
        Spacer(modifier = Modifier.width(Dimensions.size8dp))
        Text(
            txt,
            fontSize = FontSize.textSize16sp,
            fontFamily = FontFamily.bold(),
        )
    }
}


@Composable
fun PrimaryButton(
    modifier: Modifier = Modifier,
    txt: String,
    enable: Boolean = true,
    onClick: () -> Unit,
    buttonColor: Color,
) {
    Button(
        enabled = enable,
        onClick = onClick,
        modifier = modifier.height(Dimensions.size50dp),
        colors = ButtonDefaults.buttonColors(
            contentColor = AppColors.White,
            containerColor = buttonColor
        ),
        shape = RoundedCornerShape(Dimensions.size12dp)
    ) {
        Text(
            text = txt,
            fontSize = FontSize.textSize16sp,
            fontFamily = FontFamily.bold(),
            color = AppColors.White
        )
    }
}

