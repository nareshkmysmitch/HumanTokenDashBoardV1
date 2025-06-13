package com.healthanalytics.android.di

import com.healthanalytics.android.IOSNativeBridge
import com.healthanalytics.android.localstorage.createDataStore
import com.healthanalytics.android.payment.IOSRazorpayHandler
import com.healthanalytics.android.payment.RazorpayHandler
import org.koin.dsl.module

actual val platformModules = module {
    single {
        createDataStore()
    }

    single<RazorpayHandler> { IOSRazorpayHandler() }
}

