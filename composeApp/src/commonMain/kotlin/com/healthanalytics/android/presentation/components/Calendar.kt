package com.healthanalytics.android.presentation.components

import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.healthanalytics.android.presentation.theme.AppStrings
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShowDatePicker(
    selectedDate: LocalDate,
    onConfirm: (LocalDate) -> Unit,
    onCancel: () -> Unit,
    onDismiss: () -> Unit
) {

//    var selectedDate by remember {
//        mutableStateOf(
//            Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
//        )
//    }

    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = selectedDate.toEpochDays().toLong() * 24 * 60 * 60 * 1000
    )

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val selectedDate =
                            kotlinx.datetime.Instant.fromEpochMilliseconds(millis).toLocalDateTime(
                                TimeZone.currentSystemDefault()
                            ).date

                        onConfirm(selectedDate)
                    }
                }
            ) {
                Text(AppStrings.CONFIRM)
            }
        },
        dismissButton = {
            TextButton(
                onClick = onCancel
            ) {
                Text(AppStrings.CANCEL)
            }
        }
    ) {
        DatePicker(state = datePickerState)
    }
}