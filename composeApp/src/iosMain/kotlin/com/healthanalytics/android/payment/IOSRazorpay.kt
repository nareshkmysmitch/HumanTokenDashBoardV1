package com.healthanalytics.android.payment

interface IOSRazorpay {
    fun startPayment(
        amount: Int,
        currency: String,
        description: String,
        orderId: String,
        callback: RazorpayCallback
    )
}

interface RazorpayCallback {
    fun onSuccess(paymentId: String?)
    fun onError(code: Int, message: String?)
}

