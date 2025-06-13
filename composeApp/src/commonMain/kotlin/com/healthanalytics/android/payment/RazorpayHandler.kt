package com.healthanalytics.android.payment

interface RazorpayHandler {
    fun startPayment(
        amount: Int,
        currency: String,
        description: String,
        orderId: String,
        listener: RazorpayResultListener
    )
}
