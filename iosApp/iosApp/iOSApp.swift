import SwiftUI
import ComposeApp

@main
struct iOSApp: App {
    
    init(){
        EncryptionManager_iosKt.isoNativeBridge = IOSNativeBridgeProvider()
    }
    
    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}
