plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
  //  id("app.cash.sqldelight") version "2.0.1"
    id("com.squareup.sqldelight")
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
    
    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach {
        it.binaries.framework {
            baseName = "shared"
            isStatic = true
        }
    }

    sourceSets {
        commonMain.dependencies {
            //put your multiplatform dependencies here

            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.1")
            implementation("org.jetbrains.kotlinx:kotlinx-serialization-core:1.1.0")
           // implementation("app.cash.sqldelight:sqlite-driver:2.0.2")
            implementation("com.squareup.sqldelight:runtime:1.5.5")
            implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.4.0")
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }


        val androidMain by getting {
            dependencies {
                //implementation("app.cash.sqldelight:android-driver:2.0.2")
                implementation("com.squareup.sqldelight:android-driver:1.5.5")

            }
        }

        val iosMain by creating {
            dependencies {
                implementation("com.squareup.sqldelight:native-driver:1.5.5")
            }
        }

    }
}

android {
    namespace = "com.tay.taysecurity"
    compileSdk = 34
    defaultConfig {
        minSdk = 24
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

sqldelight {
    database("TaysecurityDb") {
        packageName = "com.tay.taysecurity.database"
        sourceFolders = listOf("sqldelight")
    }
}


