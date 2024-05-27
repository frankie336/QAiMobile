plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    alias(libs.plugins.googleDevtoolsKsp)
    alias(libs.plugins.hiltAndroid)
    alias(libs.plugins.kotlin.parcelize)
}

android {
    namespace = "com.app.qaimobile"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.app.qaimobile"
        minSdk = 25
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.12"
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation("androidx.compose.material3:material3:1.1.0") // Updated to the latest stable version
    implementation(libs.androidx.room.common)
    implementation("androidx.datastore:datastore-preferences:1.0.0")
    implementation(libs.play.services.basement)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.room.ktx)
    implementation(libs.androidx.adapters)
    implementation(libs.androidx.compose.material)

    // DataStore Preferences dependency
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    // Hilt Android
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    implementation(libs.hilt.navigation.compose)

    // Room
    implementation("androidx.room:room-runtime:2.5.0") // Updated to latest version
    ksp("androidx.room:room-compiler:2.5.0")

    // Constraint Layout
    implementation(libs.constraintlayout.compose)

    // Retrofit
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation(libs.logging.interceptor)

    // System UI Controller
    implementation(libs.accompanist.systemuicontroller)

    // Compose Lifecycle
    implementation(libs.lifecycle.runtime.compose)

    // DataStore
    implementation(libs.datastore.preferences)

    // Splash API
    implementation(libs.splash.screen)

    // Compose Destinations
    implementation(libs.compose.destinations.core)
    ksp(libs.compose.destinations.ksp)
}

ksp {
    arg("compose-destinations.generateNavGraphs", "false")
}