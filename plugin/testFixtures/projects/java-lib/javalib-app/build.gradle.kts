/*
 * SPDX-FileCopyrightText: 2025 Alexey Illarionov and the wasm2class-gradle-plugin project contributors
 * SPDX-License-Identifier: Apache-2.0
 */

plugins {
    application
}

dependencies {
    implementation(project(":javalib-lib"))
}

application {
    mainClass = "com.example.wasm2class.javalib.app.Main"
}
