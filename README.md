# Browshere Android Browser App

Browshere is a native Android browser app scaffold designed for mobile first. It includes the main code a usable browser app needs: a browser toolbar, URL/search input, page loading progress, secure `WebView` defaults, safe browsing support, and back/forward/home/refresh controls.

## What is included

- Native Android project structure using Gradle Kotlin DSL.
- A `MainActivity` that configures a `WebView` as the live browser surface.
- Search-or-navigate address handling for website URLs and search queries.
- Browser controls for back, forward, refresh, and home.
- Safer defaults including mixed-content blocking, safe browsing, third-party-cookie blocking, and non-HTTP(S) navigation blocking.
- A dark mobile UI optimized for phone screens.
- A GitHub Actions workflow that can build a debug APK artifact for download.

## Project structure

- `settings.gradle.kts` — Android project module definitions.
- `build.gradle.kts` — top-level Android and Kotlin plugin declarations.
- `app/build.gradle.kts` — Android app module config and dependencies.
- `app/src/main/java/com/browshere/app/MainActivity.kt` — the main browser activity and navigation logic.
- `app/src/main/res/layout/activity_main.xml` — the mobile browser screen layout.
- `app/src/main/res/values/*` — strings, colors, and theme values.
- `app/src/main/res/xml/network_security_config.xml` — cleartext blocking configuration.
- `.github/workflows/build-android-apk.yml` — CI workflow that builds a debug APK artifact.

## Easiest way to get an APK

If you are confused by Android Studio, the easiest route is:

1. Push this repo to GitHub.
2. Open the **Actions** tab.
3. Run **Build Android APK**.
4. When the workflow finishes, open the workflow run.
5. Download the `browshere-debug-apk` artifact.
6. Install that APK on your Android phone.

## Build locally in Android Studio

1. Open this project in Android Studio.
2. Let Gradle sync finish.
3. Connect an Android phone or start an emulator.
4. Click **Build > Build Bundle(s) / APK(s) > Build APK(s)**.
5. Install the generated debug APK from `app/build/outputs/apk/debug/`.

## Build from command line

You need Java 17, Gradle, and the Android SDK installed.

```bash
gradle assembleDebug
```

Then install the APK from:

```text
app/build/outputs/apk/debug/
```

## Important note about iPhone

This code is for Android. If you want Browshere on iPhone, it would need a separate iOS app target written with Swift/SwiftUI or another iOS-compatible stack.
