plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.skilllsetujava"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        applicationId = "com.example.skilllsetujava"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    // Material Design
    implementation("com.google.android.material:material:1.11.0")
// ConstraintLayout
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
// CardView
    implementation("androidx.cardview:cardview:1.0.0")
// For animations
    implementation("androidx.dynamicanimation:dynamicanimation:1.0.0")
    implementation("com.airbnb.android:lottie:6.4.0")
}