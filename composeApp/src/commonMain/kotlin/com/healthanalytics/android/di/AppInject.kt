package com.healthanalytics.android.di

import com.healthanalytics.android.data.api.ApiService
import com.healthanalytics.android.data.api.ApiServiceImpl
import com.healthanalytics.android.data.network.NetworkConfig
import com.healthanalytics.android.data.repositories.PreferencesRepository
import com.healthanalytics.android.presentation.health.HealthDataViewModel
import com.healthanalytics.android.presentation.preferences.PreferencesViewModel
import com.healthanalytics.android.presentation.recommendations.RecommendationsViewModel
import com.healthanalytics.android.presentation.screens.marketplace.MarketPlaceViewModel
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module

val sharedModules = module {
    single { NetworkConfig.createHttpClient() }
    single<ApiService> { ApiServiceImpl(get()) }
    single<PreferencesRepository> { PreferencesRepository(get()) }

    factoryOf(::HealthDataViewModel)
    factoryOf(::MarketPlaceViewModel)
    factoryOf(::PreferencesViewModel)
    factoryOf(::RecommendationsViewModel)
}

expect val platformModules: Module


fun initKoin(config: KoinAppDeclaration? = null) {
    startKoin {
        config?.invoke(this)
        modules(sharedModules, platformModules)
    }
}