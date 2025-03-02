/*
 * SPDX-FileCopyrightText: 2025 Alexey Illarionov and the wasm2class-gradle-plugin project contributors
 * SPDX-License-Identifier: Apache-2.0
 */

import at.released.wasm2class.Wasm2ClassVariantExtension

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.wasm2class)
}

android {
    namespace = "com.example.wasm2class.android.kotlin.lib.lib1"
    compileSdk = 35

    defaultConfig {
        minSdk = 28
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = false
            proguardFiles(
                getDefaultProguardFile("proguard-android.txt"),
                "proguard-rules.pro",
            )
        }
        create("staging") {
            initWith(get("release"))
        }
    }

    flavorDimensions += "version"
    productFlavors {
        create("demo") {
            dimension = "version"
        }
        create("full") {
            dimension = "version"
        }
    }

    androidComponents {
        onVariants(selector().withName("fullStaging")) { variant ->
            variant.getExtension(Wasm2ClassVariantExtension::class.java)?.apply {
                modules {
                    create("clock") {
                        wasm = file("../testwasm/clock.wasm")
                        targetPackage = "com.example.wasm2class.android.kotlin.lib.lib1.clock"
                    }
                }
            } ?: error("Wasm2ClassExtension extension not registered")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
}

wasm2class {
    targetPackage = "com.example.wasm2class.android.kotlin.lib.lib1"
    modules {
        create("helloworld") {
            wasm = file("../testwasm/helloworld.wasm")
        }
    }
}

dependencies {
    implementation(libs.chicory.wasi)
}
