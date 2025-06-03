//
//  CryptionHandler.swift
//  SmartHome
//
//  Created by Saravanan B on 13/08/20.
//  Copyright © 2020 Smitch. All rights reserved.
//

import Foundation
import UIKit
import CommonCrypto

// MARK: - AES Cryption for String-based Encryption & Decryption
//class CryptionHandler: NSObject {
//    private let aesKey = "4xlmO9YC71qv28qfWHJ4LVEYm6gNNuYi"  // 32-byte key for AES-256
//    private let aesId = "lTayxxaZZfGhRs3j"                 // 16-byte IV for CBC
//
//    // Singleton
//    static let shared = CryptionHandler()
//
//    /// Encrypt a plain text string using AES
//    func encryptString(_ input: String) -> String {
//        return input.aesEncrypt(key: aesKey, initializationVector: aesId) ?? ""
//    }
//
//    /// Decrypt a base64-encoded AES string to plain text
//    func decryptString(_ input: String) -> String {
//        return input.aesDecrypt(key: aesKey, initializationVector: aesId) ?? ""
//    }
//}


class CryptionHandler: NSObject {
    private let aesKey = "4xlmO9YC71qv28qfWHJ4LVEYm6gNNuYi"
    private let aesId = "lTayxxaZZfGhRs3j"

    static let shared = CryptionHandler()

    func encryptString(_ input: String) -> String {
        return input.aesEncrypt(key: aesKey, initializationVector: aesId) ?? ""
    }

    func decryptString(_ input: String) -> String {
        
        return input.aesDecrypt(key: aesKey, initializationVector: aesId) ?? ""
    }
}

    extension String {
        func aesEncrypt(key: String, initializationVector: String, options: Int = kCCOptionPKCS7Padding) -> String? {
            guard
                let keyData = key.data(using: .utf8),
                let data = self.data(using: .utf8),
                let cryptData = NSMutableData(length: data.count + kCCBlockSizeAES128)
            else { return nil }

            let keyLength = kCCKeySizeAES256
            let algorithm = CCAlgorithm(kCCAlgorithmAES)
            var numBytesEncrypted: size_t = 0

            let status = CCCrypt(CCOperation(kCCEncrypt),
                                 algorithm,
                                 CCOptions(options),
                                 (keyData as NSData).bytes, keyLength,
                                 initializationVector,
                                 (data as NSData).bytes, data.count,
                                 cryptData.mutableBytes, cryptData.length,
                                 &numBytesEncrypted)

            if status == kCCSuccess {
                cryptData.length = Int(numBytesEncrypted)
                let base64 = cryptData.base64EncodedString(options: [])
                return base64.filter { !$0.isWhitespace && !$0.isNewline }
            }

            return nil
        }

        func aesDecrypt(key: String, initializationVector: String, options: Int = kCCOptionPKCS7Padding) -> String? {
            guard
                let keyData = key.data(using: .utf8),
                let data = Data(base64Encoded: self, options: .ignoreUnknownCharacters),
                let cryptData = NSMutableData(length: data.count + kCCBlockSizeAES128)
            else { return nil }

            let keyLength = kCCKeySizeAES256
            let algorithm = CCAlgorithm(kCCAlgorithmAES)
            var numBytesDecrypted: size_t = 0

            let status = CCCrypt(CCOperation(kCCDecrypt),
                                 algorithm,
                                 CCOptions(options),
                                 (keyData as NSData).bytes, keyLength,
                                 initializationVector,
                                 (data as NSData).bytes, data.count,
                                 cryptData.mutableBytes, cryptData.length,
                                 &numBytesDecrypted)

            if status == kCCSuccess {
                cryptData.length = Int(numBytesDecrypted)
                return String(data: cryptData as Data, encoding: .utf8)
            }

            return nil
        }
    }


//extension String {
//    // MARK: - Encrypt the parameter string
//    func aesEncrypt(key: String, initializationVector: String, options: Int = kCCOptionPKCS7Padding) -> String? {
//        if let keyData = key.data(using: String.Encoding.utf8),
//            let data = self.data(using: String.Encoding.utf8),
//            let cryptData    = NSMutableData(length: Int((data.count)) + kCCBlockSizeAES128) {
//            
//            let keyLength              = size_t(kCCKeySizeAES256)
//            let operation: CCOperation = UInt32(kCCEncrypt)
//            let algoritm: CCAlgorithm = UInt32(kCCAlgorithmAES128)
//            let options: CCOptions   = UInt32(options)
//            
//            var numBytesEncrypted: size_t = 0
//            
//            let cryptStatus = CCCrypt(operation,
//                                      algoritm,
//                                      options,
//                                      (keyData as NSData).bytes, keyLength,
//                                      initializationVector,
//                                      (data as NSData).bytes, data.count,
//                                      cryptData.mutableBytes, cryptData.length,
//                                      &numBytesEncrypted)
//            
//            if UInt32(cryptStatus) == UInt32(kCCSuccess) {
//                cryptData.length = Int(numBytesEncrypted)
//                let base64cryptString = cryptData.base64EncodedString(options: .lineLength64Characters)
//                return base64cryptString.filter { !$0.isNewline && !$0.isWhitespace }
//            } else {
//                return nil
//            }
//        }
//        return nil
//    }
//    
//    // MARK: - Decrypt the response
//    func aesDecrypt(key: String, initializationVector: String, options: Int = kCCOptionPKCS7Padding) -> String? {
//        if let keyData = key.data(using: String.Encoding.utf8),
//            let data = NSData(base64Encoded: self, options: .ignoreUnknownCharacters),
//            let cryptData = NSMutableData(length: Int((data.length)) + kCCBlockSizeAES128) {
//            
//            let keyLength              = size_t(kCCKeySizeAES256)
//            let operation: CCOperation = UInt32(kCCDecrypt)
//            let algoritm: CCAlgorithm  = UInt32(kCCAlgorithmAES128)
//            let options: CCOptions     = UInt32(options)
//            
//            var numBytesEncrypted: size_t = 0
//            
//            let cryptStatus = CCCrypt(operation,
//                                      algoritm,
//                                      options,
//                                      (keyData as NSData).bytes, keyLength,
//                                      initializationVector,
//                                      data.bytes, data.length,
//                                      cryptData.mutableBytes, cryptData.length,
//                                      &numBytesEncrypted)
//            
//            if UInt32(cryptStatus) == UInt32(kCCSuccess) {
//                cryptData.length = Int(numBytesEncrypted)
//                let unencryptedMessage = String(data: cryptData as Data, encoding: String.Encoding.utf8)
//                return unencryptedMessage
//            } else {
//                return nil
//            }
//        }
//        return nil
//    }
//}
//
//
