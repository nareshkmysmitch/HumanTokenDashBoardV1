package com.healthanalytics.android.presentation.screens.marketplace

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.healthanalytics.android.BackHandler
import com.healthanalytics.android.data.api.Product
import com.healthanalytics.android.presentation.theme.*
import com.seiko.imageloader.rememberImagePainter
import org.koin.compose.koinInject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MarketPlaceScreen(
    modifier: Modifier = Modifier,
    viewModel: MarketPlaceViewModel = koinInject(),
    onProductClick: (Product) -> Unit = {},
    navigateBack: () -> Unit = {},
) {
    val uiState by viewModel.uiState.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedCategories by viewModel.selectedCategories.collectAsState()
    val currentSortOption by viewModel.sortOption.collectAsState()
    val filteredProducts by viewModel.filteredProducts.collectAsState(initial = emptyList())

    var showSortMenu by remember { mutableStateOf(false) }

    BackHandler { navigateBack() }
    LaunchedEffect(Unit) {
        viewModel.initializeMarketplace()
    }

    Column(
        modifier = modifier.fillMaxSize().background(AppColors.Black)
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth(), tonalElevation = 1.dp,
            color = AppColors.Black
        ) {
            Column(modifier = Modifier.fillMaxWidth().background(AppColors.Black)) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = viewModel::updateSearchQuery,
                        placeholder = { Text("Search products...") },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                        shape = MaterialTheme.shapes.medium
                    )

                    FilledIconButton(
                        onClick = { showSortMenu = true },
                        colors = IconButtonDefaults.filledIconButtonColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer
                        )
                    ) {
                        Icon(Icons.Default.Sort, contentDescription = "Sort")
                    }

                    DropdownMenu(
                        expanded = showSortMenu, onDismissRequest = { showSortMenu = false }) {
                        SortOption.entries.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option.displayName) }, onClick = {
                                    viewModel.updateSortOption(option)
                                    showSortMenu = false
                                }, leadingIcon = if (currentSortOption == option) {
                                    { Icon(Icons.Default.Check, contentDescription = null) }
                                } else null)
                        }
                    }
                }

                LazyRow(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(MarketPlaceViewModel.PRODUCT_CATEGORIES) { category ->
                        FilterChip(
                            selected = category in selectedCategories,
                            onClick = { viewModel.toggleCategory(category) },
                            label = { Text(category) },
                            leadingIcon = if (category in selectedCategories) {
                                {
                                    Icon(
                                        Icons.Default.Check,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            } else null)
                    }
                }
            }
        }

        Box(
            modifier = Modifier.fillMaxSize().padding(horizontal = 8.dp)
        ) {
            when (uiState) {
                is MarketPlaceUiState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                is MarketPlaceUiState.Success -> {
                    if (filteredProducts.isEmpty()) {
                        EmptyState(
                            modifier = Modifier.align(Alignment.Center)
                        )
                    } else {
                        LazyVerticalGrid(
                            columns = GridCells.Adaptive(minSize = 160.dp),
                            contentPadding = PaddingValues(vertical = 8.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(filteredProducts) { product ->
                                ProductCard(product = product, onProductClick = {
                                    onProductClick(product)
                                })
                            }
                        }
                    }
                }

                is MarketPlaceUiState.Error -> {
                    ErrorView(
                        message = (uiState as MarketPlaceUiState.Error).message,
                        onRetry = { viewModel.loadProducts() },
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
        }
    }
}

@Composable
private fun ProductCard(
    product: Product, onProductClick: (Product) -> Unit, modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth().clickable {
            onProductClick(product)
        },
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = AppColors.BlueBackground
        ),
    ) {
        Column {
            Box(
                modifier = Modifier.fillMaxWidth().aspectRatio(1f).background(
                    if (product.img_urls.isNullOrEmpty()) MaterialTheme.colorScheme.surfaceVariant
                    else MaterialTheme.colorScheme.surface
                )
            ) {
                product.img_urls?.firstOrNull()?.let { imageUrl ->
                    Image(
                        painter = rememberImagePainter(imageUrl),
                        contentDescription = product.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                product.rating?.let { rating ->
                    Surface(
                        color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.9f),
                        modifier = Modifier.padding(8.dp).align(Alignment.TopEnd),
                        shape = MaterialTheme.shapes.small
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                tint = Color(0xFFFFC107),
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = rating,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        }
                    }
                }
            }

            Column(
                modifier = Modifier.padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                product.name?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.titleSmall,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        color = AppColors.White
                    )
                }

                product.vendor?.name?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodySmall,
                        color = AppColors.White
                    )
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    product.price?.let { price ->
                        Text(
                            text = "₹$price",
                            style = MaterialTheme.typography.titleMedium,
                            color = AppColors.White,
                            fontFamily = FontFamily.bold()
                        )
                    }
                    product.mrp?.let { mrp ->
                        Text(
                            text = "₹$mrp",
                            style = MaterialTheme.typography.bodySmall,
                            textDecoration = TextDecoration.LineThrough,
                            color = AppColors.textSecondary
                        )
                    }
                }

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    product.tags?.filterNotNull()?.take(2)?.forEach { tag ->
                        Surface(
                            color = MaterialTheme.colorScheme.secondaryContainer,
                            shape = MaterialTheme.shapes.small
                        ) {
                            Text(
                                text = tag,
                                style = MaterialTheme.typography.labelSmall,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyState(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.SearchOff,
            contentDescription = null,
            modifier = Modifier.size(48.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "No products found",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun ErrorView(
    message: String, onRetry: () -> Unit, modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.error
        )
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = onRetry) {
            Text("Retry")
        }
    }
}

@Composable
fun AddToCartFab(
    onClick: () -> Unit, modifier: Modifier = Modifier
) {
    FloatingActionButton(
        onClick = onClick, modifier = modifier
    ) {
        Icon(
            imageVector = Icons.Default.Add, contentDescription = "Add to Cart"
        )
    }
} 