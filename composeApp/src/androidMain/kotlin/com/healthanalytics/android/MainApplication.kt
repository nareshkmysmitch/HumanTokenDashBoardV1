package com.healthanalytics.android

import android.app.Application
import com.healthanalytics.android.di.initKoin

class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        initKoin()
    }
}