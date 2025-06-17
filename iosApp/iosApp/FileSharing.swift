//
//  FileSharing.swift
//  iosApp
//
//  Created by Apple on 17/06/25.
//  Copyright © 2025 com.deepholistic. All rights reserved.
//

import UIKit

@objc(FileSharing)
public class FileSharing: NSObject {
    @objc public static func shareFile(path: String) {
        DispatchQueue.main.async {
            guard let windowScene = UIApplication.shared.connectedScenes.first as? UIWindowScene,
                  let rootVC = windowScene.windows.first?.rootViewController else {
                print("Unable to access rootViewController")
                return
            }
            let url = URL(fileURLWithPath: path)
            let activityVC = UIActivityViewController(activityItems: [url], applicationActivities: nil)
            rootVC.present(activityVC, animated: true, completion: nil)
        }
    }
}

