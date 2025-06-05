package com.healthanalytics.android.presentation.screens.marketplace

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.healthanalytics.android.data.api.ApiService
import com.healthanalytics.android.data.api.Product
import com.example.humantoken.ui.screens.Cart
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

sealed class MarketPlaceUiState {
    data object Loading : MarketPlaceUiState()
    data class Success(val products: List<Product?>) : MarketPlaceUiState()
    data class Error(val message: String) : MarketPlaceUiState()
}

sealed class CartListState {
    data object Loading : CartListState()
    data class Success(val cartList: List<Cart>) : CartListState()
    data class Error(val message: String) : CartListState()
}

sealed class CartActionState {
    data object Loading : CartActionState()
    data class Success(val message: String) : CartActionState()
    data class Error(val message: String) : CartActionState()
}

enum class SortOption(val displayName: String) {
    RATING_LOW_TO_HIGH("Low to High (Ratings)"),
    RATING_HIGH_TO_LOW("High to Low (Ratings)"),
    NAME_A_TO_Z("A to Z (Name)"),
    NAME_Z_TO_A("Z to A (Name)"),
    PRICE_LOW_TO_HIGH("Low to High (Price)"),
    PRICE_HIGH_TO_LOW("High to Low (Price)")
}

class MarketPlaceViewModel(
    private val apiService: ApiService
) : ViewModel() {

    private val _uiState = MutableStateFlow<MarketPlaceUiState>(MarketPlaceUiState.Loading)
    val uiState: StateFlow<MarketPlaceUiState> = _uiState.asStateFlow()

    private val _cartListState = MutableStateFlow<CartListState>(CartListState.Loading)
    val cartListFlow: StateFlow<CartListState> = _cartListState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _selectedCategories = MutableStateFlow<Set<String>>(emptySet())
    val selectedCategories = _selectedCategories.asStateFlow()

    private val _sortOption = MutableStateFlow<SortOption?>(null)
    val sortOption = _sortOption.asStateFlow()

    private val _allProducts = MutableStateFlow<List<Product?>>(emptyList())

    private val _cartActionState = MutableStateFlow<CartActionState>(CartActionState.Success(""))
    val cartActionState: StateFlow<CartActionState> = _cartActionState.asStateFlow()

    val filteredProducts = combine(
        _allProducts,
        _searchQuery,
        _selectedCategories,
        _sortOption
    ) { products, query, categories, sortOption ->
        var filtered = products.filterNotNull()
            .filter { product ->
                val matchesSearch = product.name?.contains(query, ignoreCase = true) ?: false ||
                        product.tags?.any { it?.contains(query, ignoreCase = true) ?: false } ?: false
                
                val matchesCategories = if (categories.isEmpty()) true else {
                    product.category?.any { it in categories } ?: false
                }
                
                matchesSearch && matchesCategories
            }

        // Apply sorting
        filtered = when (sortOption) {
            SortOption.RATING_LOW_TO_HIGH -> filtered.sortedBy { it.rating?.toDoubleOrNull() ?: 0.0 }
            SortOption.RATING_HIGH_TO_LOW -> filtered.sortedByDescending { it.rating?.toDoubleOrNull() ?: 0.0 }
            SortOption.NAME_A_TO_Z -> filtered.sortedBy { it.name ?: "" }
            SortOption.NAME_Z_TO_A -> filtered.sortedByDescending { it.name ?: "" }
            SortOption.PRICE_LOW_TO_HIGH -> filtered.sortedBy { it.price?.toDoubleOrNull() ?: 0.0 }
            SortOption.PRICE_HIGH_TO_LOW -> filtered.sortedByDescending { it.price?.toDoubleOrNull() ?: 0.0 }
            null -> filtered
        }
        
        filtered
    }

    // TODO: In a real app, get this from a secure storage or auth service
    private val dummyAccessToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX2lkIjoiNDM3OGVlYzItYTM4YS00MjAyLTk1Y2EtZDQwNGYwM2I5ZjlmIiwic2Vzc2lvbl9pZCI6IjIzN2RkOTAyLWZmZjYtNDJjNS1iYzlmLTkxY2Q2N2NhOGNmMSIsInVzZXJfaW50X2lkIjoiNzYiLCJwcm9maWxlX2lkIjoiNjUiLCJsZWFkX2lkIjoiY2QwOWJhOTAtMDI1ZC00OTI5LWI4MTMtNjI5MGUyNDU0NDI2IiwiaWF0IjoxNzQ5MDE3MTA2LCJleHAiOjE3NDk2MjE5MDZ9.5w7MbKkogQDfE-nv49P1BzWNa-7pPNLq5DoFK9rnCIc"

    init {
        loadProducts()
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun toggleCategory(category: String) {
        _selectedCategories.value = _selectedCategories.value.toMutableSet().apply {
            if (contains(category)) remove(category) else add(category)
        }
    }

    fun updateSortOption(option: SortOption) {
        _sortOption.value = option
    }

    fun loadProducts() {
        viewModelScope.launch {
            _uiState.value = MarketPlaceUiState.Loading
            try {
                val products = apiService.getProducts(dummyAccessToken)
                _allProducts.value = products ?: emptyList()
                _uiState.value = MarketPlaceUiState.Success(products ?: emptyList())
            } catch (e: Exception) {
                _uiState.value = MarketPlaceUiState.Error(e.message ?: "Unknown error occurred")
            }
        }
    }

    fun getCartList() {
        viewModelScope.launch {
            _cartListState.value = CartListState.Loading
            try {
                val cartList = apiService.getCartList(dummyAccessToken)
                if (cartList != null) {
                    _cartListState.value = CartListState.Success(cartList.filterNotNull())
                } else {
                    _cartListState.value = CartListState.Error("Failed to fetch cart items")
                }
            } catch (e: Exception) {
                _cartListState.value = CartListState.Error(e.message ?: "Unknown error occurred")
            }
        }
    }

    fun addToCart(productId: String, variantId: String = "0") {
        viewModelScope.launch {
            _cartActionState.value = CartActionState.Loading
            try {
                val response = apiService.addProduct(dummyAccessToken, productId, variantId)
                if (response != null) {
                    _cartActionState.value = CartActionState.Success(response.message ?: "Product added to cart")
                    // Refresh cart list after adding
                    getCartList()
                } else {
                    _cartActionState.value = CartActionState.Error("Failed to add product to cart")
                }
            } catch (e: Exception) {
                _cartActionState.value = CartActionState.Error(e.message ?: "Unknown error occurred")
            }
        }
    }

    fun updateCartItem(productId: String, quantity: String) {
        viewModelScope.launch {
            _cartActionState.value = CartActionState.Loading
            try {
                val response = apiService.updateProduct(dummyAccessToken, productId, quantity)
                if (response != null) {
                    _cartActionState.value = CartActionState.Success(response.message ?: "Cart updated successfully")
                    // Refresh cart list after updating
                    getCartList()
                } else {
                    _cartActionState.value = CartActionState.Error("Failed to update cart")
                }
            } catch (e: Exception) {
                _cartActionState.value = CartActionState.Error(e.message ?: "Unknown error occurred")
            }
        }
    }

    companion object {
        val PRODUCT_CATEGORIES = listOf("Product", "Blood", "Gene", "Gut")
    }
} 