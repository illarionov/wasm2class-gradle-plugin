/*
 * SPDX-FileCopyrightText: 2025 Alexey Illarionov and the wasm2class-gradle-plugin project contributors
 * SPDX-License-Identifier: Apache-2.0
 */

plugins {
    `kotlin-dsl`
}

group = "at.released.wasm2class.buildlogic.project"

dependencies {
    implementation(libs.detekt.plugin)
    implementation(libs.spotless.plugin)
}
