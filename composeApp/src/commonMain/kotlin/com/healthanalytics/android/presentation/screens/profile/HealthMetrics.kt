package com.healthanalytics.android.presentation.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import com.healthanalytics.android.presentation.theme.AppColors
import com.healthanalytics.android.presentation.theme.Dimensions
import com.healthanalytics.android.presentation.theme.FontFamily
import com.healthanalytics.android.presentation.theme.FontSize
import com.healthanalytics.android.ui.TransparentButton
import humantokendashboardv1.composeapp.generated.resources.Res
import humantokendashboardv1.composeapp.generated.resources.cancel
import humantokendashboardv1.composeapp.generated.resources.edit_metrics
import humantokendashboardv1.composeapp.generated.resources.enter_height_in_cm
import humantokendashboardv1.composeapp.generated.resources.enter_weight_in_kg
import humantokendashboardv1.composeapp.generated.resources.health_metrics_subtitle
import humantokendashboardv1.composeapp.generated.resources.health_metrics_title
import humantokendashboardv1.composeapp.generated.resources.height_cm_label
import humantokendashboardv1.composeapp.generated.resources.save
import humantokendashboardv1.composeapp.generated.resources.weight_kg_label
import humantokendashboardv1.composeapp.generated.resources.your_bmi_label
import org.jetbrains.compose.resources.stringResource

@Composable
fun HealthMetrics() {
    var isEditable by remember { mutableStateOf(false) }
    val keyboardController = LocalSoftwareKeyboardController.current
    var userWeight by remember { mutableStateOf("") }
    var userHeight by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxWidth().wrapContentHeight()
            .background(AppColors.BlueBackground, shape = RoundedCornerShape(Dimensions.size12dp))
            .padding(Dimensions.size16dp)
    ) {
        Text(
            text = stringResource(Res.string.health_metrics_title),
            fontSize = FontSize.textSize18sp,
            fontFamily = FontFamily.medium(),
            color = AppColors.White
        )
        Spacer(modifier = Modifier.height(Dimensions.size6dp))

        Text(
            text = stringResource(Res.string.health_metrics_subtitle),
            fontSize = FontSize.textSize14sp,
            fontFamily = FontFamily.regular(),
            color = AppColors.descriptionColor
        )

        Spacer(modifier = Modifier.height(Dimensions.size28dp))

        Text(
            text = stringResource(Res.string.height_cm_label),
            fontSize = FontSize.textSize14sp,
            fontFamily = FontFamily.medium(),
            color = AppColors.White
        )

        if (isEditable) {
            Spacer(modifier = Modifier.height(Dimensions.size12dp))
            OutlinedTextField(
                value = userHeight,
                onValueChange = {
                    userHeight = filterDecimalInput(it)
                },
                placeholder = {
                    Text(
                        text = stringResource(Res.string.enter_height_in_cm),
                        fontSize = FontSize.textSize14sp,
                        fontFamily = FontFamily.regular(),
                        textAlign = TextAlign.Start,
                        color = AppColors.descriptionColor,
                    )
                },
                maxLines = 1,
                singleLine = true,
                textStyle = TextStyle(
                    color = AppColors.White,
                    fontSize = FontSize.textSize14sp,
                    fontFamily = FontFamily.medium(),
                    textAlign = TextAlign.Start
                ),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = AppColors.darkPink,
                    unfocusedBorderColor = AppColors.textFieldUnFocusedColor,
                    focusedContainerColor = AppColors.Black,
                    unfocusedContainerColor = AppColors.Black,
                    errorContainerColor = AppColors.HighColor
                ),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                keyboardActions = KeyboardActions(onNext = {

                }),
                shape = RoundedCornerShape(Dimensions.size12dp),
                modifier = Modifier.fillMaxWidth()
            )
        } else {
            ShowHealthMetric()
        }

        Spacer(modifier = Modifier.height(Dimensions.size16dp))

        Text(
            text = stringResource(Res.string.weight_kg_label),
            fontSize = FontSize.textSize14sp,
            fontFamily = FontFamily.medium(),
            color = AppColors.White
        )
        if (isEditable) {
            Spacer(modifier = Modifier.height(Dimensions.size12dp))

            OutlinedTextField(
                value = userWeight,
                onValueChange = {
                    userWeight = filterDecimalInput(it)
                },
                placeholder = {
                    Text(
                        text = stringResource(Res.string.enter_weight_in_kg),
                        fontSize = FontSize.textSize14sp,
                        fontFamily = FontFamily.regular(),
                        textAlign = TextAlign.Start,
                        color = AppColors.descriptionColor,
                    )
                },
                maxLines = 1,
                singleLine = true,
                textStyle = TextStyle(
                    color = AppColors.White,
                    fontSize = FontSize.textSize14sp,
                    fontFamily = FontFamily.medium(),
                    textAlign = TextAlign.Start
                ),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = AppColors.darkPink,
                    unfocusedBorderColor = AppColors.textFieldUnFocusedColor,
                    focusedContainerColor = AppColors.Black,
                    unfocusedContainerColor = AppColors.Black,
                    errorContainerColor = AppColors.HighColor
                ),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                keyboardActions = KeyboardActions(onDone = {
                    keyboardController?.hide()
                }),
                shape = RoundedCornerShape(Dimensions.size12dp),
                modifier = Modifier.fillMaxWidth()
            )

        } else {
            ShowHealthMetric()
        }

        Spacer(modifier = Modifier.height(Dimensions.size16dp))

        Text(
            text = stringResource(Res.string.your_bmi_label),
            fontSize = FontSize.textSize14sp,
            fontFamily = FontFamily.medium(),
            color = AppColors.White
        )

        Row(modifier = Modifier.wrapContentSize(), verticalAlignment = Alignment.Bottom) {
            Text(
                text = stringResource(Res.string.your_bmi_label),
                fontSize = FontSize.textSize30sp,
                fontFamily = FontFamily.bold(),
                color = AppColors.White
            )

            Spacer(modifier = Modifier.height(Dimensions.size8dp))

            Text(
                text = stringResource(Res.string.your_bmi_label),
                fontSize = FontSize.textSize18sp,
                fontFamily = FontFamily.medium(),
                color = AppColors.White
            )
        }

        Spacer(modifier = Modifier.height(Dimensions.size28dp))
        if (isEditable) {
            Row(modifier = Modifier.fillMaxWidth()) {
                TransparentButton(
                    icon = Icons.Default.Remove,
                    txt = stringResource(Res.string.save),
                    onClicked = {},
                    modifier = Modifier.wrapContentWidth()
                )

                Spacer(modifier = Modifier.width(Dimensions.size12dp))

                TransparentButton(
                    icon = Icons.Default.Remove,
                    txt = stringResource(Res.string.cancel),
                    onClicked = { isEditable = false },
                    modifier = Modifier.wrapContentWidth()
                )
            }
        } else {
            TransparentButton(
                icon = Icons.Default.Remove,
                txt = stringResource(Res.string.edit_metrics),
                onClicked = { isEditable = true },
                modifier = Modifier.wrapContentWidth()
            )
        }
    }
}

@Composable
private fun ShowHealthMetric() {
    Spacer(modifier = Modifier.height(Dimensions.size8dp))
    Text(
        text = stringResource(Res.string.weight_kg_label),
        fontSize = FontSize.textSize14sp,
        fontFamily = FontFamily.regular(),
        color = AppColors.White
    )
}

fun filterDecimalInput(input: String): String {
    if (input.isEmpty()) return ""

    // Only allow 0-9 and .
    val cleaned = input.filter { it.isDigit() || it == '.' }

    // Only one decimal point allowed
    val parts = cleaned.split(".")
    val integerPart = parts.getOrNull(0) ?: ""
    val decimalPart = parts.getOrNull(1)

    val result = if (decimalPart != null) {
        "$integerPart.${decimalPart}"
    } else {
        integerPart
    }

    // Enforce max length of 4
    return if (result.length <= 4) result else result.take(4)
}
