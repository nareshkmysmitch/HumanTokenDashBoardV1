package com.healthanalytics.android.core

import com.seiko.imageloader.ImageLoader
import io.ktor.client.HttpClient
import io.ktor.client.engine.darwin.Darwin
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.header
//
//actual fun createPlatformImageLoader(): ImageLoader {
//    // On iOS/macOS targets, use the Darwin engine for TLS + redirects out of the box.
//    val ktorClient = HttpClient(Darwin) {
//        followRedirects = true
//
//        install(DefaultRequest) {
//            header("User-Agent", "Mozilla/5.0")
//        }
//
//        install(Logging) {
//            level = LogLevel.INFO
//        }
//
//        // If needed: configure Darwin’s trust store settings, e.g.,
//        // engine {
//        //   configureRequest { request ->
//        //     // custom certificate pinning or ATS exceptions
//        //   }
//        // }
//    }
//
//    return ImageLoader {
//        httpClient = ktorClient
//        // (Optional) you can tweak caching or placeholders here as well.
//    }
//}