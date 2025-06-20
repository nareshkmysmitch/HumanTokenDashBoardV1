package com.healthanalytics.android.presentation.screens.testbooking

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.humantoken.ui.screens.CartItem
import com.healthanalytics.android.data.api.Product
import com.healthanalytics.android.presentation.screens.marketplace.CartListState
import com.healthanalytics.android.presentation.screens.marketplace.MarketPlaceViewModel
import com.healthanalytics.android.presentation.theme.AppColors
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import kotlinx.coroutines.launch


class TestBookingScreen(
    private val viewModel: TestBookingViewModel,
    private val marketPlaceViewModel: MarketPlaceViewModel,
    //   onNavigateBack: () -> Unit,
) : Screen {

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val state by viewModel.state.collectAsState()
        val accessToken by marketPlaceViewModel.accessToken.collectAsState()
        var cartItems by remember { mutableStateOf<List<CartItem>>(emptyList()) }
        var isLoading by remember { mutableStateOf(true) }
        var error by remember { mutableStateOf<String?>(null) }
        val getCartList by marketPlaceViewModel.cartListFlow.collectAsState()
        val snackbarHostState = remember { SnackbarHostState() }
        val scope = rememberCoroutineScope()

        LaunchedEffect(Unit) {
            marketPlaceViewModel.getCartList()
        }

        LaunchedEffect(getCartList) {
            when (getCartList) {
                is CartListState.Success -> {
                    cartItems = (getCartList as CartListState.Success).cartList.flatMap { cart ->
                        cart.cart_items ?: emptyList()
                    }
                    accessToken?.let { viewModel.loadTests(it) }
                    isLoading = false
                }

                is CartListState.Error -> {
                    error = (getCartList as CartListState.Error).message
                    isLoading = false
                    scope.launch {
                        snackbarHostState.showSnackbar(
                            message = error ?: "An error occurred",
                            duration = androidx.compose.material3.SnackbarDuration.Short
                        )
                    }
                }

                is CartListState.Loading -> {
                    isLoading = true
                }
            }
        }

        Scaffold(topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Test Booking",
                        color = AppColors.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        try {
                            navigator.pop()
                        } catch (e: Exception) {
                            // Handle navigation error
                            scope.launch {
                                snackbarHostState.showSnackbar(
                                    message = "Navigation error: ${e.message}",
                                    duration = androidx.compose.material3.SnackbarDuration.Short
                                )
                            }
                        }
                    }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "back arrow",
                            tint = AppColors.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Black
                ),
            )
        }, snackbarHost = { SnackbarHost(snackbarHostState) }) { paddingValues ->
            Box(
                modifier = Modifier.fillMaxSize().background(Color.Black)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize().padding(16.dp)
                ) {
                    Spacer(Modifier.padding(paddingValues))
                    Text(
                        text = "Schedule comprehensive health screenings and diagnostic tests",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            color = Color.White.copy(alpha = 0.7f)
                        ),
                        modifier = Modifier.padding(bottom = 24.dp)
                    )

                    if (state.isLoading) {
                        Box(
                            modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = Color(0xFFF50057))
                        }
                    } else if (state.error != null) {
                        Box(
                            modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = state.error ?: "An error occurred",
                                color = Color.Red,
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    } else {
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(1),
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            items(state.availableTests) { test ->
                                val isInCart = cartItems.any { cartItem ->
                                    cartItem.product?.product_id == test.product_id
                                }
                                val updatedTest = test.copy(isAdded = isInCart)

                                LaunchedEffect(isInCart) {
                                    if (isInCart && !state.selectedTests.any { it.product_id == updatedTest.product_id }) {
                                        viewModel.toggleTestSelection(updatedTest)
                                    } else if (!isInCart) {
                                        viewModel.removeTest(updatedTest)
                                    }
                                }

                                TestCard(
                                    test = updatedTest, onSelect = {
                                        if (!updatedTest.isAdded) {
                                            marketPlaceViewModel.addToCart(
                                                updatedTest.product_id ?: "",
                                                updatedTest.variants?.firstOrNull()?.variant_id
                                                    ?: ""
                                            )
                                            scope.launch {
                                                snackbarHostState.showSnackbar(
                                                    message = "Test added to cart",
                                                    duration = androidx.compose.material3.SnackbarDuration.Short
                                                )
                                            }
                                        } else {
                                            marketPlaceViewModel.updateCartItem(
                                                updatedTest.product_id ?: "", "0"
                                            )
                                            viewModel.removeTest(updatedTest)
                                            scope.launch {
                                                snackbarHostState.showSnackbar(
                                                    message = "Test removed from cart",
                                                    duration = androidx.compose.material3.SnackbarDuration.Short
                                                )
                                            }
                                        }
                                    })
                            }
                        }
                        if (state.selectedTests.isNotEmpty()) {
                            Spacer(modifier = Modifier.padding(paddingValues))
                        }
                    }
                }

                // Bottom Bar
                if (state.selectedTests.isNotEmpty()) {
                    Surface(
                        modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth(),
                        color = Color(0xFF1A1A1A)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp).fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "${state.selectedTests.size} tests selected",
                                    color = Color.White,
                                    fontSize = 14.sp
                                )
                                Text(
                                    text = "Total: ₹${state.totalAmount.toFloat()}",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                )
                            }
                            Button(
                                onClick = {
                                    try {
                                        navigator.push(
                                            ScheduleTestBookingScreen(
                                                marketPlaceViewModel
                                            )
                                        )
                                    } catch (e: Exception) {
                                        scope.launch {
                                            snackbarHostState.showSnackbar(
                                                message = "Navigation error: ${e.message}",
                                                duration = androidx.compose.material3.SnackbarDuration.Short
                                            )
                                        }
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = AppColors.PinkButton
                                ),
                            ) {
                                Text("Schedule Tests", color = Color.White)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TestCard(
    test: Product, onSelect: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color(0xFF1A1A1A),
        shape = MaterialTheme.shapes.medium
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = test.name ?: "",
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = Color.White, fontWeight = FontWeight.Bold
                        ),
                        modifier = Modifier.padding(bottom = 4.dp, end = 12.dp),
                        maxLines = 2,
                    )
                    if (!test.description.isNullOrEmpty()) {
                        Text(
                            text = test.description,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = Color.White.copy(alpha = 0.7f)
                            ),
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }
                    Text(
                        text = "₹${test.price ?: "0.00"}",
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = Color.White, fontWeight = FontWeight.Bold
                        )
                    )
                    if (!test.vendor_name.isNullOrEmpty()) {
                        Text(
                            text = test.vendor_name,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = Color.White.copy(alpha = 0.5f)
                            ),
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
                IconButton(
                    onClick = onSelect, modifier = Modifier.size(32.dp).background(
                        color = if (test.isAdded) Color.Green.copy(alpha = 0.5f) else Color.White.copy(
                            alpha = 0.1f
                        ), shape = CircleShape
                    )
                ) {
                    Icon(
                        imageVector = if (!test.isAdded) Icons.Default.Add else Icons.Default.Check,
                        contentDescription = if (test.isAdded) "Remove test" else "Add test",
                        tint = Color.White
                    )
                }
            }
        }
    }
} 