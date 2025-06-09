package com.healthanalytics.android.presentation.screens.marketplace

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.healthanalytics.android.data.api.ApiService
import com.healthanalytics.android.data.api.Product
import com.example.humantoken.ui.screens.Cart
import com.healthanalytics.android.data.models.AddressItem
import com.healthanalytics.android.data.models.UpdateAddressListResponse
import com.healthanalytics.android.data.models.UpdateProfileRequest
import com.healthanalytics.android.data.repositories.PreferencesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
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
    RATING_LOW_TO_HIGH("Low to High (Ratings)"), RATING_HIGH_TO_LOW("High to Low (Ratings)"), NAME_A_TO_Z(
        "A to Z (Name)"
    ),
    NAME_Z_TO_A("Z to A (Name)"), PRICE_LOW_TO_HIGH("Low to High (Price)"), PRICE_HIGH_TO_LOW("High to Low (Price)")
}

sealed class ProductDetailsState {
    data object Loading : ProductDetailsState()
    data class Success(val product: Product) : ProductDetailsState()
    data class Error(val message: String) : ProductDetailsState()
}

class MarketPlaceViewModel(
    private val apiService: ApiService,
    private val preferencesRepository: PreferencesRepository,
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

    private val _cartActionState = MutableStateFlow<CartActionState>(CartActionState.Loading)
    val cartActionState: StateFlow<CartActionState> = _cartActionState.asStateFlow()

    private val _accessToken = MutableStateFlow<String?>(null)

    private val _addressList = MutableStateFlow<List<AddressItem>>(emptyList())
    val addressList = _addressList.asStateFlow()

    private val _selectedAddress = MutableStateFlow<AddressItem?>(null)
    val selectedAddress = _selectedAddress.asStateFlow()

    private val _productDetailsState = MutableStateFlow<ProductDetailsState>(ProductDetailsState.Loading)
    val productDetailsState: StateFlow<ProductDetailsState> = _productDetailsState.asStateFlow()

    val filteredProducts = combine(
        _allProducts, _searchQuery, _selectedCategories, _sortOption
    ) { products, query, categories, sortOption ->
        var filtered = products.filterNotNull().filter { product ->
            val matchesSearch =
                product.name?.contains(query, ignoreCase = true) ?: false || product.tags?.any {
                    it?.contains(query, ignoreCase = true) ?: false
                } ?: false

            val matchesCategories = if (categories.isEmpty()) true else {
                product.category?.any { it in categories } ?: false
            }

            matchesSearch && matchesCategories
        }

        // Apply sorting
        filtered = when (sortOption) {
            SortOption.RATING_LOW_TO_HIGH -> filtered.sortedBy {
                it.rating?.toDoubleOrNull() ?: 0.0
            }

            SortOption.RATING_HIGH_TO_LOW -> filtered.sortedByDescending {
                it.rating?.toDoubleOrNull() ?: 0.0
            }

            SortOption.NAME_A_TO_Z -> filtered.sortedBy { it.name ?: "" }
            SortOption.NAME_Z_TO_A -> filtered.sortedByDescending { it.name ?: "" }
            SortOption.PRICE_LOW_TO_HIGH -> filtered.sortedBy { it.price?.toDoubleOrNull() ?: 0.0 }
            SortOption.PRICE_HIGH_TO_LOW -> filtered.sortedByDescending {
                it.price?.toDoubleOrNull() ?: 0.0
            }

            null -> filtered
        }

        filtered
    }

    init {
        viewModelScope.launch {
            preferencesRepository.accessToken.collect { token ->
                _accessToken.value = token
            }
        }
    }

    // New function to initialize marketplace data
    fun initializeMarketplace() {
        viewModelScope.launch {
            try {
                val token = preferencesRepository.accessToken.first()
                if (token != null) {
                    _accessToken.value = token
                    loadProducts()
                    loadAddresses()
                    getCartList()
                } else {
                    _uiState.value = MarketPlaceUiState.Error("Access token not available")
                }
            } catch (e: Exception) {
                _uiState.value = MarketPlaceUiState.Error(e.message ?: "Failed to initialize marketplace")
            }
        }
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
                val token = _accessToken.value
                if (token != null) {
                    val products = apiService.getProducts(token)
                    _allProducts.value = products ?: emptyList()
                    _uiState.value = MarketPlaceUiState.Success(products ?: emptyList())
                } else {
                    _uiState.value = MarketPlaceUiState.Error("Access token not available")
                }
            } catch (e: Exception) {
                _uiState.value = MarketPlaceUiState.Error(e.message ?: "Unknown error occurred")
            }
        }
    }

    fun getCartList() {
        viewModelScope.launch {
            _cartListState.value = CartListState.Loading
            try {
                val token = _accessToken.value
                if (token != null) {
                    val cartList = apiService.getCartList(token)
                    _cartListState.value = CartListState.Success(cartList?.filterNotNull() ?: emptyList())
                } else {
                    _cartListState.value = CartListState.Error("Access token not available")
                }
            } catch (e: Exception) {
                _cartListState.value = CartListState.Error(e.message ?: "Unknown error occurred")
            }
        }
    }

    fun addToCart(productId: String, variantId: String) {
        viewModelScope.launch {
            _cartActionState.value = CartActionState.Loading
            try {
                val token = _accessToken.value
                if (token != null) {
                    val response = apiService.addProduct(token, productId, variantId)
                    if (response != null) {
                        _cartActionState.value = CartActionState.Success(response.message)
                        getCartList()
                    } else {
                        _cartActionState.value = CartActionState.Error("Failed to add product to cart")
                    }
                } else {
                    _cartActionState.value = CartActionState.Error("Access token not available")
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
                val token = _accessToken.value
                if (token != null) {
                    val response = apiService.updateProduct(token, productId, quantity)
                    if (response != null) {
                        _cartActionState.value = CartActionState.Success(response.message)
                        getCartList()
                    } else {
                        _cartActionState.value = CartActionState.Error("Failed to update cart item")
                    }
                } else {
                    _cartActionState.value = CartActionState.Error("Access token not available")
                }
            } catch (e: Exception) {
                _cartActionState.value = CartActionState.Error(e.message ?: "Unknown error occurred")
            }
        }
    }

    fun loadAddresses() {
        viewModelScope.launch {
            try {
                val token = _accessToken.value
                if (token != null) {
                    val addresses = apiService.getAddresses(token)
                    _addressList.value = addresses?.address_list ?: emptyList()
                    // Select the first address as default if available
                    if (_addressList.value.isNotEmpty()) {
                        // Prefer communication address if available, otherwise use the first one
                        val communicationAddress = _addressList.value.find {
                            it.address.address_type == "communication"
                        }
                        _selectedAddress.value = communicationAddress ?: _addressList.value.first()
                    }
                }
            } catch (e: Exception) {
                println("Error loading addresses: ${e.message}")
            }
        }
    }

    fun updateProfile(
        name: String,
        email: String,
        phone: String,
        diAddressId: String,
        address: UpdateAddressListResponse,
        callback: (Boolean, String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val token = _accessToken.value
                if (token != null) {
                    val request = UpdateProfileRequest(
                        name = name,
                        email = email,
                        phone = phone,
                        address = address,
                        di_address_id = diAddressId
                    )
                    val response = apiService.updateProfile(token, request)

                    if (response?.message == "Profile updated successfully") {
                        callback(true, response.message)
                    } else {
                        callback(false, response?.message ?: "Failed to update profile")
                    }
                } else {
                    callback(false, "Access token not available")
                }
            } catch (e: Exception) {
                println("Profile update error: ${e.message}")
                callback(false, e.message ?: "An error occurred")
            }
        }
    }

    fun getProductDetails(productId: String) {
        viewModelScope.launch {
            _productDetailsState.value = ProductDetailsState.Loading
            try {
                val token = _accessToken.value
                if (token != null) {
                    val product = apiService.getProductDetails(token, productId)
                    if (product != null) {
                        _productDetailsState.value = ProductDetailsState.Success(product)
                    } else {
                        _productDetailsState.value = ProductDetailsState.Error("Product not found")
                    }
                } else {
                    _productDetailsState.value = ProductDetailsState.Error("Access token not available")
                }
            } catch (e: Exception) {
                _productDetailsState.value = ProductDetailsState.Error(e.message ?: "Unknown error occurred")
            }
        }
    }

    companion object {
        val PRODUCT_CATEGORIES = listOf("Product", "Blood", "Gene", "Gut")
    }
} 