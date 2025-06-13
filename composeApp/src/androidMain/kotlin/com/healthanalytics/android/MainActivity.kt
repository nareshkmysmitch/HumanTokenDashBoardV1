package com.healthanalytics.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import com.healthanalytics.android.di.androidModule
import com.healthanalytics.android.presentation.HealthAnalyticsApp
import org.koin.core.context.loadKoinModules

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        loadKoinModules(androidModule(this@MainActivity))

        setContent {
            MaterialTheme {
                HealthAnalyticsApp()
            }
        }
    }
}

