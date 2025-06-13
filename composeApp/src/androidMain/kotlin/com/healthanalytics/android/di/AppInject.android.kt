package com.healthanalytics.android.di

import android.app.Activity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.healthanalytics.android.AndroidApplication
import com.healthanalytics.android.localstorage.createDataStore
import com.healthanalytics.android.payment.AndroidRazorpayHandler
import com.healthanalytics.android.payment.RazorpayHandler
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

actual val platformModules = module {
    // Create and provide DataStore
    single<DataStore<Preferences>> {
        createDataStore(androidContext())
    }
}

fun androidModule(activity: Activity) = module {
    single<RazorpayHandler> { AndroidRazorpayHandler(activity) }
}