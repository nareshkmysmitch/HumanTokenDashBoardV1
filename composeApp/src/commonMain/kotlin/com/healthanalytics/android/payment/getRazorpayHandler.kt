package com.healthanalytics.android.payment

// commonMain
//expect fun getRazorpayHandler(): RazorpayHandler

expect fun startRazorpayFlow(
    amount: Int,
    currency: String,
    description: String,
    orderId: String,
    razorpayHandler: RazorpayHandler,
    listener: RazorpayResultListener
)
