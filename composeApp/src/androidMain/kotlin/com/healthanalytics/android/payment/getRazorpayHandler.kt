package com.healthanalytics.android.payment

// androidMain

//@SuppressLint("ContextCastToActivity")
//actual fun getRazorpayHandler(): RazorpayHandler {
//    return AndroidRazorpayHandler(MainActivity())
//}


actual fun startRazorpayFlow(
    amount: Int,
    currency: String,
    description: String,
    orderId: String,
    razorpayHandler: RazorpayHandler,
    listener: RazorpayResultListener
) {
   razorpayHandler.startPayment(
       amount= amount,
       currency = currency,
       description = description,
       orderId = orderId,
       listener = listener
   )
}
