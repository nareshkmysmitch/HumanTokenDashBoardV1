//rootProject.name = "HumanTokenDashBoardV1"
//enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
//
//pluginManagement {
//    repositories {
//        google {
//            mavenContent {
//                includeGroupAndSubgroups("androidx")
//                includeGroupAndSubgroups("com.android")
//                includeGroupAndSubgroups("com.google")
//            }
//        }
//        mavenCentral()
//        gradlePluginPortal()
//
//
//        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev") // ✅ Add this
//    }
//}
//
//dependencyResolutionManagement {
//    repositories {
//        google {
//            mavenContent {
//                includeGroupAndSubgroups("androidx")
//                includeGroupAndSubgroups("com.android")
//                includeGroupAndSubgroups("com.google")
//            }
//        }
//        mavenCentral()
//    }
//}
//
//plugins {
//    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
//}
//
//include(":composeApp")


rootProject.name = "HumanTokenDashBoardV1"
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }
}

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

include(":composeApp")
