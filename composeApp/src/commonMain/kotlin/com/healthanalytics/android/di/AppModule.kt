package com.healthanalytics.android.di

import com.healthanalytics.android.data.api.ApiService
import com.healthanalytics.android.data.api.ApiServiceImpl
import com.healthanalytics.android.data.network.NetworkConfig
import com.healthanalytics.android.presentation.dashboard.DashboardViewModel
import com.healthanalytics.android.presentation.screens.onboard.OnboardApiService
import com.healthanalytics.android.presentation.screens.onboard.OnboardApiServiceImpl
import com.healthanalytics.android.presentation.screens.onboard.OnboardViewModel
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module


val appModule = module {
    // HTTP Client
    single { NetworkConfig.createHttpClient() }

    // API Service
    single<ApiService> { ApiServiceImpl(get()) }
    single<OnboardApiService> { OnboardApiServiceImpl(get()) }

    // ViewModels
    factoryOf(::DashboardViewModel)
    factoryOf(::OnboardViewModel)
}


fun initKoin(config: KoinAppDeclaration? = null) {
    startKoin {
        config?.invoke(this)
        modules(appModule)
    }
}