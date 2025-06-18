package com.healthanalytics.android.presentation.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.healthanalytics.android.presentation.screens.marketplace.MarketPlaceViewModel
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
fun HealthMetrics(viewModel: MarketPlaceViewModel) {
    var isEditable by remember { mutableStateOf(false) }
    val keyboardController = LocalSoftwareKeyboardController.current
    val personalData by viewModel.personalData.collectAsStateWithLifecycle()
    val userWeight = personalData?.pii_data?.weight
    val userHeight = personalData?.pii_data?.height

    var editWeight by remember { mutableStateOf("") }
    var editHeight by remember { mutableStateOf("") }
    var bmi by remember { mutableStateOf<Double?>(0.0) }
    var bmiState by remember { mutableStateOf(Pair("", Color.Black)) }

    val accessToken by viewModel.accessToken.collectAsStateWithLifecycle()
    val uiState by viewModel.uiPersonalData.collectAsState()

    LaunchedEffect(key1 = userWeight, key2 = userHeight) {
        editWeight = userWeight.toString()
        editHeight = userHeight.toString()
        bmi = viewModel.calculateBMI(userWeight, userHeight)
        bmiState = viewModel.getBMICategory(bmi)
    }

    LaunchedEffect(accessToken) {
        if (accessToken != null) {
            viewModel.loadPersonalData(accessToken)
        }
    }

    Column(
        modifier = Modifier.fillMaxWidth().wrapContentHeight()
            .background(AppColors.BlueBackground, shape = RoundedCornerShape(Dimensions.size12dp))
            .padding(Dimensions.size16dp)
    ) {
        if (uiState.isLoading) {
            Box(
                modifier = Modifier.fillMaxWidth().height(Dimensions.size180dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {

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
                    value = editHeight,
                    onValueChange = {
                        editHeight = viewModel.filterDecimalInput(it)
                        bmi = viewModel.calculateBMI(
                            editWeight.toIntOrNull(),
                            editHeight.toIntOrNull()
                        )
                        bmiState = viewModel.getBMICategory(bmi)
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
                    keyboardActions = KeyboardActions(onNext = {}),
                    shape = RoundedCornerShape(Dimensions.size12dp),
                    modifier = Modifier.fillMaxWidth()
                )
            } else {
                ShowHealthMetric(userHeight)
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
                    value = editWeight,
                    onValueChange = {
                        editWeight = viewModel.filterDecimalInput(it)
                        bmi = viewModel.calculateBMI(
                            editWeight.toIntOrNull(),
                            editHeight.toIntOrNull()
                        )
                        bmiState = viewModel.getBMICategory(bmi)
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
                ShowHealthMetric(userWeight)
            }

            Spacer(modifier = Modifier.height(Dimensions.size16dp))

            if (bmi != null) {

                Text(
                    text = stringResource(Res.string.your_bmi_label),
                    fontSize = FontSize.textSize14sp,
                    fontFamily = FontFamily.medium(),
                    color = AppColors.White
                )


                Row(modifier = Modifier.wrapContentSize(), verticalAlignment = Alignment.Bottom) {
                    Text(
                        text = viewModel.displayBMI(bmi),
                        fontSize = FontSize.textSize30sp,
                        fontFamily = FontFamily.bold(),
                        color = AppColors.White
                    )

                    Spacer(modifier = Modifier.width(Dimensions.size4dp))

                    Text(
                        text = bmiState.first,
                        fontSize = FontSize.textSize18sp,
                        fontFamily = FontFamily.medium(),
                        color = bmiState.second
                    )
                }
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
                        onClicked = {
                            isEditable = false
                            keyboardController?.hide()
                        },
                        modifier = Modifier.wrapContentWidth()
                    )
                }
            } else {
                TransparentButton(
                    icon = Icons.Default.Remove,
                    txt = stringResource(Res.string.edit_metrics),
                    onClicked = {
                        isEditable = true
                    },
                    modifier = Modifier.wrapContentWidth()
                )
            }
        }
    }
}

@Composable
private fun ShowHealthMetric(value: Int?) {
    Spacer(modifier = Modifier.height(Dimensions.size8dp))
    Text(
        text = "$value",
        fontSize = FontSize.textSize14sp,
        fontFamily = FontFamily.regular(),
        color = AppColors.White
    )
}

