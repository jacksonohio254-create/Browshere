# Browshere Android Browser App

Browshere is now a native Android browser app scaffold instead of a desktop Electron shell. It is designed for mobile first and includes the core code a usable browser app needs: a browser toolbar, URL/search input, page loading progress, secure `WebView` defaults, safe browsing support, and back/forward/home/refresh controls.

## What is included

- Native Android project structure using Gradle Kotlin DSL.
- A `MainActivity` that configures a `WebView` as the live browser surface.
- Search-or-navigate address handling for website URLs and search queries.
- Browser controls for back, forward, refresh, and home.
- Safer defaults including mixed-content blocking, safe browsing, third-party-cookie blocking, and non-HTTP(S) navigation blocking.
- A dark mobile UI optimized for phone screens.

## Project structure

- `settings.gradle.kts` — Android project module definitions.
- `build.gradle.kts` — top-level Android and Kotlin plugin declarations.
- `app/build.gradle.kts` — Android app module config and dependencies.
- `app/src/main/java/com/browshere/app/MainActivity.kt` — the main browser activity and navigation logic.
- `app/src/main/res/layout/activity_main.xml` — the mobile browser screen layout.
- `app/src/main/res/values/*` — strings, colors, and theme values.
- `app/src/main/res/xml/network_security_config.xml` — cleartext blocking configuration.

## How to run on mobile

### Option 1: Android Studio

1. Open this project in Android Studio.
2. Let Gradle sync finish.
3. Connect an Android phone or start an emulator.
4. Press **Run**.

### Option 2: Build from command line

```bash
gradle assembleDebug
```

Then install the APK on an Android device.

## Important note about iPhone

This code is for Android. If you want Browshere on iPhone, it would need a separate iOS app target written with Swift/SwiftUI or another iOS-compatible stack.
