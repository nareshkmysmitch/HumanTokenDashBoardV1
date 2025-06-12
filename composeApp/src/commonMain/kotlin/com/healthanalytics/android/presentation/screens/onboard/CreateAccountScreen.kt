package com.healthanalytics.android.presentation.screens.onboard

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
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
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.healthanalytics.android.components.DHToolBar
import com.healthanalytics.android.components.PrimaryButton
import com.healthanalytics.android.data.models.onboard.AccountDetails
import com.healthanalytics.android.presentation.components.ShowDatePicker
import com.healthanalytics.android.presentation.screens.onboard.viewmodel.OnboardViewModel
import com.healthanalytics.android.presentation.theme.AppColors
import com.healthanalytics.android.presentation.theme.AppStrings
import com.healthanalytics.android.presentation.theme.AppTextStyles
import com.healthanalytics.android.presentation.theme.Dimensions
import com.healthanalytics.android.presentation.theme.FontFamily
import com.healthanalytics.android.presentation.theme.FontSize
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun CreateAccountContainer(
    onBackClick: () -> Unit = {},
    navigateToAddress: () -> Unit,
    onboardViewModel: OnboardViewModel
) {
    CreateAccountScreen(
        accountDetails = onboardViewModel.getAccountDetails(),
        onBackClick = onBackClick,
        onContinueClick = { accountDetails ->
            onboardViewModel.saveAccountDetails(
                accountDetails = accountDetails
            )
            navigateToAddress()
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateAccountScreen(
    onBackClick: () -> Unit = {},
    onContinueClick: (AccountDetails) -> Unit = { },
    accountDetails: AccountDetails?
) {
    var firstName by remember { mutableStateOf(accountDetails?.firstName ?: "") }
    var lastName by remember { mutableStateOf(accountDetails?.lastName ?: "") }
    var email by remember { mutableStateOf(accountDetails?.email ?: "") }
    var emailError by remember { mutableStateOf("") }
    var selectedDate by remember { mutableStateOf(accountDetails?.dob) }
    var selectedGender by remember { mutableStateOf(accountDetails?.gender ?: "") }
    var weight by remember { mutableStateOf(accountDetails?.weight ?: "") }
    var height by remember { mutableStateOf(accountDetails?.height ?: "") }
    var showDatePicker by remember { mutableStateOf(false) }
    var showGenderDropdown by remember { mutableStateOf(false) }

    val firstNameFocusRequester = remember { FocusRequester() }
    val weightFocusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current

    val genderOptions = listOf("Male", "Female")

    // Email validation regex
    val emailRegex = remember {
        Regex("^[A-Za-z0-9+_.-]+@([A-Za-z0-9.-]+\\.[A-Za-z]{2,})$")
    }

    // Focus on first name field when screen loads
    LaunchedEffect(Unit) {
        firstNameFocusRequester.requestFocus()
        keyboardController?.show()
    }

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

            DHToolBar(
                title = AppStrings.CREATE_ACCOUNT,
                onBackClick = onBackClick
            )

            Column(
                modifier = Modifier.verticalScroll(rememberScrollState())
                    .padding(bottom = Dimensions.size60dp)
            ) {

                Spacer(modifier = Modifier.height(Dimensions.size50dp))

                // First Name Field
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    FieldNameText(
                        name = AppStrings.FIRST_NAME
                    )
                    OutlinedTextField(
                        value = firstName,
                        onValueChange = { firstName = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .focusRequester(firstNameFocusRequester),
                        maxLines = 1,
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

                // Last Name Field
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    FieldNameText(
                        name = AppStrings.LAST_NAME
                    )
                    OutlinedTextField(
                        value = lastName,
                        onValueChange = { lastName = it },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 1,
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

                // Email Field
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    FieldNameText(
                        name = AppStrings.EMAIL
                    )
                    OutlinedTextField(
                        value = email,
                        onValueChange = { newValue ->
                            email = newValue
                            // Only validate if email contains @ and appears to be a complete attempt
                            emailError =
                                if (newValue.isNotEmpty() && newValue.contains("@") && !emailRegex.matches(
                                        newValue
                                    )
                                ) {
                                    "Please enter a valid email address"
                                } else {
                                    ""
                                }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 1,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = AppColors.inputBorder,
                            unfocusedBorderColor = AppColors.outline,
                            focusedTextColor = AppColors.inputText,
                            unfocusedTextColor = AppColors.inputText,
                            cursorColor = AppColors.inputText
                        ),
                        shape = RoundedCornerShape(Dimensions.cornerRadiusSmall)
                    )

                    // Show email error message
                    if (emailError.isNotEmpty()) {
                        Text(
                            text = emailError,
                            style = AppTextStyles.caption,
                            color = AppColors.error,
                            modifier = Modifier.padding(top = Dimensions.size4dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Date of Birth Field
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    FieldNameText(
                        name = AppStrings.DOB
                    )
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

                    // Date Picker Dialog
                    if (showDatePicker) {
                        ShowDatePicker(
                            selectedDate = selectedDate ?: Clock.System.now()
                                .toLocalDateTime(TimeZone.currentSystemDefault()).date,
                            onDismiss = {
                                showDatePicker = false
                            },
                            onCancel = {
                                showDatePicker = false
                            },
                            onConfirm = {
                                selectedDate = it
                                showDatePicker = false
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Gender Field
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    FieldNameText(
                        name = AppStrings.GENDER
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
                                .menuAnchor(
                                    type = MenuAnchorType.PrimaryEditable,
                                ),
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
                    FieldNameText(
                        name = AppStrings.WEIGHT
                    )
                    OutlinedTextField(
                        value = weight,
                        onValueChange = { weight = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .focusRequester(weightFocusRequester),
                        maxLines = 1,
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
                    FieldNameText(
                        name = AppStrings.HEIGHT
                    )
                    OutlinedTextField(
                        value = height,
                        onValueChange = { height = it },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 1,
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
            }
        }

        Box(
            modifier = Modifier.fillMaxWidth()
                .background(AppColors.backgroundDark)
                .align(Alignment.BottomCenter),
            contentAlignment = Alignment.Center
        ) {
            PrimaryButton(
                modifier = Modifier.padding(Dimensions.size16dp),
                isEnable = firstName.isNotEmpty() && lastName.isNotEmpty() && email.isNotEmpty() &&
                        emailError.isEmpty() && selectedDate != null && selectedGender.isNotEmpty() &&
                        weight.isNotEmpty() && height.isNotEmpty(),
                buttonName = AppStrings.CONTINUE,
                onclick = {
                    if (firstName.isNotEmpty() && lastName.isNotEmpty() && email.isNotEmpty() &&
                        emailError.isEmpty() && selectedDate != null && selectedGender.isNotEmpty() &&
                        weight.isNotEmpty() && height.isNotEmpty()
                    ) {
                        onContinueClick(
                            AccountDetails(
                                firstName = firstName.trim(),
                                lastName = lastName.trim(),
                                email = email.trim(),
                                dob = selectedDate,
                                gender = selectedGender,
                                weight = weight.trim(),
                                height = height.trim()
                            )
                        )
                    }
                }
            )
        }
    }
}

@Composable
fun FieldNameText(
    name: String
) {
    Text(
        text = name,
        fontSize = FontSize.textSize14sp,
        fontFamily = FontFamily.medium(),
        color = AppColors.textSecondary,
        modifier = Modifier.padding(bottom = Dimensions.size8dp)
    )
}

@Preview
@Composable
fun CreateAccountScreenPreview() {
    CreateAccountScreen(
        accountDetails = null
    )
}
