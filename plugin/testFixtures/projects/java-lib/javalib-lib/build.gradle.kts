/*
 * SPDX-FileCopyrightText: 2025 Alexey Illarionov and the wasm2class-gradle-plugin project contributors
 * SPDX-License-Identifier: Apache-2.0
 */

plugins {
    `java-library`
    alias(libs.plugins.wasm2class)
}

wasm2class {
    targetPackage = "com.example.wasm2class.javalib.lib"
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
    implementation(libs.chicory.annotations)
    implementation(libs.chicory.wasi)
}
