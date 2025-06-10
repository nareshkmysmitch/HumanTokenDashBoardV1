package com.example.humantoken.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.healthanalytics.android.BackHandler
import com.healthanalytics.android.data.api.Variant
import com.healthanalytics.android.presentation.screens.marketplace.CartListState
import com.healthanalytics.android.presentation.screens.marketplace.CartActionState
import com.healthanalytics.android.presentation.screens.marketplace.MarketPlaceViewModel
import com.seiko.imageloader.rememberImagePainter
import kotlinx.coroutines.flow.collectLatest
import kotlinx.serialization.Serializable
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel

@Serializable
data class Product(
    val id: String? = null,
    val product_id: String? = null,
    val name: String? = null,
    val description: String? = null,
    val category: List<String>? = null,
    val vendor_id: String? = null,
    val vendor_product_id: String? = null,
    val price: String? = null,
    val mrp: String? = null,
    val sku: String? = null,
    val stock: Int? = null,
    val img_urls: List<String>? = null,
    val tags: List<String>? = null,
    val rating: String? = null,
    val n_rating: Int? = null,
    val is_active: Boolean? = null,
    val type: String? = null,
    val created_at: String? = null,
    val updated_at: String? = null
)

@Serializable
data class CartItem(
    val id: String? = null,
    val cart_id: String? = null,
    val product_id: String? = null,
    val variant_id: String? = null,
    val quantity: Int? = null,
    val metadata: Map<String, String>? = emptyMap(),
    val created_at: String? = null,
    val updated_at: String? = null,
    val product: Product? = null,
    val variant: Variant? = null
)

@Serializable
data class Cart(
    val id: String? = null,
    val cart_id: String? = null,
    val user_id: String? = null,
    val status: String? = null,
    val type: String? = null,
    val created_at: String? = null,
    val updated_at: String? = null,
    val cart_items: List<CartItem>? = null
)

@Serializable
data class EncryptedResponse(
    val status: String,
    val message: String,
    val data: String // This will contain the encrypted data
)

@Serializable
data class CartResponse(
    val status: String? = null,
    val message: String? = null,
    val data: List<Cart>? = null
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(
    onBackClick: () -> Unit,
    onCheckoutClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: MarketPlaceViewModel = koinInject(),
) {
    var cartItems by remember { mutableStateOf<List<CartItem>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    var snackbarMessage by remember { mutableStateOf<String?>(null) }

    BackHandler(enabled = true) {
        onBackClick()
    }
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

    // Collect cart action state for showing feedback
    LaunchedEffect(Unit) {
        viewModel.cartActionState.collectLatest { state ->
            when (state) {
                is CartActionState.Success -> {
                    if (state.message.isNotEmpty()) {
                        snackbarMessage = state.message
                    }
                }
                is CartActionState.Error -> {
                    snackbarMessage = state.message
                }
                is CartActionState.Loading -> {
                    // Handle loading if needed
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "My Cart",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        },
        snackbarHost = {
            snackbarMessage?.let { message ->
                Snackbar(
                    modifier = Modifier.padding(16.dp),
                    action = {
                        TextButton(onClick = { snackbarMessage = null }) {
                            Text("Dismiss")
                        }
                    }
                ) {
                    Text(message)
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                error != null -> {
                    Text(
                        text = error ?: "An error occurred",
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(16.dp)
                    )
                }
                cartItems.isEmpty() -> {
                    EmptyCartMessage()
                }
                else -> {
                    LazyColumn(
                        modifier = modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(cartItems) { item ->
                            CartItemCard(
                                item = item,
                                onQuantityDecrease = {
                                    item.product?.let { product ->
                                        val newQuantity = (item.quantity ?: 1) - 1
//                                        if (newQuantity > 0) {
//                                            product.product_id?.let { viewModel.addToCart(it, item.variant_id ?: "0") }
//                                        }
                                        if (newQuantity > 0) {
                                            product.product_id?.let { viewModel.updateCartItem(it,
                                                newQuantity.toString()
                                            ) }
                                        }
                                    }
                                },
                                onQuantityIncrease = {
                                    item.product?.let { product ->
                                        val newQuantity = (item.quantity ?: 1) + 1
                                        if (newQuantity > 0) {
                                            product.product_id?.let { viewModel.updateCartItem(it,
                                                newQuantity.toString()
                                            ) }
                                        }
                                    }

                                },
                                onDeleteClick = {
                                    item.product?.product_id?.let { productId ->
                                        viewModel.updateCartItem(productId, "0")
                                    }
                                }
                            )
                        }

                        item {
                            Spacer(modifier = Modifier.height(16.dp))
                            OrderSummary(cartItems = cartItems)
                            Spacer(modifier = Modifier.height(16.dp))
                            CheckoutButton(
                                onClick = onCheckoutClick,
                                modifier = Modifier.padding(horizontal = 16.dp)
                            )
                            Spacer(modifier = Modifier.height(24.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyCartMessage(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Your cart is empty",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun CartItemCard(
    item: CartItem,
    onQuantityDecrease: () -> Unit,
    onQuantityIncrease: () -> Unit,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Product Image
            Image(
                painter = rememberImagePainter(item.product?.img_urls?.firstOrNull() ?: ""),
                contentDescription = item.product?.name,
                modifier = Modifier
                    .size(60.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )

            // Product Details
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = item.product?.name ?: "",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "₹${item.product?.price ?: "--"}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF2196F3)
                )
            }

            // Quantity Controls
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                IconButton(
                    onClick = onQuantityDecrease,
                    modifier = Modifier
                        .size(32.dp)
                        .background(Color(0xFF1C1B1F), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Remove,
                        contentDescription = "Decrease quantity",
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }

                Text(
                    text = item.quantity.toString(),
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.widthIn(min = 24.dp),
                    textAlign = TextAlign.Center
                )

                IconButton(
                    onClick = onQuantityIncrease,
                    modifier = Modifier
                        .size(32.dp)
                        .background(Color(0xFF1C1B1F), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Increase quantity",
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            // Delete Button
            IconButton(
                onClick = onDeleteClick,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete item",
                    tint = Color.Red
                )
            }
        }
    }
}

@Composable
private fun OrderSummary(
    cartItems: List<CartItem>,
    modifier: Modifier = Modifier
) {
    val subtotal = cartItems.sumOf { item ->
        item.quantity?.let { item.product?.price?.toDoubleOrNull()?.times(it) } ?: 0.0
    }
    val tax = subtotal * 0.18
    val total = subtotal + tax

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "ORDER SUMMARY",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

            OrderSummaryRow("Subtotal", "₹${formatPrice(subtotal)}")
            OrderSummaryRow("Tax (18%)", "₹${formatPrice(tax)}")
            OrderSummaryRow("Shipping", "Free", valueColor = Color.Green)
            HorizontalDivider()
            OrderSummaryRow(
                "Grand Total",
                "₹${formatPrice(total)}",
                titleStyle = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                valueStyle = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
            )
        }
    }
}

private fun formatPrice(price: Double): String {
    return buildString {
        append(((price * 100).toInt() / 100.0).toString())
        if (!contains('.')) append(".00")
        else if (split('.')[1].length == 1) append('0')
    }
}

@Composable
private fun OrderSummaryRow(
    title: String,
    value: String,
    titleStyle: TextStyle = MaterialTheme.typography.bodyMedium,
    valueStyle: TextStyle = MaterialTheme.typography.bodyMedium,
    valueColor: Color = MaterialTheme.colorScheme.onSurface
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = title, style = titleStyle)
        Text(text = value, style = valueStyle, color = valueColor)
    }
}

@Composable
private fun CheckoutButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF2196F3)
        ),
        shape = RoundedCornerShape(12.dp),
        contentPadding = PaddingValues(vertical = 16.dp)
    ) {
        Text(
            text = "Proceed to Checkout",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )
    }
} 