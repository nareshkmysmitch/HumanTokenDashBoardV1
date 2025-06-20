package com.healthanalytics.android.presentation.screens.questionnaire.types

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.KeyboardArrowUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import com.healthanalytics.android.data.models.questionnaire.Question
import com.healthanalytics.android.presentation.screens.questionnaire.QuestionSection
import com.healthanalytics.android.presentation.screens.questionnaire.viewmodel.QuestionnaireViewModel
import com.healthanalytics.android.presentation.theme.AppColors
import com.healthanalytics.android.presentation.theme.Dimensions
import com.healthanalytics.android.presentation.theme.FontFamily
import com.healthanalytics.android.presentation.theme.FontSize
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun DropDownQuestion(
    modifier: Modifier = Modifier,
    questionnaire: Question,
    viewModel: QuestionnaireViewModel
) {

    QuestionSection(question = questionnaire)

    var selectedValue by remember(questionnaire) { mutableStateOf("") }
    var isExpanded by remember(questionnaire) { mutableStateOf(false) }

    if (selectedValue.isEmpty()) {
        selectedValue = questionnaire.text_answer ?: ""
        viewModel.saveNextButtonState(true)
    }

    Column {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = Dimensions.size16dp)
                .background(
                    color = AppColors.primaryTextColor,
                    shape = RoundedCornerShape(Dimensions.size12dp)
                )
                .padding(horizontal = Dimensions.size6dp)
        ) {
            AutoCompleteTextBox(
                selectedValue = selectedValue,
                isExpanded = isExpanded,
                onTextChanged = {
                    selectedValue = it
                    viewModel.saveDropDownAnswer(it)
                    viewModel.saveNextButtonState(true)
                    if (selectedValue.length > 2) {
                        isExpanded = true
                    }
                },
                onExpanded = {
                    isExpanded = it
                }
            )
        }

        if (isExpanded) {
            CountryList(
                selectedValue = selectedValue,
                onItemSelected = {
                    selectedValue = it
                    isExpanded = false
                    viewModel.saveDropDownAnswer(it)
                }
            )
        }
    }
}

@Composable
fun AutoCompleteTextBox(
    modifier: Modifier = Modifier,
    selectedValue: String,
    isExpanded: Boolean,
    onTextChanged: (String) -> Unit,
    onExpanded: (Boolean) -> Unit,
) {

    OutlinedTextField(
        value = selectedValue,
        onValueChange = { input ->
            onTextChanged(input)
        },
        singleLine = true,
        maxLines = 1,
        textStyle = TextStyle(
            color = AppColors.textPrimary,
            fontSize = FontSize.textSize16sp,
            fontFamily = FontFamily.medium(),
            textAlign = TextAlign.Start
        ),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
            disabledContainerColor = Color.White,
            cursorColor = AppColors.textPrimary,
            errorCursorColor = AppColors.textPrimary,
            selectionColors = TextSelectionColors(
                handleColor = Color.White,
                backgroundColor = Color.White
            ),
            focusedIndicatorColor = Color.White,
            unfocusedIndicatorColor = Color.White,
            disabledIndicatorColor = Color.White,
        ),
        modifier = modifier
            .fillMaxWidth()
            .height(Dimensions.size56dp),
        trailingIcon = {
            IconButton(
                onClick = {
                    if (isExpanded) onExpanded(false) else onExpanded(true)
                }
            ) {
                Icon(
                    imageVector = if (isExpanded) Icons.Rounded.KeyboardArrowUp else Icons.Rounded.KeyboardArrowDown,
                    contentDescription = null
                )
            }
        }
    )
}

@Composable
fun CountryList(
    modifier: Modifier = Modifier,
    selectedValue: String,
    onItemSelected: (String) -> Unit
) {
    val countryList = emptyList<String>()

    val filterList = countryList.filter {
        it.lowercase().contains(selectedValue)
    }

    val list = filterList.ifEmpty {
        countryList.toList()
    }

    LazyColumn(
        modifier = modifier
            .fillMaxWidth()
            .padding(
                start = Dimensions.size16dp,
                end = Dimensions.size16dp,
                top = Dimensions.size10dp
            )
    ) {
//        items(list) { country ->
//            Text(
//                text = country,
//                fontSize = FontSize.textSize14sp,
//                color = AppColors.textPrimary,
//                fontFamily = FontFamily.medium(),
//                modifier = modifier
//                    .fillMaxWidth()
//                    .clickable(
//                        onClick = {
//                            onItemSelected(country)
//                        }
//                    )
//                    .padding(vertical = Dimensions.size10dp)
//            )
//        }
    }
}

@Preview
@Composable
private fun PreviewAutoCompleteTextBox() {
    Column(
        modifier = Modifier.background(
            color = AppColors.backgroundDark
        )
    ) {
        AutoCompleteTextBox(
            selectedValue = "ind",
            isExpanded = false,
            onTextChanged = {}) {
        }
        CountryList(selectedValue = "ind") {}
    }
}