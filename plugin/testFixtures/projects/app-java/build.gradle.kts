/*
 * SPDX-FileCopyrightText: 2025 Alexey Illarionov and the wasm2class-gradle-plugin project contributors
 * SPDX-License-Identifier: Apache-2.0
 */

plugins {
    `java`
    application
    alias(libs.plugins.wasm2class)
}

wasm2class {
    targetPackage = "com.example.wasm2class.app.java"
    create("helloworld") {
        wasmFile = file("testwasm/helloworld.wasm")
    }
    create("clock") {
        wasmFile = file("testwasm/clock.wasm")
    }
}

application {
    mainClass = "com.example.wasm2class.app.java.Main"
}
