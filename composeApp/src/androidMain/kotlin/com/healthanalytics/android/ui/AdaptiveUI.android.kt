package com.healthanalytics.android.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.healthanalytics.android.presentation.theme.AppColors

@Composable
actual fun ShowAlertDialog(
    modifier: Modifier,
    title: String,
    message: String,
    onNegativeTxt: String,
    onPositiveTxt: String,
    onDismiss: () -> Unit,
    onLogout: () -> Unit,
) {

    AlertDialog(
        shape = RoundedCornerShape(46f),
        containerColor = AppColors.PurpleCardBackground,
        title = {
            Text(
                text = title,
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium
            )
        },
        text = {
            Text(
                text = message,
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Normal
            )
        },
        onDismissRequest = {
        },
        confirmButton = {
            Text(
                text = onPositiveTxt,
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Normal,
                modifier = Modifier
                    .clip(RoundedCornerShape(36f))
                    .background(
                        color = AppColors.Transparent,
                        shape = RoundedCornerShape(36f)
                    )
                    .onTextClick(
                        rippleEffect = true,
                        onClick = {
                            onLogout()
                        }
                    )
            )
        },
        dismissButton = {
            Text(
                text = onNegativeTxt,
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Normal,
                modifier = Modifier
                    .clip(RoundedCornerShape(36f))
                    .background(
                        color = AppColors.Transparent,
                        shape = RoundedCornerShape(36f)
                    )
                    .onTextClick(
                        rippleEffect = true,
                        onClick = {
                            onDismiss()
                        }
                    )
            )
        }
    )
}