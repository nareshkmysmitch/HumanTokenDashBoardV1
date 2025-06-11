package com.healthanalytics.android.presentation.screens.onboard

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.healthanalytics.android.presentation.screens.onboard.viewmodel.OnboardViewModel
import com.healthanalytics.android.presentation.theme.AppColors
import com.healthanalytics.android.presentation.theme.AppTextStyles
import com.healthanalytics.android.presentation.theme.Dimensions
import humantokendashboardv1.composeapp.generated.resources.Res
import humantokendashboardv1.composeapp.generated.resources.ic_calendar_icon
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun CreateAccountContainer(
    onBackClick: () -> Unit = {},
    navigateToHealthProfile: () -> Unit,
    onboardViewModel: OnboardViewModel
) {
    CreateAccountScreen(
        onBackClick = onBackClick,
        onContinueClick = { firstName, lastName, email ->
            onboardViewModel.saveAccountDetails(
                firstName = firstName,
                lastName = lastName,
                email = email
            )
            navigateToHealthProfile()
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateAccountScreen(
    onBackClick: () -> Unit = {},
    onContinueClick: (String, String, String) -> Unit = { _, _, _ -> }
) {
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var emailError by remember { mutableStateOf("") }

    val firstNameFocusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current

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
                text = "Create your account",
                style = AppTextStyles.headingLarge.copy(fontSize = 28.sp),
                color = AppColors.textPrimary,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = Dimensions.spacingXXLarge - Dimensions.spacingSmall)
            )

            // First Name Field
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "FIRST NAME",
                    style = AppTextStyles.labelMedium,
                    color = AppColors.textSecondary,
                    modifier = Modifier.padding(bottom = Dimensions.spacingSmall)
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
                Text(
                    text = "LAST NAME",
                    style = AppTextStyles.labelMedium,
                    color = AppColors.textSecondary,
                    modifier = Modifier.padding(bottom = Dimensions.spacingSmall)
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
                Text(
                    text = "EMAIL",
                    style = AppTextStyles.labelMedium,
                    color = AppColors.textSecondary,
                    modifier = Modifier.padding(bottom = Dimensions.spacingSmall)
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
                        modifier = Modifier.padding(top = Dimensions.spacingXSmall)
                    )
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            // Continue Button
            Button(
                onClick = {
                    if (firstName.isNotEmpty() && lastName.isNotEmpty() && email.isNotEmpty() && emailError.isEmpty()) {
                        onContinueClick(firstName.trim(), lastName.trim(), email.trim())
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(Dimensions.buttonHeight),
                enabled = firstName.isNotEmpty() && lastName.isNotEmpty() && email.isNotEmpty() && emailError.isEmpty(),
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
