package com.healthanalytics.android.presentation.screens.testbooking

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.humantoken.ui.screens.CartItem
import com.healthanalytics.android.BackHandler
import com.healthanalytics.android.data.api.Product
import com.healthanalytics.android.data.models.onboard.Slot
import com.healthanalytics.android.presentation.components.ShowDatePicker
import com.healthanalytics.android.presentation.screens.marketplace.CartListState
import com.healthanalytics.android.presentation.screens.marketplace.MarketPlaceViewModel
import com.healthanalytics.android.presentation.screens.onboard.TimeSlotCard
import com.healthanalytics.android.presentation.theme.AppColors
import com.healthanalytics.android.presentation.theme.AppTextStyles
import com.healthanalytics.android.presentation.theme.Dimensions
import com.healthanalytics.android.presentation.theme.FontFamily
import com.healthanalytics.android.utils.DateUtils
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow

class ScheduleTestBookingScreen(
    private val viewModel: MarketPlaceViewModel
) : Screen {

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        var cartItems by remember { mutableStateOf<List<CartItem>>(emptyList()) }
        var selectedDate by remember {
            mutableStateOf(Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date)
        }
        var isLoading by remember { mutableStateOf(true) }
        var error by remember { mutableStateOf<String?>(null) }

        val selectedAddress by viewModel.selectedAddress.collectAsState()

        val address1 = selectedAddress?.address?.address_line_1.orEmpty()
        val address2 = selectedAddress?.address?.address_line_2.orEmpty()
        val city = selectedAddress?.address?.city.orEmpty()
        val state = selectedAddress?.address?.state.orEmpty()
        val pincode = selectedAddress?.address?.pincode.orEmpty()
        val country = selectedAddress?.address?.country.orEmpty()

        val getCartList by viewModel.cartListFlow.collectAsState()

        val totalPrice by remember(cartItems) {
            derivedStateOf {
                cartItems.sumOf { it.product?.price?.toDoubleOrNull() ?: 0.0 }
            }
        }

        var showDatePicker by remember { mutableStateOf(false) }
        val slotState by viewModel.slotAvailability.collectAsStateWithLifecycle()
        var selectedTimeSlot by remember { mutableStateOf<Slot?>(null) }

        LaunchedEffect(Unit) {
            viewModel.getCartList()
            viewModel.loadAddresses()
        }

        LaunchedEffect(getCartList) {
            when (getCartList) {
                is CartListState.Success -> {
                    cartItems =
                        (getCartList as CartListState.Success).cartList.filter { it.type == "non_product" }
                            .flatMap { it.cart_items ?: emptyList() }
                    isLoading = false
                }

                is CartListState.Error -> {
                    error = (getCartList as CartListState.Error).message
                    isLoading = false
                }

                is CartListState.Loading -> isLoading = true
            }
        }

        BackHandler(enabled = true, onBack = { navigator.pop() })

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
                    }, navigationIcon = {
                        IconButton(onClick = { navigator.pop() }) {
                            Icon(
                                Icons.Default.ArrowBack,
                                contentDescription = "Back",
                                tint = Color.White
                            )
                        }
                    }, colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Black)
                )
            }) { paddingValues ->
            Box(
                modifier = Modifier.fillMaxSize().background(Color.Black).padding(paddingValues)
            ) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(16.dp)
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
                            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1A1A))
                        ) {
                            Column(Modifier.padding(16.dp)) {
                                Text(
                                    text = "Selected Tests",
                                    color = Color.White,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(bottom = 16.dp)
                                )
                                cartItems.forEach { item ->
                                    Row(
                                        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            item.product?.name.orEmpty(),
                                            color = Color.White,
                                            modifier = Modifier.weight(1f)
                                        )
                                        Text(
                                            "₹${item.product?.price ?: "0.00"}", color = Color.White
                                        )
                                    }
                                }
                                HorizontalDivider(
                                    color = Color.White.copy(alpha = 0.1f),
                                    modifier = Modifier.padding(vertical = 8.dp)
                                )
                                Row(
                                    Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Total", color = Color.White, fontWeight = FontWeight.Bold)
                                    Text(
                                        "₹$totalPrice",
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
                            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1A1A))
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    "Select Date",
                                    color = Color.White,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(bottom = 16.dp)
                                )
                                Box(
                                    modifier = Modifier.fillMaxWidth()
                                        .clip(RoundedCornerShape(Dimensions.cornerRadiusSmall))
                                        .background(Color.DarkGray)
                                        .clickable { showDatePicker = true }
                                        .padding(16.dp)) {
                                    Text(
                                        selectedDate.toString(),
                                        style = AppTextStyles.bodyMedium,
                                        color = AppColors.inputText
                                    )
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                // Slot Grid using Row and Column
                                val slots = slotState.data?.slots.orEmpty().chunked(2)
                                if (slots.isNotEmpty()) {
                                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                        slots.forEach { rowSlots ->
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                                            ) {
                                                rowSlots.forEach { timeSlot ->
                                                    val startLocalDateTime =
                                                        timeSlot.start_time?.let {
                                                            DateUtils.fromIsoFormat(it)
                                                        }
                                                    val startDateFormat =
                                                        DateUtils.formatForDisplay(
                                                            startLocalDateTime
                                                        )

                                                    Box(
                                                        modifier = Modifier.weight(1f)
                                                    ) {
                                                        TimeSlotCard(
                                                            time = startDateFormat,
                                                            isSelected = selectedTimeSlot == timeSlot
                                                        ) {
                                                            selectedTimeSlot = timeSlot
                                                        }
                                                    }
                                                }

                                                // Fill remaining space if only 1 item in this row
                                                if (rowSlots.size == 1) {
                                                    Spacer(modifier = Modifier.weight(1f))
                                                }
                                            }
                                        }
                                    }
                                } else if (slotState.data?.slots != null) {
                                    Text(
                                        text = "No slots available,",
                                        color = AppColors.textSecondary,
                                        fontFamily = FontFamily.semiBold(),
                                        fontSize = 14.sp
                                    )
                                }
                            }
                        }
                    }

                    // Address Section
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1A1A))
                        ) {
                            Column(Modifier.padding(16.dp)) {
                                Text(
                                    "Collection Address",
                                    color = Color.White,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(bottom = 8.dp)
                                )
                                Text(
                                    "Where should we collect your blood sample?",
                                    color = Color.White.copy(alpha = 0.7f),
                                    fontSize = 14.sp,
                                    modifier = Modifier.padding(bottom = 16.dp)
                                )
                                Column {
                                    Text(address1, color = Color.White)
                                    if (address2.isNotEmpty()) Text(address2, color = Color.White)
                                    if (city.isNotEmpty()) Text(city, color = Color.White)
                                    Row {
                                        if (state.isNotEmpty()) Text(
                                            "$state - ", color = Color.White
                                        )
                                        Text(pincode, color = Color.White)
                                    }
                                    Text(country, color = Color.White)
                                }
                            }
                        }
                    }

                    item { Spacer(modifier = Modifier.height(50.dp)) }
                }

                if (showDatePicker) {
                    ShowDatePicker(
                        selectedDate = selectedDate,
                        onDismiss = { showDatePicker = false },
                        onCancel = { showDatePicker = false },
                        onConfirm = {
                            selectedDate = it
                            showDatePicker = false
                            viewModel.getSlotAvailability(selectedDate.toString())
                        })
                }

                Button(
                    onClick = { /* Confirm action */ },
                    modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth().padding(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AppColors.PinkButton)
                ) {
                    Text(
                        "Confirm Appointment",
                        modifier = Modifier.padding(vertical = 4.dp),
                        color = Color.White
                    )
                }
            }
        }
    }
}

