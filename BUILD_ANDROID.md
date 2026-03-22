# How to build Browshere Android APK

If you are confused about what tool to use, use one of these two paths.

## Fastest path if you only want an APK

Use **GitHub Actions**.

1. Put this project in a GitHub repository.
2. Open the repository in GitHub.
3. Tap or click **Actions**.
4. Open **Build Android APK**.
5. Tap **Run workflow**.
6. Wait for the workflow to finish.
7. Download the `browshere-debug-apk` artifact.
8. Install that APK on your Android phone.

You do **not** need Android Studio for this path.

## Best path if you have a laptop or desktop

Use **Android Studio**.

1. Install Android Studio.
2. Open this project folder.
3. Wait for Gradle sync to finish.
4. Connect your Android phone with USB debugging enabled, or start an emulator.
5. Click **Build > Build Bundle(s) / APK(s) > Build APK(s)**.
6. After the build finishes, open `app/build/outputs/apk/debug/`.
7. Install the generated APK.

## Command line path

If you have Java 17 and the Android SDK installed, run:

```bash
gradle assembleDebug
```

The APK will be created in:

```text
app/build/outputs/apk/debug/
```

## What tool should you use?

- If you are on your phone: use **GitHub Actions**.
- If you are on a computer and want the easiest app-building tool: use **Android Studio**.
- If you already know terminals and SDK setup: use **gradle assembleDebug**.
