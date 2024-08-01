plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.kotlinAndroid)
    alias(libs.plugins.compose.compiler)
    id("dagger.hilt.android.plugin")
    kotlin("kapt")
}

android {
    namespace = "com.tay.taysecurity.android"
    compileSdk = 34
    defaultConfig {
        applicationId = "com.tay.taysecurity.android"
        minSdk = 28
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
    }
     signingConfigs {
        create("release") {
            storeFile = file("../keystore/taysecurity.jks")
            storePassword = "taySecurity@2024"
            keyAlias = "taySecurity2024"
            keyPassword = "taySecurity@2024"
        }
    }

    buildFeatures {
        compose = true
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            signingConfig = signingConfigs.getByName("release")
        }
        getByName("debug") {
             isDebuggable = true
        }
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

    implementation("com.google.dagger:hilt-android:2.44")
    implementation("androidx.hilt:hilt-navigation-compose:1.0.0")
    kapt("com.google.dagger:hilt-android-compiler:2.44")

    implementation(projects.shared)
    implementation("androidx.compose.ui:ui:1.6.8")
    implementation("androidx.compose.ui:ui-tooling:1.6.8")
   // implementation("androidx.compose.ui:ui-tooling-preview:1.6.8")
   // implementation("androidx.compose.foundation:foundation:1.6.8")
    implementation("androidx.compose.material:material:1.6.8")
    implementation("androidx.compose.material:material-icons-extended:1.2.0")



    implementation("androidx.activity:activity-compose:1.9.0")

    //implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.navigation:navigation-compose:2.7.7")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.3")
    implementation("androidx.compose.runtime:runtime-livedata:1.6.8")
    //implementation("androidx.constraintlayout:constraintlayout-compose:1.0.1")
}

kapt {
    correctErrorTypes = true
}