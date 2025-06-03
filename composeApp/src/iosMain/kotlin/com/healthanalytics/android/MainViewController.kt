package com.healthanalytics.android

import androidx.compose.ui.window.ComposeUIViewController
import com.healthanalytics.android.presentation.screens.marketplace.MarketPlaceScreen

fun MainViewController() = ComposeUIViewController { MarketPlaceScreen() }