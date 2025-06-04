
package com.healthanalytics.android.presentation.screens.onboard

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.datetime.*
import com.healthanalytics.android.presentation.theme.AppColors
import com.healthanalytics.android.presentation.theme.AppTextStyles
import com.healthanalytics.android.presentation.theme.Dimensions
import humantokendashboardv1.composeapp.generated.resources.Res
import humantokendashboardv1.composeapp.generated.resources.ic_calendar_icon
import org.jetbrains.compose.resources.painterResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HealthProfileScreen(
    onBackClick: () -> Unit = {},
    onContinueClick: (String, String, String, String) -> Unit = { _, _, _, _ -> }
) {
    var selectedDate by remember { mutableStateOf<LocalDate?>(null) }
    var selectedGender by remember { mutableStateOf("") }
    var weight by remember { mutableStateOf("") }
    var height by remember { mutableStateOf("") }
    var showDatePicker by remember { mutableStateOf(false) }
    var showGenderDropdown by remember { mutableStateOf(false) }
    
    val weightFocusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current
    
    val genderOptions = listOf("Male", "Female")

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColors.backgroundDark)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(Dimensions.cardPadding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top section with back button and logo
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = Dimensions.spacingMedium),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Back button
                TextButton(
                    onClick = onBackClick,
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = AppColors.textPrimary
                    )
                ) {
                    Text(
                        text = "← Back",
                        style = AppTextStyles.bodyMedium,
                        color = AppColors.textPrimary
                    )
                }

                // Logo and title
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(Res.drawable.ic_calendar_icon),
                        contentDescription = "Logo",
                        modifier = Modifier.size(Dimensions.iconSize)
                    )
                    Spacer(modifier = Modifier.width(Dimensions.spacingSmall))
                    Text(
                        text = "Deep Holistics",
                        style = AppTextStyles.headingSmall,
                        color = AppColors.textPrimary
                    )
                }

                // Empty space for balance
                Spacer(modifier = Modifier.width(Dimensions.spacingXXLarge))
            }

            Spacer(modifier = Modifier.height(Dimensions.spacingXXLarge + Dimensions.spacingSmall))

            // Title
            Text(
                text = "Your Health Profile",
                style = AppTextStyles.headingLarge.copy(fontSize = 28.sp),
                color = AppColors.textPrimary,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = Dimensions.spacingXXLarge)
            )

            // Date of Birth Field
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "DATE OF BIRTH",
                    style = AppTextStyles.labelMedium,
                    color = AppColors.textSecondary,
                    modifier = Modifier.padding(bottom = Dimensions.spacingSmall)
                )
                
                if (showDatePicker) {
                    val datePickerState = rememberDatePickerState()
                    
                    DatePicker(
                        state = datePickerState,
                        colors = DatePickerDefaults.colors(
                            containerColor = AppColors.inputBackground,
                            titleContentColor = AppColors.textPrimary,
                            headlineContentColor = AppColors.textPrimary,
                            weekdayContentColor = AppColors.textSecondary,
                            subheadContentColor = AppColors.textSecondary,
                            yearContentColor = AppColors.textPrimary,
                            currentYearContentColor = AppColors.textPrimary,
                            selectedYearContentColor = AppColors.buttonText,
                            selectedYearContainerColor = AppColors.buttonBackground,
                            dayContentColor = AppColors.textPrimary,
                            selectedDayContentColor = AppColors.buttonText,
                            selectedDayContainerColor = AppColors.buttonBackground,
                            todayContentColor = AppColors.buttonBackground,
                            todayDateBorderColor = AppColors.buttonBackground
                        )
                    )
                    
                    LaunchedEffect(datePickerState.selectedDateMillis) {
                        datePickerState.selectedDateMillis?.let { millis ->
                            val instant = Instant.fromEpochMilliseconds(millis)
                            val localDateTime = instant.toLocalDateTime(TimeZone.UTC)
                            selectedDate = localDateTime.date
                            showDatePicker = false
                        }
                    }
                } else {
                    OutlinedTextField(
                        value = selectedDate?.toString() ?: "",
                        onValueChange = { },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showDatePicker = true },
                        enabled = false,
                        placeholder = {
                            Text(
                                text = "Select date",
                                color = AppColors.inputHint,
                                style = AppTextStyles.bodyMedium
                            )
                        },
                        trailingIcon = {
                            Text(
                                text = "📅",
                                style = AppTextStyles.bodyMedium,
                                color = AppColors.inputHint
                            )
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = AppColors.inputBorder,
                            unfocusedBorderColor = AppColors.outline,
                            disabledBorderColor = AppColors.outline,
                            focusedTextColor = AppColors.inputText,
                            unfocusedTextColor = AppColors.inputText,
                            disabledTextColor = AppColors.inputText,
                            cursorColor = AppColors.inputText
                        ),
                        shape = RoundedCornerShape(Dimensions.cornerRadiusSmall)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Gender Field
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "GENDER",
                    style = AppTextStyles.labelMedium,
                    color = AppColors.textSecondary,
                    modifier = Modifier.padding(bottom = Dimensions.spacingSmall)
                )
                ExposedDropdownMenuBox(
                    expanded = showGenderDropdown,
                    onExpandedChange = { showGenderDropdown = !showGenderDropdown }
                ) {
                    OutlinedTextField(
                        value = selectedGender,
                        onValueChange = { },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        readOnly = true,
                        placeholder = {
                            Text(
                                text = "Select gender",
                                color = AppColors.inputHint,
                                style = AppTextStyles.bodyMedium
                            )
                        },
                        trailingIcon = {
                            Text(
                                text = "▼",
                                style = AppTextStyles.bodyMedium,
                                color = AppColors.inputHint
                            )
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = AppColors.inputBorder,
                            unfocusedBorderColor = AppColors.outline,
                            focusedTextColor = AppColors.inputText,
                            unfocusedTextColor = AppColors.inputText,
                            cursorColor = AppColors.inputText
                        ),
                        shape = RoundedCornerShape(Dimensions.cornerRadiusSmall)
                    )
                    
                    ExposedDropdownMenu(
                        expanded = showGenderDropdown,
                        onDismissRequest = { showGenderDropdown = false },
                        modifier = Modifier.background(AppColors.inputBackground)
                    ) {
                        genderOptions.forEach { option ->
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = option,
                                        color = AppColors.inputText
                                    )
                                },
                                onClick = {
                                    selectedGender = option
                                    showGenderDropdown = false
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Weight Field
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "WEIGHT (KG)",
                    style = AppTextStyles.labelMedium,
                    color = AppColors.textSecondary,
                    modifier = Modifier.padding(bottom = Dimensions.spacingSmall)
                )
                OutlinedTextField(
                    value = weight,
                    onValueChange = { weight = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(weightFocusRequester),
                    maxLines = 1,
                    placeholder = {
                        Text(
                            text = "Enter weight",
                            color = AppColors.inputHint,
                            style = AppTextStyles.bodyMedium
                        )
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AppColors.inputBorder,
                        unfocusedBorderColor = AppColors.outline,
                        focusedTextColor = AppColors.inputText,
                        unfocusedTextColor = AppColors.inputText,
                        cursorColor = AppColors.inputText
                    ),
                    shape = RoundedCornerShape(Dimensions.cornerRadiusSmall)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Height Field
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "HEIGHT (CM)",
                    style = AppTextStyles.labelMedium,
                    color = AppColors.textSecondary,
                    modifier = Modifier.padding(bottom = Dimensions.spacingSmall)
                )
                OutlinedTextField(
                    value = height,
                    onValueChange = { height = it },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 1,
                    placeholder = {
                        Text(
                            text = "Enter height",
                            color = AppColors.inputHint,
                            style = AppTextStyles.bodyMedium
                        )
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AppColors.inputBorder,
                        unfocusedBorderColor = AppColors.outline,
                        focusedTextColor = AppColors.inputText,
                        unfocusedTextColor = AppColors.inputText,
                        cursorColor = AppColors.inputText
                    ),
                    shape = RoundedCornerShape(Dimensions.cornerRadiusSmall)
                )
            }

            Spacer(modifier = Modifier.height(40.dp))

            // Continue Button
            Button(
                onClick = {
                    if (selectedDate != null && selectedGender.isNotEmpty() && 
                        weight.isNotEmpty() && height.isNotEmpty()) {
                        onContinueClick(selectedDate.toString(), selectedGender, weight, height)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(Dimensions.buttonHeight),
                enabled = selectedDate != null && selectedGender.isNotEmpty() && 
                         weight.isNotEmpty() && height.isNotEmpty(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = AppColors.buttonBackground,
                    contentColor = AppColors.buttonText,
                    disabledContainerColor = AppColors.inputBackground,
                    disabledContentColor = AppColors.textSecondary
                ),
                shape = RoundedCornerShape(Dimensions.cornerRadiusLarge)
            ) {
                Text(
                    text = "Continue",
                    style = AppTextStyles.buttonText
                )
            }
        }

        }
}
