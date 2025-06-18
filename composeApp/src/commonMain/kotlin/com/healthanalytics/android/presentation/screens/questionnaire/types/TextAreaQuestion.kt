package com.healthanalytics.android.presentation.screens.questionnaire.types

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import com.healthanalytics.android.data.models.questionnaire.Question
import com.healthanalytics.android.modifier.onBoxClick
import com.healthanalytics.android.presentation.screens.questionnaire.QuestionSection
import com.healthanalytics.android.presentation.screens.questionnaire.viewmodel.QuestionnaireViewModel
import com.healthanalytics.android.presentation.theme.AppColors
import com.healthanalytics.android.presentation.theme.Dimensions
import com.healthanalytics.android.presentation.theme.FontFamily
import com.healthanalytics.android.presentation.theme.FontSize

@Composable
fun TextAreaQuestion(
    modifier: Modifier = Modifier,
    questionnaire: Question,
    viewModel: QuestionnaireViewModel
) {
    QuestionSection(question = questionnaire)

    var selectedValue by remember(questionnaire) { mutableStateOf("") }

    LaunchedEffect(key1 = questionnaire) {
        val textAnswer = questionnaire.text_answer ?: ""
        selectedValue = textAnswer
        viewModel.saveTextAreaAnswer(textAnswer)
        if (selectedValue.isNotEmpty()) {
            viewModel.saveNextButtonState(true)
        }
    }

    val focusRequester = remember { FocusRequester() }
    val keyBoard = LocalSoftwareKeyboardController.current

    Column {
        Box(
            modifier = modifier
                .padding(horizontal = Dimensions.size16dp)
                .background(
                    color = AppColors.gray_100,
                    shape = RoundedCornerShape(Dimensions.size12dp)
                )
                .fillMaxWidth()
                .height(Dimensions.size200dp)
                .onBoxClick(
                    onClick = {
                        focusRequester.requestFocus()
                        keyBoard?.show()
                    }
                )
        ) {
            TextArea(
                focusRequester = focusRequester,
                selectedValue = selectedValue,
                onTextChanged = {
                    selectedValue = validateTextAreaInput(it)

                    if (selectedValue.isEmpty()) {
                        viewModel.clearTextAreaAnswer()
                        val buttonState = questionnaire.required != true
                        viewModel.saveNextButtonState(buttonState)
                    } else {
                        viewModel.saveNextButtonState(true)
                        viewModel.saveTextAreaAnswer(selectedValue)
                    }
                }
            )
        }
        Spacer(modifier = modifier.padding(top = Dimensions.size4dp))
        Text(
            text = selectedValue.length.toString().plus("/").plus("250"),
            fontSize = FontSize.textSize14sp,
            color = AppColors.secondaryTextColor,
            fontFamily = FontFamily.regular(),
            modifier = modifier
                .align(Alignment.End)
                .padding(end = Dimensions.size18dp)
        )
    }
}

@Composable
fun TextArea(
    modifier: Modifier = Modifier,
    selectedValue: String,
    onTextChanged: (String) -> Unit,
    focusRequester: FocusRequester,
) {
    val maxLength = 250
    TextField(
        placeholder = {
            Text(
                "type your answer here",
                fontFamily = FontFamily.medium(),
                fontSize = FontSize.textSize16sp,
                color = AppColors.textLabelColor
            )
        },
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = AppColors.gray_100,
                shape = RoundedCornerShape(Dimensions.size12dp)
            )
            .padding(
                paddingValues = PaddingValues(
                    Dimensions.size12dp
                )
            )
            .focusRequester(focusRequester),
        value = selectedValue,
        onValueChange = { input ->
            if (input.length <= maxLength) {
                onTextChanged(input)
            }
        },
        keyboardOptions = KeyboardOptions(
            imeAction = ImeAction.Done
        ),
        maxLines = 10,
        textStyle = TextStyle(
            color = AppColors.primaryTextColor,
            fontSize = FontSize.textSize16sp,
            fontFamily = FontFamily.medium(),
            textAlign = TextAlign.Start
        ),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,
            focusedBorderColor = Color.Transparent,
            unfocusedBorderColor = Color.Transparent,
            cursorColor = AppColors.primaryTextColor
        )
    )
}

private fun validateTextAreaInput(input: String): String {
    val dotCount = input.count { it == '.' }

    return when {
        input.isEmpty() || input == "." || input.first() == ' ' || input.first() == ',' -> {
            ""
        }

        dotCount > 1 -> {
            input.removeRange(input.lastIndexOf('.'), input.lastIndexOf('.') + 1)
        }

        else -> {
            input
        }
    }
}
