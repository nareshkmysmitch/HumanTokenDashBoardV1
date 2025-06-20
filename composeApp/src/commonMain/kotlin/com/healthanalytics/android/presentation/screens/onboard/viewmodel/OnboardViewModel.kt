package com.healthanalytics.android.presentation.screens.onboard.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.healthanalytics.android.data.models.onboard.AccountCreation
import com.healthanalytics.android.data.models.onboard.AccountCreationResponse
import com.healthanalytics.android.data.models.onboard.AccountDetails
import com.healthanalytics.android.data.models.onboard.AuthResponse
import com.healthanalytics.android.data.models.onboard.CommunicationAddress
import com.healthanalytics.android.data.models.onboard.GenerateOrderId
import com.healthanalytics.android.data.models.onboard.GenerateOrderIdResponse
import com.healthanalytics.android.data.models.onboard.OnboardUiState
import com.healthanalytics.android.data.models.onboard.OtpResponse
import com.healthanalytics.android.data.models.onboard.PaymentRequest
import com.healthanalytics.android.data.models.onboard.Slot
import com.healthanalytics.android.data.models.onboard.SlotRequest
import com.healthanalytics.android.data.models.onboard.SlotsAvailability
import com.healthanalytics.android.data.models.onboard.UpdateSlot
import com.healthanalytics.android.data.models.onboard.VerifyOtp
import com.healthanalytics.android.data.repositories.PreferencesRepository
import com.healthanalytics.android.presentation.screens.onboard.api.OnboardApiService
import com.healthanalytics.android.utils.Resource
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.TimeZone

class OnboardViewModel(
    private val onboardApiService: OnboardApiService,
    private val preferencesRepository: PreferencesRepository,
) : ViewModel() {

    private var phoneNumber: String = ""
    private var countryCode: String = "+91"
    private var mh: String = ""
    private var leadId = ""
    private var accessToken = ""

    private var accountDetails: AccountDetails? = null
    private var communicationAddress: CommunicationAddress? = null
    private var otpVerifiedResponse: OtpResponse? = null
    private var generateOrderIdResponse: GenerateOrderIdResponse? = null

    private val _loginState = MutableSharedFlow<Resource<AuthResponse?>>()
    val loginState: SharedFlow<Resource<AuthResponse?>> = _loginState

    private val _otpVerifyState = MutableSharedFlow<Resource<OtpResponse?>>()
    val otpVerifyState: SharedFlow<Resource<OtpResponse?>> = _otpVerifyState

    private val _accountCreationState =
        MutableSharedFlow<Resource<AccountCreationResponse?>>()
    val accountCreationState: SharedFlow<Resource<AccountCreationResponse?>> = _accountCreationState

    private val _slotAvailability =
        MutableStateFlow<Resource<SlotsAvailability?>>(Resource.Loading())
    val slotAvailability: StateFlow<Resource<SlotsAvailability?>> = _slotAvailability

    private val _updateSlot = MutableSharedFlow<Resource<SlotsAvailability?>>()
    val updateSlot: SharedFlow<Resource<SlotsAvailability?>> = _updateSlot

    private val _onBoardUiState = MutableStateFlow(OnboardUiState())
    val onBoardUiState: StateFlow<OnboardUiState> = _onBoardUiState

    private val _paymentStatus = MutableSharedFlow<Resource<OtpResponse?>>()
    val paymentStatus: SharedFlow<Resource<OtpResponse?>> = _paymentStatus

    private val _accountDetailsState = MutableStateFlow(AccountDetails())
    val accountDetailsState: StateFlow<AccountDetails> = _accountDetailsState

    fun getPhoneNumber() = phoneNumber

    fun getCountryCode() = countryCode

    fun getGeneratedOrderDetail() = generateOrderIdResponse

    init {
        getAccessTokenFromDataStore()
    }

    fun updateOnBoardState() {
        _onBoardUiState.update {
            it.copy(
                isLoading = false,
                hasAccessToken = true
            )
        }
    }

    fun getAccessTokenFromDataStore() {
        viewModelScope.launch {
            preferencesRepository.accessToken
                .onStart {
                    _onBoardUiState.update {
                        it.copy(
                            isLoading = true
                        )
                    }
                }
                .catch { e ->
                    _onBoardUiState.update {
                        it.copy(
                            isLoading = true
                        )
                    }
                }
                .collect { accessToken ->
                    if (accessToken?.isNotEmpty() == true) {
                        _onBoardUiState.update {
                            it.copy(
                                isLoading = false,
                                hasAccessToken = true
                            )
                        }
                    } else {
                        _onBoardUiState.update {
                            it.copy(
                                isLoading = false,
                                hasAccessToken = false
                            )
                        }
                    }
                }
        }
    }

    fun getAccessToken() = accessToken

    fun getAccountDetails() = _accountDetailsState.value

    fun getAddressDetails() = communicationAddress

    fun saveAccountDetails(accountDetails: AccountDetails) {
        this.accountDetails = accountDetails
        updateAccountDetails(accountDetails)
    }

    fun updateAccountDetails(accountDetails: AccountDetails) {
        _accountDetailsState.value = accountDetails
    }

    fun updateAccountField(update: (AccountDetails) -> AccountDetails) {
        _accountDetailsState.value = update(_accountDetailsState.value)
    }

    fun sendOTP(phoneNumber: String) {
        this.phoneNumber = phoneNumber
        viewModelScope.launch {
            try {
                val response = onboardApiService.sendOTP(phoneNumber)
                if (response?.mh?.isNotEmpty() == true) {
                    mh = response.mh
                    _loginState.emit(Resource.Success(response))
                } else {
                    _loginState.emit(Resource.Error(errorMessage = "Something went wrong..."))
                }
            } catch (_: Exception) {
                _loginState.emit(Resource.Error(errorMessage = "Something went wrong..."))
            }
        }
    }

    fun resendOTP() {
        viewModelScope.launch {
            try {
                onboardApiService.sendOTP(phoneNumber)
            } catch (_: Exception) {
            }
        }
    }

    fun verifyOtp(otp: String) {
        viewModelScope.launch {
            try {
                val response = onboardApiService.verifyOTP(
                    VerifyOtp(
                        mh = mh,
                        country_code = "91",
                        otp = otp
                    )
                )
                if (response?.is_verified == true) {
                    otpVerifiedResponse = response
                    saveProfileDetailsInDataStore(otpVerifiedResponse)
                    _otpVerifyState.emit(Resource.Success(response))
                } else {
                    _otpVerifyState.emit(Resource.Error(errorMessage = "Something went wrong..."))
                }
            } catch (_: Exception) {
                _otpVerifyState.emit(Resource.Error(errorMessage = "Something went wrong..."))
            }
        }
    }

    fun saveProfileDetailsInDataStore(otpVerifiedResponse: OtpResponse?) {
        viewModelScope.launch {
            if (otpVerifiedResponse != null) {
                // Save access token and login state
                if (otpVerifiedResponse.access_token != null) {
                    preferencesRepository.saveAccessToken(otpVerifiedResponse.access_token)
                    preferencesRepository.saveIsLogin(true)
                }

                // Save user details from PiiUser
                otpVerifiedResponse.pii_user?.let { user ->
                    // Save basic user info
                    user.name?.let { preferencesRepository.saveUserName(it) }
                    user.email?.let { preferencesRepository.saveUserEmail(it) }
                    user.mobile?.let { preferencesRepository.saveUserPhone(it) }
                    user.gender?.let { preferencesRepository.saveGender(it) }

                    // Save communication address if available
                    user.communication_address?.let { address ->
                        address.address_line_1?.let { preferencesRepository.saveUserAddress(it) }
                        address.pincode?.let { preferencesRepository.saveUserPincode(it) }
                        address.state?.let { preferencesRepository.saveUserState(it) }
                        address.city?.let { preferencesRepository.saveUserDistrict(it) }
                        address.country?.let { preferencesRepository.saveUserCountry(it) }
                    }

                    // If communication address is not available, try billing address
                    if (user.communication_address == null) {
                        user.billing_address?.let { address ->
                            address.address_line_1?.let { preferencesRepository.saveUserAddress(it) }
                            address.pincode?.let { preferencesRepository.saveUserPincode(it) }
                            address.state?.let { preferencesRepository.saveUserState(it) }
                            address.city?.let { preferencesRepository.saveUserDistrict(it) }
                            address.country?.let { preferencesRepository.saveUserCountry(it) }
                        }
                    }
                }
            }
        }
    }

    fun createAccount(communicationAddress: CommunicationAddress) {
        this.communicationAddress = communicationAddress
        val accountCreation = AccountCreation(
            mobile = phoneNumber,
            first_name = accountDetails?.firstName ?: "",
            last_name = accountDetails?.lastName ?: "",
            email = accountDetails?.email ?: "",
            gender = accountDetails?.gender?.lowercase() ?: "",
            height = accountDetails?.height ?: "",
            weight = accountDetails?.weight ?: "",
            country_code = "91",
            communication_address = communicationAddress
        )

        viewModelScope.launch {
            try {
                val response = onboardApiService.createAccount(accountCreation)
                leadId = response?.lead_id ?: ""
                _accountCreationState.emit(Resource.Success(response))
            } catch (_: Exception) {
                _accountCreationState.emit(Resource.Error(errorMessage = "Something went wrong..."))
            }
        }
    }

    fun getSlotAvailability(selectedDate: String) {

        val slotRequest = SlotRequest(
            date = selectedDate,
            lead_id = leadId,
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

    fun updateSlot(selectedSlot: Slot) {
        val updateSlot = UpdateSlot(
            appointment_date = selectedSlot.start_time.toString(),
            source = "THYROCARE",
            lead_id = leadId
        )
        viewModelScope.launch {
            try {
                val response = onboardApiService.updateSlot(updateSlot)
                _updateSlot.emit(Resource.Success(response))
                generateOrderId()
            } catch (_: Exception) {
                _updateSlot.emit(Resource.Error(errorMessage = "Something went wrong..."))
            }
        }
    }

    fun generateOrderId() {
        //todo remove hardcoded values
        val generateOrderId = GenerateOrderId(
            lead_id = leadId,
            coupon_code = "null"
        )

//        "5d3c488f-db1f-445e-8054-8f161b28886f"

        viewModelScope.launch {
            try {
                val response = onboardApiService.generateOrderId(generateOrderId)
                if (response != null) {
                    generateOrderIdResponse = response
                    println("generateOrderId....$generateOrderIdResponse")
                }
            } catch (_: Exception) {
            }
        }
    }

    fun getPaymentStatus(orderId: String) {
        val paymentRequest = PaymentRequest(
            payment_order_id = orderId,
            lead_id = leadId,
            coupon_code = "null"
        )

        viewModelScope.launch {
            try {
                val response = onboardApiService.getPaymentStatus(paymentRequest)
                _paymentStatus.emit(Resource.Success(response))
            } catch (_: Exception) {
                _paymentStatus.emit(Resource.Error(errorMessage = "Something went wrong..."))
            }
        }
    }

    fun isAccountDetailsValid(accountDetails: AccountDetails): Boolean {
        val emailRegex = Regex("^[A-Za-z0-9+_.-]+@([A-Za-z0-9.-]+\\.[A-Za-z]{2,})$")
        return accountDetails.firstName.isNotEmpty() &&
                accountDetails.lastName.isNotEmpty() &&
                accountDetails.email.isNotEmpty() &&
                emailRegex.matches(accountDetails.email) &&
                accountDetails.dob != null &&
                accountDetails.gender.isNotEmpty() &&
                accountDetails.weight.isNotEmpty() &&
                accountDetails.height.isNotEmpty() &&
                accountDetails.streetAddress.isNotEmpty() &&
                accountDetails.city.isNotEmpty() &&
                accountDetails.state.isNotEmpty() &&
                accountDetails.zipCode.isNotEmpty()
    }

    override fun onCleared() {
        super.onCleared()
        println("onCleared...........")
    }
}