plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "id.rammonitor.simple"
    compileSdk = 35

    defaultConfig {
        applicationId = "id.rammonitor.simple"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
    }
}
