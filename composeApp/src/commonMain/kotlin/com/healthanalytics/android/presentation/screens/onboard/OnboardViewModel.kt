package com.healthanalytics.android.presentation.screens.onboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.healthanalytics.android.data.models.onboard.AccountCreation
import com.healthanalytics.android.data.models.onboard.AccountCreationResponse
import com.healthanalytics.android.data.models.onboard.AuthResponse
import com.healthanalytics.android.data.models.onboard.CommunicationAddress
import com.healthanalytics.android.data.models.onboard.OtpResponse
import com.healthanalytics.android.data.models.onboard.Slot
import com.healthanalytics.android.data.models.onboard.SlotRequest
import com.healthanalytics.android.data.models.onboard.SlotsAvailability
import com.healthanalytics.android.data.models.onboard.UpdateSlot
import com.healthanalytics.android.data.models.onboard.VerifyOtp
import com.healthanalytics.android.utils.Resource
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.TimeZone

class OnboardViewModel(
    val onboardApiService: OnboardApiService
) : ViewModel() {

    private var phoneNumber: String = ""
    private var mh: String = ""
    private var firstName: String = ""
    private var lastName: String = ""
    private var email: String = ""
    private var dob: String = ""
    private var selectedGender: String = ""
    private var weight: String = ""
    private var height: String = ""
    private var leadId = ""
//    private var leadId = "49e69e7e-d371-42a9-8ee5-61f00e46e514"

    private var communicationAddress: CommunicationAddress? = null
    private var otpVerifiedResponse: OtpResponse? = null

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
                    _otpVerifyState.emit(Resource.Success(response))
                } else {
                    _otpVerifyState.emit(Resource.Error(errorMessage = "Something went wrong..."))
                }
            } catch (_: Exception) {
                _otpVerifyState.emit(Resource.Error(errorMessage = "Something went wrong..."))
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
            gender = selectedGender.lowercase(),
            height = height,
            weight = weight,
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
            user_timezone = TimeZone.currentSystemDefault().toString()
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
            } catch (_: Exception) {
                _updateSlot.emit(Resource.Error(errorMessage = "Something went wrong..."))
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        println("onCleared...........")
    }
}