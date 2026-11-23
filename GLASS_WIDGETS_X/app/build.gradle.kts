plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.glasswidgetsx"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.glasswidgetsx"
        minSdk = 26
        targetSdk = 36
        versionCode = 5
        versionName = "1.5.0"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
}
