package com.healthanalytics.android.core


import com.seiko.imageloader.ImageLoader
import io.ktor.client.*
 import io.ktor.client.plugins.logging.*
import io.ktor.client.plugins.*
import io.ktor.client.request.header

//actual fun createPlatformImageLoader(): ImageLoader {
//    // Build a Ktor HttpClient backed by OkHttp, enabling redirects + a User-Agent header.
//    val ktorClient = HttpClient(OkHttp) {
//        followRedirects = true
//
//        install(DefaultRequest) {
//            // Some CDNs (like Shopify) reject requests without a UA header
//            header("User-Agent", "Mozilla/5.0")
//        }
//
//        install(Logging) {
//            level = LogLevel.INFO
//        }
//
//        // If you need custom SSL tweaks (e.g., trust chain), configure here.
//        // OkHttp engine will use the platform’s default TLS settings.
//    }
//
//    return ImageLoader {
//        httpClient = ktorClient
//        // You can tune Seiko’s caching, placeholder painters, etc., here as well:
//        // placeholder(android.R.drawable.stat_sys_download)      // optional default
//        // error(android.R.drawable.stat_notify_error)            // optional error
//    }
//}
