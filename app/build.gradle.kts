plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.taskydad.unityadstester"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.taskydad.unityadstester"
        minSdk = 23
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {
    // Unity Ads Android SDK. Check Unity's release notes before upgrading this version.
    implementation("com.unity3d.ads:unity-ads:4.16.5")
}
