//
//  RazorpayBridgeProvider.swift
//  iosApp
//
//  Created by Apple on 10/06/25.
//  Copyright © 2025 com.deepholistic. All rights reserved.
//

import SwiftUI
import ComposeApp
import Razorpay

class RazorpayNativeBridgeProvider : IOSRazorpay {
    func startPayment(amount: Int32, currency: String, description: String, orderId: String, callback: any RazorpayCallback) {
        RazorpayBridge.shared.startPayments(amount:Int(amount),currency: currency,description: description,orderId: orderId,callback: callback)
    }
}
