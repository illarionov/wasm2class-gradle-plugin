/*
 * SPDX-FileCopyrightText: 2025 Alexey Illarionov and the wasm2class-gradle-plugin project contributors
 * SPDX-License-Identifier: Apache-2.0
 */

import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    application
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.wasm2class)
}

kotlin {
    compilerOptions {
        jvmTarget = JvmTarget.JVM_11
    }
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

application {
    mainClass = "com.example.wasm2class.kotlin.app.MainKt"
}

wasm2class {
    targetPackage = "com.example.wasm2class.kotlin.app"
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

