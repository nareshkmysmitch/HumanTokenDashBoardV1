package com.healthanalytics.android.presentation.screens.testbooking

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.humantoken.ui.screens.CartItem
import com.healthanalytics.android.BackHandler
import com.healthanalytics.android.data.api.Product
import com.healthanalytics.android.presentation.screens.marketplace.CartListState
import com.healthanalytics.android.presentation.screens.marketplace.MarketPlaceViewModel
import kotlinx.coroutines.flow.collectLatest


@Composable
fun ScheduleTestBookingScreen(
    onNavigateBack: () -> Unit,
    viewModel: MarketPlaceViewModel,
    localTestList: List<Product>,
) {
    var cartItems by remember { mutableStateOf<List<CartItem>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    LaunchedEffect(Unit) {
        viewModel.getCartList()
        viewModel.cartListFlow.collectLatest { state ->
            when (state) {
                is CartListState.Success -> {
                    cartItems = state.cartList.flatMap { cart ->
                        cart.cart_items ?: emptyList()
                    }
                    isLoading = false
                }
                is CartListState.Error -> {
                    error = state.message
                    isLoading = false
                }
                is CartListState.Loading -> {
                    isLoading = true
                }
            }
        }
    }
    BackHandler(enabled = true, onBack = onNavigateBack)
    println("localTestList --> $localTestList")

}