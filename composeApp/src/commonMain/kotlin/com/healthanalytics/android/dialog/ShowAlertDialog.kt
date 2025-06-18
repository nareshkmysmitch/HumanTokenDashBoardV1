package com.healthanalytics.android.dialog

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.window.DialogProperties
import com.healthanalytics.android.modifier.onTextClick
import com.healthanalytics.android.presentation.theme.AppColors
import com.healthanalytics.android.presentation.theme.Dimensions
import com.healthanalytics.android.presentation.theme.FontFamily
import com.healthanalytics.android.presentation.theme.FontSize

@Composable
fun CommonAlertDialog(
    title: String,
    description: String,
    confirmButtonName: String,
    dismissButtonName: String,
    confirmButtonTextSize: TextUnit = FontSize.textSize18sp,
    dismissButtonTextSize: TextUnit = FontSize.textSize18sp,
    titleTextSize: TextUnit = FontSize.textSize18sp,
    descriptionTextSize: TextUnit = FontSize.textSize14sp,
    onConfirmClick: () -> Unit,
    onDismissClick: () -> Unit,
    onDialogDismiss: () -> Unit = {},
    isCancelable: Boolean = true
) {
    AlertDialog(
        shape = RoundedCornerShape(46f),
        containerColor = AppColors.gray_100,
        properties = DialogProperties(
            dismissOnClickOutside = isCancelable,
            dismissOnBackPress = isCancelable
        ),
        title = {
            if (title.isNotEmpty()) {
                Text(
                    text = title,
                    color = AppColors.primaryTextColor,
                    fontSize = titleTextSize,
                    fontFamily = FontFamily.medium(),
                )
            }
        },
        text = {
            if (description.isNotEmpty()) {
                Text(
                    text = description,
                    color = AppColors.primaryTextColor,
                    fontSize = descriptionTextSize,
                    fontFamily = FontFamily.regular()
                )
            }
        },
        onDismissRequest = {
            onDialogDismiss()
        },
        confirmButton = {
            Text(
                text = confirmButtonName,
                color = AppColors.primaryColor,
                fontSize = confirmButtonTextSize,
                fontFamily = FontFamily.medium(),
                modifier = Modifier
                    .clip(RoundedCornerShape(36f))
                    .background(
                        color = AppColors.Transparent,
                        shape = RoundedCornerShape(36f)
                    )
                    .onTextClick(
                        rippleEffect = true,
                        onClick = {
                            onConfirmClick()
                        }
                    )
                    .padding(
                        horizontal = Dimensions.size8dp,
                        vertical =  Dimensions.size4dp
                    )
            )
        },
        dismissButton = {
            if (dismissButtonName.isNotEmpty()) {
                Text(
                    text = dismissButtonName,
                    color = AppColors.lightGreen,
                    fontSize = dismissButtonTextSize,
                    fontFamily = FontFamily.medium(),
                    modifier = Modifier
                        .clip(RoundedCornerShape(36f))
                        .background(
                            color = AppColors.Transparent,
                            shape = RoundedCornerShape(36f)
                        )
                        .onTextClick(
                            rippleEffect = true,
                            onClick = {
                                onDismissClick()
                            }
                        )
                        .padding(
                            horizontal =  Dimensions.size8dp,
                            vertical =  Dimensions.size4dp
                        )
                )
            }
        }
    )
}