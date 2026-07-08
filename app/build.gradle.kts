plugins {
    id("com.android.application")
}

android {
    namespace = "com.example.callblocker"
    compileSdk = 34

    buildFeatures {
        aidl = false
        renderScript = false
        shaders = false
    }
    defaultConfig {
        applicationId = "com.example.callblocker"
        minSdk = 29
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
}
