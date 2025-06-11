package com.healthanalytics.android.payment

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.razorpay.Checkout
import com.razorpay.PaymentData
import com.razorpay.PaymentResultWithDataListener
import org.json.JSONObject

class RazorpayActivity : Activity(), PaymentResultWithDataListener {

    companion object {
        private var resultListener: RazorpayResultListener? = null

        fun launch(
            amount : Int,
            currency: String,
            description: String,
            orderId: String,
            context: Context,
            listener: RazorpayResultListener
        ) {
            resultListener = listener
            val intent = Intent(context, RazorpayActivity::class.java).apply {
                putExtra("amount", amount)
                putExtra("currency", currency)
                putExtra("description", description)
                putExtra("orderId", orderId)
            }
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val checkout = Checkout()
        checkout.setKeyID("rzp_test_cyBH5yEyn030HW")

        val options = JSONObject().apply {
            put("amount", intent.getIntExtra("amount", 0))
            put("currency", intent.getStringExtra("currency"))
            put("description", intent.getStringExtra("description"))
//            put("razorpay_order_id", intent.getStringExtra("orderId"))

//            put("prefill", JSONObject().apply {
//                put("email", intent.getStringExtra("email"))
//                put("contact", intent.getStringExtra("contact"))
//            })
        }

        println("options.....$options")

        checkout.open(this, options)
    }

    override fun onPaymentSuccess(razorpayPaymentId: String?, paymentData: PaymentData?) {
        println("orderId....${paymentData?.orderId}")
        println("paymentId....${paymentData?.paymentId}")
        println("signature....${paymentData?.signature}")

        resultListener?.onPaymentSuccess(paymentData?.paymentId)
        finish()
    }

    override fun onPaymentError(code: Int, description: String?, paymentData: PaymentData?) {
        resultListener?.onPaymentError(code, description)
        finish()
    }


}
