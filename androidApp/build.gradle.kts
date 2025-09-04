plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.kotlinAndroid)
    alias(libs.plugins.compose.compiler)
    id("dagger.hilt.android.plugin")
    id ("kotlin-parcelize")
    kotlin("kapt")
}

android {
    namespace = "com.tay.taysecurity.android"
    compileSdk = 36
    defaultConfig {
        applicationId = "com.tay.taysecurity.android"
        minSdk = 28
        targetSdk = 36
        versionCode = 7
        versionName = "1.0.6"
    }
     signingConfigs {
        create("release") {
            storeFile = file("../keystore/valusure.jks")
            storePassword = "taySecurity@2025"
            keyAlias = "taySecurity2025"
            keyPassword = "taySecurity@2025"
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

    implementation("com.google.dagger:hilt-android:2.57")
    implementation("androidx.hilt:hilt-navigation-compose:1.2.0")
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation ("com.google.code.gson:gson:2.8.9")
    implementation("io.coil-kt.coil3:coil-compose:3.3.0")
    implementation("androidx.exifinterface:exifinterface:1.3.3")
    implementation("com.google.accompanist:accompanist-permissions:0.29.0-alpha")
    kapt("com.google.dagger:hilt-android-compiler:2.57")
    implementation("com.google.android.play:app-update-ktx:2.1.0")

    implementation(projects.shared)
    implementation("androidx.compose.ui:ui:1.8.3")
    implementation("androidx.compose.ui:ui-tooling:1.8.3")
    implementation("androidx.compose.material:material:1.8.3")
    implementation("androidx.compose.material:material-icons-extended:1.7.8")


    implementation(libs.androidx.core.splashscreen)
    implementation("androidx.activity:activity-compose:1.10.1")
    implementation("androidx.navigation:navigation-compose:2.9.3")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.9.2")
    implementation("androidx.compose.runtime:runtime-livedata:1.8.3")

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.10.2")

}

kapt {
    correctErrorTypes = true
}