package com.healthanalytics.android.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.healthanalytics.android.presentation.theme.AppColors
import com.healthanalytics.android.presentation.theme.Dimensions
import com.healthanalytics.android.presentation.theme.FontFamily
import com.healthanalytics.android.presentation.theme.FontSize

@Composable
fun TransparentButton(icon: ImageVector, txt: String, onClicked: () -> Unit) {
    OutlinedButton(
        onClick = onClicked,
        colors = ButtonDefaults.buttonColors(
            contentColor = AppColors.white,
            containerColor = AppColors.Black
        ),
        border = BorderStroke(
            width = 1.dp,
            color = Color(0XFFF3F4F6)
        )
    ) {
        Icon(
            imageVector = icon,
            contentDescription = "Remove",
            modifier = Modifier.size(Dimensions.size20dp),
            tint = AppColors.white
        )
        Spacer(modifier = Modifier.width(Dimensions.size8dp))
        Text(
            txt,
            fontSize = FontSize.textSize16sp,
            fontFamily = FontFamily.bold(),
        )
    }
}