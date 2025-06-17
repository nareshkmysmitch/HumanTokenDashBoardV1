package com.healthanalytics.android.presentation.screens.marketplace

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.humantoken.ui.screens.Cart
import com.healthanalytics.android.data.api.ApiService
import com.healthanalytics.android.data.api.Product
import com.healthanalytics.android.data.models.Address
import com.healthanalytics.android.data.models.AddressItem
import com.healthanalytics.android.data.models.LoadingState
import com.healthanalytics.android.data.models.UpdateAddressListResponse
import com.healthanalytics.android.data.models.UpdateProfileRequest
import com.healthanalytics.android.data.models.onboard.SlotRequest
import com.healthanalytics.android.data.models.onboard.SlotsAvailability
import com.healthanalytics.android.data.models.profile.UploadCommunicationPreference
import com.healthanalytics.android.data.repositories.PreferencesRepository
import com.healthanalytics.android.presentation.screens.onboard.api.OnboardApiService
import com.healthanalytics.android.presentation.screens.profile.CommunicationStyle
import com.healthanalytics.android.utils.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.TimeZone

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

sealed class CommunicationPreferenceType(val type: String) {
    data object Biohacker : CommunicationPreferenceType("bio_hacker")
    data object Doctor : CommunicationPreferenceType("doctor")
    data object CloseFriend : CommunicationPreferenceType("friend")
}

sealed class ProductDetailsState {
    data object Loading : ProductDetailsState()
    data class Success(val product: Product) : ProductDetailsState()
    data class Error(val message: String) : ProductDetailsState()
}

sealed class LogoutState {
    data object Initial : LogoutState()
    data object Loading : LogoutState()
    data class Success(val message: String) : LogoutState()
    data class Error(val message: String) : LogoutState()
}

class MarketPlaceViewModel(
    private val apiService: ApiService,
    private val preferencesRepository: PreferencesRepository,
    private val onboardApiService: OnboardApiService,
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
    val accessToken: StateFlow<String?> = _accessToken.asStateFlow()

    private val _addressList = MutableStateFlow<List<AddressItem>>(emptyList())
    val addressList = _addressList.asStateFlow()

    private val _selectedAddress = MutableStateFlow<AddressItem?>(null)
    val selectedAddress = _selectedAddress.asStateFlow()

    private val _productDetailsState =
        MutableStateFlow<ProductDetailsState>(ProductDetailsState.Loading)
    val productDetailsState: StateFlow<ProductDetailsState> = _productDetailsState.asStateFlow()

    private val _leadId = MutableStateFlow<String?>(null)
    val leadId: StateFlow<String?> = _leadId.asStateFlow()

    // User Profile States
    private val _userName = MutableStateFlow<String?>(null)
    val userName: StateFlow<String?> = _userName.asStateFlow()

    private val _userEmail = MutableStateFlow<String?>(null)
    val userEmail: StateFlow<String?> = _userEmail.asStateFlow()

    private val _userPhone = MutableStateFlow<String?>(null)
    val userPhone: StateFlow<String?> = _userPhone.asStateFlow()

    // Address States from Preferences
    private val _cachedAddressLine1 = MutableStateFlow<String?>(null)
    val cachedAddressLine1: StateFlow<String?> = _cachedAddressLine1.asStateFlow()

    private val _cachedAddressLine2 = MutableStateFlow<String?>(null)
    val cachedAddressLine2: StateFlow<String?> = _cachedAddressLine2.asStateFlow()

    private val _cachedCity = MutableStateFlow<String?>(null)
    val cachedCity: StateFlow<String?> = _cachedCity.asStateFlow()

    private val _cachedState = MutableStateFlow<String?>(null)
    val cachedState: StateFlow<String?> = _cachedState.asStateFlow()

    private val _cachedPincode = MutableStateFlow<String?>(null)
    val cachedPincode: StateFlow<String?> = _cachedPincode.asStateFlow()

    private val _cachedCountry = MutableStateFlow<String?>(null)
    val cachedCountry: StateFlow<String?> = _cachedCountry.asStateFlow()

    private val _cachedAddressId = MutableStateFlow<String?>(null)
    val cachedAddressId: StateFlow<String?> = _cachedAddressId.asStateFlow()

    private val _logoutState = MutableStateFlow<LogoutState>(LogoutState.Initial)
    val logoutState: StateFlow<LogoutState> = _logoutState.asStateFlow()

    private val _communicationSelected =
        MutableStateFlow<CommunicationStyle>(CommunicationStyle.Doctor)
    val communicationSelected: StateFlow<CommunicationStyle?> = _communicationSelected.asStateFlow()

    var initialPreferenceValue: CommunicationStyle = CommunicationStyle.Doctor

    fun clearLogoutState() {
        _logoutState.value = LogoutState.Initial
    }

    // Helper function to convert UpdateAddressListResponse to Address
    private fun createAddress(response: UpdateAddressListResponse): Address {
        return Address(
            address = response.address_line_1 ?: "",
            pincode = response.pincode ?: "",
            address_line_1 = response.address_line_1 ?: "",
            address_line_2 = response.address_line_2,
            city = response.city ?: "",
            state = response.state ?: "",
            country = response.country ?: "",
            address_type = "communication"
        )
    }

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
                println("Access Token Updated: $token")
                _accessToken.value = token
            }
        }

        // Collect user details
        viewModelScope.launch {
            preferencesRepository.userName.collect { name ->
                println("User Name Updated: $name")
                _userName.value = name
            }
        }
        viewModelScope.launch {
            preferencesRepository.userEmail.collect { email ->
                println("User Email Updated: $email")
                _userEmail.value = email
            }
        }
        viewModelScope.launch {
            preferencesRepository.userPhone.collect { phone ->
                println("User Phone Updated: $phone")
                _userPhone.value = phone
            }
        }

        // Collect cached address
        viewModelScope.launch {
            preferencesRepository.addressLine1.collect { _cachedAddressLine1.value = it }
        }
        viewModelScope.launch {
            preferencesRepository.addressLine2.collect { _cachedAddressLine2.value = it }
        }
        viewModelScope.launch {
            preferencesRepository.city.collect { _cachedCity.value = it }
        }
        viewModelScope.launch {
            preferencesRepository.state.collect { _cachedState.value = it }
        }
        viewModelScope.launch {
            preferencesRepository.pincode.collect { _cachedPincode.value = it }
        }
        viewModelScope.launch {
            preferencesRepository.country.collect { _cachedCountry.value = it }
        }
        viewModelScope.launch {
            preferencesRepository.addressId.collect { _cachedAddressId.value = it }
        }

        viewModelScope.launch {
            preferencesRepository.leadId.collect { leadId ->
                println("Lead ID Updated: $leadId")
                _leadId.value = leadId
            }
        }
    }

    fun initializeMarketplace() {
        println("Initializing Marketplace")
        viewModelScope.launch {
            try {
                val token = _accessToken.value
                println("Current Token: $token")

                if (token != null) {
                    loadProducts()
                    loadAddresses()
                    getCartList()
                } else {
                    println("Token not available, waiting for token...")
                    // Wait for the first token value
                    preferencesRepository.accessToken.collect { newToken ->
                        if (newToken != null) {
                            println("Token received: $newToken")
                            _accessToken.value = newToken
                            loadProducts()
                            loadAddresses()
                            getCartList()
                            return@collect // Exit after first valid token
                        }
                    }
                }
            } catch (e: Exception) {
                println("Marketplace initialization error: ${e.message}")
                _uiState.value =
                    MarketPlaceUiState.Error(e.message ?: "Failed to initialize marketplace")
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
                println("Loading products with token: $token")
                if (token != null) {
                    val products = apiService.getProducts(token)
                    _allProducts.value = products ?: emptyList()
                    _uiState.value = MarketPlaceUiState.Success(products ?: emptyList())
                } else {
                    _uiState.value = MarketPlaceUiState.Error("Access token not available")
                }
            } catch (e: Exception) {
                println("Error loading products: ${e.message}")
                _uiState.value = MarketPlaceUiState.Error(e.message ?: "Unknown error occurred")
            }
        }
    }

    fun getCartList() {
        viewModelScope.launch {
            _cartListState.value = CartListState.Loading
            try {
                val token = _accessToken.value
                println("Loading cart with token: $token")
                if (token != null) {
                    val cartList = apiService.getCartList(token)
                    _cartListState.value =
                        CartListState.Success(cartList?.filterNotNull() ?: emptyList())
                } else {
                    _cartListState.value = CartListState.Error("Access token not available")
                }
            } catch (e: Exception) {
                println("Error loading cart: ${e.message}")
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
                        _cartActionState.value =
                            CartActionState.Error("Failed to add product to cart")
                    }
                } else {
                    _cartActionState.value = CartActionState.Error("Access token not available")
                }
            } catch (e: Exception) {
                _cartActionState.value =
                    CartActionState.Error(e.message ?: "Unknown error occurred")
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
                _cartActionState.value =
                    CartActionState.Error(e.message ?: "Unknown error occurred")
            }
        }
    }

    fun loadAddresses() {
        viewModelScope.launch {
            try {
                // First set the cached address if available
                if (_cachedAddressLine1.value != null) {
                    val cachedAddressResponse = UpdateAddressListResponse(
                        address_line_1 = _cachedAddressLine1.value,
                        address_line_2 = _cachedAddressLine2.value,
                        city = _cachedCity.value,
                        state = _cachedState.value,
                        pincode = _cachedPincode.value,
                        country = _cachedCountry.value,
                        di_address_id = _cachedAddressId.value
                    )

                    val cachedAddress = AddressItem(
                        address = createAddress(cachedAddressResponse),
                        address_id = _cachedAddressId.value ?: ""
                    )
                    _selectedAddress.value = cachedAddress
                    _addressList.value = listOf(cachedAddress)
                }

                // Then try to load from API
                val token = _accessToken.value
                println("Loading addresses with token: $token")
                if (token != null) {
                    val addresses = apiService.getAddresses(token)
                    println("Addresses response: $addresses")
                    if (addresses?.address_list?.isNotEmpty() == true) {
                        _addressList.value = addresses.address_list
                        // Select the first address as default if available
                        val communicationAddress = addresses.address_list.find {
                            it.address.address_type == "communication"
                        }
                        val selectedAddress = communicationAddress ?: addresses.address_list.first()
                        _selectedAddress.value = selectedAddress

                        // Save the new address to preferences
                        preferencesRepository.saveAddress(
                            addressLine1 = selectedAddress.address.address_line_1,
                            addressLine2 = selectedAddress.address.address_line_2,
                            city = selectedAddress.address.city,
                            state = selectedAddress.address.state,
                            pincode = selectedAddress.address.pincode,
                            country = selectedAddress.address.country,
                            addressId = selectedAddress.address_id
                        )
                    }
                } else {
                    println("Cannot load addresses: Token is null")
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
        callback: (Boolean, String) -> Unit,
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
                    _productDetailsState.value =
                        ProductDetailsState.Error("Access token not available")
                }
            } catch (e: Exception) {
                _productDetailsState.value =
                    ProductDetailsState.Error(e.message ?: "Unknown error occurred")
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            try {
                _logoutState.value = LogoutState.Loading
                val token = _accessToken.value
                if (token == null) {
                    _logoutState.value = LogoutState.Error("Access token not available")
                    return@launch
                }

                val success = apiService.logout(token)
                if (success) {
                    // Clear all preferences
                    preferencesRepository.clearAllPreferences()
                    _logoutState.value = LogoutState.Success("Logged out successfully")
                } else {
                    _logoutState.value = LogoutState.Error("Failed to logout")
                }
            } catch (e: Exception) {
                _logoutState.value = LogoutState.Error(e.message ?: "Unknown error occurred")
            }
        }
    }

    companion object {
        val PRODUCT_CATEGORIES = listOf("Product", "Blood", "Gene", "Gut")
    }


    private val _slotAvailability =
        MutableStateFlow<Resource<SlotsAvailability?>>(Resource.Loading())
    val slotAvailability: StateFlow<Resource<SlotsAvailability?>> = _slotAvailability

    fun getSlotAvailability(selectedDate: String) {

        val slotRequest = SlotRequest(
            date = selectedDate,
            lead_id = leadId.value ?: "",
            user_timezone = TimeZone.Companion.currentSystemDefault().toString()
        )

        viewModelScope.launch {
            try {
                val response = onboardApiService.getSlotsAvailability(slotRequest)
                _slotAvailability.value = Resource.Success(response)
            } catch (_: Exception) {
                _slotAvailability.value = Resource.Error(errorMessage = "Something went wrong...")
            }
        }
    }

    fun setCommunicationPreference(preference: CommunicationStyle) {
        viewModelScope.launch {
            _communicationSelected.emit(preference)
        }
    }

    private fun setInitialPreference(preference: CommunicationStyle) {
        initialPreferenceValue = preference
    }

    private val _uiCommunicationPreference = MutableStateFlow(LoadingState())
    val uiCommunicationPreference: StateFlow<LoadingState> =
        _uiCommunicationPreference.asStateFlow()


    fun loadCommunicationPreference(accessToken: String?) {
        viewModelScope.launch {
            try {
                _uiCommunicationPreference.update { it.copy(isLoading = true) }
                val preferenceResponse =
                    accessToken?.let { apiService.getCommunicationPreference(it) }
                val preference = preferenceResponse?.preference?.communication_preference
                    ?: CommunicationPreferenceType.Doctor.type //default
                when (preference) {
                    CommunicationPreferenceType.Biohacker.type -> {
                        val responsePreference = CommunicationStyle.Biohacker
                        setCommunicationPreference(responsePreference)
                        setInitialPreference(responsePreference)
                    }

                    CommunicationPreferenceType.Doctor.type -> {
                        val responsePreference = CommunicationStyle.Doctor
                        setCommunicationPreference(responsePreference)
                        setInitialPreference(responsePreference)
                    }

                    CommunicationPreferenceType.CloseFriend.type -> {
                        val responsePreference = CommunicationStyle.CloseFriend
                        setCommunicationPreference(responsePreference)
                        setInitialPreference(responsePreference)
                    }
                }
                _uiCommunicationPreference.update {
                    it.copy(
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _uiCommunicationPreference.update {
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Failed to load preference"
                    )
                }
            }
        }
    }

    fun saveCommunicationPreference(accessToken: String, preference: CommunicationStyle) {
        viewModelScope.launch {
            try {
                _uiCommunicationPreference.update { it.copy(isLoading = true) }
                val communicationPreference = UploadCommunicationPreference(
                    fields = listOf("communication_preference"),
                    communication_preference = preference.type
                )
                val preferenceResponse =
                    apiService.saveCommunicationPreference(accessToken, communicationPreference)

                if (preferenceResponse) {
                    setInitialPreference(preference)
                }
                _uiCommunicationPreference.update {
                    it.copy(
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _uiCommunicationPreference.update {
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Failed to uploaded preference"
                    )
                }
            }
        }
    }

} 