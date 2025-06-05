package com.healthanalytics.android.presentation.screens.onboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.healthanalytics.android.data.models.onboard.AccountCreation
import com.healthanalytics.android.data.models.onboard.AuthResponse
import com.healthanalytics.android.data.models.onboard.CommunicationAddress
import com.healthanalytics.android.data.models.onboard.OtpResponse
import com.healthanalytics.android.data.models.onboard.SlotRequest
import com.healthanalytics.android.data.models.onboard.SlotsAvailability
import com.healthanalytics.android.data.models.onboard.VerifyOtp
import com.healthanalytics.android.utils.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class OnboardViewModel(
    val onboardApiService: OnboardApiService
) : ViewModel() {

    private var phoneNumber: String = ""
     var mh: String = ""
    private var firstName: String = ""
    private var lastName: String = ""
    private var email: String = ""
    private var dob: String = ""
    private var selectedGender: String = ""
    private var weight: String = ""
    private var height: String = ""
    private var communicationAddress: CommunicationAddress? = null
    private var otpVerifiedResponse: OtpResponse? = null

    private val _loginState = MutableStateFlow<Resource<AuthResponse?>>(Resource.Loading())
    val loginState: StateFlow<Resource<AuthResponse?>> = _loginState

    private val _otpVerifyState = MutableStateFlow<Resource<OtpResponse?>>(Resource.Loading())
    val otpVerifyState: StateFlow<Resource<OtpResponse?>> = _otpVerifyState

    private val _accountCreationState = MutableStateFlow<Resource<OtpResponse?>>(Resource.Loading())
    val accountCreationState: StateFlow<Resource<OtpResponse?>> = _accountCreationState

    private val _slotAvailability = MutableStateFlow<Resource<SlotsAvailability?>>(Resource.Loading())
    val slotAvailability: StateFlow<Resource<SlotsAvailability?>> = _slotAvailability

    fun getPhoneNumber() = phoneNumber

    fun saveAccountDetails(firstName: String, lastName: String, email: String) {
        this.firstName = firstName
        this.lastName = lastName
        this.email = email
    }

    fun saveProfileDetails(
        selectedDate: String,
        selectedGender: String,
        weight: String,
        height: String
    ) {
        dob = selectedDate
        this.selectedGender = selectedGender
        this.weight = weight
        this.height = height
    }

    fun sendOTP(phoneNumber: String) {
        this.phoneNumber = phoneNumber
        viewModelScope.launch {
            try {
                val response = onboardApiService.sendOTP(phoneNumber)
                if (response?.mh?.isNotEmpty() == true) {
                    mh = response.mh

                    println("sendOTP..mh...$mh")
                    _loginState.value = Resource.Success(response)
                } else {
                    _loginState.value = Resource.Error(errorMessage = "Something went wrong...")
                }
            } catch (_: Exception) {
                _loginState.value = Resource.Error(errorMessage = "Something went wrong...")
            }
        }
    }

    fun resendOTP() {
        viewModelScope.launch {
            try {
                onboardApiService.sendOTP(phoneNumber)
            } catch (_: Exception) {
                _loginState.value = Resource.Error(errorMessage = "Something went wrong...")
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
                    println("otpVerifiedResponse....$response")
                    _otpVerifyState.value = Resource.Success(response)
                } else {
                    _otpVerifyState.value = Resource.Error(errorMessage = "Something went wrong...")
                }
            } catch (_: Exception) {
                _otpVerifyState.value = Resource.Error(errorMessage = "Something went wrong...")
            }
        }
    }

    fun createAccount(communicationAddress: CommunicationAddress) {
        this.communicationAddress = communicationAddress
        val accountCreation = AccountCreation(
            mobile = phoneNumber,
            first_name = firstName,
            last_name = lastName,
            email = email,
            gender = selectedGender,
            height = height,
            weight = weight,
            communication_address = communicationAddress
        )

        viewModelScope.launch {
            try {
                val response = onboardApiService.createAccount(accountCreation)
//                if (response?.is_verified == true) {
//                    _accountCreationState.value = Resource.Success(response)
//                } else {
//                    _accountCreationState.value = Resource.Error(errorMessage = "Something went wrong...")
//                }
            } catch (_: Exception) {
                _accountCreationState.value = Resource.Error(errorMessage = "Something went wrong...")
            }
        }
    }

    fun getSlotAvailability(slotRequest: SlotRequest){
        viewModelScope.launch {
            try {
                val response = onboardApiService.getSlotsAvailability(slotRequest)
                _slotAvailability.value = Resource.Success(response)
            } catch (_: Exception) {
                _slotAvailability.value = Resource.Error(errorMessage = "Something went wrong...")
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        println("onCleared...........")
    }
}