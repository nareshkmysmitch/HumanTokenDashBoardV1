package com.healthanalytics.android.presentation.screens.onboard

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.healthanalytics.android.components.PrimaryButton
import com.healthanalytics.android.data.models.onboard.AuthResponse
import com.healthanalytics.android.presentation.screens.onboard.viewmodel.OnboardViewModel
import com.healthanalytics.android.presentation.theme.AppColors
import com.healthanalytics.android.presentation.theme.AppStrings
import com.healthanalytics.android.presentation.theme.AppTextStyles
import com.healthanalytics.android.presentation.theme.Dimensions
import com.healthanalytics.android.presentation.theme.FontFamily
import com.healthanalytics.android.presentation.theme.FontSize
import com.healthanalytics.android.utils.Resource
import humantokendashboardv1.composeapp.generated.resources.Res
import humantokendashboardv1.composeapp.generated.resources.rounded_logo
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharedFlow
import org.jetbrains.compose.resources.painterResource
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.healthanalytics.android.payment.RazorpayHandler


@Composable
fun LoginScreenContainer(
    onboardViewModel: OnboardViewModel,
    navigateToOtpVerification: () -> Unit
) {
    LoginScreen(
        loginState = onboardViewModel.loginState,
        onContinueClick = {
            onboardViewModel.sendOTP(it)
        },
        navigateToOtpVerification = navigateToOtpVerification,
        phoneNumber = onboardViewModel.getPhoneNumber()
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    loginState: SharedFlow<Resource<AuthResponse?>>,
    onContinueClick: (String) -> Unit = {},
    onCountryCodeClick: () -> Unit = {},
    navigateToOtpVerification: () -> Unit,
    phoneNumber: String
) {

    GetOTPResponse(
        loginState = loginState,
        navigateToOtpVerification = navigateToOtpVerification
    )

    var phoneNumber by remember { mutableStateOf(phoneNumber) }
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        delay(100)
        focusRequester.requestFocus()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(Dimensions.screenPadding)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
                .padding(top = Dimensions.size12dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Image(
                    painter = painterResource(Res.drawable.rounded_logo),
                    contentDescription = AppStrings.appName,
                    modifier = Modifier.size(Dimensions.size24dp)
                )

                Spacer(modifier = Modifier.width(Dimensions.size8dp))

                Text(
                    text = "Deep Holistics",
                    fontSize = FontSize.textSize18sp,
                    fontFamily = FontFamily.bold(),
                    color = AppColors.textPrimary,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(Dimensions.size50dp))

            Text(
                text = AppStrings.LOGIN_TITLE,
                fontSize = FontSize.textSize18sp,
                fontFamily = FontFamily.bold(),
                color = AppColors.textPrimary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(Dimensions.size8dp))

            Text(
                text = AppStrings.LOGIN_SUBTITLE,
                fontSize = FontSize.textSize24sp,
                fontFamily = FontFamily.bold(),
                color = AppColors.textPrimary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(Dimensions.size48dp))

            FieldNameText(
                name = AppStrings.PHONE_NUMBER,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(Dimensions.size8dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(Dimensions.inputFieldHeight)
                    .border(
                        width = 1.dp,
                        color = AppColors.tertiaryTextColor,
                        shape = RoundedCornerShape(Dimensions.cornerRadiusMedium)
                    )
                    .background(
                        color = AppColors.gray,
                        shape = RoundedCornerShape(Dimensions.cornerRadiusMedium)
                    ),
                verticalAlignment = Alignment.CenterVertically
            ) {
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
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = AppStrings.countryCode,
                            fontFamily = FontFamily.medium(),
                            fontSize = FontSize.textSize16sp,
                            color = AppColors.primaryTextColor
                        )

                        Icon(
                            imageVector = Icons.Rounded.KeyboardArrowDown,
                            contentDescription = null,
                            tint = AppColors.primaryTextColor
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .width(1.dp)
                        .height(32.dp)
                        .background(AppColors.secondaryTextColor)
                )

                OutlinedTextField(
                    value = phoneNumber,
                    onValueChange = { newValue ->
                        val filteredValue = newValue.filter { it.isDigit() }
                        if (filteredValue.length <= 15) {
                            phoneNumber = filteredValue
                        }
                    },
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
                    textStyle = TextStyle(
                        fontFamily = FontFamily.medium(),
                        fontSize = FontSize.textSize16sp,
                        color = AppColors.primaryTextColor
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
                    ),
                    maxLines = 1,
                )
            }

            Spacer(modifier = Modifier.height(Dimensions.size32dp))

            PrimaryButton(
                buttonName = AppStrings.CONTINUE,
                enable = phoneNumber.isNotBlank(),
                onClick = {
                    onContinueClick(phoneNumber.trim())
                }
            )
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
                navigateToOtpVerification()
            }
        }

        else -> {}
    }
}

class LoginScreenNav(
    private val onboardViewModel: OnboardViewModel,
    private val razorpayHandler: RazorpayHandler,
    private val isLoggedIn: () -> Unit
) : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        LoginScreenContainer(
            onboardViewModel = onboardViewModel,
            navigateToOtpVerification = {
                navigator.push(OTPScreenNav(onboardViewModel, razorpayHandler, isLoggedIn))
            }
        )
    }
}




