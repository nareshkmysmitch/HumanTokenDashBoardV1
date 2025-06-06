package com.healthanalytics.android

import android.app.Application
import com.healthanalytics.android.di.initKoin
import org.koin.android.ext.koin.androidContext

class AndroidApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        initKoin {
            androidContext(this@AndroidApplication)
        }
    }
}