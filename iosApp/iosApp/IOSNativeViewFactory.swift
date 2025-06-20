//
//  IOSNativeViewFactory.swift
//  iosApp
//
//  Created by Puvi on 28/05/25.
//
import SwiftUI
import ComposeApp

class IOSNativeViewFactory:  NativeViewFactory {
    func showAlertDialog(primaryText: String, secondaryText: String, onDismiss: @escaping () -> Void, onLogout: @escaping () -> Void, onNegativeTxt: String, onPositiveTxt: String) {
        let alert = UIAlertController(
             title: primaryText,
             message: secondaryText,
             preferredStyle: .alert
           )

           let okAction = UIAlertAction(title: onPositiveTxt, style: .default) { _ in
             print("OK tapped")
             onLogout()
           }

           let cancelAction = UIAlertAction(title: onNegativeTxt, style: .cancel) { _ in
             print("Cancel tapped")
             onDismiss()
           }

           alert.addAction(okAction)
           alert.addAction(cancelAction)

           DispatchQueue.main.async {
             if let windowScene = UIApplication.shared.connectedScenes.first as? UIWindowScene,
               let rootVC = windowScene.windows.first(where: { $0.isKeyWindow })?.rootViewController {

               var topVC = rootVC
               while let presented = topVC.presentedViewController {
                 topVC = presented
               }

               topVC.present(alert, animated: true)
             }
           }
    }
    
    
    
    
    static var shared = IOSNativeViewFactory()


}



