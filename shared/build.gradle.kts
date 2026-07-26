plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    id("app.cash.sqldelight") version "2.0.2"
    kotlin("plugin.serialization") version "1.8.0"
}

kotlin {
    androidTarget {
        compilations.all {
            kotlinOptions {
                jvmTarget = "1.8"
            }
        }
    }

    repositories {
        google()
        mavenCentral()
    }
    val ktorVersion = "3.1.1"
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.9.0")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.9.0")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-core:1.1.0")
                implementation("io.ktor:ktor-client-core:$ktorVersion")
                implementation("io.ktor:ktor-client-content-negotiation:$ktorVersion")
                implementation("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")



                implementation("app.cash.sqldelight:sqlite-driver:2.0.2")
                implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.4.0")
                implementation("com.google.code.gson:gson:2.9.0")
            }
        }

        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }

        val androidMain by getting {
            dependencies {
                implementation("app.cash.sqldelight:android-driver:2.0.2")
                implementation("io.ktor:ktor-client-okhttp:$ktorVersion")
            }
        }

        val iosMain by creating {
            dependencies {
                implementation("app.cash.sqldelight:native-driver:2.0.2")
                implementation("io.ktor:ktor-client-darwin:$ktorVersion")
            }
        }

    }
}

android {
    namespace = "com.tay.taysecurity"
    compileSdk = 37
    defaultConfig {
        minSdk = 29
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

sqldelight {
    databases {
        create("TaysecurityDb") {
            packageName.set("com.tay.taysecurity.database")
        }
    }
}





