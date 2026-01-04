plugins {
    alias(libs.plugins.android.application)
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.skilllsetujava"
    compileSdk = 36

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

    // ---------------- CORE ANDROID ----------------
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.activity:activity:1.8.2")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")

    // ---------------- UI COMPONENTS ----------------
    implementation("androidx.cardview:cardview:1.0.0")
    implementation("androidx.drawerlayout:drawerlayout:1.2.0")

    // ---------------- ANIMATIONS ----------------
    implementation("androidx.dynamicanimation:dynamicanimation:1.0.0")
    implementation("com.airbnb.android:lottie:6.4.0")

    // ---------------- GEMINI AI (CORRECT) ----------------
    implementation("com.google.ai.client.generativeai:generativeai:0.9.0")

    // Required by Gemini Futures
    implementation("com.google.guava:guava:31.1-android")

    // ---------------- COROUTINES (SAFE) ----------------
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")

    // ---------------- JSON ----------------
    implementation("com.google.code.gson:gson:2.10.1")

    // ---------------- TESTING ----------------
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")


    // Firebase BoM (Bill of Materials)
    implementation(platform("com.google.firebase:firebase-bom:33.5.1"))
    // Firebase Authentication
    implementation("com.google.firebase:firebase-auth")
    // Firebase Firestore
    implementation("com.google.firebase:firebase-firestore")
    implementation("com.google.firebase:firebase-analytics")
    // Firebase Storage (for profile pictures)
    implementation ("com.google.firebase:firebase-storage")
    // Google Sign-In
    implementation("com.google.android.gms:play-services-auth:20.7.0")


}
