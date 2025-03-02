/*
 * SPDX-FileCopyrightText: 2025 Alexey Illarionov and the wasm2class-gradle-plugin project contributors
 * SPDX-License-Identifier: Apache-2.0
 */

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.wasm2class)
}

android {
    namespace = "com.example.wasm2class.android.java.app"
    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "com.example.wasm2class.android.java.app"
        minSdk = libs.versions.minSdk.get().toInt()
        targetSdk = libs.versions.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            signingConfig = signingConfigs.getByName("debug")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

wasm2class {
    targetPackage = "com.example.wasm2class.android.java.app"
    modules {
        create("helloworld") {
            wasm = file("../testwasm/helloworld.wasm")
        }
        create("clock") {
            wasm = file("../testwasm/clock.wasm")
        }
    }
}

dependencies {
    implementation(libs.chicory.wasi)
}
