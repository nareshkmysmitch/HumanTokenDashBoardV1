package com.healthanalytics.android.payment

//actual fun getRazorpayHandler(): RazorpayHandler = IOSRazorpayHandler()


actual fun startRazorpayFlow(
    amount: Int,
    currency: String,
    description: String,
    orderId: String,
    razorpayHandler: RazorpayHandler,
    listener: RazorpayResultListener
) {
    razorpayHandler.startPayment(
        amount, currency, description, orderId, listener
    )
}
