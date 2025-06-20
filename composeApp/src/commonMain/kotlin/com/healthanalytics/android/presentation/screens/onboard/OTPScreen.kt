package com.healthanalytics.android.presentation.screens.onboard

import androidx.compose.foundation.background
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.healthanalytics.android.components.DHToolBar
import com.healthanalytics.android.components.PrimaryButton
import com.healthanalytics.android.data.models.onboard.OtpResponse
import com.healthanalytics.android.modifier.onBoxClick
import com.healthanalytics.android.presentation.screens.onboard.viewmodel.OnboardViewModel
import com.healthanalytics.android.presentation.screens.questionnaire.setBorder
import com.healthanalytics.android.presentation.theme.AppColors
import com.healthanalytics.android.presentation.theme.AppStrings
import com.healthanalytics.android.presentation.theme.AppTextStyles
import com.healthanalytics.android.presentation.theme.Dimensions
import com.healthanalytics.android.presentation.theme.FontFamily
import com.healthanalytics.android.presentation.theme.FontSize
import com.healthanalytics.android.utils.Resource
import kotlinx.coroutines.delay
import org.jetbrains.compose.ui.tooling.preview.Preview
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.healthanalytics.android.payment.RazorpayHandler


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
        countryCode = onboardViewModel.getCountryCode(),
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
    otpVerifyState: Resource<OtpResponse?>?,
    countryCode: String
) {
    var otpValues by remember { mutableStateOf(List(6) { "" }) }
    var resendTimer by remember { mutableStateOf(60) }
    var isTimerActive by remember { mutableStateOf(true) }
    val focusRequesters = remember { List(6) { FocusRequester() } }
    val keyboardController = LocalSoftwareKeyboardController.current
    var lastRequestedFocusIndex by remember { mutableStateOf<Int?>(null) }

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
                onBackClick = onBackClick
            )

            Spacer(modifier = Modifier.height(Dimensions.size80dp))

            Text(
                text = AppStrings.CONFIRM_YOUR_PHONE,
                fontFamily = FontFamily.bold(),
                color = AppColors.primaryTextColor,
                textAlign = TextAlign.Center,
                fontSize = FontSize.textSize24sp
            )

            Spacer(modifier = Modifier.height(Dimensions.size8dp))

            Text(
                text = AppStrings.WE_VE_SENT_A_SECURITY_CODE_TO,
                fontFamily = FontFamily.medium(),
                color = AppColors.secondaryTextColor,
                textAlign = TextAlign.Center,
                fontSize = FontSize.textSize16sp
            )
            Spacer(modifier = Modifier.height(Dimensions.size4dp))

            Text(
                text = "$countryCode $phoneNumber",
                fontFamily = FontFamily.regular(),
                color = AppColors.secondaryTextColor,
                textAlign = TextAlign.Center,
                fontSize = FontSize.textSize16sp
            )

            Spacer(modifier = Modifier.height(40.dp))

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
                                newOtpValues[index] = newValue.last().toString()
                                otpValues = newOtpValues
                                // Always move to next field if available
                                if (index < newOtpValues.size - 1) {
                                    lastRequestedFocusIndex = index + 1
                                }
                            } else {
                                newOtpValues[index] = ""
                                otpValues = newOtpValues
                                // Always move to previous field if available
                                if (index > 0) {
                                    val lastValue = otpValues[otpValues.size-1].toString()
                                    if (lastValue.isEmpty()){
                                        lastRequestedFocusIndex = index - 1
                                    }
                                }
                            }
                        },
                        focusRequester = focusRequesters[index],
                        keyboardController = keyboardController
                    )
                }
            }

            // Focus management after state update
            LaunchedEffect(lastRequestedFocusIndex, otpValues) {
                lastRequestedFocusIndex?.let { idx ->
                    if (idx in 0..5) {
                        focusRequesters[idx].requestFocus()
                    }
                    lastRequestedFocusIndex = null
                }
            }

            if (isTimerActive) {
                Spacer(modifier = Modifier.height(Dimensions.size40dp))

                val minutes = resendTimer / 60
                val seconds = resendTimer % 60
                val timerText = "${minutes}:${seconds.toString().padStart(2, '0')}"

                Text(
                    text = AppStrings.YOU_CAN_RESEND_THE_CODE_IN.plus(" ") + timerText,
                    fontFamily = FontFamily.regular(),
                    color = AppColors.secondaryTextColor,
                    textAlign = TextAlign.Center,
                    fontSize = FontSize.textSize16sp
                )
            } else {
                Spacer(modifier = Modifier.height(Dimensions.size10dp))

                TextButton(
                    onClick = {
                        onResendClick()
                        resendTimer = 60
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
        }

        PrimaryButton(
            modifier = Modifier.align(Alignment.BottomCenter),
            buttonName = AppStrings.CONTINUE,
            enable = otpValues.all { it.isNotEmpty() },
            onClick = {
                val otp = otpValues.joinToString("")
                onContinueClick(otp)
            }
        )

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
    modifier: Modifier = Modifier,
    keyboardController: SoftwareKeyboardController? = null
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
            color = AppColors.primaryTextColor
        ),
        modifier = modifier
            .size(Dimensions.size48dp)
            .fillMaxSize()
            .focusRequester(focusRequester)
            .background(
                color = AppColors.backGround,
                shape = RoundedCornerShape(8.dp)
            )
            .clip(shape = RoundedCornerShape(8.dp))
            .setOTPFieldBorder(value.isNotEmpty()),
        decorationBox = { innerTextField ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .onBoxClick(
                        onClick = {
                            focusRequester.requestFocus()
                            keyboardController?.show()
                        }
                    ),
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
        phoneNumber = "1234567890",
        otpVerified = {},
        otpVerifyState = Resource.Loading(),
        countryCode = "+91"
    )
}

class OTPScreenNav(
    private val onboardViewModel: OnboardViewModel,
    private val razorpayHandler: RazorpayHandler,
    private val isLoggedIn: () -> Unit
) : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        OTPContainer(
            onboardViewModel = onboardViewModel,
            onBackClick = { navigator.pop() },
            navigateToAccountCreation = {
                navigator.push(CreateAccountScreenNav(onboardViewModel, razorpayHandler, isLoggedIn))
            }
        )
    }
}

