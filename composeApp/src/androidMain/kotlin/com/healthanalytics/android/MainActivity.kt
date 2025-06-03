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

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {

//            HealthAnalyticsApp()

            var isLogin: Boolean by remember { mutableStateOf(false) }

            if (isLogin) {
                OTPScreen(
                    phoneNumber = "9080706050"
                )
            } else {
                LoginScreen(
                    onContinueClick = {
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