package com.example.humantoken.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material.icons.outlined.Assignment
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.LocalShipping
import androidx.compose.material.icons.outlined.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.healthanalytics.android.BackHandler
import com.healthanalytics.android.data.api.Product
import com.healthanalytics.android.presentation.screens.marketplace.CartActionState
import com.healthanalytics.android.presentation.screens.marketplace.MarketPlaceViewModel
import com.healthanalytics.android.presentation.screens.marketplace.ProductDetailsState
import com.healthanalytics.android.presentation.theme.AppColors
import com.healthanalytics.android.presentation.theme.FontFamily
import com.seiko.imageloader.rememberImagePainter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailScreen(
    product: Product,
    onNavigateBack: () -> Unit,
    onNavigateToCart: () -> Unit,
    viewModel: MarketPlaceViewModel,
) {
    val scrollState = rememberScrollState()
    var selectedTab by remember { mutableStateOf(1) }

    // Collect states
    val cartActionState by viewModel.cartActionState.collectAsState()
    val productDetailsState by viewModel.productDetailsState.collectAsState()
    var snackbarMessage by remember { mutableStateOf<String?>(null) }

    var buttonText by remember { mutableStateOf("Add to Cart") }

    // Fetch product details when the screen is first shown
    LaunchedEffect(Unit) {
        product.product_id?.let { viewModel.getProductDetails(it) }
    }

    // Handle cart action state changes
    LaunchedEffect(cartActionState) {
        when (cartActionState) {
            is CartActionState.Success -> {
                buttonText = "Successfully added to cart"
                snackbarMessage = (cartActionState as CartActionState.Success).message
            }

            is CartActionState.Error -> {
                buttonText = "Failed to add to cart \n Try again"
                snackbarMessage = (cartActionState as CartActionState.Error).message
            }

            else -> {}
        }
    }

    BackHandler(enabled = true, onBack = onNavigateBack)
    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = AppColors.Black,
                    navigationIconContentColor = AppColors.White,
                    titleContentColor = AppColors.White
                ),
                title = {
                    when (productDetailsState) {
                        is ProductDetailsState.Success -> {
                            (productDetailsState as ProductDetailsState.Success).product.name?.let {
                                Text(
                                    text = it,
                                    color = AppColors.White,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        else -> {
                            product.name?.let {
                                Text(
                                    text = it,
                                    color = AppColors.White,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { onNavigateBack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "back arrow",
                            tint = AppColors.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { onNavigateToCart() }) {
                        Icon(
                            imageVector = Icons.Default.ShoppingCart,
                            contentDescription = "shopping cart",
                            tint = AppColors.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                },
            )
        },
        containerColor = AppColors.Black,
    ) { paddingValues ->
        when (productDetailsState) {
            is ProductDetailsState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            is ProductDetailsState.Error -> {
                Box(
                    modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = (productDetailsState as ProductDetailsState.Error).message,
                        color = Color.Red
                    )
                }
            }

            is ProductDetailsState.Success -> {
                val currentProduct = (productDetailsState as ProductDetailsState.Success).product
                Column(
                    modifier = Modifier.fillMaxSize().padding(paddingValues)
                        .verticalScroll(scrollState)
                ) {
                    // Product Image
                    if (currentProduct.img_urls?.isNotEmpty() == true && currentProduct.img_urls.firstOrNull() != null) {
                        currentProduct.img_urls.firstOrNull()?.let {
                            Image(
                                painter = rememberImagePainter(it),
                                contentDescription = currentProduct.name,
                                contentScale = ContentScale.FillWidth,
                                modifier = Modifier.fillMaxWidth().height(300.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Product Info Section

                    currentProduct.vendor?.name?.let {
                        Text(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            text = it,
                            style = MaterialTheme.typography.bodySmall,
                            color = AppColors.textSecondary
                        )
                    }

                    currentProduct.name?.let {
                        Text(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            text = it,
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = AppColors.White
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))

                    val price = if (currentProduct.price != null) {
                        "₹${currentProduct.price}"
                    } else {
                        "--"
                    }
                    Text(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        text = price,
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold,
                        color = AppColors.White,
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Rating Section
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    ) {
                        repeat(5) {
                            Icon(
                                imageVector = Icons.Default.StarBorder,
                                contentDescription = null,
                                tint = AppColors.Yellow
                            )
                        }
                        val rating = if (currentProduct.rating != null) {
                            currentProduct.rating.toString()
                        } else {
                            "0"
                        }
                        val nRating = if (currentProduct.n_rating != null) {
                            currentProduct.n_rating.toString()
                        } else {
                            "0"
                        }
                        val ratingText = " ($rating) · $nRating reviews"
                        Text(
                            text = ratingText,
                            style = MaterialTheme.typography.bodyMedium,
                            color = AppColors.textSecondary,
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Tags:",
                        modifier = Modifier.padding(horizontal = 16.dp),
                        style = MaterialTheme.typography.bodyMedium,
                        color = AppColors.textSecondary,
                    )
                    Row(
                        Modifier.padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        currentProduct.tags?.forEach { tag ->
                            SuggestionChip(
                                onClick = { }, label = {
                                    tag?.replaceFirstChar { it.uppercase() }?.let { Text(it) }
                                }, colors = SuggestionChipDefaults.suggestionChipColors(
                                    containerColor = AppColors.Black,
                                    labelColor = AppColors.White,
                                )
                            )
                        }
                    }

                    /*
                                        Spacer(modifier = Modifier.height(24.dp))

                    // Quantity Selector
                    Row(
                        modifier = Modifier.padding(horizontal = 24.dp).border(
                            width = 1.dp,
                            color = Color(0xFF1C1B1F),
                            shape = RoundedCornerShape(12.dp)
                        ).clip(RoundedCornerShape(12.dp)),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier.clip(
                                RoundedCornerShape(topStart = 8.dp, bottomStart = 8.dp)
                            ).clickable(onClick = { if (quantity > 1) quantity-- })
                                                    .padding(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = "−",
                                fontSize = 20.sp,
                                color = Color.Black,
                                modifier = Modifier
                            )
                        }
                        Box(
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = quantity.toString(),
                                style = MaterialTheme.typography.titleMedium,
                                color = Color.Black
                            )
                        }
                        Box(
                            modifier = Modifier.clip(
                                RoundedCornerShape(topEnd = 8.dp, bottomEnd = 8.dp)
                                                )
                                .clickable(onClick = { currentProduct.stock?.let { if (quantity < it) quantity++ } })
                                .padding(horizontal = 8.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = "+",
                                fontSize = 20.sp,
                                color = Color.Black,
                                modifier = Modifier.padding(horizontal = 12.dp)
                            )
                        }
                    }*/

                    Spacer(modifier = Modifier.height(16.dp))

                    // Add to Cart Button
                    Button(
                        onClick = {
                            if (buttonText != "Successfully added to cart") {
                                val productId = currentProduct.product_id
                                val variantId = currentProduct.variants?.firstOrNull()?.variant_id
                                productId?.let { productId ->
                                    variantId?.let { variantId ->
                                        viewModel.addToCart(productId, variantId)
                                    }
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = AppColors.PinkButton
                        )
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                        ) {
                            Icon(
                                imageVector = Icons.Default.ShoppingCart,
                                contentDescription = null,
                                tint = AppColors.White
                            )
                            Text(
                                text = buttonText, modifier = Modifier.padding(vertical = 8.dp),
                                fontFamily = FontFamily.semiBold(),
                                color = AppColors.White
                            )
                        }
                    }


                    Spacer(modifier = Modifier.height(24.dp))

                    // Shipping Info
                    Column(
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        InfoRow(Icons.Outlined.LocalShipping, "Free shipping over $50")
                        InfoRow(Icons.Outlined.Verified, "Quality guaranteed")
                        InfoRow(Icons.Outlined.Assignment, "60-day return policy")
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Stock Indicator
                    currentProduct.stock?.let {
                        if (it > 0) {
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Outlined.CheckCircle,
                                    contentDescription = null,
                                    tint = Color.Green
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Only ${currentProduct.stock} left", color = AppColors.White)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Tabs Section
                    Column(modifier = Modifier.fillMaxWidth()) {
                        ScrollableTabRow(
                            selectedTabIndex = selectedTab, edgePadding = 16.dp,
                            containerColor = AppColors.BlueCardBackground,
                            contentColor = AppColors.White,
                            indicator = { tabPositions ->
                                TabRowDefaults.Indicator(
                                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                                    height = 2.dp,
                                    color = AppColors.White
                                )
                            }) {
                            listOf(
                                "Product Details", "Ingredients", "Directions", "Reviews"
                            ).forEachIndexed { index, title ->
                                Tab(
                                    selected = selectedTab == index,
                                    onClick = { selectedTab = index },
                                    text = {
                                        Text(
                                            text = title,
                                            style = MaterialTheme.typography.bodyLarge.copy(
                                                fontWeight = if (selectedTab == index) {
                                                    FontWeight.SemiBold
                                                } else {
                                                    FontWeight.Normal
                                                }
                                            ),
                                            color = if (selectedTab == index) {
                                                AppColors.White
                                            } else {
                                                AppColors.White.copy(alpha = 0.6f)
                                            }
                                        )
                                    })
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // Tab Content
                        when (selectedTab) {
                            0 -> {
                                // Product Details Tab
                                Column(
                                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
                                ) {
                                    Text(
                                        text = "Product Information",
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.SemiBold,
                                        color = AppColors.White
                                    )
                                    Spacer(modifier = Modifier.height(16.dp))
                                    ProductInfoRow("SKU:", currentProduct.sku ?: "")
                                    ProductInfoRow(
                                        "Category:", currentProduct.category?.firstOrNull() ?: ""
                                    )
                                    ProductInfoRow(
                                        "Stock:", "${currentProduct.stock ?: 0} available"
                                    )
                                }
                            }

                            1 -> {
                                // Ingredients Tab
                                Column(
                                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
                                ) {
                                    Text(
                                        text = "Product ingredients information will be available soon.",
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = AppColors.White
                                    )
                                }
                            }

                            2 -> {
                                // Directions Tab
                                Column(
                                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
                                ) {
                                    Text(
                                        text = "Usage directions will be available soon.",
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = AppColors.White
                                    )
                                }
                            }

                            3 -> {
                                // Reviews Tab
                                Column(
                                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "Customer Reviews",
                                            style = MaterialTheme.typography.titleLarge,
                                            fontWeight = FontWeight.SemiBold,
                                            color = AppColors.White
                                        )
                                        OutlinedButton(
                                            onClick = { },
                                            border = BorderStroke(1.dp, AppColors.White),
                                            shape = RoundedCornerShape(8.dp)
                                        ) {
                                            Text(
                                                text = "Write a Review", color = AppColors.textSecondary
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(16.dp))
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        repeat(5) {
                                            Icon(
                                                imageVector = Icons.Default.StarBorder,
                                                contentDescription = null,
                                                tint = Color.Yellow,
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }
                                        Text(
                                            text = " Based on 0 reviews",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = AppColors.textSecondary
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(32.dp))
                                    Box(
                                        modifier = Modifier.fillMaxWidth(),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Column(
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            verticalArrangement = Arrangement.spacedBy(16.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Message,
                                                contentDescription = null,
                                                tint = AppColors.White.copy(alpha = 0.6f),
                                                modifier = Modifier.size(48.dp)
                                            )
                                            Text(
                                                text = "No reviews yet. Be the first to review this product.",
                                                style = MaterialTheme.typography.bodyLarge,
                                                color = AppColors.White.copy(alpha = 0.6f),
                                                textAlign = TextAlign.Center
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun InfoRow(icon: ImageVector, text: String) {
    Row(
        modifier = Modifier.padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color.Green,
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = text,
            fontFamily = FontFamily.medium(),
            color = AppColors.textSecondary
        )
    }
}

@Composable
private fun ProductInfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp, horizontal = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            color = AppColors.textSecondary
        )
        Text(
            text = value, style = MaterialTheme.typography.bodyLarge, color = AppColors.textSecondary
        )
    }
} 