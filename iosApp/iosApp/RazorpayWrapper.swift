//
//  RazorpayWrapper.swift
//  iosApp
//
//  Created by Apple on 10/06/25.
//  Copyright © 2025 com.deepholistic. All rights reserved.
//



import Foundation
import Razorpay
import ComposeApp
import UIKit

class RazorpayBridge: NSObject, RazorpayPaymentCompletionProtocol {
    var razorpay: RazorpayCheckout!
    
    static let shared = RazorpayBridge()
    private var callback: RazorpayCallback?


    func startPayments(amount: Int,currency:String,description:String,orderId:String,callback: RazorpayCallback) {
        razorpay = RazorpayCheckout.initWithKey("rzp_test_cyBH5yEyn030HW", andDelegate: self)
        self.callback = callback


        let options: [String:Any] = [
            "amount": amount,
            "currency": currency,
            "description": description,
            "order_id": orderId,
        ]

        DispatchQueue.main.async {
            if let rootVC = UIApplication.shared.keyWindow?.rootViewController {
                self.razorpay.open(options, displayController: rootVC)
            }
        }
    }

    func onPaymentSuccess(_ payment_id: String) {
        print("Payment Success: \(payment_id)")
        self.callback?.onSuccess(paymentId:payment_id)
    }

    func onPaymentError(_ code: Int32, description str: String) {
        print("Payment Error: \(code), \(str)")
        self.callback?.onError(code:code,message: "")
    }
}

