# Health Analytics Mobile App (v1)

A simplified Kotlin Multiplatform (KMP) and Compose Multiplatform (CMP) mobile application that
replicates your Health Analytics website functionality without complex dependencies.

## Features

### 🔐 Phone-based OTP Authentication

- Send OTP to phone number
- Verify OTP for secure login
- Direct integration with your website's authentication API

### 📊 Complete Health Dashboard

- Real-time biomarker data visualization
- Health status indicators and trends
- Category-based biomarker filtering
- Detailed biomarker information cards

### 🛒 Marketplace Integration

- Product browsing with real data from your API
- Add to cart functionality
- Product details and pricing
- Category-based product organization

### 💡 Health Recommendations

- Personalized health recommendations
- Priority-based recommendations (High, Medium, Low)
- Category organization (Nutrition, Exercise, Lifestyle)

### 📱 Mobile-Optimized UI

- Bottom navigation with 4 main sections
- Top toolbar with Profile and Chat access
- Material 3 design system
- Responsive layout for all screen sizes

## Architecture

### Simplified Dependencies

This version eliminates complex dependencies that were causing import issues:

- ❌ No NavHostController (using simple state management)
- ❌ No Koin dependency injection (using direct instantiation)
- ✅ Pure Compose Multiplatform UI
- ✅ Ktor client for API communication
- ✅ Kotlinx serialization for data handling

### Project Structure

```
mobile_app_v1/
├── shared/                          # Kotlin Multiplatform shared code
│   ├── src/commonMain/kotlin/
│   │   ├── data/
│   │   │   ├── models/             # Data classes
│   │   │   ├── network/            # API client
│   │   │   └── repositories/       # Data repositories
│   │   └── presentation/
│   │       ├── screens/            # UI screens
│   │       ├── components/         # Reusable UI components
│   │       └── theme/              # Material theme
│   └── build.gradle.kts            # Shared module config
├── androidApp/                     # Android app
│   ├── src/main/kotlin/            # Android-specific code
│   └── build.gradle.kts            # Android app config
├── settings.gradle.kts             # Project settings
└── build.gradle.kts                # Root build config
```

## API Integration

The app connects directly to your Health Analytics website APIs:

- Authentication: `/v4/human-token/lead/send-otp` and `/v4/human-token/lead/verify-otp`
- Health Data: `/v4/human-token/health-data`
- Products: `/v4/human-token/market-place/products`
- Cart: `/v4/human-token/cart-items`

## Setup Instructions

### 1. Configure API Base URL

Update the base URL in
`shared/src/commonMain/kotlin/com/healthanalytics/shared/data/network/ApiClient.kt`:

```kotlin
private const val BASE_URL = "https://your-api-domain.com"
```

### 2. Build for Android

```bash
cd mobile_app_v1
./gradlew :androidApp:build
```

### 3. Install on Android Device

```bash
./gradlew :androidApp:installDebug
```

## Key Differences from v1

### Simplified Navigation

- Uses Compose state management instead of Navigation Compose
- Direct screen switching with enum-based navigation
- No complex navigation graphs or route definitions

### Direct Dependency Injection

- Manual dependency instantiation instead of Koin
- Repository instances created directly in composables
- Simpler dependency management

### Streamlined Build Configuration

- Minimal plugin dependencies
- Standard Kotlin Multiplatform setup
- No experimental features that could cause compatibility issues

## Supported Platforms

- ✅ Android (API 24+)
- ✅ iOS (Framework ready, UI wrapper needed)

## Next Steps

1. Test the Android app with your actual API endpoints
2. Add iOS app wrapper using SwiftUI
3. Implement additional features like cart management
4. Add offline data caching
5. Implement push notifications for health alerts

This version provides a solid foundation for your mobile app without the complexity issues from the
previous implementation.

* `/composeApp` is for code that will be shared across your Compose Multiplatform applications.
  It contains several subfolders:
    - `commonMain` is for code that’s common for all targets.
    - Other folders are for Kotlin code that will be compiled for only the platform indicated in the
      folder name.
      For example, if you want to use Apple’s CoreCrypto for the iOS part of your Kotlin app,
      `iosMain` would be the right folder for such calls.

* `/iosApp` contains iOS applications. Even if you’re sharing your UI with Compose Multiplatform,
  you need this entry point for your iOS app. This is also where you should add SwiftUI code for
  your project.

Learn more
about [Kotlin Multiplatform](https://www.jetbrains.com/help/kotlin-multiplatform-dev/get-started.html),
[Compose Multiplatform](https://github.com/JetBrains/compose-multiplatform/#compose-multiplatform),
[Kotlin/Wasm](https://kotl.in/wasm/)…

We would appreciate your feedback on Compose/Web and Kotlin/Wasm in the public Slack
channel [#compose-web](https://slack-chats.kotlinlang.org/c/compose-web).
If you face any issues, please report them
on [YouTrack](https://youtrack.jetbrains.com/newIssue?project=CMP).

You can open the web application by running the `:composeApp:wasmJsBrowserDevelopmentRun` Gradle
task.