package com.healthanalytics.android.payment

interface RazorpayResultListener {
    fun onPaymentSuccess(paymentId: String?)
    fun onPaymentError(code: Int, message: String?)
}
