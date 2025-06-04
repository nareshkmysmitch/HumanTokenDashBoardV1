package com.healthanalytics.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.tooling.preview.Preview
import com.healthanalytics.android.presentation.HealthAnalyticsApp
import com.healthanalytics.android.presentation.screens.onboard.LoginScreen
import com.healthanalytics.android.presentation.screens.onboard.OTPScreen
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import com.healthanalytics.android.di.initKoin
import com.healthanalytics.android.presentation.screens.onboard.OnboardViewModel
import org.koin.compose.viewmodel.koinViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initKoin()
        setContent {
//            HealthAnalyticsApp()

            var isLogin: Boolean by remember { mutableStateOf(false) }
            val onboardViewModel: OnboardViewModel = koinViewModel()

            if (isLogin) {
                OTPScreen(
                    phoneNumber = onboardViewModel.getPhoneNumber(),
                    onContinueClick = {
                        onboardViewModel.verifyOtp(it)
                    },
                    onResendClick = {
                        onboardViewModel.sendOTP(onboardViewModel.getPhoneNumber())
                    },
                    onBackClick = {
                        isLogin = false
                    }
                )
            } else {
                LoginScreen(
                    loginState = onboardViewModel.loginState,
                    onContinueClick = {
                        onboardViewModel.sendOTP(it)
                    },
                    navigateToOtpVerification = {
                        isLogin = true
                    }
                )
            }
        }
    }
}


@Preview
@Composable
fun AppAndroidPreview() {
    HealthAnalyticsApp()
}