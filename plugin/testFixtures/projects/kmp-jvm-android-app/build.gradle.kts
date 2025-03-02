/*
 * SPDX-FileCopyrightText: 2025 Alexey Illarionov and the wasm2class-gradle-plugin project contributors
 * SPDX-License-Identifier: Apache-2.0
 */

import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.application)
    alias(libs.plugins.wasm2class)
}

kotlin {
    androidTarget()
    jvm {
        @Suppress("OPT_IN_USAGE")
        mainRun {
            mainClass = "com.example.wasm2class.kmp.MainKt"
        }
    }

    sourceSets {
        commonMain.dependencies {
            implementation(libs.chicory.wasi)
        }
    }
}

android {
    namespace = "com.example.wasm2class.kmp"
    compileSdk = 35

    defaultConfig {
        minSdk = 28
        targetSdk = 35

        applicationId = "com.example.wasm2class.kmp.androidApp"
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    lint {
        checkOnly.add("NewApi")
        checkDependencies = false
    }
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile> {
    compilerOptions {
        jvmTarget = JvmTarget.JVM_11
    }
}

wasm2class {
    targetPackage = "com.example.wasm2class.kmp"
    modules {
        create("helloworld") {
            wasm = file("../testwasm/helloworld.wasm")
        }
        create("clock") {
            wasm = file("../testwasm/clock.wasm")
        }
    }
}
