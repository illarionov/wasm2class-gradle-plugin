/*
 * SPDX-FileCopyrightText: 2025 Alexey Illarionov and the wasm2class-gradle-plugin project contributors
 * SPDX-License-Identifier: Apache-2.0
 */

plugins {
    application
    alias(libs.plugins.wasm2class)
}

wasm2class {
    targetPackage = "com.example.wasm2class.app.java"
    modules {
        create("helloworld") {
            wasm = file("testwasm/helloworld.wasm")
        }
        create("clock") {
            wasm = file("testwasm/clock.wasm")
        }
    }
}

dependencies {
    implementation(libs.chicory.wasi)
}

application {
    mainClass = "com.example.wasm2class.app.java.Main"
}
