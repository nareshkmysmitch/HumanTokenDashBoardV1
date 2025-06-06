package com.healthanalytics.android.di


import com.healthanalytics.android.data.api.ApiService
import com.healthanalytics.android.data.api.ApiServiceImpl
import com.healthanalytics.android.data.network.NetworkConfig
import com.healthanalytics.android.presentation.screens.marketplace.MarketPlaceViewModel
import com.healthanalytics.android.presentation.screens.dashboard.DashboardViewModel
import com.healthanalytics.android.data.api.ChatService
import com.healthanalytics.android.data.api.ChatServiceImpl
import com.healthanalytics.android.presentation.screens.chat.ChatViewModel
import io.ktor.client.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.*
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val appModule = module {
    // HTTP Client
    single { NetworkConfig.createHttpClient() }

    // API Service
    single<ApiService> { ApiServiceImpl(get()) }
    single<ChatService> { ChatServiceImpl(get()) }

    // ViewModels
    factoryOf(::MarketPlaceViewModel)
    singleOf(::ChatViewModel)
}

fun initKoin() = org.koin.core.context.startKoin {
    modules(appModule)
} 