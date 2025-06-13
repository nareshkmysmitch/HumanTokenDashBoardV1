package com.healthanalytics.android.payment

import android.app.Activity


class AndroidRazorpayHandler(private val activity: Activity) : RazorpayHandler {
    override fun startPayment(
        amount: Int,
        currency: String,
        description: String,
        orderId: String,
        listener: RazorpayResultListener
    ) {
        RazorpayActivity.launch(
            amount = amount,
            currency = currency,
            description = description,
            orderId = orderId,
            context = activity,
            listener = listener
        )
    }
}

