package com.healthanalytics.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import com.healthanalytics.android.di.initKoin
import com.healthanalytics.android.presentation.HealthAnalyticsApp

class MainActivity : ComponentActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                HealthAnalyticsApp()
            }
        }
    }
}