package com.healthanalytics.android.di

import com.healthanalytics.android.localstorage.createDataStore
import org.koin.dsl.module

actual val platformModules = module {
    single {
        createDataStore()
    }
}