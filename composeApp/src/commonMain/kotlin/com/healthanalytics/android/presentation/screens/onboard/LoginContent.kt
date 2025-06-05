package com.healthanalytics.android.presentation.screens.onboard

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.healthanalytics.android.data.models.onboard.AuthResponse
import com.healthanalytics.android.presentation.theme.AppColors
import com.healthanalytics.android.presentation.theme.AppStrings
import com.healthanalytics.android.presentation.theme.AppTextStyles
import com.healthanalytics.android.presentation.theme.Dimensions
import com.healthanalytics.android.utils.Resource
import humantokendashboardv1.composeapp.generated.resources.Res
import humantokendashboardv1.composeapp.generated.resources.ic_calendar_icon
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharedFlow
import org.jetbrains.compose.resources.painterResource


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    loginState: SharedFlow<Resource<AuthResponse?>>,
    onContinueClick: (String) -> Unit = {},
    onCountryCodeClick: () -> Unit = {},
    navigateToOtpVerification: () -> Unit
) {

    GetOTPResponse(
        loginState = loginState,
        navigateToOtpVerification = navigateToOtpVerification
    )

    var phoneNumber by remember { mutableStateOf("") }
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        delay(100) // Small delay to ensure the UI is fully composed
        focusRequester.requestFocus()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColors.backgroundDark)
            .padding(Dimensions.screenPadding)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
                .padding(top = 80.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // App Logo
            Image(
                painter = painterResource(Res.drawable.ic_calendar_icon),
                contentDescription = AppStrings.appName,
                modifier = Modifier.size(80.dp)
            )

            Spacer(modifier = Modifier.height(Dimensions.spacingXXLarge))

            // Login Title
            Text(
                text = AppStrings.loginTitle,
                style = AppTextStyles.headingSmall,
                color = AppColors.textPrimary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(Dimensions.spacingXSmall))

            // Login Subtitle
            Text(
                text = AppStrings.loginSubtitle,
                style = AppTextStyles.headingSmall,
                color = AppColors.textPrimary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(Dimensions.spacingXXLarge))

            // Phone Number Label
            Text(
                text = AppStrings.phoneNumberLabel,
                style = AppTextStyles.labelMedium,
                color = AppColors.textSecondary,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(Dimensions.spacingSmall))

            // Phone Number Input Container with border
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(Dimensions.inputFieldHeight)
                    .border(
                        width = 1.dp,
                        color = Color(0xFF4A4A4A),
                        shape = RoundedCornerShape(Dimensions.cornerRadiusMedium)
                    )
                    .background(
                        color = AppColors.inputBackground,
                        shape = RoundedCornerShape(Dimensions.cornerRadiusMedium)
                    ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Country Code Button
                OutlinedButton(
                    onClick = onCountryCodeClick,
                    modifier = Modifier
                        .height(Dimensions.inputFieldHeight)
                        .padding(0.dp),
                    shape = RoundedCornerShape(
                        topStart = Dimensions.cornerRadiusMedium,
                        bottomStart = Dimensions.cornerRadiusMedium,
                        topEnd = 0.dp,
                        bottomEnd = 0.dp
                    ),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = Color.Transparent,
                        contentColor = AppColors.textPrimary
                    ),
                    border = null,
                    contentPadding = PaddingValues(horizontal = 16.dp)
                ) {
                    Text(
                        text = "${AppStrings.countryCode} ▼",
                        style = AppTextStyles.bodyMedium,
                        color = AppColors.textPrimary
                    )
                }

                // Vertical divider line
                Box(
                    modifier = Modifier
                        .width(1.dp)
                        .height(32.dp)
                        .background(Color(0xFF4A4A4A))
                )

                // Phone Number TextField
                OutlinedTextField(
                    value = phoneNumber,
                    onValueChange = { phoneNumber = it },
                    placeholder = {
                        Text(
                            text = AppStrings.phoneNumberPlaceholder,
                            style = AppTextStyles.bodyMedium,
                            color = AppColors.inputHint
                        )
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(Dimensions.inputFieldHeight)
                        .focusRequester(focusRequester),
                    textStyle = AppTextStyles.bodyMedium.copy(
                        color = AppColors.inputText
                    ),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Phone
                    ),
                    singleLine = true,
                    shape = RoundedCornerShape(
                        topStart = 0.dp,
                        bottomStart = 0.dp,
                        topEnd = Dimensions.cornerRadiusMedium,
                        bottomEnd = Dimensions.cornerRadiusMedium
                    ),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent,
                        cursorColor = AppColors.primary
                    )
                )
            }

            Spacer(modifier = Modifier.height(Dimensions.spacingXLarge))

            // Continue Button below phone input
            Button(
                onClick = { onContinueClick(phoneNumber.trim()) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(Dimensions.buttonHeight),
                shape = RoundedCornerShape(Dimensions.cornerRadiusLarge),
                colors = ButtonDefaults.buttonColors(
                    containerColor = AppColors.buttonBackground,
                    contentColor = AppColors.buttonText,
                    disabledContainerColor = AppColors.inputBackground,
                    disabledContentColor = AppColors.textSecondary
                ),
                enabled = phoneNumber.isNotBlank()
            ) {
                Text(
                    text = AppStrings.continueButton,
                    style = AppTextStyles.buttonText
                )
            }
        }
    }
}

@Composable
fun GetOTPResponse(
    loginState: SharedFlow<Resource<AuthResponse?>>,
    navigateToOtpVerification: () -> Unit
) {
    val response by loginState.collectAsStateWithLifecycle(null)

    when (response) {
        is Resource.Loading -> {}
        is Resource.Error -> {}
        is Resource.Success -> {
            LaunchedEffect(response) {
                println("Resource.Success.....")
                navigateToOtpVerification()
            }
        }
        else -> {}
    }
}


