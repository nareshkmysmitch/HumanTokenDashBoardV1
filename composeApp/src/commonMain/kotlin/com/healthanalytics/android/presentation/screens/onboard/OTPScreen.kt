package com.healthanalytics.android.presentation.screens.onboard

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.healthanalytics.android.components.DHToolBar
import com.healthanalytics.android.components.PrimaryButton
import com.healthanalytics.android.data.models.onboard.OtpResponse
import com.healthanalytics.android.presentation.screens.onboard.viewmodel.OnboardViewModel
import com.healthanalytics.android.presentation.theme.AppColors
import com.healthanalytics.android.presentation.theme.AppStrings
import com.healthanalytics.android.presentation.theme.AppTextStyles
import com.healthanalytics.android.presentation.theme.Dimensions
import com.healthanalytics.android.presentation.theme.FontFamily
import com.healthanalytics.android.utils.Resource
import kotlinx.coroutines.delay
import org.jetbrains.compose.ui.tooling.preview.Preview
import cafe.adriel.voyager.core.screen.Screen

private fun Modifier.noRippleClickable(onClick: () -> Unit): Modifier = composed {
    clickable(
        indication = null,
        interactionSource = remember { MutableInteractionSource() }
    ) {
        onClick()
    }
}

@Composable
fun OTPContainer(
    onboardViewModel: OnboardViewModel,
    onBackClick: () -> Unit = {},
    navigateToAccountCreation: () -> Unit
) {
    val otpVerifyState by onboardViewModel.otpVerifyState.collectAsStateWithLifecycle(null)

    OTPScreen(
        otpVerifyState = otpVerifyState,
        phoneNumber = onboardViewModel.getPhoneNumber(),
        onBackClick = onBackClick,
        otpVerified = navigateToAccountCreation,
        onResendClick = {
            onboardViewModel.resendOTP()
        },
        onContinueClick = {
            onboardViewModel.verifyOtp(it)
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OTPScreen(
    phoneNumber: String,
    onBackClick: () -> Unit = {},
    onContinueClick: (String) -> Unit = {},
    onResendClick: () -> Unit = {},
    otpVerified: () -> Unit,
    otpVerifyState: Resource<OtpResponse?>?
) {
    var otpValues by remember { mutableStateOf(List(6) { "" }) }
    var resendTimer by remember { mutableStateOf(45) }
    var isTimerActive by remember { mutableStateOf(true) }
    val focusRequesters = remember { List(6) { FocusRequester() } }
    val keyboardController = LocalSoftwareKeyboardController.current

    LaunchedEffect(Unit) {
        delay(100)
        focusRequesters[0].requestFocus()
    }

    LaunchedEffect(resendTimer, isTimerActive) {
        if (isTimerActive && resendTimer > 0) {
            delay(1000)
            resendTimer--
        } else if (resendTimer == 0) {
            isTimerActive = false
        }
    }

    LaunchedEffect(otpValues) {
        if (otpValues.all { it.isNotEmpty() }) {
            keyboardController?.hide()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(Dimensions.screenPadding)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            DHToolBar(
                title = AppStrings.LOGIN,
                onBackClick = onBackClick
            )

            Spacer(modifier = Modifier.height(80.dp))

            Text(
                text = AppStrings.CONFIRM_YOUR_PHONE,
                fontFamily = FontFamily.medium(),
                color = AppColors.textPrimary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(Dimensions.size16dp))

            Text(
                text = AppStrings.WE_VE_SENT_A_SECURITY_CODE_TO,
                fontFamily = FontFamily.regular(),
                color = AppColors.textSecondary,
                textAlign = TextAlign.Center
            )

            Text(
                text = phoneNumber,
                style = AppTextStyles.bodyMedium.copy(fontWeight = FontWeight.Medium),
                color = AppColors.textPrimary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(60.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                otpValues.forEachIndexed { index, value ->
                    OTPInputField(
                        value = value,
                        onValueChange = { newValue ->
                            val newOtpValues = otpValues.toMutableList()

                            if (newValue.isNotEmpty()) {
                                // Check if all previous fields are filled (sequential entry)
                                val canEnterValue = if (index == 0) {
                                    true // First field can always be filled
                                } else {
                                    (0 until index).all { newOtpValues[it].isNotEmpty() }
                                }

                                if (canEnterValue) {
                                    newOtpValues[index] = newValue
                                    otpValues = newOtpValues

                                    // Auto-focus next field when entering a value
                                    if (index < 5) {
                                        focusRequesters[index + 1].requestFocus()
                                    }
                                }
                            } else {
                                // For deletion, only allow if this field currently has a value
                                if (newOtpValues[index].isNotEmpty()) {
                                    // Clear current field and all fields after it
                                    for (i in index until newOtpValues.size) {
                                        newOtpValues[i] = ""
                                    }
                                    otpValues = newOtpValues

                                    // Focus previous field when removing value, but stay on current if it's first
                                    if (index > 0) {
                                        focusRequesters[index - 1].requestFocus()
                                    }
                                }
                            }
                        },
                        focusRequester = focusRequesters[index],
                        modifier = Modifier.size(48.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            // Resend Timer
            if (isTimerActive) {
                Text(
                    text = AppStrings.YOU_CAN_RESEND_THE_CODE_IN.plus(
                        resendTimer.toString().padStart(2, '0')
                    ),
                    style = AppTextStyles.bodyMedium,
                    color = AppColors.textSecondary,
                    textAlign = TextAlign.Center
                )
            } else {
                TextButton(
                    onClick = {
                        onResendClick()
                        resendTimer = 45
                        isTimerActive = true
                        otpValues = List(6) { "" }
                        focusRequesters[0].requestFocus()
                    }
                ) {
                    Text(
                        text = AppStrings.RESEND_CODE,
                        style = AppTextStyles.bodyMedium,
                        color = AppColors.primary
                    )
                }
            }

            Spacer(modifier = Modifier.height(60.dp))

            PrimaryButton(
                buttonName = AppStrings.CONTINUE,
                isEnable = otpValues.all { it.isNotEmpty() },
                onclick = {
                    val otp = otpValues.joinToString("")
                    onContinueClick(otp)
                }
            )
        }

        GetVerifyOTPResponse(
            otpVerifyState = otpVerifyState,
            otpVerified = otpVerified
        )
    }
}

@Composable
fun GetVerifyOTPResponse(
    otpVerifyState: Resource<OtpResponse?>?,
    otpVerified: () -> Unit
) {
    when (otpVerifyState) {
        is Resource.Error<*> -> {}
        is Resource.Loading<*> -> {}
        is Resource.Success -> {
            LaunchedEffect(Unit) {
                otpVerified()
            }
        }

        else -> {}
    }
}

@Composable
private fun OTPInputField(
    value: String,
    onValueChange: (String) -> Unit,
    focusRequester: FocusRequester,
    modifier: Modifier = Modifier
) {
    BasicTextField(
        value = value,
        onValueChange = { newValue ->
            if (newValue.length <= 1 && newValue.all { it.isDigit() }) {
                onValueChange(newValue)
            }
        },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        cursorBrush = SolidColor(
            value = AppColors.primary
        ),
        textStyle = AppTextStyles.headingSmall.copy(
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            color = AppColors.textPrimary
        ),
        modifier = modifier
            .fillMaxSize()
            .focusRequester(focusRequester)
            .background(
                color = AppColors.surfaceVariant,
                shape = RoundedCornerShape(8.dp)
            )
            .border(
                width = 1.dp,
                color = if (value.isNotEmpty()) AppColors.primary else AppColors.outline,
                shape = RoundedCornerShape(8.dp)
            ),
        decorationBox = { innerTextField ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .noRippleClickable {
                        focusRequester.requestFocus()
                    },
                contentAlignment = Alignment.Center
            ) {
                innerTextField()
            }
        }
    )
}

@Preview
@Composable
fun OTPScreenPreview() {
    OTPScreen(
        otpVerifyState = Resource.Loading(),
        phoneNumber = "+91 1234567890",
        onBackClick = {},
        otpVerified = {},
        onResendClick = {},
        onContinueClick = {}
    )
}

class OTPScreenNav(
    private val onboardViewModel: OnboardViewModel,
    private val onBackClick: () -> Unit,
    private val navigateToAccountCreation: () -> Unit
) : Screen {
    @Composable
    override fun Content() {
        OTPContainer(
            onboardViewModel = onboardViewModel,
            onBackClick = onBackClick,
            navigateToAccountCreation = navigateToAccountCreation
        )
    }
}

