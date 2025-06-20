package com.healthanalytics.android.presentation.screens.questionnaire.howtomeasure

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import com.healthanalytics.android.data.models.questionnaire.BottomSheetQuestionState
import com.healthanalytics.android.data.models.questionnaire.Question
import com.healthanalytics.android.modifier.onRowClick
import com.healthanalytics.android.presentation.screens.questionnaire.viewmodel.QuestionnaireViewModel
import com.healthanalytics.android.presentation.theme.AppColors
import com.healthanalytics.android.presentation.theme.Dimensions
import com.healthanalytics.android.presentation.theme.FontFamily
import com.healthanalytics.android.presentation.theme.FontSize

@Composable
fun HowToMeasureSection(
    modifier: Modifier = Modifier, question: Question, viewModel: QuestionnaireViewModel
) {
    Spacer(modifier = modifier.padding(top = Dimensions.size8dp))
    Row(
        modifier = modifier
            .fillMaxWidth()
            .onRowClick(
                onClick = {
                    question.data_type?.let { _ ->
                        viewModel.setBottomSheetState(
                            BottomSheetQuestionState(
                                question = question,
                                showHowToMeasureBottomSheet = true
                            )
                        )
                    }
                }
            ),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "how_to_measure",
            fontSize = FontSize.textSize14sp,
            color = AppColors.TextGrey,
            fontFamily = FontFamily.medium(),
            textAlign = TextAlign.Start,
            textDecoration = TextDecoration.Underline
        )
        Spacer(modifier = modifier.padding(start = Dimensions.size4dp))
        Icon(
            imageVector = Icons.Outlined.Info,
            contentDescription = null,
            tint = AppColors.TextGrey,
            modifier = modifier.size(
                Dimensions.size16dp
            )
        )
    }
}




