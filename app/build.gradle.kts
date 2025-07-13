plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("org.jetbrains.kotlin.kapt")
    alias(libs.plugins.google.services) // ✅ Плагин Google Services через alias
}

android {
    namespace = "com.example.moneymind"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.moneymind"
        minSdk = 24
        targetSdk = 34
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

    buildFeatures {
        viewBinding = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {
    // 🔹 Room (локальная база данных)
    val room_version = "2.6.1"
    implementation("androidx.room:room-runtime:$room_version")
    kapt("androidx.room:room-compiler:$room_version")
    implementation("androidx.room:room-ktx:$room_version")

    // 🔹 Material Components
    implementation("com.google.android.material:material:1.12.0")

    // 🔹 ViewModel + LiveData
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.7.0")

    // 🔹 Kotlin Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")

    // 🔹 AndroidX Core
    implementation("androidx.core:core-ktx:1.12.0")
    implementation(libs.activity.ktx)
    implementation(libs.appcompat)
    implementation(libs.constraintlayout)

    // 🔹 Charts + ViewPager2
    implementation(libs.mpandroidchart)
    implementation(libs.viewpager2)

    // 🔹 WorkManager
    implementation("androidx.work:work-runtime-ktx:2.9.0")

    // 🔹 Firebase
    implementation("com.google.firebase:firebase-auth:22.3.1")
    implementation("com.google.firebase:firebase-firestore:24.10.1")

    // 🔹 Google Sign-In (Firebase Auth через Google аккаунт)
    implementation("com.google.android.gms:play-services-auth:21.0.0")

    // 🔹 Тестирование
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}