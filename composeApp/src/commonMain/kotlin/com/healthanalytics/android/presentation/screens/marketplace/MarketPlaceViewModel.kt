package com.healthanalytics.android.presentation.screens.marketplace

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.healthanalytics.android.data.api.ApiService
import com.healthanalytics.android.data.api.Product
import com.healthanalytics.android.data.models.AddressItem
import com.healthanalytics.android.data.models.UpdateAddressListResponse
import com.healthanalytics.android.data.models.UpdateProfileRequest
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

enum class SortOption(val displayName: String) {
    RATING_LOW_TO_HIGH("Low to High (Ratings)"), RATING_HIGH_TO_LOW("High to Low (Ratings)"), NAME_A_TO_Z(
        "A to Z (Name)"
    ),
    NAME_Z_TO_A("Z to A (Name)"), PRICE_LOW_TO_HIGH("Low to High (Price)"), PRICE_HIGH_TO_LOW("High to Low (Price)")
}

class MarketPlaceViewModel(
    private val apiService: ApiService
) : ViewModel() {

    private val _uiState = MutableStateFlow<MarketPlaceUiState>(MarketPlaceUiState.Loading)
    val uiState: StateFlow<MarketPlaceUiState> = _uiState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _selectedCategories = MutableStateFlow<Set<String>>(emptySet())
    val selectedCategories = _selectedCategories.asStateFlow()

    private val _sortOption = MutableStateFlow<SortOption?>(null)
    val sortOption = _sortOption.asStateFlow()

    private val _allProducts = MutableStateFlow<List<Product?>>(emptyList())

    private val _addressList = MutableStateFlow<List<AddressItem>>(emptyList())
    val addressList = _addressList.asStateFlow()

    private val _selectedAddress = MutableStateFlow<AddressItem?>(null)
    val selectedAddress = _selectedAddress.asStateFlow()

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

    // TODO: In a real app, get this from a secure storage or auth service
    private val dummyAccessToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX2lkIjoiNTkzN2RiNzItNmVlMy00NTEwLTgzYjktM2UwNzI2MmRlNjQ5Iiwic2Vzc2lvbl9pZCI6IjNlNjNkY2U4LWJmY2ItNDY5Yi1hMDE1LWQ1ODRmMTVjNjRmZiIsInVzZXJfaW50X2lkIjoiNTc3IiwiaWF0IjoxNzQ5MTI5MTgyLCJleHAiOjE3NDk3MzM5ODJ9.dXgmh8whbL1IxEJSE_TAE9gxe1da-KFg2M87eWOXPU0"

    init {
        loadProducts()
        loadAddresses()
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

    fun loadAddresses() {
        viewModelScope.launch {
            try {
                val addressData = apiService.getAddresses(dummyAccessToken)
                if (addressData != null) {
                    _addressList.value = addressData.address_list
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
                val request = UpdateProfileRequest(
                    name = name,
                    email = email,
                    phone = phone,
                    address = address,
                    di_address_id = diAddressId
                )
                val response = apiService.updateProfile(dummyAccessToken, request)

                if (response?.message == "Profile updated successfully") {
                    callback(true, response.message)
                } else {
                    callback(false, response?.message ?: "Failed to update profile")
                }
            } catch (e: Exception) {
                println("Profile update error: ${e.message}")
                callback(false, e.message ?: "An error occurred")
            }
        }
    }

    companion object {
        val PRODUCT_CATEGORIES = listOf("Product", "Blood", "Gene", "Gut")
    }
} 