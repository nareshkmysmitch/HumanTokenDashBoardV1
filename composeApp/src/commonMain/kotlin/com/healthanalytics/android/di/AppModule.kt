package com.healthanalytics.android.di

import com.healthanalytics.android.data.api.ApiService
import com.healthanalytics.android.data.api.ApiServiceImpl
import com.healthanalytics.android.data.network.NetworkConfig
import com.healthanalytics.android.presentation.dashboard.DashboardViewModel
import com.healthanalytics.android.presentation.health.HealthDataViewModel
import com.healthanalytics.android.presentation.screens.marketplace.MarketPlaceViewModel
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val appModule = module {
    // HTTP Client
    single { NetworkConfig.createHttpClient() }

    // API Service
    single<ApiService> { ApiServiceImpl(get()) }

    // ViewModels
    factoryOf(::DashboardViewModel)
    factoryOf(::HealthDataViewModel)
    factoryOf(::MarketPlaceViewModel)
}

fun initKoin() = org.koin.core.context.startKoin {
    modules(appModule)
} 