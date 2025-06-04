package com.healthanalytics.android.presentation.screens.onboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.healthanalytics.android.data.models.onboard.AuthResponse
import com.healthanalytics.android.data.models.onboard.OtpResponse
import com.healthanalytics.android.data.models.onboard.VerifyOtp
import com.healthanalytics.android.utils.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class OnboardViewModel(
    val onboardApiService: OnboardApiService
) : ViewModel() {

    private var phoneNumber: String = ""
    private var mh: String = ""

    private val _loginState = MutableStateFlow<Resource<AuthResponse?>>(Resource.Loading())
    val loginState: StateFlow<Resource<AuthResponse?>> = _loginState

    private val _otpVerifyState = MutableStateFlow<Resource<OtpResponse?>>(Resource.Loading())
    val otpVerifyState: StateFlow<Resource<OtpResponse?>> = _otpVerifyState

    fun getPhoneNumber() = phoneNumber

    fun sendOTP(phoneNumber: String) {
        this.phoneNumber = phoneNumber
        viewModelScope.launch {
            try {
                val response = onboardApiService.sendOTP(phoneNumber)
                if (response?.mh?.isNotEmpty() == true) {
                    mh = response.mh
                    _loginState.value = Resource.Success(response)
                } else {
                    _loginState.value = Resource.Error(errorMessage = "Something went wrong...")
                }
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
                    _otpVerifyState.value = Resource.Success(response)
                } else {
                    _otpVerifyState.value = Resource.Error(errorMessage = "Something went wrong...")
                }
            } catch (_: Exception) {
                _otpVerifyState.value = Resource.Error(errorMessage = "Something went wrong...")
            }
        }
    }

}