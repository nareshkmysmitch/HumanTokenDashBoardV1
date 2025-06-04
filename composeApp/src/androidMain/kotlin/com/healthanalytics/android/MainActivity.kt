package com.healthanalytics.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.healthanalytics.android.presentation.HealthAnalyticsApp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
//            App()
            HealthAnalyticsApp()
        }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
//    App()
    HealthAnalyticsApp()
}