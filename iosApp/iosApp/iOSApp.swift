import SwiftUI
import ComposeApp

@main
struct iOSApp: App {
    
    init(){
        EncryptionManager_iosKt.isoNativeBridge = IOSNativeBridgeProvider()
        IOSRazorpayInitializer_iosKt.iosRazorpay = RazorpayNativeBridgeProvider()
    }

    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}
