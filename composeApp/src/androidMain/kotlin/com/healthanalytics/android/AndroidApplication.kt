package com.healthanalytics.android

import android.app.Application
import com.healthanalytics.android.di.androidModule
import com.healthanalytics.android.di.initKoin
import com.healthanalytics.android.utils.appContext
import org.koin.android.ext.koin.androidContext

class AndroidApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        appContext = this
        initKoin {
            androidContext(this@AndroidApplication)
        }
    }
}