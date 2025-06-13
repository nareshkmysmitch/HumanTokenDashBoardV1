package com.healthanalytics.android.payment


lateinit var iosRazorpay: IOSRazorpay


class IOSRazorpayHandler : RazorpayHandler {

    private var resultListener: RazorpayResultListener? = null

    override fun startPayment(
        amount: Int,
        currency: String,
        description: String,
        orderId: String,
        listener: RazorpayResultListener
    ) {
        resultListener = listener
        iosRazorpay.startPayment(
            amount = amount,
            currency = currency,
            description = description,
            orderId = orderId,
            callback = object : RazorpayCallback{
                override fun onSuccess(paymentId: String?) {
                    resultListener?.onPaymentSuccess("")
                }

                override fun onError(code: Int, message: String?) {
                    resultListener?.onPaymentError(1,"")
                }
            }
        )
    }
}