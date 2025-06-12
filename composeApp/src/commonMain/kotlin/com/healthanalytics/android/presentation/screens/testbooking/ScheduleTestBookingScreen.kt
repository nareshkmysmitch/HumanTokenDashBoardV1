package com.healthanalytics.android.presentation.screens.testbooking

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.humantoken.ui.screens.CartItem
import com.healthanalytics.android.BackHandler
import com.healthanalytics.android.presentation.screens.marketplace.CartListState
import com.healthanalytics.android.presentation.screens.marketplace.MarketPlaceViewModel
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleTestBookingScreen(onNavigateBack: () -> Unit, viewModel: MarketPlaceViewModel) {
    var cartItems by remember { mutableStateOf<List<CartItem>>(emptyList()) }
    var selectedDate by remember {
        mutableStateOf(
            Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
        )
    }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }

    val selectedAddress by viewModel.selectedAddress.collectAsState()

    // Initialize address fields from selectedAddress
    var address1 by remember(selectedAddress) {
        mutableStateOf(selectedAddress?.address?.address_line_1 ?: "")
    }
    var address2 by remember(selectedAddress) {
        mutableStateOf(selectedAddress?.address?.address_line_2 ?: "")
    }
    var city by remember(selectedAddress) {
        mutableStateOf(selectedAddress?.address?.city ?: "")
    }
    var state by remember(selectedAddress) {
        mutableStateOf(selectedAddress?.address?.state ?: "")
    }
    var pincode by remember(selectedAddress) {
        mutableStateOf(selectedAddress?.address?.pincode ?: "")
    }
    var country by remember(selectedAddress) {
        mutableStateOf(selectedAddress?.address?.country ?: "")
    }
    val getCartList by viewModel.cartListFlow.collectAsState()

    var totalPrice by remember { mutableStateOf(0.0) }

    LaunchedEffect(Unit) {
        viewModel.getCartList()
        viewModel.loadAddresses()
        viewModel.cartListFlow.collect { state ->
            when (state) {
                is CartListState.Success -> {
                    cartItems = state.cartList.flatMap { cart ->
                        cart.cart_items?.filter { it.product?.type == "non_product" } ?: emptyList()
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

    LaunchedEffect(getCartList) {
        when (getCartList) {
            is CartListState.Success -> {
                cartItems = (getCartList as CartListState.Success).cartList.flatMap { cart ->
                    cart.cart_items ?: emptyList()
                }
                isLoading = false
            }

            is CartListState.Error -> {
                error = (getCartList as CartListState.Error).message
                isLoading = false
            }

            is CartListState.Loading -> {
                isLoading = true
            }
        }
    }

    BackHandler(enabled = true, onBack = onNavigateBack)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Schedule Your Tests",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Black
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
                .padding(paddingValues)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                item {
                    Text(
                        text = "Choose your preferred date, and time",
                        color = Color.White.copy(alpha = 0.7f),
                        modifier = Modifier.padding(bottom = 24.dp)
                    )
                }

                // Selected Tests Section
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFF1A1A1A)
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = "Selected Tests",
                                color = Color.White,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(bottom = 16.dp)
                            )
                            println("cartItems --> $cartItems")
                            
                            // Calculate total price once
                            LaunchedEffect(cartItems) {
                                totalPrice = cartItems.sumOf { item ->
                                    item.product?.price?.toDoubleOrNull() ?: 0.0
                                }
                            }
                            
                            cartItems.forEach { item ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 8.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = item.product?.name ?: "",
                                        color = Color.White,
                                        modifier = Modifier.weight(1f)
                                    )
                                    Text(
                                        text = "₹${item.product?.price ?: "0.00"}",
                                        color = Color.White
                                    )
                                }
                            }
                            HorizontalDivider(
                                color = Color.White.copy(alpha = 0.1f),
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Total",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "₹$totalPrice",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                // Date Selection Section
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFF1A1A1A)
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = "Select Date",
                                color = Color.White,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(bottom = 16.dp)
                            )
                            OutlinedTextField(
                                value = selectedDate.toString(),
                                onValueChange = { },
                                readOnly = true,
                                trailingIcon = {
                                    Icon(
                                        Icons.Default.CalendarToday,
                                        contentDescription = "Select Date",
                                        tint = Color.White
                                    )
                                },
                                colors = OutlinedTextFieldDefaults.colors(
                                    unfocusedBorderColor = Color.White.copy(alpha = 0.3f),
                                    focusedBorderColor = Color.White,
                                    unfocusedTextColor = Color.White,
                                    focusedTextColor = Color.White
                                ),
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }

                // Collection Address Section
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFF1A1A1A)
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = "Collection Address",
                                color = Color.White,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                            Text(
                                text = "Where should we collect your blood sample?",
                                color = Color.White.copy(alpha = 0.7f),
                                fontSize = 14.sp,
                                modifier = Modifier.padding(bottom = 16.dp)
                            )

                            // Display the first address from the address list
                            Column {
                                Text(
                                    text = address1,
                                    color = Color.White
                                )
                                if (address2.isNotEmpty()) {
                                    Text(
                                        text = address2,
                                        color = Color.White
                                    )
                                }
                                if (city.isNotEmpty()) {
                                    Text(
                                        text = city,
                                        color = Color.White
                                    )
                                }
                                Row {
                                    if (state.isNotEmpty()) {
                                        Text(
                                            text = "$state - ",
                                            color = Color.White
                                        )
                                    }
                                    Text(
                                        text = pincode,
                                        color = Color.White
                                    )
                                }


                                Text(
                                    text = country,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            }

            // Confirm Button
            Button(
                onClick = { /* Handle appointment confirmation */ },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF8B5CF6)
                )
            ) {
                Text(
                    text = "Confirm Appointment",
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }
        }
    }
}