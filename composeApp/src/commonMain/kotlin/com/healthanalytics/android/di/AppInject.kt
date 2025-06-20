package com.healthanalytics.android.di

import com.healthanalytics.android.data.api.ApiService
import com.healthanalytics.android.data.api.ApiServiceImpl
import com.healthanalytics.android.data.api.ChatApiService
import com.healthanalytics.android.data.api.ChatApiServiceImpl
import com.healthanalytics.android.data.network.NetworkConfig
import com.healthanalytics.android.data.repositories.PreferencesRepository
import com.healthanalytics.android.data.repository.BiomarkerRepository
import com.healthanalytics.android.data.repository.BiomarkerRepositoryImpl
import com.healthanalytics.android.presentation.screens.health.HealthDataViewModel
import com.healthanalytics.android.presentation.preferences.PreferencesViewModel
import com.healthanalytics.android.presentation.screens.chat.ChatViewModel
import com.healthanalytics.android.presentation.screens.health.BioMarkerReportViewModel
import com.healthanalytics.android.presentation.screens.recommendations.RecommendationsViewModel
import com.healthanalytics.android.presentation.screens.marketplace.MarketPlaceViewModel
import com.healthanalytics.android.presentation.screens.symptoms.SymptomsViewModel
import com.healthanalytics.android.presentation.screens.onboard.viewmodel.OnboardViewModel
import com.healthanalytics.android.presentation.screens.onboard.api.OnboardApiService
import com.healthanalytics.android.presentation.screens.onboard.api.OnboardApiServiceImpl
import com.healthanalytics.android.presentation.screens.questionnaire.QuestionnaireApiService
import com.healthanalytics.android.presentation.screens.questionnaire.QuestionnaireApiServiceImpl
import com.healthanalytics.android.presentation.screens.questionnaire.viewmodel.QuestionnaireViewModel
import com.healthanalytics.android.presentation.screens.testbooking.TestBookingViewModel
import com.healthanalytics.android.utils.KermitLogger
import kotlinx.serialization.json.Json
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module

val loggingModule = module {
    single { KermitLogger() }
}


val sharedModules = module {

    single { NetworkConfig.createHttpClient() }
    single<ApiService> { ApiServiceImpl(get()) }
    single<BiomarkerRepository> { BiomarkerRepositoryImpl(get()) }

    single<ChatApiService> { ChatApiServiceImpl(get()) }
    single<OnboardApiService> { OnboardApiServiceImpl(get()) }
    single<QuestionnaireApiService> { QuestionnaireApiServiceImpl(get()) }
    single<PreferencesRepository> { PreferencesRepository(get()) }

    factoryOf(::HealthDataViewModel)
    factoryOf(::MarketPlaceViewModel)
    factoryOf(::TestBookingViewModel)
    factoryOf(::PreferencesViewModel)
    factoryOf(::ChatViewModel)
    factoryOf(::RecommendationsViewModel)
    viewModelOf(::OnboardViewModel)
    viewModelOf(::SymptomsViewModel)
    viewModelOf(::QuestionnaireViewModel)
    factoryOf(::BioMarkerReportViewModel)

}

val serializationModule = module {
    single<Json> {
        Json {
            prettyPrint = true
            isLenient = true
            ignoreUnknownKeys = true
            explicitNulls = true
            encodeDefaults = true
        }
    }
}


expect val platformModules: Module

fun initKoin(config: KoinAppDeclaration? = null) {
    startKoin {
        config?.invoke(this)
        modules(sharedModules, platformModules, loggingModule,serializationModule)
    }
}