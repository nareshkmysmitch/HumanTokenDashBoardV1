package com.healthanalytics.android.presentation.screens.questionnaire.types

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import com.healthanalytics.android.constants.CommonConstants
import com.healthanalytics.android.constants.UnitConstants
import com.healthanalytics.android.constants.ViewConstants
import com.healthanalytics.android.data.models.questionnaire.Question
import com.healthanalytics.android.data.models.questionnaire.TextBoxDefaultValues
import com.healthanalytics.android.extension.cmToInch
import com.healthanalytics.android.extension.inchToCM
import com.healthanalytics.android.presentation.screens.questionnaire.QuestionSection
import com.healthanalytics.android.presentation.screens.questionnaire.viewmodel.QuestionnaireViewModel
import com.healthanalytics.android.presentation.theme.AppColors
import com.healthanalytics.android.presentation.theme.Dimensions
import com.healthanalytics.android.presentation.theme.FontFamily
import com.healthanalytics.android.presentation.theme.FontFamily.medium
import com.healthanalytics.android.presentation.theme.FontSize
import com.seiko.imageloader.rememberImagePainter
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun TextBoxQuestion(
    modifier: Modifier = Modifier,
    questionnaire: Question,
    viewModel: QuestionnaireViewModel,
) {

    val dataType = questionnaire.data_type
    val defaultValues = getTextBoxDefaultValues(dataType = dataType)

    var unit by remember(questionnaire) { mutableStateOf(defaultValues.unit) }
    var text by remember(questionnaire) { mutableStateOf("") }
    var errorText by remember(questionnaire) { mutableStateOf("") }
    var buttonState by remember(questionnaire) { mutableStateOf(false) }

    LaunchedEffect(key1 = questionnaire) {
        if (questionnaire.text_answer != null) {
            buttonState = true
            text = when (dataType) {
                ViewConstants.DATA_TYPE_WAIST, ViewConstants.DATA_TYPE_HIP, ViewConstants.DATA_TYPE_NECK -> {
                    if (unit == UnitConstants.INCH) {
                        questionnaire.text_answer.toString().toDouble().cmToInch()
                    } else {
                        questionnaire.text_answer ?: ""
                    }
                }

                else -> {
                    questionnaire.text_answer ?: ""
                }
            }
        }

        if (questionnaire.required == false) {
            buttonState = true
        }
    }

    if (buttonState) {
        if (text.trim().isNotEmpty()) {
            viewModel.saveTextBoxAnswer(
                input = text, datatype = dataType, unit = unit
            )
        }
    } else {
        viewModel.clearTextBoxAnswer()
    }

    viewModel.saveNextButtonState(buttonState)

    Column(
        modifier = Modifier.verticalScroll(rememberScrollState())
            .padding(bottom = Dimensions.size80dp)
    ) {

        QuestionSection(
            question = questionnaire
        )

        val imageUrl = questionnaire.answers?.firstOrNull()?.value

        if (imageUrl != null) {
            Image(
                painter = rememberImagePainter(imageUrl),
                contentDescription = questionnaire.data_type,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxWidth()
                    .padding(horizontal = Dimensions.size16dp)
                    .clip(RoundedCornerShape(Dimensions.size12dp))
            )
        }

        Spacer(modifier = Modifier.height(Dimensions.size28dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = modifier
                .height(Dimensions.size56dp)
                .padding(horizontal = Dimensions.size16dp)
                .fillMaxWidth()
                .background(
                    color = AppColors.gray_100,
                    shape = RoundedCornerShape(Dimensions.size12dp)
                )
                .padding(
                    start = Dimensions.size10dp,
                    end = Dimensions.size10dp
                )
        ) {
            TextBox(
                modifier = modifier.weight(1f),
                defaultValues = defaultValues,
                text = text,
                onTextChanged = { input ->
                    errorText = ""
                    try {
                        if (input.isEmpty()) {
                            buttonState = (if (questionnaire.required == true) false else false)
                        }
                        text = validateInput(
                            input = input,
                            dataType = dataType,
                            maxLength = defaultValues.maxLength
                        )

                        when {
                            text.trim().isNotEmpty() -> {
                                if (dataType == ViewConstants.DATA_TYPE_WAIST || dataType == ViewConstants.DATA_TYPE_HIP || dataType == ViewConstants.DATA_TYPE_NECK) {
                                    val floatValue = text.toFloat()
                                    val minAndMax = getMinAndMaxValues(
                                        dataType = dataType,
                                        gender = viewModel.getGender()
                                    )
                                    validateWaistHipAndNeck(
                                        unit = unit,
                                        minAndMax = minAndMax,
                                        floatValue = floatValue,
                                        onError = { error -> errorText = error },
                                        buttonState = { enable -> buttonState = enable }
                                    )
                                } else {
                                    buttonState = true
                                }
                            }

                            questionnaire.required == false -> {
                                buttonState = true
                            }

                            else -> {
                                buttonState = true
                            }
                        }
                    } catch (_: Exception) {
                    }
                }
            )

            if (unit.isNotEmpty()) {
                UnitText(unit = unit, text = text, onValuesChanged = { (newValue, newUnit) ->
                    text = newValue
                    unit = newUnit

                    if (text.isNotEmpty()) {
                        val floatValue = text.toFloat()
                        val minAndMax = getMinAndMaxValues(dataType, viewModel.getGender())

                        validateWaistHipAndNeck(
                            unit = unit,
                            minAndMax = minAndMax,
                            floatValue = floatValue,
                            onError = { error -> errorText = error },
                            buttonState = { enable -> buttonState = enable })
                    }
                })
            }
        }

        if (errorText.isNotEmpty()) {
            ErrorText(error = errorText)
        }
    }
}

@Composable
fun TextBox(
    modifier: Modifier = Modifier,
    defaultValues: TextBoxDefaultValues,
    text: String,
    onTextChanged: (String) -> Unit
) {
    TextField(
        placeholder = {
            Text(
                defaultValues.placeHolder,
                fontFamily = FontFamily.regular(),
                fontSize = FontSize.textSize14sp,
                color = AppColors.textLabelColor
            )
        },
        value = TextFieldValue(
            text = text,
            selection = TextRange(text.length),
        ),
        onValueChange = { input ->
            onTextChanged(input.text)
        },
        singleLine = true,
        maxLines = 1,
        keyboardOptions = KeyboardOptions(keyboardType = defaultValues.keyboardType),
        textStyle = TextStyle(
            color = AppColors.primaryTextColor,
            fontSize = FontSize.textSize16sp,
            fontFamily = medium(),
            textAlign = TextAlign.Start,
        ),
        modifier = modifier.fillMaxWidth(),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,
            focusedBorderColor = Color.Transparent,
            unfocusedBorderColor = Color.Transparent,
            cursorColor = AppColors.primaryTextColor
        )
    )
}

@Composable
fun UnitText(
    modifier: Modifier = Modifier,
    unit: String,
    text: String,
    onValuesChanged: (Pair<String, String>) -> Unit,
) {
    Text(
        text = unit.plus("<>"),
        fontSize = FontSize.textSize14sp,
        color = AppColors.primaryTextColor,
        fontFamily = FontFamily.semiBold(),
        modifier = modifier
            .clickable(
                onClick = {
                    if (unit == UnitConstants.INCH) {
                        if (text.isNotEmpty()) {
                            val textValue = text.toDouble()
                            onValuesChanged(Pair(textValue.inchToCM(), UnitConstants.CM))
                        } else {
                            onValuesChanged(Pair(text, UnitConstants.CM))
                        }
                    } else {
                        if (text.isNotEmpty()) {
                            val textValue = text.toDouble()
                            onValuesChanged(Pair(textValue.cmToInch(), UnitConstants.INCH))
                        } else {
                            onValuesChanged(Pair(text, UnitConstants.INCH))
                        }
                    }
                }
            )
    )
}

@Composable
fun ErrorText(
    modifier: Modifier = Modifier,
    error: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .padding(
                start = Dimensions.size16dp,
                end = Dimensions.size16dp,
                top = Dimensions.size4dp
            )
    ) {
        Icon(
            imageVector = Icons.Default.Info,
            contentDescription = null,
            tint = AppColors.error,
            modifier = modifier.size(Dimensions.size16dp)
        )
        Text(
            text = error,
            fontSize = FontSize.textSize12sp,
            color = AppColors.error,
            fontFamily = FontFamily.regular(),
            modifier = modifier.padding(start = Dimensions.size4dp)
        )
    }
}


@Composable
fun getTextBoxDefaultValues(
    dataType: String?
): TextBoxDefaultValues {

    return when (dataType) {
        ViewConstants.DATA_TYPE_AGE -> {
            val textBoxDefaultValues = TextBoxDefaultValues(
                keyboardType = KeyboardType.Number,
                maxLength = 2,
                showUnit = false,
                unit = "",
                placeHolder = "Enter your age"
            )
            textBoxDefaultValues
        }

        ViewConstants.DATA_TYPE_WAIST, ViewConstants.DATA_TYPE_HIP, ViewConstants.DATA_TYPE_NECK -> {
            val textBoxDefaultValues = TextBoxDefaultValues(
                keyboardType = KeyboardType.Decimal,
                maxLength = 5,
                showUnit = true,
                unit = UnitConstants.INCH,
                placeHolder = getPlaceHolderText(dataType)
            )
            textBoxDefaultValues
        }

        ViewConstants.DATA_TYPE_WEIGHT, ViewConstants.DATA_TYPE_HEIGHT -> {
            val textBoxDefaultValues = TextBoxDefaultValues(
                keyboardType = KeyboardType.Decimal,
                maxLength = 5,
                showUnit = false,
                unit = "",
                placeHolder = ""
            )
            textBoxDefaultValues
        }

        ViewConstants.DATA_TYPE_NUMERIC -> {
            val textBoxDefaultValues = TextBoxDefaultValues(
                keyboardType = KeyboardType.Number,
                maxLength = 3,
                showUnit = false,
                unit = "",
                placeHolder = ""
            )
            textBoxDefaultValues
        }

        else -> {
            val textBoxDefaultValues = TextBoxDefaultValues(
                keyboardType = KeyboardType.Text,
                maxLength = 50,
                showUnit = false,
                unit = "",
                placeHolder = ""
            )
            textBoxDefaultValues
        }
    }
}

private fun getPlaceHolderText(dataType: String?): String {
    return when (dataType) {
        ViewConstants.DATA_TYPE_WAIST -> "Enter your waist circumference"
        ViewConstants.DATA_TYPE_HIP -> "Enter your hip circumference"
        ViewConstants.DATA_TYPE_NECK -> "Enter your neck circumference"
        else -> ""
    }
}


private fun getMinAndMaxValues(dataType: String?, gender: String): Pair<Float, Float> {
    var minAndMax = Pair(9.0f, 57.0f)
    when (gender) {
        CommonConstants.FEMALE -> {
            when (dataType) {
                ViewConstants.DATA_TYPE_WAIST -> {
                    minAndMax = Pair(20.0f, 50.0f)
                }

                ViewConstants.DATA_TYPE_HIP -> {
                    minAndMax = Pair(30.0f, 55.0f)
                }

                ViewConstants.DATA_TYPE_NECK -> {
                    minAndMax = Pair(9.0f, 23.6f)
                }
            }
        }

        else -> {
            when (dataType) {
                ViewConstants.DATA_TYPE_WAIST -> {
                    minAndMax = Pair(24.0f, 54.0f)
                }

                ViewConstants.DATA_TYPE_HIP -> {
                    minAndMax = Pair(31.0f, 56.0f)
                }

                ViewConstants.DATA_TYPE_NECK -> {
                    minAndMax = Pair(10.0f, 24.5f)
                }
            }
        }
    }

    return minAndMax
}

private fun validateWaistHipAndNeck(
    unit: String,
    minAndMax: Pair<Float, Float>,
    floatValue: Float,
    onError: (String) -> Unit,
    buttonState: (Boolean) -> Unit
) {
    val (min, max) = minAndMax

    val enteredValue = if (unit == UnitConstants.INCH) {
        floatValue
    } else {
        floatValue.toDouble().cmToInch().toFloat()
    }

    if (enteredValue !in min..max) {
        onError("Value outside range")
        buttonState(false)
    } else {
        onError("")
        buttonState(true)
    }
}

private fun validateInput(input: String, dataType: String?, maxLength: Int): String {
    val dotCount = input.count { it == '.' }

    return when {
        input.isEmpty() || input == "." || input == " " || input.first() == ',' -> {
            ""
        }

        dotCount > 1 -> {
            input.removeRange(input.lastIndexOf('.'), input.lastIndexOf('.') + 1)
        }

        (dataType == ViewConstants.DATA_TYPE_WAIST || dataType == ViewConstants.DATA_TYPE_HIP ||
                dataType == ViewConstants.DATA_TYPE_NECK) && input.lastOrNull() == ',' -> {
            input.removeRange(input.lastIndexOf(','), input.lastIndexOf(',') + 1)
        }

        input.length > maxLength -> {
            input.take(maxLength)
        }

        else -> {
            input
        }
    }
}

@Preview
@Composable
private fun TextBoxPreview() {
    val textBoxDefaultValues = TextBoxDefaultValues(
        keyboardType = KeyboardType.Number,
        maxLength = 0,
        showUnit = false,
        unit = "",
        placeHolder = ""
    )
    Column(
        modifier = Modifier
            .background(color = AppColors.TextGrey)
            .padding(Dimensions.size12dp)
    ) {
        TextBox(defaultValues = textBoxDefaultValues, text = "h3ll0", onTextChanged = {})
    }
}