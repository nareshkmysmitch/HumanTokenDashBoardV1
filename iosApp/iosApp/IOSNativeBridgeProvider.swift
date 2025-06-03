//
//  Untitled.swift
//  iosApp
//
//  Created by Puvi on 02/06/25.
//

import SwiftUI
import ComposeApp

class IOSNativeBridgeProvider : IOSNativeBridge {
    
    func nativeDecrypt(data: String) -> String {
        CryptionHandler.shared.decryptString(data)
    }
    
    func nativeEncrypt(data: String) -> String {
        CryptionHandler.shared.encryptString(data)
    }

    
}
