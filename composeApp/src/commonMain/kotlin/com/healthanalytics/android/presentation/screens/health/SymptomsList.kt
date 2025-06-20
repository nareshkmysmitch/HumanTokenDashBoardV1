package com.healthanalytics.android.presentation.screens.health

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import com.healthanalytics.android.data.models.home.SymptomsData
import com.healthanalytics.android.presentation.theme.AppColors
import com.healthanalytics.android.presentation.theme.Dimensions
import com.healthanalytics.android.presentation.theme.Dimensions.size12dp
import com.healthanalytics.android.presentation.theme.Dimensions.size16dp
import com.healthanalytics.android.presentation.theme.Dimensions.size4dp
import com.healthanalytics.android.presentation.theme.Dimensions.size8dp
import com.healthanalytics.android.presentation.theme.FontFamily
import com.healthanalytics.android.presentation.theme.FontSize
import com.healthanalytics.android.utils.AppConstants
import humantokendashboardv1.composeapp.generated.resources.Res
import humantokendashboardv1.composeapp.generated.resources.search_symptoms
import humantokendashboardv1.composeapp.generated.resources.symptom_count_plural
import humantokendashboardv1.composeapp.generated.resources.symptom_count_single
import org.jetbrains.compose.resources.stringResource


@Composable
fun SymptomsList(viewModel: HealthDataViewModel, onSymptomsClick: (SymptomsData?) -> Unit) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val symptomsList = viewModel.getSymptomsFilterList()
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.updateFilter(AppConstants.ALL)
        viewModel.updateSearchQuery("")
    }
    Spacer(modifier = Modifier.height(size16dp))

    OutlinedTextField(
        value = uiState.searchQuery,
        onValueChange = {
            viewModel.updateSearchQuery(it)
        },
        placeholder = {
            Text(
                text = stringResource(Res.string.search_symptoms),
                fontSize = FontSize.textSize16sp,
                fontFamily = FontFamily.regular(),
                textAlign = TextAlign.Start,
                color = AppColors.descriptionColor,
            )
        },
        maxLines = 1,
        singleLine = true,
        textStyle = TextStyle(
            color = AppColors.White,
            fontSize = FontSize.textSize16sp,
            fontFamily = FontFamily.medium(),
            textAlign = TextAlign.Start
        ),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = AppColors.darkPink,
            unfocusedBorderColor = AppColors.textFieldUnFocusedColor,
            focusedContainerColor = AppColors.Black,
            unfocusedContainerColor = AppColors.Black,
            errorBorderColor = AppColors.error
        ),
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Remove",
                tint = AppColors.descriptionColor
            )
        },
        keyboardActions = KeyboardActions(onDone = {
            keyboardController?.hide()
        }),
        shape = RoundedCornerShape(size12dp),
        modifier = Modifier.fillMaxWidth().padding(horizontal = size12dp)
    )

    Spacer(modifier = Modifier.height(size16dp))

    Card(
        modifier = Modifier.fillMaxWidth().wrapContentHeight()
            .padding(horizontal = size12dp),
        colors = CardDefaults.cardColors(containerColor = AppColors.CardGrey),
    ) {
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(Dimensions.size10dp)
        ) {
            val lastPosition = symptomsList.size.minus(1)
            items(symptomsList) { symptoms ->
                SymptomCard(
                    symptoms = symptoms,
                    onSymptomsClick = onSymptomsClick,
                )

                if (lastPosition != symptomsList.indexOf(symptoms)) {
                    HorizontalDivider(modifier = Modifier.padding(start = size12dp))
                }
            }
        }
    }
}

@Composable
fun SymptomCard(symptoms: SymptomsData?, onSymptomsClick: (SymptomsData?) -> Unit) {
    val name = symptoms?.name
    val count = symptoms?.count
    val lastReported = symptoms?.last_reported

    val isLatest = symptoms?.is_latest == true

    val countString = if (count == 1) {
        "$count ${stringResource(Res.string.symptom_count_single)}"
    } else {
        "$count ${stringResource(Res.string.symptom_count_plural)}"
    }

    Column(
        modifier = Modifier.fillMaxWidth().padding(size12dp)
            .clickable { symptoms?.let { onSymptomsClick(it) } }) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                if (isLatest) {
                    Box(
                        modifier = Modifier.size(size8dp)
                            .background(color = AppColors.error, shape = RoundedCornerShape(50))
                    )
                    Spacer(modifier = Modifier.width(size4dp))
                }
                Text(
                    text = name ?: "",
                    maxLines = 2,
                    fontSize = FontSize.textSize16sp,
                    fontFamily = FontFamily.medium(),
                    color = AppColors.White,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
            }

            Surface(
                color = AppColors.tagOrange.copy(alpha = 0.2f), shape = MaterialTheme.shapes.small
            ) {
                Text(
                    text = countString,
                    modifier = Modifier.wrapContentWidth()
                        .padding(horizontal = size8dp, vertical = size4dp),
                    fontSize = FontSize.textSize12sp,
                    fontFamily = FontFamily.medium(),
                    color = AppColors.tagOrange
                )
            }
        }

        Text(
            text = "Last updated: ${formatDate(lastReported ?: "")}",
            fontSize = FontSize.textSize12sp,
            fontFamily = FontFamily.regular(),
            color = AppColors.White,
        )

    }
}