# Unity Ads Tester (Android / Kotlin)

A small native Android app to initialize Unity Ads, load one placement, show the loaded ad, and display SDK callbacks in a live log.

## Build on GitHub Actions

1. Create a new GitHub repository, for example `unity-ads-tester`.
2. Upload/extract all files from this project into the repository root. Keep `.github/workflows/build.yml` in place.
3. Commit to the `main` branch.
4. Open **Actions** → **Build Unity Ads Tester APK** → **Run workflow**.
5. Wait for the workflow to finish successfully.
6. Open the completed workflow run, scroll to **Artifacts**, and download `UnityAdsTester-debug`.
7. Extract the ZIP and install `app-debug.apk` on your Android phone. Allow installation from the browser/files app if Android asks.

## Use the app

1. In Unity Dashboard, select the correct project and copy the **Android Game ID**.
2. Copy the exact ad Unit / Placement identifier configured for Android.
3. Open Unity Ads Tester and paste the Game ID and Placement ID.
4. Keep **Test Mode** enabled for initial testing.
5. Tap **Initialize SDK** and wait for the success callback.
6. Tap **Load Ad** and wait for `SUCCESS: Ad loaded`.
7. Tap **Show Ad**.

## Important

- Use your own Android Game ID and exact Android placement/ad unit identifier.
- Do not paste Secret Keys, API keys, or dashboard credentials into this app.
- Test Mode is enabled by default. Do not use repeated artificial ad clicks or generate invalid traffic.
- A successful SDK initialization does not guarantee ad inventory. A load failure can be caused by placement configuration, dashboard status, network, consent/privacy settings, unsupported SDK configuration, or no available inventory.
- This project is a diagnostic starter, not a guarantee that an ad will load instantly.
- SDK dependency is pinned in `app/build.gradle.kts`. Before production use, check Unity's official Android SDK release notes and migration guide for the newest compatible version and API changes.
- This starter intentionally builds a debug APK. It does not include signing keys or a Play Store release bundle.

## Project structure

- `app/src/main/java/.../MainActivity.kt` — native UI and Unity Ads callbacks
- `app/src/main/AndroidManifest.xml` — Internet permissions and launcher activity
- `app/build.gradle.kts` — Android config and Unity Ads dependency
- `.github/workflows/build.yml` — GitHub Actions APK build
